package org.newdawn.spaceinvaders;

/**
 * A wrapper class that provides timing methods. This class
 * provides us with a central location where we can add
 * our current timing implementation. Initially, we're going to
 * rely on the GAGE timer. (@see http://java.dnsalias.com)
 * 
 * 2013-02-07: I've replaced the no-longer-available GAGE timer
 * with System.nanoTime(), since that is now widely available.
 * --- Marc Liberatore
 * 
 * @author Kevin Glass
 */
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