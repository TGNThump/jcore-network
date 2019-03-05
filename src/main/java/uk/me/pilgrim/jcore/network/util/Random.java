/**
 * This file is part of network.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package uk.me.pilgrim.jcore.network.util;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author Benjamin Pilgrim &lt;ben@pilgrim.me.uk&gt;
 */
public class Random {
	private static Queue<Double> preload = new LinkedList<Double>();
	private static java.util.Random random = new java.util.Random();
	
	public static void preload(Double... values) {
		for (Double value : values) preload.add(value);
	}
	
	public static Double getDouble() {
		if (preload.size() > 0) return preload.poll();
		else return random.nextDouble();
	}
	
	/**
	 * @param max exclusive
	 * @return returns a value from 0 (inclusive) to max (exclusive)
	 */
	public static int getInt(int max) {
		double slice = 1d/(double)max;
		double random = getDouble();
		
		int i;
		for (i = 0; random > slice; random-=slice) {i++;}
		return i;
	}
}
