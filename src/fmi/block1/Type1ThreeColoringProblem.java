package fmi.block1;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.sat4j.core.Vec;
import org.sat4j.core.VecInt;
import org.sat4j.specs.IVec;
import org.sat4j.specs.IVecInt;

import fmi.block1.ThreeColoring.Color;
/**
 * 
 * A coloring is valid iff
 *	  (a) it only uses three colors (red, green, blue);
 *	  (b) no two adjacent vertices are colored both blue; and
 *	  (c) all vertices colored green or red have at least one successor colored blue.
 *
 */
public class Type1ThreeColoringProblem implements ThreeColoringProblem {

	public Integer r(Integer vertex) {
		return (vertex-1)*3 + 1;
	}

	public Integer g(Integer vertex) {
		return (vertex-1)*3 + 2;
	}

	public Integer b(Integer vertex) {
		return (vertex-1)*3 + 3;
	}

	@Override
	public IVec<IVecInt> buildCNF(DirectedGraph<Integer, DefaultEdge> graph) {
		IVec<IVecInt> phi1 = new Vec<IVecInt>();
		IVec<IVecInt> phi2 = new Vec<IVecInt>();
		IVec<IVecInt> phi3 = new Vec<IVecInt>();
		IVec<IVecInt> phi4 = new Vec<IVecInt>();

		IVec<IVecInt> phi = new Vec<IVecInt>();

		IVecInt temp = new VecInt();
		for(Integer vertex: graph.vertexSet()) {
			//either red, green or blue --------
			temp.push(r(vertex));
			temp.push(g(vertex));
			temp.push(b(vertex));
			phi1.push(temp);
			temp = new VecInt();
			//----------------------------------

			//only 1 color per vertex ----------
			temp.push(-r(vertex));
			temp.push(-g(vertex));
			phi2.push(temp);
			temp = new VecInt();

			temp.push(-g(vertex));
			temp.push(-b(vertex));
			phi2.push(temp);
			temp = new VecInt();

			temp.push(-b(vertex));
			temp.push(-r(vertex));
			phi2.push(temp);
			temp = new VecInt();
			//----------------------------------

			//no two blue neighbors -------------
			for(DefaultEdge edge: graph.outgoingEdgesOf(vertex)) {
				Integer neighbor = graph.getEdgeTarget(edge);
				temp.push(-b(vertex));
				temp.push(-b(neighbor));
				phi3.push(temp);
				temp = new VecInt();
			}

			for(DefaultEdge edge: graph.incomingEdgesOf(vertex)) {
				Integer neighbor = graph.getEdgeSource(edge);
				temp.push(-b(vertex));
				temp.push(-b(neighbor));
				phi3.push(temp);
				temp = new VecInt();
			}
			//----------------------------------

			//green or red vertices have at least one blue successor
			temp.push(-g(vertex));
			for(DefaultEdge edge: graph.outgoingEdgesOf(vertex)) {
				Integer successor = graph.getEdgeTarget(edge);
				temp.push(b(successor));
			}
			phi4.push(temp);
			temp = new VecInt();

			temp.push(-r(vertex));
			for(DefaultEdge edge: graph.outgoingEdgesOf(vertex)) {
				Integer successor = graph.getEdgeTarget(edge);
				temp.push(b(successor));
			}
			phi4.push(temp);
			temp = new VecInt();
			//----------------------------------
		}

		phi1.copyTo(phi);
		phi2.copyTo(phi);
		phi3.copyTo(phi);
		phi4.copyTo(phi);

		return phi;
	}



	@Override
	public void getColoringFromModel(ThreeColoring<Integer> coloring, int[] model) {
		for(int i=0; i<model.length; i+=1) {
			System.out.println(model[i]);
			if(model[i] > 0) {
				int val = model[i] % 3;
				if(val == 1) {
					coloring.setColor(i/3 + 1, Color.RED);
				}
				else if(val == 2) {
					coloring.setColor(i/3 + 1, Color.GREEN);
				}
				else if(val == 0) {
					coloring.setColor(i/3 + 1, Color.BLUE);
				}
			}
		}
	}

}
