package de.stckoverflw.stckutils.extension

import net.kyori.adventure.text.format.TextColor
import net.md_5.bungee.api.ChatColor

fun ChatColor.asTextColor() = TextColor.color(this.color.rgb)
