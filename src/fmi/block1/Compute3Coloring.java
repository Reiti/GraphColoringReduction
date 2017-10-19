package fmi.block1;


import java.io.*;
import java.util.Map;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.*;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;
import org.sat4j.specs.TimeoutException;



/**
 * Main Class instantiating and running the SAT solver
 *
 */
public class Compute3Coloring {

	private static final String PROBLEMA3COLORING="type1";
	private static final String PROBLEMB3COLORING="type2";
	
	/**
	 * Has two arguments: the first argument is the path to the DIMACS file; the second argument specifies the type of coloring.
	 * 
	 * @param args 
	 * @throws ImportException
	 * @throws IOException
	 * @throws TimeoutException
	 */
	public static void main(String[] args) throws ImportException, IOException, TimeoutException {
	
		// Deal with the command line parameters
		if(args.length<2)
		{
			System.out.println("USAGE: Compute3Coloring path problem");
		} else
		{
		  String path=args[0];
		  String problem=args[1];
		  ThreeColoringProblem reduction;
		  
		  switch (problem) {
          case PROBLEMA3COLORING:
        	  	reduction=new Type1ThreeColoringProblem();
               	break;
          case PROBLEMB3COLORING: 
        	  	reduction=new Type2ThreeColoringProblem();
        	  	break;     	  	
          default: System.out.println("Second parameter must be one of the Following: " + PROBLEMA3COLORING+ ", " + PROBLEMB3COLORING);
                   return;	  
		  }

		  // Read graph from File
		  DirectedGraph<Integer,DefaultEdge> graph = readGraphFromFile(path);

		 //export graph TODO: remove this
			DOTExporter exporter = new DOTExporter();
		  	String targetdir = "graph";
			new File(targetdir).mkdirs();
			exporter.exportGraph(graph, new FileWriter(targetdir + "result.dot"));
		//----------------------------
			// Compute the CNF formula		
			IVec<IVecInt> cnf = reduction.buildCNF(graph);
			
			//  Initialize the SAT solver
			ISolver solver = SolverFactory.newDefault();
			
			try {
				// Feeds the formula to the solver
				solver.addAllClauses(cnf);		
				
				IProblem coloringInstance = solver;

				solver.setTimeout(3600);

				// Computes a model / a coloring  if possible
				if (coloringInstance.isSatisfiable()) {
						int model[] = coloringInstance.model();				
						// Initialize empty Coloring 
						ThreeColoring<Integer> coloring = new BasicThreeColoring<Integer>(graph);
						// Compute a Coloring from the model
						reduction.getColoringFromModel(coloring, model);
						// Check whether the computed coloring is valid
						System.out.println("Coloring: " + coloring);
						if(problem.equals(PROBLEMA3COLORING)) {
							if (verifyType1(graph, coloring)) {
								System.out.println("Coloring valid!");
							} else {
								System.out.println("Coloring invalid!");
							}
						} else {
							if (verifyType2(graph, coloring)) {
								System.out.println("Coloring valid!");
							} else {
								System.out.println("Coloring invalid!");
							}
						}
					}		
				else {
					System.out.println("There is no valid coloring.");
				}			
			} catch (ContradictionException e) {
				System.out.println("There is no valid coloring.");
			}	
		}
	}
		
	/**
	 * Reads a directed graph in DIMACS format from file
	 * 
	 * @param path Path to the DIMACS file
	 * @return DirectedGraph object corresponding to graph in the DIMACS file
	 * @throws ImportException
	 * @throws IOException
	 */
	private static DirectedGraph<Integer,DefaultEdge> readGraphFromFile(String path) throws ImportException, IOException {
		VertexProvider<Integer> vertexProvider = new IntVertexProvider();
		EdgeProvider<Integer,DefaultEdge> edgeProvider = new IntEdgeProvider();
				
		DIMACSImporter<Integer,DefaultEdge> importer = new DIMACSImporter<Integer,DefaultEdge>(vertexProvider, edgeProvider);
		DirectedGraph<Integer,DefaultEdge> importDiGraph = new DefaultDirectedGraph<Integer,DefaultEdge>(DefaultEdge.class);
		Reader reader;
		reader = new FileReader(path);
		importer.importGraph(importDiGraph, reader);
		reader.close();
		
        return importDiGraph;		
	}
	
	private static class  IntVertexProvider implements VertexProvider<Integer>{
		public Integer buildVertex(String arg0, Map<String, String> arg1) {			
			return Integer.valueOf(arg0);
		}		
	}
	
	private static class IntEdgeProvider implements EdgeProvider<Integer,DefaultEdge>{
		public DefaultEdge buildEdge(Integer arg0, Integer arg1, String arg2, Map<String, String> arg3) {
			return new DefaultEdge(); 
		}
	}

	private static boolean verifyType1(DirectedGraph<Integer, DefaultEdge> g, ThreeColoring<Integer> coloring) {
		for(Integer v: g.vertexSet()) {
			if(coloring.getColor(v) == ThreeColoring.Color.BLUE) {
				for (DefaultEdge e : g.outgoingEdgesOf(v)) {
					if(coloring.getColor(g.getEdgeTarget(e)) == ThreeColoring.Color.BLUE) {
						return false;
					}
				}
				for (DefaultEdge e : g.incomingEdgesOf(v)) {
					if(coloring.getColor(g.getEdgeSource(e)) == ThreeColoring.Color.BLUE) {
						return false;
					}
				}
			}
			else if(coloring.getColor(v) == ThreeColoring.Color.GREEN || coloring.getColor(v) == ThreeColoring.Color.RED) {
				boolean blue = false;
				for(DefaultEdge e: g.outgoingEdgesOf(v)) {
					if(coloring.getColor(g.getEdgeTarget(e)) == ThreeColoring.Color.BLUE) {
						blue = true;
					}
				}
				if(!blue) {
					return false;
				}
			}
		}
		return true;
	}

	private static boolean verifyType2(DirectedGraph<Integer, DefaultEdge> g, ThreeColoring<Integer> coloring) {
		boolean at_least_one_blue = false;
		for(Integer v: g.vertexSet()) {
			if(coloring.getColor(v) == ThreeColoring.Color.BLUE) {
				//no two blue neighbors
				for (DefaultEdge e : g.outgoingEdgesOf(v)) {
					if(coloring.getColor(g.getEdgeTarget(e)) == ThreeColoring.Color.BLUE) {
						System.out.println("blue adj 1");
						return false;
					}
				}
				for (DefaultEdge e : g.incomingEdgesOf(v)) {
					if(coloring.getColor(g.getEdgeSource(e)) == ThreeColoring.Color.BLUE) {
						System.out.println("blue adj");
						return false;
					}
				}
				//---------------------

				//all neighbors of blue are red
				for (DefaultEdge e : g.outgoingEdgesOf(v)) {
					if(coloring.getColor(g.getEdgeTarget(e)) != ThreeColoring.Color.RED) {
						System.out.println("all red 1");
						return false;
					}
				}
				for (DefaultEdge e : g.incomingEdgesOf(v)) {
					if(coloring.getColor(g.getEdgeSource(e)) != ThreeColoring.Color.RED) {
						System.out.println("all red");
						return false;
					}
				}
				//---------------------
			}
			else if(coloring.getColor(v) == ThreeColoring.Color.RED) {
				//Red has at least one blue successor
				boolean blue = false;
				for(DefaultEdge e: g.outgoingEdgesOf(v)) {
					if (coloring.getColor(g.getEdgeTarget(e)) == ThreeColoring.Color.BLUE) {
						blue = true;
					}
				}
				if(!blue) {
					return false;
				}
			}
			//at least one blue
			if(coloring.getColor(v) == ThreeColoring.Color.BLUE) {
				at_least_one_blue = true;
			}
		}

		return at_least_one_blue;
	}


}
