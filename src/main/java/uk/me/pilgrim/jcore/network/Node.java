/**
 * This file is part of network.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package uk.me.pilgrim.jcore.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.me.pilgrim.jcore.network.algorithms.Dijkstra;
import uk.me.pilgrim.jcore.network.util.MathUtils;
import uk.me.pilgrim.jcore.network.util.Random;

/**
 * @author Benjamin Pilgrim &lt;ben@pilgrim.me.uk&gt;
 */
public class Node {
	
	private final Graph graph;
	
	public Node(Graph graph) {
		this.graph = graph;
	}
	
	public static int count = 0;
	public int id = count++;
	
	public List<Edge> out = new ArrayList<Edge>();
	public List<Edge> in = new ArrayList<Edge>();
	
	public String toString() {
		return "" + (id + 1);
	}
	
	public Stream<Edge> edges(){
		return Stream.concat(out.stream(), in.stream()).distinct();
	}
	
	public long degree() {
		return edges().count();
	}
	
	public Set<Node> neighbours(){
		return out.stream().map(node -> node.follow(this)).collect(Collectors.toSet());
	}
	
	public double localClusteringCoefficient() {
		if (degree() < 2) return 0;
		
		Set<Node> neighbours = neighbours();
		
//		System.out.println("Neighbours: " + neighbours);
		
		Set<Edge> edgesBetweenNeighbours = neighbours.stream()
			.flatMap(node -> node.out.stream())
			.filter(edge -> neighbours.contains(edge.destination) && neighbours.contains(edge.origin))
			.distinct().collect(Collectors.toSet());
		
//		System.out.println("Edges Between Neighbours: " + edgesBetweenNeighbours);
		
		return (double) edgesBetweenNeighbours.size() / (double) MathUtils.binomialCoeff((int)degree(), 2);	
	}

	public Node randomNeighbour() {		
		return out.get(Random.getInt(out.size())).follow(this);
	}

	/**
	 * @return
	 */
	public double getClosenessCentrality() {
    	Dijkstra dijkstra = new Dijkstra(graph, this);
    	double sum = dijkstra.getDistances().values().stream().reduce(0.0, Double::sum);
    	return sum / graph.nodes.size()-1;
	}
	
	public double getCentrality() {
    	Dijkstra dijkstra = new Dijkstra(graph, this);
    	
    	dijkstra.getDistances().remove(this);
    	return dijkstra.getDistances().values().stream().mapToDouble(value -> {
    		if (Double.isInfinite(value)) return 0;
    		else return 1/value;
    	}).average().getAsDouble();
   	}
}
