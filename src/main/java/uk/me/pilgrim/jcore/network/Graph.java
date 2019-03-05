/**
 * This file is part of network.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package uk.me.pilgrim.jcore.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.me.pilgrim.jcore.network.algorithms.Traverse;
import uk.me.pilgrim.jcore.network.util.MathUtils;
import uk.me.pilgrim.jcore.network.util.Pair;

/**
 * @author Benjamin Pilgrim &lt;ben@pilgrim.me.uk&gt;
 */
public class Graph {
	
	public boolean isDirected = false;
	
	public List<Node> nodes = new ArrayList<Node>();
	public List<Edge> edges = new ArrayList<Edge>();
	
	public Graph(boolean isDirected) {
		this.isDirected = isDirected;
	}
	
	public int n() {
		return nodes.size();
	}
	
	public int m() {
		return edges.size();
	}
	
	public void createNodes(int count) {
		for(int i = 0; i < count; i++) {
			addNode();
		}
	}
	
	public Node get(int i) {
		return nodes.get(i-1);
	}
	
	public void addNode() {
		nodes.add(new Node(this));
	}

	public void addEdge(int origin, int destination) {
		addEdge(get(origin), get(destination));
	}
	
	public void addEdge(Node origin, Node destination) {
		if (origin.neighbours().contains(destination)) {
//			System.out.println("Skipping adding duplicate edge (" + origin + ", " + destination + ")");
			return;
		}
		
		edges.add(new Edge(origin, destination, isDirected));
	}
	
	public void addEdges(int origin, int... destinations) {
		for (int destination : destinations) addEdge(origin, destination);
	}

	public void print() {
		for(Node node : this.nodes) {
    		System.out.println(node + " " + node.neighbours());
    	}
	}
	
	public long sumDegrees() {
		return 2 * m();
	}
	
	public long averageDegree() {
		return (2 * m()) / n();
	}
	
	public long density() {
		return m() / MathUtils.binomialCoeff(n(), 2);
	}
	
	public void walk(Function<Node, Node> walker, Node node, int steps) {
		System.out.println("0: " + node);
		for (int i = 0; i < steps; i++) {
			node = walker.apply(node);
			System.out.println((i+1) + ": " + node);
		}
	}
	
	public double localClusteringCoefficient() {
		return nodes.stream().map(node -> node.localClusteringCoefficient()).mapToDouble(a -> a).average().getAsDouble();
	}

	public void maximalCliques() {
		bronKerbosch(new ArrayList<>(), new ArrayList<>(nodes), new ArrayList<>(), 0);
	}
	
	public void bronKerbosch(List<Node> r, List<Node> p, List<Node> x, int depth){
//		System.out.print(new String(new char[depth]).replace("\0", " "));
//		System.out.println("bronKerbosch(r" + r + ", p" + p + ", x" + x + ")");
		
		if (p.isEmpty() && x.isEmpty()) {
//			System.out.print(new String(new char[depth]).replace("\0", " "));
			System.out.println(r);
		} else {
			while (p.size() > 0) {
				Node u = p.remove(0);
				
				List<Node> r2 = new ArrayList<Node>(r);
				r2.add(u);
				List<Node> p2 = p.stream().filter(n -> u.neighbours().contains(n)).collect(Collectors.toList());
				List<Node> x2 = x.stream().filter(n -> u.neighbours().contains(n)).collect(Collectors.toList());
				
				bronKerbosch(r2, p2, x2, depth+1);
				
				x.add(u);
			}
		}
	}
	
	public List<Node> getNCore(int n) {
		List<Node> nodes = new ArrayList<>(this.nodes);
		
		for (int i = 0; i < nodes.size(); i++) {
			Node node = nodes.get(i);
			
			long degree = node.edges().filter(edge -> nodes.contains(edge.destination) && nodes.contains(edge.origin)).count();
			
//			System.out.println("Node " + node + ", degree: " + degree);

			if (degree < n) {
				nodes.remove(node);
//				System.out.println("Removed " + node);
				i = -1;
			}
		}
		
		System.out.println(nodes);
		return nodes;	
	}
	
	public boolean isBipartite() {
		int[] colors = new int[nodes.size()];
		
		for (int i=0;i < nodes.size(); i++) {
			if (colors[i] != 0) continue;
			Node currentNode = nodes.get(i);
			colors[i] = 1;
			
			Stack<Node> nodeStack = new Stack<Node>();
			nodeStack.push(currentNode);
			
			while (!nodeStack.isEmpty()) {
				Node u = nodeStack.pop();
				int uIndex = nodes.indexOf(u);
				
				for (Node neighbour : u.neighbours()) {
					int index = nodes.indexOf(neighbour);
					if (colors[index] == colors[uIndex]) {
						return false;
					} else if (colors[index] == 0) {
						nodeStack.push(neighbour);
						colors[index] = 3 - colors[uIndex];
					}
				}
			}
		}
		
	
		return true;
	}
	
	public Set<Set<Node>> getConnectedComponents(){
		Set<Set<Node>> components = new HashSet<Set<Node>>();
		Stream<Node> unvisited = nodes.stream().filter(node -> components.stream().flatMap(Collection::stream).noneMatch(n -> n.equals(node)));
		
		unvisited.forEach(root -> {
			Set<Node> connected = new HashSet<Node>();
        	
        	connected.add(root);
        	Traverse.DepthFirstSearch(
    			// returns a given node's edges.
    			(depth, node) -> node.out,
    			// returns the destination node of an edge given a source node.
    			(depth, edge, source) -> edge.follow(source),
    			// function called when visiting a given node. return false to continue the search.
    			(depth, node) -> {
    				connected.add(node);
    				return false;
    			},
    			root
    		);
        	
        	components.add(connected);
		});
		
		return components;
	}
	
	public void sortAll() {
		nodes.forEach(node -> {
			node.out.sort(Collections.reverseOrder(Comparator.comparing(edge -> ((Edge) edge).destination.id)));
			node.in.sort(Collections.reverseOrder(Comparator.comparing(edge -> ((Edge) edge).origin.id)));
		});
	}
	
	public Set<Set<Node>> getStronglyConnectedComponents() {
		List<Pair<Integer, Node>> visited = new ArrayList<>();
		
		Traverse.DepthFirstSearch(
			// returns a given node's edges.
			(depth, node) -> {
				visited.add(new Pair<Integer, Node>(depth, node));
				return node.out;
			},
			// returns the destination node of an edge given a source node.
			(depth, edge, source) -> edge.destination,
			// function called when visiting a given node. return false to continue the search.
			(depth, node) -> false,
			nodes.get(0)
		);
		
		Stack<Node> s = new Stack<>();
		for (int i = 0; i < visited.size() && i >= 0; i++) {
			if (i == visited.size()-1 || visited.get(i+1).getKey() <= visited.get(i).getKey()) {
				s.push(visited.remove(i).getValue());
				i-=2;
			}
		}
				
		//		System.out.println(s);

		Set<Set<Node>> scc = new HashSet<Set<Node>>();
		
		while (!s.isEmpty()) {
			Node v = s.pop();
			Set<Node> component = new HashSet<Node>();
			component.add(v);
			Traverse.DepthFirstSearch(
				// returns a given node's edges.
				(depth, node) -> node.in.stream()
					.filter(edge -> scc.stream().flatMap(Collection::stream).noneMatch(n -> n.equals(edge.origin)))
					.collect(Collectors.toList()),
				// returns the destination node of an edge given a source node.
				(depth, edge, source) -> edge.origin,
				// function called when visiting a given node. return false to continue the search.
				(depth, node) -> {
					
					component.add(node);
					s.remove(node);
					return false;
				},
				v
			);
			scc.add(component);
		}
		
		return scc;
	}

	public void reverseAllEdges() {
		edges.forEach(Edge::reverse);
	}
	
	public double getAssortativity() {
		Map<Long, List<Node>> classes = nodes.stream().collect(Collectors.groupingBy(Node::degree));
		
		int internalEdgesCount = 0;

		
		for (Entry<Long, List<Node>> entry : classes.entrySet()) {
			List<Node> c = entry.getValue();
			System.out.println("Degree: " + entry.getKey());
			System.out.println("  Nodes: " + c);
			List<Edge> internalEdges = c.stream().flatMap(node -> node.out.stream()).filter(edge -> c.contains(edge.destination) && c.contains(edge.origin)).collect(Collectors.toList());
			internalEdgesCount += internalEdges.size();
			System.out.println("  Internal edges: " + internalEdges);
		}
		
		double sumOfSquareOfSumOfDegrees = classes.entrySet().stream().map(entry -> entry.getKey() * entry.getValue().size()).mapToDouble(d->d*d).sum();
		
		double q = (2*m()) * 2 * internalEdgesCount - sumOfSquareOfSumOfDegrees;
		double qmax = (2*m())*(2*m())-sumOfSquareOfSumOfDegrees;
		
		return q/qmax;
	}
}
