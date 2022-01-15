package de.stckoverflw.stckutils.extension

import de.stckoverflw.stckutils.config.Config
import net.axay.kspigot.items.ItemMetaLoreBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.translation.GlobalTranslator
import net.md_5.bungee.api.ChatColor
import java.util.Locale
import java.util.regex.Pattern

fun Component.coloredString(): String {
    return ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacy('&').serialize(this))
}

fun TranslatableComponent.coloredString(locale: Locale = Config.languageConfig.defaultLanguage): String {
    return GlobalTranslator.render(
        this,
        locale
    ).coloredString()
}

fun TranslatableComponent.render(locale: Locale): Component =
    GlobalTranslator.render(this, locale)

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

fun errorTranslatable(key: String, vararg args: ComponentLike): TranslatableComponent =
    Component.translatable(key, Colors.ERROR, args.map { it.asComponent().color(Colors.ERROR_ARGS) })

fun successTranslatable(key: String, vararg args: ComponentLike): TranslatableComponent =
    Component.translatable(key, Colors.SUCCESS, args.map { it.asComponent().color(Colors.SUCCESS_SECONDARY) })
