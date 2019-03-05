/**
 * This file is part of network.
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package uk.me.pilgrim.jcore.network.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Supplier;

import uk.me.pilgrim.jcore.network.Edge;
import uk.me.pilgrim.jcore.network.Node;
import uk.me.pilgrim.jcore.network.util.Pair;
import uk.me.pilgrim.jcore.network.util.TriFunction;

/**
 * @author Benjamin Pilgrim &lt;ben@pilgrim.me.uk&gt;
 */
@SuppressWarnings("hiding")
public class Traverse {
	
	private static final boolean debug = false;
	
	private static void debug(String text) {
		if (!debug) return;
		System.out.println(text);
	}
	
	public static List<Edge> BreadthFirstSearch(Node source, Node target){
		return Traverse.BreadthFirstSearch(
				(depth, node) -> node.out,
				(depth, edge, from) -> edge.follow(from),
				(depth, node) -> node.equals(target),
				source
			);
	}

	public static List<Edge> DepthFirstSearch(Node source, Node target){
		return Traverse.DepthFirstSearch(
				(depth, node) -> node.out,
				(depth, edge, from) -> edge.follow(from),
				(depth, node) -> node.equals(target),
				source
			);
	}
	
	public static <Node, Edge> List<Edge> BreadthFirstSearch(
		BiFunction<Integer, Node, List<Edge>> edges,
		TriFunction<Integer, Edge, Node, Node> child,
		BiPredicate<Integer, Node> pred,
		Node root
	){
		Queue<Pair<Integer, Node>> queue = new LinkedList<Pair<Integer, Node>>();
		
		return Search(
			queue::add, queue::poll, queue::size,
			edges, child, pred, root
		);
	}
	
	public static <Node, Edge> List<Edge> DepthFirstSearch(
		BiFunction<Integer, Node, List<Edge>> edges,
		TriFunction<Integer, Edge, Node, Node> child,
		BiPredicate<Integer, Node> pred,
		Node root
	){
		Stack<Pair<Integer, Node>> stack = new Stack<Pair<Integer, Node>>();
		
		return Search(
			stack::push, stack::pop, stack::size,
			edges, child, pred, root
		);
	}
	
//	private static <Node, Edge> List<Edge> Search(
//		Consumer<Pair<Integer, Node>> insert,
//		Supplier<Pair<Integer, Node>> remove,
//		Supplier<Integer> size,
//		
//		BiFunction<Integer, Node, List<Edge>> edges,
//		TriFunction<Integer, Edge, Node, Node> child,
//		BiPredicate<Integer, Node> pred,
//		Node root
//	){
//		debug("Search(" + root + "): ");
//		Map<Node, Pair<Node, Edge>> parent = new HashMap<Node, Pair<Node, Edge>>();
//		Set<Node> seen = new HashSet<Node>();
//		
//		insert.accept(new Pair<Integer, Node>(0, root));
//		seen.add(root);
//		
//		while (size.get() > 0) {
//			Pair<Integer, Node> v = remove.get();
//			debug("  " + v.getValue() + ", (" + v.getKey() + ") {");
//			List<Edge> us = edges.apply(v.getKey(), v.getValue());
//			for(Edge u : us){
//				debug("    " + u);
//				Node w = child.apply(v.getKey(), u, v.getValue());
//				
//				if (!seen.contains(w)) {
//					parent.put(w, new Pair<Node, Edge>(v.getValue(), u));
//					
//					if (pred.test(v.getKey() + 1, w)) {
//						List<Edge> path = new ArrayList<Edge>();
//						
//						Pair<Node, Edge> p = parent.get(w);
//				
//						while(true){
//							path.add(p.getValue());
//							if (!parent.containsKey(p.getKey())) break;
//							p = parent.get(p.getKey());
//						}
//						
//						Collections.reverse(path);
//						debug("    SUCCESS");
//						debug("  }");
//						
//						return path;
//					}
//					
//					seen.add(w);
//					insert.accept(new Pair<Integer, Node>(v.getKey() + 1, w));
//				}
//			}
//			debug("  }");
//		}
//		
//		debug("  FAIL");
//		return new ArrayList<Edge>();
//	}
	
	private static <Node, Edge> List<Edge> Search(
			Consumer<Pair<Integer, Node>> insert,
			Supplier<Pair<Integer, Node>> remove,
			Supplier<Integer> size,
			
			BiFunction<Integer, Node, List<Edge>> edges,
			TriFunction<Integer, Edge, Node, Node> child,
			BiPredicate<Integer, Node> pred,
			Node root
		){
			debug("Search(" + root + "): ");
			Map<Node, Pair<Node, Edge>> parent = new HashMap<Node, Pair<Node, Edge>>();
			Set<Node> seen = new HashSet<Node>();
			
			insert.accept(new Pair<Integer, Node>(0, root));
			
			while (size.get() > 0) {
				Pair<Integer, Node> v = remove.get();
				if (!seen.contains(v.getValue())) {

					debug("  " + v.getValue() + ", (" + v.getKey() + ") {");
					List<Edge> us = edges.apply(v.getKey(), v.getValue());
					for(Edge u : us){
						debug("    " + u);
						Node w = child.apply(v.getKey(), u, v.getValue());
						
							parent.put(w, new Pair<Node, Edge>(v.getValue(), u));
							
							if (pred.test(v.getKey() + 1, w)) {
								List<Edge> path = new ArrayList<Edge>();
								
								Pair<Node, Edge> p = parent.get(w);
						
								while(true){
									path.add(p.getValue());
									if (!parent.containsKey(p.getKey())) break;
									p = parent.get(p.getKey());
								}
								
								Collections.reverse(path);
								debug("    SUCCESS");
								debug("  }");
								
								return path;
							}
							
							
							insert.accept(new Pair<Integer, Node>(v.getKey() + 1, w));
						
					}
					debug("  }");
					seen.add(v.getValue());
				}
			}
			
			debug("  FAIL");
			return new ArrayList<Edge>();
		}
}
