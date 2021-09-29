package de.stckoverflw.stckutils.config

import de.stckoverflw.stckutils.config.impl.*

object Config {

    lateinit var resetSettingsConfig: ResetSettingsConfig
    lateinit var challengeConfig: ChallengeConfig
    lateinit var challengeDataConfig: ChallengeDataConfig
    lateinit var gameChangeConfig: GameChangeConfig
    lateinit var gameChangeDataConfig: GameChangeDataConfig
    lateinit var goalConfig: GoalConfig
    lateinit var positionDataConfig: PositionDataConfig
    lateinit var timerConfig: TimerConfig
    lateinit var timerDataConfig: TimerDataConfig
    lateinit var allItemsDataConfig: AllItemsDataConfig
    lateinit var allMobsDataConfig: AllMobsDataConfig
    lateinit var allAdvancementsDataConfig: AllAdvancementsDataConfig

    operator fun invoke() {
        reloadConfig()
    }

    fun reloadConfig() {
        resetSettingsConfig = ResetSettingsConfig()
        challengeConfig = ChallengeConfig()
        challengeDataConfig = ChallengeDataConfig()
        gameChangeConfig = GameChangeConfig()
        gameChangeDataConfig = GameChangeDataConfig()
        timerConfig = TimerConfig()
        timerDataConfig = TimerDataConfig()
        goalConfig = GoalConfig()
        allItemsDataConfig = AllItemsDataConfig()
        allMobsDataConfig = AllMobsDataConfig()
        allAdvancementsDataConfig = AllAdvancementsDataConfig()
    }

    fun reloadPositions() {
        positionDataConfig = PositionDataConfig()
    }
}
