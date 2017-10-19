package fmi.block1;


import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;

/**
 * A three coloring of a set of vertices
 *
 * @param <V> Type of the Vertices - must coincide with the type of the vertices in the graph
 */
public interface ThreeColoring<V> {

	public enum Color {
	    RED, GREEN, BLUE
	}
	
	/**
	 * Sets the color of a specific vertex
	 * 
	 * @param vertex
	 * @param color
	 */
	public void setColor(V vertex, Color color);
	
	/**
	 * Returns the color of a specific vertex
	 * 
	 * @param vertex
	 */
	public Color getColor(V vertex);
	
	/**
	 * 
	 * @return Set of all colored Vertices
	 */
	public Set<V> getDomain();
	
	/**
	 * Tests whether a vertex is already colored
	 * @param vertex
	 * @return true if the vertex is already colored
	 */
	public boolean isColored(V vertex);
	
	/**
	 * Tests whether the coloring is valid for a given graph
	 * @param graph
	 * @return true if the coloring is valid, false otherwise
	 */
	public boolean isValidColoringOf(Graph<V,DefaultEdge> graph);
}
