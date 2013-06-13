package com.crypticbit.javelin.js;

import static org.junit.Assert.assertEquals;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

public class CommitTest {

    // FIXME - test production of graph that contains a merge

    private JsonCasAdapter jca1, jca2, jca3, jca4;

    public CommitTest() throws StoreException, IOException {
	String c1 = "[\"a\"]";
	String c2 = "[\"a\",\"b\"]";
	String c3 = "[\"a\",\"b\",\"c\"]";
	String c4 = "[\"a\",\"b\",\"c\",\"d\"]";
	String c5 = "[\"a\",\"b\",\"c\",\"d\",\"e\"]";
	String c6 = "[\"a\",\"b\",\"c\",\"d\",\"e\",\"f\"]";
	String c7 = "[\"a\",\"b\",\"c\",\"d\",\"e\",\"f\",\"g\"]";

	jca1 = new JsonCasAdapter(new StorageFactory().createMemoryCas());
	jca1.write(c1).commit().write(c2).commit();
	jca2 = jca1.branch();
	jca1.write(c3).commit();
	jca2.write(c4).commit();
	jca3 = jca2.branch();
	jca3.write(c5).commit().write(c6);
	jca4 = jca2.branch();
	jca4.write(c7).commit();
    }

    @Test
    public void testCreateChangeSet() {
	// fail("Not yet implemented");
    }

    @Test
    public void testfindLca() throws StoreException, IOException {
	assertEquals(jca2.getCommit(), jca4.getCommit().findLca(jca3.getCommit()));
    }

    @Test
    public void testGetAsGraph() throws StoreException, IOException {
	assertEquals(3, jca1.getCommit().getAsGraphToRoot().vertexSet().size());
	assertEquals(3, jca2.getCommit().getAsGraphToRoot().vertexSet().size());
	assertEquals(4, jca3.getCommit().getAsGraphToRoot().vertexSet().size());
	assertEquals(4, jca4.getCommit().getAsGraphToRoot().vertexSet().size());
    }

    @Test
    public void testGetShortestHistory() {
	// fail("Not yet implemented");
    }

    private void show() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	JGraph jgraph = new JGraph(new JGraphModelAdapter<CommitDao, DefaultEdge>(Commit
		.getAsGraphToRoots(new Commit[] { jca4.getCommit(), jca3.getCommit() })));
	jgraph.setPreferredSize(new Dimension(400, 400));
	final JGraphHierarchicalLayout hir = new JGraphHierarchicalLayout();
	final JGraphFacade graphFacade = new JGraphFacade(jgraph);
	hir.run(graphFacade);
	final Map<?, ?> nestedMap = graphFacade.createNestedMap(true, true); //
	jgraph.getGraphLayoutCache().edit(nestedMap);
	JFrame frame = new JFrame("FrameDemo"); // 2. Optional: What happens when the frame closes?
						// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 3. Create
						// componentsand put them in the frame.
	// ...create emptyLabel...
	frame.getContentPane().add(jgraph, BorderLayout.CENTER); // 4. Size the frame.
	frame.pack(); // 5. Show it.
	frame.setVisible(true);
    }

    public static void main(String args[]) throws StoreException, IOException {
	new CommitTest().show();

    }

}
