# Space Invaders 102: Timing and Animation in Java

Adapted from: <http://www.cokeandcode.com/info/tut2d-2.html>

Originally written by [Kevin Glass].

Markdownified and lightly edited by [Marc Liberatore].

[Kevin Glass]:http://www.cokeandcode.com/
[Marc Liberatore]:http://people.cs.umass.edu/~liberato/

# Introduction

This tutorial follows on from [Space Invaders 101]. The tutorial will cover high resolution timers and the issues that raises in Java. In addition to timing the tutorial will adapt the previously developed source to add some basic animation. 

[Space Invaders 101]:https://github.com/marcliberatore/spaceinvaders-101-java

The [complete source] for the tutorial is on GitHub. It's intended that you read through this tutorial with the source code at your side. The tutorial isn't going to cover every line of code but should give you enough to fully understand how it works. 

[complete source]:https://github.com/marcliberatore/spaceinvaders-102-java

_Disclaimer: This tutorial is provided as is. I don't guarantee that the provided source is perfect or that that it provides best practices._

# Timing

What exactly do we mean by timing? Timing is used in many places in games. For many games timing is used to limit the rate at which the screen is updated, the frames per second. Most games use some sort of movement and animation which is normally based on timing. 

Currently, Java has some issues with timing. In Java 1.4.2 the simplest way to get hold of the time is to use System.currentTimeMillis(). Infact it was used to time the movement in the first tutorial. However, on some Windows based machines the resolution of the timer (the smallest value you can time) isn't very useful. On most systems (Linux, SunOS, etc) the resolution is at least 1 millisecond. However, on some Windows sytems the resolution is bad enough (15ms or so) to mean that timing cannot be relied on to give a consistant result, which in turn can lead to stuttering in updates. Let's look at some possible solutions. 

## Averaging the Time

One way to deal with the inconsistency of timing on Windows is to average the change in time between frames. Historically this was a very common way to deal with the problem. Using this method the change in time between frames is recorded across the last 5 (or more) frames. Instead of using the actual change in time, the average of the last few frames is taken and used instead. 

So the algorithm looks something like this: 

* Each game loop record the render time between frames. 
* Average the last 5 frame times 
* Use the average to move and update game elements 

While this doesn't give perfect results, it does give "good" results. It's not required on anything other than Windows. So if you do choose to implement this method it's nice to add a check on the system property "os.name". 

## Wait for Java 1.5

**A note from Marc:** This discussion that follows is no longer relevant, as Java 1.5 (and later) are now in wide release. (To be fair, the tutorial was written about ten years ago!) You should almost certainly use `System.nanoTime()`, and possibly the averaging technique described above, unless you're using a library which handles this for you. [LWJGL]'s `Sys` class provides static methods that work well across platforms. You should use it if you're using LWJGL.

[LWJGL]:http://www.lwjgl.org/

**And now back to Kevin.**

The timing issue with Java has been well known for a while. Finally, in Java 1.5 its going to be fixed. However, Java 1.5 is currently in beta (in testing) and its not available for commerical use right now. Sun rarely give fixed dates for releases of Java so no one is quite sure when 1.5 will be available. Most Java releases have also taken a while to really be ready for use. In addition, its fair to say that games players are only going to upgrade their Java when absolutely required, so most won't have 1.5 for quite a while. That being said, using 1.5 is by far the most simple way to get a high resolution timer. 

Using the Java 1.5 timer couldn't be simpler: 

```java
long currentTime = System.nanoTime()
```

# Wrapping up the Timing

As you can see timing can be implemented in a different ways depending upon circumstances. With this in mind let's wrap up our use of the timer in a single class. This way if we choose to try out a different timing method then we only have to make our changes in one place. 

```java
public class SystemTimer {
	/**
	 * Get the high resolution time in milliseconds
	 * 
	 * @return The high resolution time in milliseconds
	 */
	public static long getTime() {
		return System.nanoTime() / 1000000L;
	}
	
	/**
	 * Sleep for a fixed number of milliseconds. 
	 * 
	 * @param duration The amount of time in milliseconds to sleep for
	 * @throws InterruptedException 
	 */
	public static void sleep(long duration) throws InterruptedException {
		Thread.sleep(java.lang.Math.max(0L,duration));
	}
}
```

This timer class is based on the use of `System.nanoTime()`. We need to support two main operations, getting the time and sleeping for a set period of time. Java's built-in classes supports both of these operations. The main job of this class is to map between the timer ticks provided from the timer (in nanoseconds) to milliseconds and back. 

# Updating the Game Loop

Next we need to modify our existing game loop to make use of the high resolution timer. First, lets change how we calculate our delta (change in time) value. Instead of calling System.currentTimeMillis() we'll use our new SystemTimer class. That'll look something like this: 

```java
// work out how long its been since the last update, this
// will be used to calculate how far the entities should
// move this loop
long delta = SystemTimer.getTime() - lastLoopTime;
lastLoopTime = SystemTimer.getTime();
```

Now, in the last tutorial since the timer wasn't designed to be perfect we didn't worry to much about a few lost milliseconds. This time we can rely on our timer to be millisecond accurate so we're going to try and strictly limit our frame time so we get exactly 100 frames per second (FPS). 

To do this we're going to want each cycle round the game loop to take exactly ten milliseconds. We know at what time the cycle started (lastLoopTime) and we know what time it is now, so with a small amount of maths we can sleep for the right amount of time like this: 

```java
// we want each frame to take 10 milliseconds, to do this
// we've recorded when we started the frame. We add 10 milliseconds
// to this and then factor in the current time to give 
// us our final value to wait for
SystemTimer.sleep(lastLoopTime+10-SystemTimer.getTime());
```

# Animating Them Aliens

One of the things directly dependant on accurate timing is any form of animation. Since we've now got accurate timing lets do some animation! The aliens coming down towards our player are little static for traditional space invaders. Lets make them dance around on the way down. 

Since we designed our source nicely last time our changes are limited to one class, **AlienEntity**. Instead of the entity maintaining just a single sprite we'll add a few sprites and flip between them over time, i.e., animation. Our first step is to add some addition variables to our AlienEntity: 

```java
/** The animation frames */
private Sprite[] frames = new Sprite[4];
/** The time since the last frame change took place */
private long lastFrameChange;
/** The frame duration in milliseconds, i.e. how long any given frame of animation lasts */
private long frameDuration = 250;
/** The current frame of animation being displayed */
private int frameNumber;
```

The _frames_ array is going to hold our frames of animation. _lastFrameChange_ is going to be a record of the last time we changed animation frame. _frameDuration_ will be the length of time that each frame will be displayed on the screen. Making this small will make the aliens dance more quickly. Finally, _frameNumber_ will be the index of the frame we are currently showing in a frames array. This will be incremented to cycle us through the animation. 

Next we're going to need to load up our sprites. We're going to modify the constructor to grab the frames. However, our standard Entity class will already load one sprite for us (the one it used to display). So we need to load two additional ones, like so: 

```java
public AlienEntity(Game game,int x,int y) {
	super("sprites/alien.gif",x,y);
	
	// setup the animatin frames
	frames[0] = sprite;
	frames[1] = SpriteStore.get().getSprite("sprites/alien2.gif");
	frames[2] = sprite;
	frames[3] = SpriteStore.get().getSprite("sprites/alien3.gif");
	
	this.game = game;
	dx = -moveSpeed;
}
```

We've asked the Entity class to load "sprites/alien.gif" for us. Then we need to go off and load up a couple of additional sprites. We put the frame loaded by Entity and our two additional frames in the array in the right place to play the animation. Note that we've modified the constructor slightly to remove the name of the sprite, so the **Game** class will need some minor modifications. 

The final step in getting our animation to play is to update the current sprite as time progresses. We already have a handy place in which we can perform this action. Our "move()" method already gets told when time passes, so we can update the animation there. 

```java
public void move(long delta) {
	// since the move tells us how much time has passed
	// by we can use it to drive the animation, however
	// its the not the prettiest solution
	lastFrameChange += delta;
	
	// if we need to change the frame, update the frame number
	// and flip over the sprite in use
	if (lastFrameChange > frameDuration) {
		// reset our frame change time counter
		lastFrameChange = 0;
		
		// update the frame
		frameNumber++;
		if (frameNumber >= frames.length) {
			frameNumber = 0;
		}
		
		sprite = frames[frameNumber];
	}

	...
}
```

So, as time passes our lastFrameChange counter will get updated. Once it's passed our frameDuration limit we reset it. In addition we move to the next frame number. Then we reset the current sprite by setting the "sprite" member in the Entity super class to the current frame of animation. Next time the entity is rendered a different sprite is drawn and the animation takes place! 

# Finishing Off

Hopefully this tutorial has been of some help. While I recommend using the GAGE timer (**which is no longer supported, hence use of System.nanoTime() -- Marc**) the other methods and other native libraries have been used by a great number of people and are probably just as good. It's worth looking around and choosing the method that suits your need and methods best. 

# Exercises for the Reader

The following exercises might help the reader in understanding timing and animation more fully in this context. 

## Add animation for the shots

This should be a matter of adding some functionality similar to that added to AlienEntity to ShotEntity. 

## Implement timing using a different timer

There are a few native timer implementations. It should be relatively simple to try a different one. This would help understanding how Java uses native libraries to provide extra functionality. 

## Implement time averaging.

One of the other methods for providing useful timing on all platforms is to average the change in time across frames. The algorithm for doing is discussed above. 

## Add an Animated Sprite

Another way to implement the animation would have been to add a "AnimatedSprite". The flip over of images could have been handled within the class. In this way any entity could have been animated by simply using an animated sprite. 

# Credits

Tutorial and Source written by [Kevin Glass]  
Game sprites provided by [Ari Feldman]  
A large number of people over at the [Java Gaming Forums]  

[Ari Feldman]: http://www.widgetworx.com/widgetworx/portfolio/spritelib.html
[Java Gaming Forums]: http://www.java-gaming.org/
