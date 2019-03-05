/**
 * This file is part of network.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package uk.me.pilgrim.jcore.network.util;

import java.util.AbstractMap;

/**
 * @author Benjamin Pilgrim &lt;ben@pilgrim.me.uk&gt;
 */
public class Pair<K, V> extends AbstractMap.SimpleEntry<K, V> {

	private static final long serialVersionUID = -1070573552096074805L;

	public Pair(K key, V value) {
		super(key, value);
	}
	
}
