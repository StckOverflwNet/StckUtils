package de.stckoverflw.stckutils.i18n

import de.stckoverflw.stckutils.StckUtilsPlugin
import java.text.MessageFormat
import java.util.*

class TranslationsProvider(private val resourceDirectory: String = "translations") {

    private val resourceBundles: MutableMap<Pair<String, Locale>, ResourceBundle> = mutableMapOf()

    @Throws(MissingResourceException::class)
    fun getBundle(locale: Locale, bundleName: String): ResourceBundle? {
        val bundle = "$resourceDirectory.$bundleName"
        val bundleKey = bundle to locale

        if (resourceBundles[bundleKey] == null) {
            val resourceBundle = ResourceBundle.getBundle(bundle, locale)
            resourceBundles[bundleKey] = resourceBundle
        }
        return resourceBundles[bundleKey]
    }

    @Throws(MissingResourceException::class)
    fun get(key: String, locale: Locale, bundleName: String): String? {
        val bundle = getBundle(locale, bundleName)
        return bundle?.getString(key)
    }

    fun hasKey(key: String, locale: Locale, bundleName: String): Boolean {
        return try {
            val bundle = getBundle(locale, bundleName)
            bundle?.keys?.toList()?.contains(key) == true
        } catch (exception: MissingResourceException) {
            false
        }
    }

    fun translate(key: String, locale: Locale, bundleName: String, replacements: Array<Any?> = arrayOf()): String {
        val translation = get(key, locale, bundleName) ?: return key

        return try {
            val formatter = MessageFormat(translation, locale)
            formatter.format(replacements)
        } catch (exception: MissingResourceException) {
            key
        }
    }

    fun translateWithPrefix(key: String, locale: Locale, bundleName: String, replacements: Array<Any?> = arrayOf()): String {
        return StckUtilsPlugin.prefix + translate(key, locale, bundleName, replacements)
    }
}
