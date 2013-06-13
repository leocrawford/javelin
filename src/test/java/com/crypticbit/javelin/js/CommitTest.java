package com.crypticbit.javelin.js;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Map;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

public class CommitTest {

    // FIXME - test production of graph that contains a merge

    @Test
    public void testGetAsGraph() throws StoreException, IOException {
	String c1 = "[\"a\"]";
	String c2 = "[\"a\",\"b\"]";
	String c3 = "[\"a\",\"b\",\"c\"]";
	String c4 = "[\"a\",\"b\",\"c\",\"d\"]";
	String c5 = "[\"a\",\"b\",\"c\",\"d\",\"e\"]";
	String c6 = "[\"a\",\"b\",\"c\",\"d\",\"e\",\"f\"]";
	String c7 = "[\"a\",\"b\",\"c\",\"d\",\"e\",\"f\",\"g\"]";

	JsonCasAdapter jca1, jca2, jca3, jca4;
	jca1 = new JsonCasAdapter(new StorageFactory().createMemoryCas());

	jca1.write(c1).commit().write(c2).commit();

	Graph<CommitDao, DefaultEdge> asGraph = jca1.getCommit().getAsGraphToRoot();
	System.out.println("jca1="+jca1.getCommit());
	
	jca2 = jca1.branch();
	jca1.write(c3).commit();
	jca2.write(c4).commit();

	System.out.println("jca2="+jca2.getCommit());
	
	jca3 = jca2.branch();
	jca3.write(c5).commit().write(c6);
	jca4 = jca2.branch();
	jca4.write(c7).commit();

	// ListenableGraph g = new ListenableDirectedGraph(DefaultEdge.class);
	// create a visualization using JGraph, via the adapter

	System.out.println("jca1="+jca1.getCommit());
	System.out.println("jca2="+jca2.getCommit());
	System.out.println("jca3="+jca3.getCommit());
	System.out.println("jca4="+jca4.getCommit());
	
	System.out.println(jca4.getCommit().findLca(jca3.getCommit()));
	
	JGraph jgraph = new JGraph(new JGraphModelAdapter(Commit.getAsGraphToRoots(new Commit[] { jca4.getCommit(),
		jca3.getCommit()})));
	jgraph.setPreferredSize(new Dimension(400, 400));
	final JGraphHierarchicalLayout hir = new JGraphHierarchicalLayout();
	final JGraphFacade graphFacade = new JGraphFacade(jgraph);
	hir.run(graphFacade);
	final Map nestedMap = graphFacade.createNestedMap(true, true); //
	jgraph.getGraphLayoutCache().edit(nestedMap);
	JFrame frame = new JFrame("FrameDemo"); // 2. Optional: What happens when the frame closes?
						// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 3. Create
						// componentsand put them in the frame.
	// ...create emptyLabel...
	frame.getContentPane().add(jgraph, BorderLayout.CENTER); // 4. Size the frame.
	frame.pack(); // 5. Show it.
	frame.setVisible(true);

    }

    @Test
    public void testGetShortestHistory() {
	// fail("Not yet implemented");
    }

    @Test
    public void testCreateChangeSet() {
	// fail("Not yet implemented");
    }

    public static void main(String args[]) throws StoreException, IOException {
	new CommitTest().testGetAsGraph();
    }

}
