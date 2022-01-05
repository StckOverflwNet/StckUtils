package de.stckoverflw.stckutils.minecraft.timer

import de.stckoverflw.stckutils.extension.KeyIdentifiable

enum class AccessLevel(override val key: String) : KeyIdentifiable {
    OPERATOR("timer.access_level.operator"),
    HIDDEN("timer.access_level.hidden"),
    EVERYONE("timer.access_level.everyone"),
    NONE("timer.access_level.none"),
}
