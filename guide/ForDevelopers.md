# For Developers

### Quick Access

- [Prerequirities](#prerequirities)
- [Overview](#overview)
- [Translations](#translations)
- [Creating new Challenges](#creating-new-challenges)
- [Creating new GameChanges](#creating-new-gamechanges)
- [Goals](#creating-new-goals)
- [Using the Timer](#using-the-timer)
- [Useful Extensions](#useful-extensions)
- [Using the Config](#using-the-config)

## Prerequirities

> We recommend using [IntelliJ](https://www.jetbrains.com/idea/) as IDE

You'll need to install the Kotlin plugin and use a java 17 jdk as project and gradle sdk.

## Overview

> ℹ️ all packages referenced are relative to the root package (de.stckoverflw.stckutils)

Challenges, GameChanges and Goals are located in
the [minecraft](https://github.com/StckOverflwNet/StckUtils/tree/master/src/main/kotlin/de/stckoverflw/stckutils/minecraft)
package.

## Translations

We use ResourceBundles (.properties) to provide multilanguage support.
Those are stored in `src/main/resources/translations`.
You'll need to add new translations for challenges, gamgechanges and goals you create.
Creating one is pretty easy, just create one in the `minecraft` folder under challenge, gamechange or goal,
the .properties should be named like the id of the challenge, gamechange or goal:

> challenge-id.description
> challenge-id.name

example-challenge.properties:
```properties
challenge-id.description=\n\
This is the lore of the challenge's item in the challenges gui.
challenge-id.name=This is the name of the challenge's item in the challenges gui.
```
You can of course add more translations to this, make sure that the key must start with the corresponding id:
`challenge-id.key=value`

You don't have to worry about registering those translations btw!

We use TranslatableComponents to use those translations at runtime.
You can create one for (i.e. the challenge) name like this:
```kotlin
import net.kyori.adventure.text.Component.translatable

translatable("challenge-id.name")
```
You can send this to a Player.
If you plan to use translations in ItemStacks you have to render them for a locale, like this:
```kotlin
import net.kyori.adventure.text.Component.translatable

translatable("challenge-id.name").render(Locale)
```
You can get a locale from a Player with `Player#locale()`

If you need a translation as String (idk why you would need this tbh):
`translatable("challenge-id.name").render(Locale).plainText()`

## Creating new Challenges

Creating a new Challenge is pretty easy:

Just create a new kotlin object and let it extend Challenge (preferably place in the minecraft/challenge/impl package):

```kotlin
object ChallengeName : Challenge() {

}
```

Then you'll need to implement the members of Challenge:

> ```kotlin
> override val id: String
> ```
> The id must be unique. It represents the challenge in (data) configs.

> ```kotlin
> override val material: Material
> ```
> The material is used as the Challenge's item material in the settings gui.

> ```kotlin
> override val usesEvents: Boolean
> ```
> Specifies if this Challenge should be registered as Listener.

> ```kotlin
> override fun configurationGUI(): GUI<ForInventoryFiveByNine>?
> ```
> This is the gui to configure additional settings for your challenge.
> Set this to null if you don't need it.
> It's accessible through the settings gui.
> When your challenge is activated the user can right click onto the Challenge's item to open it.

You'd also need to add translations for the name and description.
Just create a new ResourceBundle at `/resources/translations/minecraft/challenges` with the challenge id as name:

> challenge-id.description=\n\
> Your description.
> challenge-id.name=ChallengeName

so an Example would look like this:

```kotlin
package de.stckoverflw.stckutils.minecraft.challenge.impl

object ExampleChallenge : Challenge() {
    override val id: String = "example-challenge"
    override val material: Material = Material.STONE
    override val usesEvents: Boolean = false

    override fun configurationGUI(): GUI<ForInventoryFiveByNine>? = null
}
```
example-challenge.properties:
```properties
example-challenge.description=\n\
This is just an example challenge.
example-challenge.name=ExampleChallenge
```

now you can further add some logic to it, like an EventHandler (make sure to set `usesEvents` to `true`):

```kotlin
// [...]
override val usesEvents: Boolean = true

// [...]
@EventHandler
fun onJoin(event: PlayerJoinEvent) {
    // do something
}
```

There are also some more useful things you might want to use:

> ````kotlin
> active
> ````
> This determines whether a Challenge is activated or not.

> ```kotlin
> lose()
> ```
> Call this to end and lose the challenge.

> ```kotlin
> override fun prepareChallenge()
> ```
> This is called before the Timer starts to prepare the Challenge.

> ```kotlin
> override fun update()
> ```
> This is called synchronously with the Timer (every 20 ticks (~ 1 second if the server is running stable)).

> ```kotlin
> override fun onTimerToggle()
> ```
> This is called synchronously when the Timer is toggled (start/stop, not reset).

> ```kotlin
> override fun onToggle()
> ```
> This is called synchronously when the Challenge state is changed (just by clicking the item in the settings gui).

## Creating new GameChanges

There are two types of GameChanges:

1. GameExtensions

> GameExtensions add additional functionalities to the game

2. GameRules

> GameRules change already existing features in the game

both work pretty similar to the Challenges, just create an object that extends either GameExtension or GameRule:

```kotlin
object GameExtensionName : GameExtension() {

}
```

or

```kotlin
object GameRuleName : GameRule() {

}
```

Then you'll need to implement the members of GameChange:

> ```kotlin
> override val id: String
> ```
> The id must be unique. It represents the gamechange in (data) configs.

> ```kotlin
> override fun item(locale: Locale): ItemStack
> ```
> This is the item that will appear in the settings gui.

> ```kotlin
> override val usesEvents: Boolean
> ```
> Specifies if this GameChange should be registered as Listener.

> ```kotlin
> override fun click(event: GUIClickEvent<ForInventoryFiveByNine>)
> ```
> This function is called when the GameChange's item in the settings gui is clicked.

> ```kotlin
> override fun run()
> ```
> This function is run everytime someone joins or quits the server and when the timer starts.

So an Example would look like this (similar for GameRule):

```kotlin
package de.stckoverflw.stckutils.minecraft.challenge.impl

object ExampleGameRule : GameExtension() {
    override val id: String = "example-game-extension"
    override val material: Material = Material.STONE
    override val usesEvents: Boolean = false

    override fun configurationGUI(locale: Locale): GUI<ForInventoryFiveByNine>? = null
}
```
example-game-extension.properties:
```properties
example-game-extension.description=\n\
This is just an example game extension.
example-game-extension.name=ExampleGameExtension
```

There are also some more useful things you might want to use:

> ```kotlin
> active
> ```
> This determines whether a GameChange is activated or not.

> ```kotlin
>  override fun onTimerToggle()
> ```
> This is called synchronously when the Timer is toggled (start/stop, not reset).

## Creating new Goals

There are two types of Goals:

1. TeamGoals

> Play cooperative towards one goal. Once one player reaches the goal everyone wins.

2. Battles

> Play against each other towards one goal. The first player to reach the goal wins.

both work pretty similar to the Challenges and GameChanges, just create an object that extends either TeamGoal or
Battle:

```kotlin
object TeamGoalName : TeamGoal() {

}
```

or

```kotlin
object BattleName : Battle() {

}
```

> ```kotlin
> override val id: String
> ```
> The id must be unique. It represents the Goal in (data) configs.

> ```kotlin
> override val material: Material
> ```
> The material is used as the Goal's item material in the settings gui.

So an example would looke like this (similar for Battle):

```kotlin
package de.stckoverflw.stckutils.minecraft.goal.impl

object ExampleGoal : TeamGoal() {
    override val id: String = "example-goal"
    override val material: Material = Material.STONE
}
```
example-goal.properties:
```properties
example-goal.description=\n\
This is just an example goal.
example-goal.name=ExampleGoal
```

There are also some more useful things you might want to use:

> ````kotlin
> active
> ````
> This determines whether a Goal is activated or not.

> ```kotlin
> win()
> ```
> Call this to end and win the Game.

> ```kotlin
> override fun onTimerToggle()
> ```
> This is called synchronously when the Timer is toggled (start/stop, not reset).

> ```kotlin
> override fun onToggle()
> ```
> This is called synchronously when the Goal state is changed (just by clicking the item in the settings gui).

## Using the Timer

You can use the Timer via static reference because it's an object and instantiated on startup:

```kotlin
Timer.time: Long
```

This is the time in seconds that the Timer currently is set to.

```kotlin
Timer.backwardsStartTime: Long
```

This holds the start time when using the Direction BACKWARDS.

```kotlin
Timer.running: Boolean
```

You won't really need this one I guess but you could to get whether the Timer is running or not.

```kotlin
Timer.additionalInfo: ArrayList<String>
```

If not empty this will be displayed behind the time in the actionbar when the Timer is running.

```kotlin
Timer.color: TextColor
```

This is the Color the Timer is displayed in.

```kotlin
Timer.joinWhileRunning: AccessLevel
```

This is the AccessLevel needed to join the server while the Timer is running.

```kotlin
Timer.direction: TimerDirection
```

This is the Direction the Timer is running to.

```kotlin
Timer.formatTime(seconds): Component
```

This formats a Long (defaults to Timer.time) to a pretty String in the current Color (like `3d 4h 50m 10s` or `51 seconds`).

```kotlin
Timer.start()
Timer.stop()
Timer.reset()
```

Those should be self explaining.

## Useful Extensions

- Coming soon -

For now just have a look [here](../../../tree/main/src/main/kotlin/de/stckoverflw/stckutils/extension)

## Using the Config

When using the Config you can use the Config class, it's an object and can be used via static reference:

The Config class contains some configs. Use data configs to store data and normal configs to store settings. This is
crucial for being able to reset only data or only settings or to ensure that users can easily create a backup.

The Configs are basically YamlConfigs so you could use all those methods theoretically although we recommend using the
provided methods for setting and getting something from the config.
