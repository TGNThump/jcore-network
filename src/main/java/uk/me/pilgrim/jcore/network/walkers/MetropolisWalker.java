/**
 * This file is part of network.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package uk.me.pilgrim.jcore.network.walkers;

import java.util.function.Function;

import uk.me.pilgrim.jcore.network.Node;
import uk.me.pilgrim.jcore.network.util.Random;

/**
 * @author Benjamin Pilgrim &lt;ben@pilgrim.me.uk&gt;
 */
public class MetropolisWalker implements Function<Node, Node> {

	@Override
	public Node apply(Node current) {
		Node next = current.randomNeighbour();
		
		double probability = Math.min(1d, (double)current.degree() / (double)next.degree());
				
		if (probability < 1d) {
			if (Random.getDouble() < probability) return next;
			return current;
		} else return next;
		
	}
	
}
