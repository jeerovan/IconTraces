package com.jeerovan.icon.traces

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// IconRepository.kt
object IconRepository {
    // Cache the list so we don't reflect every time
    private var cachedIcons: List<IconItem>? = null

    suspend fun getIcons(context: Context): List<IconItem> = withContext(Dispatchers.IO) {
        if (cachedIcons != null) return@withContext cachedIcons!!

        val rClass = R.drawable::class.java
        val fields = rClass.fields

        // 1. Create a fast lookup map for all drawable IDs
        // Capacity hint avoids resizing overhead
        val resourceMap = HashMap<String, Int>(fields.size)
        for (field in fields) {
            try {
                // We only care about fields that look like resources we own
                if (field.name.startsWith("_") || field.name.startsWith("fg_")) {
                    resourceMap[field.name] = field.getInt(null)
                }
            } catch (e: Exception) {
                // Ignore reflection errors
            }
        }

        val icons = ArrayList<IconItem>()

        // 2. Iterate map to find adaptive icons ("_") and pair with foregrounds ("fg_")
        for ((name, id) in resourceMap) {
            if (name.startsWith("_")) {
                // Logic: if adaptive is "_youtube", foreground is "fg_youtube"
                // string concat: "fg" + "_youtube" = "fg_youtube"
                val expectedForegroundName = "fg$name"

                val foregroundId = resourceMap[expectedForegroundName]

                if (foregroundId != null) {
                    icons.add(IconItem(name, id, foregroundId))
                } else {
                    // Optional: Handle missing foregrounds (e.g., skip or use generic)
                    Log.w("IconLoader", "Missing foreground for $name")
                }
            }
        }

        cachedIcons = icons
        icons
    }
}
