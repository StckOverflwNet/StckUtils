package de.stckoverflw.stckutils.command

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
                "Your language is currently set to ${
                player.language.language.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
                }"
            )
        }
        literal("Deutsch") {
            runs {
                player.language = Locale.GERMAN
                player.inventory.setItem(8, getSettingsItem(player.language))
                player.sendMessage("Your language was set to German")
            }
        }
        literal("English") {
            runs {
                player.language = Locale.ENGLISH
                player.inventory.setItem(8, getSettingsItem(player.language))
                player.sendMessage("Your language was set to English")
            }
        }
    }
}
