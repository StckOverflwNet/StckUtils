package de.stckoverflw.stckutils.config.impl

import de.stckoverflw.stckutils.config.AbstractConfig
import org.bukkit.entity.Player
import java.util.*

class LanguageConfig : AbstractConfig("languages.yml") {

    private val defaultLocale = Locale.ENGLISH

    var defaultLanguage: Locale
        get() = Locale((getSetting("default") ?: defaultLocale.language) as String)
        set(value) = setSetting("default", value.language)

    fun setLanguage(offlinePlayer: Player, locale: Locale) {
        yaml.set(offlinePlayer.uniqueId.toString(), locale.language)
        save()
    }

    fun getLanguage(offlinePlayer: Player): Locale {
        return if (yaml.contains(offlinePlayer.uniqueId.toString())) {
            Locale(yaml.getString(offlinePlayer.uniqueId.toString()) ?: defaultLanguage.language)
        } else {
            setLanguage(offlinePlayer, defaultLanguage)
            defaultLanguage
        }
    }

    fun setSetting(setting: String, value: Any?) {
        yaml.set(setting, value)
        save()
    }

    fun getSetting(setting: String): Any? {
        return yaml.get(setting)
    }
}
