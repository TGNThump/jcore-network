package uk.me.pilgrim.jcore.network;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	Graph graph = new Graph(false);

    	graph.createNodes(8);

    	graph.addEdges(1, 2);
    	graph.addEdges(2, 3, 5, 6);
    	graph.addEdges(3, 7, 4);
    	graph.addEdges(4, 3, 8);
    	graph.addEdges(5, 1, 6);
    	graph.addEdges(6, 7);
    	graph.addEdges(7, 6);
    	graph.addEdges(8, 4, 7);
    	
    	graph.sortAll();
    	
    	System.out.println(graph.getAssortativity());
    	
    	
    }
}
