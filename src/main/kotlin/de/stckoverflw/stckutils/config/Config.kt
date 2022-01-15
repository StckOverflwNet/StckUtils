package de.stckoverflw.stckutils.config

import de.stckoverflw.stckutils.config.impl.AllAdvancementsDataConfig
import de.stckoverflw.stckutils.config.impl.AllItemsDataConfig
import de.stckoverflw.stckutils.config.impl.AllMobsDataConfig
import de.stckoverflw.stckutils.config.impl.ChallengeConfig
import de.stckoverflw.stckutils.config.impl.ChallengeDataConfig
import de.stckoverflw.stckutils.config.impl.GameChangeConfig
import de.stckoverflw.stckutils.config.impl.GameChangeDataConfig
import de.stckoverflw.stckutils.config.impl.GoalConfig
import de.stckoverflw.stckutils.config.impl.HideConfig
import de.stckoverflw.stckutils.config.impl.LanguageConfig
import de.stckoverflw.stckutils.config.impl.PositionDataConfig
import de.stckoverflw.stckutils.config.impl.ResetSettingsConfig
import de.stckoverflw.stckutils.config.impl.TimerConfig
import de.stckoverflw.stckutils.config.impl.TimerDataConfig

object Config {

    lateinit var resetSettingsConfig: ResetSettingsConfig
    lateinit var challengeConfig: ChallengeConfig
    lateinit var challengeDataConfig: ChallengeDataConfig
    lateinit var gameChangeConfig: GameChangeConfig
    lateinit var gameChangeDataConfig: GameChangeDataConfig
    lateinit var goalConfig: GoalConfig
    lateinit var hideConfig: HideConfig
    lateinit var positionDataConfig: PositionDataConfig
    lateinit var timerConfig: TimerConfig
    lateinit var timerDataConfig: TimerDataConfig
    lateinit var allItemsDataConfig: AllItemsDataConfig
    lateinit var allMobsDataConfig: AllMobsDataConfig
    lateinit var allAdvancementsDataConfig: AllAdvancementsDataConfig
    lateinit var languageConfig: LanguageConfig

    operator fun invoke() {
        reloadConfig()
    }

    private fun reloadConfig() {
        resetSettingsConfig = ResetSettingsConfig()
        challengeConfig = ChallengeConfig()
        challengeDataConfig = ChallengeDataConfig()
        gameChangeConfig = GameChangeConfig()
        gameChangeDataConfig = GameChangeDataConfig()
        hideConfig = HideConfig()
        timerConfig = TimerConfig()
        timerDataConfig = TimerDataConfig()
        goalConfig = GoalConfig()
        allItemsDataConfig = AllItemsDataConfig()
        allMobsDataConfig = AllMobsDataConfig()
        allAdvancementsDataConfig = AllAdvancementsDataConfig()
        languageConfig = LanguageConfig()
    }

    fun reloadPositions() {
        positionDataConfig = PositionDataConfig()
    }
}
