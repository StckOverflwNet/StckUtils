package de.stckoverflw.stckutils.command

import de.stckoverflw.stckutils.StckUtilsPlugin
import de.stckoverflw.stckutils.extension.language
import de.stckoverflw.stckutils.util.getSettingsItem
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.literal
import net.axay.kspigot.commands.runs
import java.util.*

class LanguageCommand {

    fun register() = command("language", true) {
        runs {
            player.sendMessage(
                StckUtilsPlugin.translationsProvider.translateWithPrefix(
                    "language.current",
                    player.language,
                    "messages",
                    arrayOf(
                        player.language.language.replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase(
                                Locale.getDefault()
                            ) else it.toString()
                        }
                    )
                )
            )
        }
        literal("Deutsch") {
            runs {
                player.language = Locale.GERMAN
                player.inventory.setItem(8, getSettingsItem(player.language))
                player.sendMessage(
                    StckUtilsPlugin.translationsProvider.translateWithPrefix(
                        "language.set",
                        player.language,
                        "messages",
                        arrayOf(literal)
                    )
                )
            }
        }
        literal("English") {
            runs {
                player.language = Locale.ENGLISH
                player.inventory.setItem(8, getSettingsItem(player.language))
                player.sendMessage(
                    StckUtilsPlugin.translationsProvider.translateWithPrefix(
                        "language.set",
                        player.language,
                        "messages",
                        arrayOf(literal)
                    )
                )
            }
        }
    }
}
