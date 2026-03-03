package com.jeerovan.icon.traces

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser

object IconRepository {
    // Cache the raw string names (very lightweight on RAM)
    private var cachedDrawableNames: List<String>? = null

    // 1. Private function to parse XML and get unique names ONCE
    private suspend fun loadDrawableNames(context: Context): List<String> = withContext(Dispatchers.IO) {
        if (cachedDrawableNames != null) return@withContext cachedDrawableNames!!

        val names = mutableSetOf<String>()
        try {
            // Get string names from appfilter.xml (FAST)
            val parser = context.resources.getXml(context.resources.getIdentifier("appfilter", "xml", context.packageName))
            var eventType = parser.eventType

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "item") {
                    val drawableName = parser.getAttributeValue(null, "drawable")
                    if (drawableName != null && drawableName.startsWith("_")) {
                        names.add(drawableName)
                    }
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
            Log.e("IconLoader", "Error parsing appfilter.xml", e)
        }

        val nameList = names.toList()
        cachedDrawableNames = nameList
        nameList
    }

    // 2. Public function to get a specific page of resolved IconItems (SLOW part, but paginated)
    suspend fun getIconsPage(context: Context, offset: Int, limit: Int): List<IconItem> = withContext(Dispatchers.IO) {
        val names = loadDrawableNames(context)

        // Return empty if we've reached the end
        if (offset >= names.size) return@withContext emptyList()

        // Get the specific chunk of names
        val chunk = names.drop(offset).take(limit)

        val packageName = context.packageName
        val resources = context.resources
        val icons = ArrayList<IconItem>(chunk.size)

        // Resolve IDs only for this specific chunk
        for (drawableName in chunk) {
            val adaptiveId = resources.getIdentifier(drawableName, "drawable", packageName)
            val expectedForegroundName = "fg$drawableName"
            val foregroundId = resources.getIdentifier(expectedForegroundName, "drawable", packageName)

            if (adaptiveId != 0 && foregroundId != 0) {
                icons.add(IconItem(drawableName, adaptiveId, foregroundId))
            }
        }
        icons
    }
}
