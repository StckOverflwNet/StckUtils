package de.stckoverflw.stckutils.extension

import de.stckoverflw.stckutils.config.Config
import de.stckoverflw.stckutils.util.Colors
import net.axay.kspigot.extensions.bukkit.render
import net.axay.kspigot.items.ItemMetaLoreBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TranslatableComponent
import java.util.Locale
import java.util.regex.Pattern

fun ItemMetaLoreBuilder.addComponent(component: Component, locale: Locale = Config.languageConfig.defaultLanguage) {
    if (component is TranslatableComponent) {
        component.render(locale)
    } else {
        component
    }.split(Pattern.compile("\\n")).forEach {
        +it
    }
}

fun Component.split(regex: Pattern) =
    ComponentSplitting.split(this, regex)

fun errorTranslatable(key: String, vararg args: ComponentLike = arrayOf()): TranslatableComponent =
    Component.translatable(key)
        .args(args.map { it.asComponent().color(Colors.ERROR_ARGS) })
        .color(Colors.ERROR)

fun successTranslatable(key: String, vararg args: ComponentLike = arrayOf()): TranslatableComponent =
    Component.translatable(key)
        .args(args.map { it.asComponent().color(Colors.SUCCESS_SECONDARY) })
        .color(Colors.SUCCESS)
