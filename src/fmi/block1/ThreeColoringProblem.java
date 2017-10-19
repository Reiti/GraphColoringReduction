package fmi.block1;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

/**
 * Defines the Reduction steps for a 3-Coloring problem on directed graphs
 * 
 */
public interface ThreeColoringProblem {
	/**
	 * Takes a graph and generates a (representation of a) propositional formula that is satisfiable iff the graph has a valid coloring.  
	 * 
	 * @param graph
	 * @return cnf formula
	 */
	IVec<IVecInt> buildCNF(DirectedGraph<Integer, DefaultEdge> graph);
	
	/**
	 * Takes a model of the formula generated via buildCNF method and an empty coloring. 
	 * Updates the coloring to an valid coloring of the graph used to generate the formula.
	 * 
	 * @param coloring
	 * @param model
	 */
	void getColoringFromModel(ThreeColoring<Integer> coloring, int[] model);

}
