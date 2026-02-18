package com.jeerovan.icon.traces

import android.content.Context
import android.content.res.XmlResourceParser
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

/**
 * Parses appfilter.xml to extract supported component names.
 * Returns a Set of package names (e.g., "com.whatsapp") to effectively filter missing apps.
 */
fun parseSupportedPackages(context: Context, xmlResId: Int): Set<String> {
    val supportedPackages = HashSet<String>()
    val parser: XmlResourceParser = context.resources.getXml(xmlResId)

    try {
        var eventType = parser.eventType
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                if (parser.name == "item") {
                    // Look for format: component="ComponentInfo{com.package.name/activity}"
                    val component = parser.getAttributeValue(null, "component")

                    if (!component.isNullOrEmpty()) {
                        extractPackageFromComponent(component)?.let { pkg ->
                            if(! supportedPackages.contains(pkg)) supportedPackages.add(pkg)
                        }
                    }
                }
            }
            eventType = parser.next()
        }
    } catch (e: XmlPullParserException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        parser.close()
    }

    return supportedPackages
}

/**
 * Helper to parse "ComponentInfo{com.foo/com.foo.Activity}" -> "com.foo"
 */
private fun extractPackageFromComponent(componentInfo: String): String? {
    // Typical format: ComponentInfo{package.name/activity.name}
    val start = componentInfo.indexOf('{')
    val separator = componentInfo.indexOf('/')

    if (start != -1 && separator != -1 && separator > start) {
        return componentInfo.substring(start + 1, separator)
    }
    return null
}
