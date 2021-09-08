package de.stckoverflw.stckutils.config

object Config {

    lateinit var resetSettings: ResetSettings
    lateinit var challengeSettings: ChallengeSettings
    lateinit var gameChangeConfig: GameChangeConfig
    lateinit var positionConfig: PositionConfig

    operator fun invoke() {
        reloadConfig()
    }

    fun reloadConfig() {
        resetSettings = ResetSettings()
        challengeSettings = ChallengeSettings()
        gameChangeConfig = GameChangeConfig()
    }

    fun reloadPositions() {
        positionConfig = PositionConfig()
    }
}
