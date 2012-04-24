package com.common;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class UndirectedWeightedGraph extends SimpleWeightedGraph<String, DefaultWeightedEdge> {

	private static final long serialVersionUID = 1L;

	public UndirectedWeightedGraph () {
		super(DefaultWeightedEdge.class);
	}
	
	public void addWeightedEdge (String v1, String v2, double weight) {
		if (!containsVertex(v1))
			addVertex(v1);
		if (!containsVertex(v2))
			addVertex(v2);
		DefaultWeightedEdge e1 = new DefaultWeightedEdge();
		DefaultWeightedEdge e2 = new DefaultWeightedEdge();
		addEdge(v1, v2, e1);
		addEdge(v2, v1, e2);
		setEdgeWeight(e1, weight);
		setEdgeWeight(e2, weight);
	}
	
	@Override
	public String toString() {
		System.out.println("Nodes");
		for( String v : vertexSet())
			System.out.print(v + " ");
		System.out.println(".");
		
		System.out.println("Edges");
		for( DefaultWeightedEdge edge: edgeSet())
			System.out.println(getEdgeSource(edge)+","+getEdgeTarget(edge)+"="+getEdgeWeight(edge));
		return super.toString();
	}
}