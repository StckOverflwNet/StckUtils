package de.stckoverflw.stckutils.i18n

import de.stckoverflw.stckutils.minecraft.challenge.ChallengeManager
import de.stckoverflw.stckutils.minecraft.gamechange.GameChangeManager
import de.stckoverflw.stckutils.minecraft.goal.GoalManager
import net.kyori.adventure.key.Key
import net.kyori.adventure.translation.GlobalTranslator
import net.kyori.adventure.translation.TranslationRegistry
import java.util.Locale
import java.util.ResourceBundle

class TranslationsProvider {

    val key = Key.key("de.stckoverflw.stckutils")

    private val translationRegistry = TranslationRegistry.create(key).apply {
        defaultLocale(Locale.US)
    }

    val locales = listOf(Locale.US, Locale.GERMANY)

    fun setDefault(locale: Locale) {
        translationRegistry.defaultLocale(locale)
    }

    fun registerTranslations() {
        val bundleNames = mutableListOf(
            "translations.general.general",
            "translations.general.items",
            "translations.general.messages",
        )
        bundleNames.apply {
            addAll(ChallengeManager.challenges.map { "translations.minecraft.challenge.${it.id}" })
            addAll(GameChangeManager.gameChanges.map { "translations.minecraft.gamechange.${it.id}" })
            addAll(GoalManager.goals.map { "translations.minecraft.goal.${it.id}" })
        }

        locales.forEach { locale ->
            bundleNames.forEach { bundleName ->
                translationRegistry.registerAll(
                    locale,
                    ResourceBundle.getBundle(bundleName, locale),
                    false
                )
            }
        }
        GlobalTranslator.translator().addSource(translationRegistry)
    }
}
