# Space Invaders 102

## Description

Space Invaders 102 is a Java2D tutorial game, written and placed in the public domain by [Kevin Glass]. It is a successor to the Space Invaders 101 tutorial that aims to cover issues in high-resolution timing in Java games, as well as some basic sprite animation.

See the [tutorial] for a nuts-and-bolts explanation of how the game was designed and implemented.

[Kevin Glass]:http://www.cokeandcode.com/
[tutorial]:https://github.com/marcliberatore/spaceinvaders-102-java/blob/master/TUTORIAL.md

### Timers

Unfortunately, the original version depends upon a now-unavailable timing library (the "GAGE Timer") under Windows. I've updated the code to use `System.nanoTime()` instead, as that's now widely available. I've lightly edited the tutorial to reflect this change.

As mentioned in the original tutorial, the various timers have different properties across different host platforms of the JVM. Later tutorials in this series use [LWJGL], which includes a pair of calls, `Sys.getTime()` and `Sys.getTimerResolution()` to provide better, more consistent cross-platform timer behavior.

[LWJGL]:http://www.lwjgl.org/

## Build Instructions

You'll need Apache Maven 3.0+ and a recent JDK. Clone the repository, and use:

```bash
cd spaceinvaders-102-java
mvn package
```

to create an executable jar in `target`. 

## Running the Game

You can execute the jar by double-clicking it in your GUI, or using

```bash
java -jar spaceinvaders-java-102-1.0.0-SNAPSHOT.jar
```

after navigating to the `target` directory.

## License

All Java code placed in the public domain by Kevin Glass.

Sprites taken from [SpriteLib], licensed under the Common Public License 1.0.

[SpriteLib]:http://www.widgetworx.com/widgetworx/portfolio/spritelib.html
