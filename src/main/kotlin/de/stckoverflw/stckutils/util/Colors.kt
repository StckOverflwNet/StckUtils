package de.stckoverflw.stckutils.util

import de.stckoverflw.stckutils.extension.asTextColor
import net.axay.kspigot.chat.KColors

object Colors {

    val PRIMARY = KColors.BLUE.asTextColor()
    val SECONDARY = KColors.GRAY.asTextColor()

    val ACTIVE = KColors.LIMEGREEN.asTextColor()
    val INACTIVE = KColors.INDIANRED.asTextColor()

    val ERROR = KColors.RED.asTextColor()
    val ERROR_ARGS = INACTIVE
    val SUCCESS = KColors.DARKGREEN.asTextColor()
    val SUCCESS_SECONDARY = ACTIVE

    val CONFIGURATION = KColors.MISTYROSE.asTextColor()

    val CHALLENGE = KColors.ORANGERED.asTextColor()
    val CHALLENGE_SECONDARY = KColors.ORANGE.asTextColor()

    val CHALLENGE_COMPOUND = CHALLENGE
    val CHALLENGE_COMPOUND_SECONDARY = SECONDARY

    val GAME_CHANGE = KColors.ROYALBLUE.asTextColor()
    val GAME_CHANGE_SECONDARY = KColors.DODGERBLUE.asTextColor()

    val GAME_CHANGE_COMPOUND = GAME_CHANGE
    val GAME_CHANGE_COMPOUND_SECONDARY = SECONDARY

    val GOAL = KColors.DARKGOLDENROD.asTextColor()
    val GOAL_SECONDARY = KColors.GOLD.asTextColor()

    val GOAL_COMPOUND = GOAL
    val GOAL_COMPOUND_SECONDARY = SECONDARY

    val MORE_SETTINGS = KColors.AQUA.asTextColor()
    val MORE_SETTINGS_SECONDARY = KColors.MEDIUMAQUAMARINE.asTextColor()

    val WORLD_RESET_GUI = KColors.MEDIUMVIOLETRED.asTextColor()
    val WORLD_RESET_GUI_SECONDARY = KColors.PALEVIOLETRED.asTextColor()

    val TIMER_SETTINGS = KColors.PURPLE.asTextColor()
    val TIMER_SETTINGS_SECONDARY = KColors.MEDIUMPURPLE.asTextColor()

    val TIMER_ITEM = KColors.INDIGO.asTextColor()
    val TIMER_ITEM_SECONDARY = SECONDARY

    val TIMER_DIRECTION = KColors.PURPLE.asTextColor()

    val VILLAGE_SPAWN = KColors.SADDLEBROWN.asTextColor()
    val VILLAGE_SPAWN_SECONDARY = KColors.ROSYBROWN.asTextColor()

    val GOAL_TYPE = KColors.ORANGE.asTextColor()
    val GOAL_TYPE_SECONDARY = SECONDARY

    val JOIN_WHILE_RUNNING = KColors.DARKOLIVEGREEN.asTextColor()
    val JOIN_WHILE_RUNNING_SECONDARY = SECONDARY

    val RESET = KColors.DARKRED.asTextColor()
    val RESET_SECONDARY = SECONDARY

    val COLOR_COMPOUND_SECONDARY = SECONDARY

    val WORLD_RESET = KColors.DARKRED.asTextColor()
    val WORLD_RESET_SECONDARY = SECONDARY

    val TIMER_COLOR = KColors.VIOLET.asTextColor()
    val TIMER_COLOR_SECONDARY = SECONDARY

    val SETTINGS = ACTIVE

    val ACCENT_AQUA = KColors.AQUAMARINE.asTextColor()

    val GO_BACK = KColors.AQUA.asTextColor()
    val GO_BACK_SECONDARY = KColors.MEDIUMAQUAMARINE.asTextColor()
}
