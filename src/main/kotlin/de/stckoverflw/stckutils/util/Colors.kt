package de.stckoverflw.stckutils.util

import net.axay.kspigot.chat.KColors
import net.kyori.adventure.text.format.TextColor

object Colors {

    val PRIMARY: TextColor = KColors.BLUE
    val SECONDARY: TextColor = KColors.GRAY

    val ACTIVE: TextColor = KColors.LIMEGREEN
    val INACTIVE: TextColor = KColors.INDIANRED

    val ERROR: TextColor = KColors.RED
    val ERROR_SECONDARY: TextColor = INACTIVE
    val SUCCESS: TextColor = KColors.DARKGREEN
    val SUCCESS_SECONDARY: TextColor = ACTIVE

    val CONFIGURATION: TextColor = KColors.MISTYROSE

    val CHALLENGE: TextColor = KColors.ORANGERED
    val CHALLENGE_SECONDARY: TextColor = KColors.ORANGE

    val CHALLENGE_COMPOUND: TextColor = CHALLENGE
    val CHALLENGE_COMPOUND_SECONDARY: TextColor = SECONDARY

    val GAME_CHANGE: TextColor = KColors.ROYALBLUE
    val GAME_CHANGE_SECONDARY: TextColor = KColors.DODGERBLUE

    val GAME_CHANGE_COMPOUND: TextColor = GAME_CHANGE
    val GAME_CHANGE_COMPOUND_SECONDARY: TextColor = SECONDARY

    val GOAL: TextColor = KColors.DARKGOLDENROD
    val GOAL_SECONDARY: TextColor = KColors.GOLD

    val GOAL_COMPOUND: TextColor = GOAL
    val GOAL_COMPOUND_SECONDARY: TextColor = SECONDARY

    val MORE_SETTINGS: TextColor = KColors.AQUA
    val MORE_SETTINGS_SECONDARY: TextColor = KColors.MEDIUMAQUAMARINE

    val WORLD_RESET_GUI: TextColor = KColors.MEDIUMVIOLETRED
    val WORLD_RESET_GUI_SECONDARY: TextColor = KColors.PALEVIOLETRED

    val TIMER_SETTINGS: TextColor = KColors.PURPLE
    val TIMER_SETTINGS_SECONDARY: TextColor = KColors.MEDIUMPURPLE

    val TIMER_ITEM: TextColor = KColors.INDIGO
    val TIMER_ITEM_SECONDARY: TextColor = SECONDARY

    val TIMER_DIRECTION: TextColor = KColors.PURPLE

    val VILLAGE_SPAWN: TextColor = KColors.SADDLEBROWN
    val VILLAGE_SPAWN_SECONDARY: TextColor = KColors.ROSYBROWN

    val GOAL_TYPE: TextColor = KColors.ORANGE
    val GOAL_TYPE_SECONDARY: TextColor = SECONDARY

    val JOIN_WHILE_RUNNING: TextColor = KColors.DARKOLIVEGREEN
    val JOIN_WHILE_RUNNING_SECONDARY: TextColor = SECONDARY

    val RESET: TextColor = KColors.DARKRED
    val RESET_SECONDARY: TextColor = SECONDARY

    val COLOR_COMPOUND_SECONDARY: TextColor = SECONDARY

    val WORLD_RESET: TextColor = KColors.DARKRED
    val WORLD_RESET_SECONDARY: TextColor = SECONDARY

    val TIMER_COLOR: TextColor = KColors.VIOLET
    val TIMER_COLOR_SECONDARY: TextColor = SECONDARY

    val SETTINGS: TextColor = ACTIVE

    val ACCENT_AQUA: TextColor = KColors.AQUAMARINE

    val GO_BACK: TextColor = KColors.AQUA
    val GO_BACK_SECONDARY: TextColor = KColors.MEDIUMAQUAMARINE

    val PLAYER_HEAD: TextColor = KColors.DARKBLUE
}
