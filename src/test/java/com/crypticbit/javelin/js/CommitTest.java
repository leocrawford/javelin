package com.crypticbit.javelin.js;

import static org.junit.Assert.assertEquals;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.junit.Test;

import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.crypticbit.javelin.js.convert.VisitorException;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import difflib.PatchFailedException;

public class CommitTest extends TestUtils {

    // FIXME - test production of graph that contains a merge

    private DataStructure jca1, jca2, jca3, jca4;

    public CommitTest() throws StoreException, IOException, VisitorException {
	String c1 = "[\"a\"]";
	String c2 = "[\"a\",\"b\"]";
	String c3 = "[\"a\",\"b\",\"c1\"]";
	String c4 = "[\"a\",\"b\",\"c2\",\"d\"]";
	String c5 = "[\"a\",\"b\",\"c2\",\"d\",\"e\"]";
	String c6 = "[\"a\",\"b\",\"c2\",\"d\",[\"f\"],\"f\"]";
	String c7 = "[\"a\",\"b1\",\"c2\",\"d\",[\"f\"],\"g\"]";

	jca1 = new DataStructure(new StorageFactory().createMemoryCas());
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
    public void testCreateChangeSet() throws JsonSyntaxException, StoreException, PatchFailedException, IOException,
	    VisitorException {
//	ThreeWayDiff patch = jca1.getCommit().createChangeSet(jca4.getCommit());
	// System.out.println("X-"+patch.apply());
	jca1.merge(jca4);
	// FIXME - should be unnecessary
	// jca1.checkout();
	System.out.println(jca1.read());
	System.out.println(jca1.read().getAsJsonArray().get(4).getAsJsonArray().get(0));
    }

    // @Test
    // public void testfindLca() throws StoreException, IOException {
    // assertEquals(jca2.getCommit(),
    // jca4.getCommit().findLca(jca3.getCommit()));
    // }

    @Test
    public void testGetAsGraph() throws StoreException, IOException, JsonSyntaxException, VisitorException {
	assertEquals(3, jca1.getCommit().getAsGraphToRoot().vertexSet().size());
	assertEquals(3, jca2.getCommit().getAsGraphToRoot().vertexSet().size());
	assertEquals(4, jca3.getCommit().getAsGraphToRoot().vertexSet().size());
	assertEquals(4, jca4.getCommit().getAsGraphToRoot().vertexSet().size());
    }

    @Test
    public void testNavigate() throws JsonSyntaxException, StoreException, VisitorException {
	String c8 = "{\"a\":\"b\",\"c\":{\"d\":\"e\"},\"f\":[\"g\",1,2,3,{\"k\":true,\"l\":false}]}";
	jca4.write(c8).commit();

	assertEquals(false, jca4.getCommit().navigate("f[4].l"));
	assertEquals("e", jca4.getCommit().navigate("c.d"));
    }

    private void show(Commit... commits) throws JsonSyntaxException, StoreException, PatchFailedException, IOException,
	    VisitorException {
	enableLog("com.crypticbit.javelin.js", Level.FINEST);
	// jca1.merge(jca4);

	JGraphModelAdapter<Commit, DefaultEdge> model = new JGraphModelAdapter<Commit, DefaultEdge>(Commit
		.getAsGraphToRoots(commits));

	JGraph jgraph = new JGraph(model);

	jgraph.addGraphSelectionListener(new GraphSelectionListener() {

	    @Override
	    public void valueChanged(GraphSelectionEvent e) {
		if (e.getCell() instanceof DefaultGraphCell) {
		    Commit c = (Commit) ((DefaultGraphCell) e.getCell()).getUserObject();
		    try {
			System.out.println(c.getElement() + "," + c.getParents());
		    }
		    catch (JsonSyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    }
		    catch (StoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    }
		    catch (VisitorException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		    }
		}

	    }
	});
	jgraph.setPreferredSize(new Dimension(400, 400));
	final JGraphHierarchicalLayout hir = new JGraphHierarchicalLayout();
	final JGraphFacade graphFacade = new JGraphFacade(jgraph);
	hir.run(graphFacade);
	final Map<?, ?> nestedMap = graphFacade.createNestedMap(true, true); //
	// jgraph.getGraphLayoutCache().edit(nestedMap);
	JFrame frame = new JFrame("FrameDemo"); // 2. Optional: What happens
						// when the frame closes?
	// frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 3. Create
	// componentsand put them in the frame.
	// ...create emptyLabel...
	frame.getContentPane().add(jgraph, BorderLayout.CENTER); // 4. Size the
								 // frame.
	frame.pack(); // 5. Show it.
	frame.setVisible(true);
    }

    public static void main(String args[]) throws StoreException, IOException, JsonSyntaxException,
	    PatchFailedException, VisitorException {
	new CommitTest().show();

    }

}
