package de.stckoverflw.stckutils.config.impl

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.AbstractConfig
import java.util.Locale

class LanguageConfig : AbstractConfig("languages.yml") {

    private val defaultLocale = Locale.ENGLISH

    var defaultLanguage: Locale
        get() = Locale((getSetting("default") ?: defaultLocale.language) as String)
        set(value) {
            setSetting("default", value.language)
            StckUtilsPlugin.translationsProvider.setDefault(value)
        }

    fun setSetting(setting: String, value: Any?) {
        yaml.set(setting, value)
        save()
    }

    fun getSetting(setting: String): Any? {
        return yaml.get(setting)
    }
}
