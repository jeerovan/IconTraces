package com.jeerovan.icon.traces

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.graphics.Canvas
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

data class IconItem(
    val name: String,
    val resId: Int,
    val foregroundResId: Int
)
data class RequestApp(
    val label: String,
    val packageName: String,
    val componentName: String, // Full Activity path
    val icon: Drawable,
    var isSelected: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val allIcons = mutableListOf<IconItem>()
    private val _visibleIcons = MutableStateFlow<List<IconItem>>(emptyList())
    val visibleIcons = _visibleIcons.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _supportedPackages = MutableStateFlow<Set<String>>(emptySet())
    private val _missingApps = MutableStateFlow<List<RequestApp>>(emptyList())
    val missingApps = _missingApps.asStateFlow()

    init {
        viewModelScope.launch {
            // 1. Load ALL icons in background
            allIcons.addAll(IconRepository.getIcons(getApplication()))
            // 2. Publish only the first 100 to UI immediately so it renders FAST
            _visibleIcons.value = allIcons.take(100)
            loadSupportedPackages()
        }
    }
    // Call this when user scrolls to end
    fun loadNextPage() {
        val currentSize = _visibleIcons.value.size
        val nextChunk = allIcons.drop(currentSize).take(100)
        if (nextChunk.isNotEmpty()) {
            _visibleIcons.value += nextChunk
        }
    }
    private fun loadSupportedPackages() {
        viewModelScope.launch(Dispatchers.IO) {
            val context = getApplication<Application>()
            val packages = parseSupportedPackages(context, R.xml.appfilter)
            _supportedPackages.value = packages
            val missingPackages = getMissingApps(context,packages)
            _missingApps.value = missingPackages
        }
    }

    suspend fun getMissingApps(
        context: Context,
        supportedPackages: Set<String> // Pass your existing supported list here
    ): List<RequestApp> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        // Query all launchable apps
        val apps = pm.queryIntentActivities(intent, 0)

        apps.mapNotNull { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName

            // Filter logic: Skip if already supported or is the icon pack itself
            if (supportedPackages.contains(pkg) || pkg == context.packageName) {
                null
            } else {
                RequestApp(
                    label = resolveInfo.loadLabel(pm).toString(),
                    packageName = pkg,
                    componentName = "${pkg}/${resolveInfo.activityInfo.name}",
                    icon = resolveInfo.loadIcon(pm)
                )
            }
        }.sortedBy { it.label }
    }
    fun toggleSelection(packageName: String, isChecked: Boolean) {
        // 1. Get the current list
        val currentList = _missingApps.value

        // 2. Map over the list to create a new one with the updated item
        // This preserves the order and other items' states
        val updatedList = currentList.map { app ->
            if (app.packageName == packageName) {
                // Create a copy of the data object with the new selection state
                app.copy(isSelected = isChecked)
            } else {
                app
            }
        }

        // 3. Emit the new list to StateFlow
        _missingApps.value = updatedList
    }
    fun selectAll(select: Boolean) {
        val currentList = _missingApps.value
        _missingApps.value = currentList.map { it.copy(isSelected = select) }
    }
    fun sendRequest(context: Context, selectedApps: List<RequestApp>) {
        viewModelScope.launch(Dispatchers.IO) {
            val uris = ArrayList<Uri>()
            val sb = StringBuilder()

            sb.append("<!-- Copy to appfilter.xml -->\n")

            // Clear old cache to prevent junk buildup
            File(context.cacheDir, "requests").deleteRecursively()
            val requestDir = File(context.cacheDir, "requests").apply { mkdirs() }

            selectedApps.forEach { app ->
                // 1. Generate a standardized filename (e.g., com_whatsapp.png)
                // Replacing dots with underscores makes it resource-friendly
                val safeName = app.packageName.replace(".", "_")
                val file = File(requestDir, "$safeName.png")

                try {
                    // 2. Save Icon to File
                    FileOutputStream(file).use { out ->
                        val bitmap = drawableToBitmap(app.icon) // Use helper from previous step
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }

                    // 3. Get URI via FileProvider
                    val uri = FileProvider.getUriForFile(
                        context, "${context.packageName}.provider", file
                    )
                    uris.add(uri)

                    // 4. Append XML Snippet for Email Body
                    // This creates the exact line you need for your project
                    sb.append("<item component=\"ComponentInfo{${app.componentName}}\" drawable=\"$safeName\" />\n")

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // 5. Launch Email Intent on Main Thread
            withContext(Dispatchers.Main) {
                launchEmailIntent(context, uris, sb.toString())
            }
        }
    }

    private fun launchEmailIntent(context: Context, uris: ArrayList<Uri>, body: String) {
        // 1. Create the base intent for sending multiple images
        val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("jeerovan@gmail.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Traces - Icon Request (${uris.size} icons)")
            putExtra(Intent.EXTRA_TEXT, body)
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // 2. Query for apps that can specifically handle "mailto" (Email apps)
        val pm = context.packageManager
        val mailtoIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        val emailApps = pm.queryIntentActivities(mailtoIntent, 0)

        // 3. Filter the original "send image" candidates to match only email apps
        val sendCandidates = pm.queryIntentActivities(emailIntent, 0)
        val targetedIntents = ArrayList<Intent>()

        for (candidate in sendCandidates) {
            val packageName = candidate.activityInfo.packageName

            // Check if this package is also an Email app
            val isEmailApp = emailApps.any { it.activityInfo.packageName == packageName }

            // Special case: Include Gmail and Outlook explicitly if detection fails
            if (isEmailApp || packageName.contains("gm") || packageName.contains("mail") || packageName.contains("outlook")) {
                val targetedIntent = Intent(emailIntent)
                targetedIntent.setPackage(packageName)
                targetedIntents.add(targetedIntent)
            }
        }

        if (targetedIntents.isNotEmpty()) {
            // 4. Create a chooser with the first intent, and add the rest as "initial intents"
            val chooserIntent = Intent.createChooser(targetedIntents.removeAt(0), "Send Request via...")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedIntents.toTypedArray())
            context.startActivity(chooserIntent)
        } else {
            // Fallback: If no strict email match found, just let the user pick from ANY app
            // This prevents the "No app can perform this action" crash
            context.startActivity(Intent.createChooser(emailIntent, "Send Request via..."))
        }
    }


    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) return drawable.bitmap

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth.takeIf { it > 0 } ?: 1,
            drawable.intrinsicHeight.takeIf { it > 0 } ?: 1,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }
}