package de.stckoverflw.stckutils.config

object Config {

    lateinit var resetSettings: ResetSettings
    lateinit var challengeSettings: ChallengeSettings
    lateinit var gameChangeConfig: GameChangeConfig
    lateinit var goalConfig: GoalConfig
    lateinit var positionConfig: PositionConfig
    lateinit var timerConfig: TimerConfig

    operator fun invoke() {
        reloadConfig()
    }

    fun reloadConfig() {
        resetSettings = ResetSettings()
        challengeSettings = ChallengeSettings()
        gameChangeConfig = GameChangeConfig()
        timerConfig = TimerConfig()
        goalConfig = GoalConfig()
    }

    fun reloadPositions() {
        positionConfig = PositionConfig()
    }
}
