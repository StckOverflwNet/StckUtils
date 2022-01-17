package de.stckoverflw.stckutils.command

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.extension.sendPrefixMessage
import de.stckoverflw.stckutils.extension.successTranslatable
import de.stckoverflw.stckutils.util.Permissions
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.literal
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.kyori.adventure.text.Component.text

class DefaultLanguageCommand {

    fun register() = command("default-language", true) {
        requiresPermission(Permissions.LANGUAGE_COMMAND)
        runs {
            player.sendPrefixMessage(
                successTranslatable("language.current", text(Config.languageConfig.defaultLanguage.displayLanguage))
            )
        }
        StckUtilsPlugin.translationsProvider.locales.forEach {
            literal(it.displayLanguage) {
                runs {
                    Config.languageConfig.defaultLanguage = it
                    player.sendPrefixMessage(successTranslatable("language.set", text(literal)))
                }
            }
        }
    }
}
