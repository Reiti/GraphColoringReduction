package fmi.block1;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

/**
 * A three coloring of a set of vertices. A coloring is considered to be valid for a graph if all vertices are colored.
 *
 * @param <V> Type of the Vertices - must coincide with the type of the vertices in the graph
 */
public class BasicThreeColoring<V> implements ThreeColoring<V> {

	private Map<V,Color> coloring;
	
	public BasicThreeColoring () {
		coloring= new HashMap<V,Color>();		
	}
	
	public BasicThreeColoring (int numberOfVertices) {
		coloring= new HashMap<V,Color>(numberOfVertices);		
	}
	
	public BasicThreeColoring (Collection<V> vertexSet) {
		this(vertexSet.size());		
	}
	
	public BasicThreeColoring (Graph<V,DefaultEdge> graph) {
		this(graph.vertexSet());
	}

	@Override
	public void setColor(V vertex, Color color) {
		coloring.put(vertex, color);		
	}

	@Override
	public Color getColor(V vertex) {
		return coloring.get(vertex);
	}
	
	@Override
	public Set<V> getDomain() {
		return coloring.keySet();
	}

	@Override
	public boolean isColored(V vertex) {
		return coloring.containsKey(vertex);
	}

	/**
	 * Test whether all vertices of the graph are colored
	 */
	public boolean isValidColoringOf(Graph<V, DefaultEdge> graph) {
		for(V vertex : graph.vertexSet()){
			if(!isColored(vertex)){
				System.out.println("Vertex not colored: " + vertex);
				return false;
			}			
		}	
		return true;
	}

	@Override
	public String toString() {
		return coloring.toString();
	}

}
