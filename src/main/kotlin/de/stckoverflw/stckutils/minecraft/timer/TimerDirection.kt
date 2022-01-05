package de.stckoverflw.stckutils.minecraft.timer

import de.stckoverflw.stckutils.extension.KeyIdentifiable

enum class TimerDirection(override val key: String) : KeyIdentifiable {

    FORWARDS("timer.direction.forwards"),
    BACKWARDS("timer.direction.backwards");
}
