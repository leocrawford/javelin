package com.crypticbit.diff.demo.swing;

import java.awt.BorderLayout;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import com.crypticbit.diff.demo.swing.JSONEditPanel.UpdateType;
import com.crypticbit.javelin.js.Commit;
import com.crypticbit.javelin.js.JsonCasAdapter;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.tree.JGraphTreeLayout;

import difflib.PatchFailedException;

public class CommitBuilder extends JFrame {


    public static final class MergeableDirectedGraph extends ListenableDirectedGraph<Commit, DefaultEdge> {
	public MergeableDirectedGraph(Class<? extends DefaultEdge> edgeClass) {
	    super(edgeClass);

	}

	public void merge(DirectedGraph<Commit, DefaultEdge> asGraphToRoots1) {
	    for (DefaultEdge x : asGraphToRoots1.edgeSet()) {
		Commit edgeSource = asGraphToRoots1.getEdgeSource(x);
		Commit edgeTarget = asGraphToRoots1.getEdgeTarget(x);
		if (!containsVertex(edgeSource))
		    addVertex(edgeSource);
		if (!containsVertex(edgeTarget))
		    addVertex(edgeTarget);
		addEdge(edgeSource, edgeTarget);
	    }

	}
    }

    private JsonCasAdapter jca1, jca2, jca3, jca4;
    private SmartJGraph commitPanel;

    public CommitBuilder() throws StoreException, IOException, JsonSyntaxException, PatchFailedException,
	    InterruptedException {
	addDummyData();

	commitPanel = new SmartJGraph();
	commitPanel.merge(new Commit[] { jca4.getCommit(), jca1.getCommit() });
	JSplitPane jSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	final JSONEditPanel jsonPanel = new JSONEditPanel();
	
	jSplit.add(commitPanel);
	jSplit.add(jsonPanel);
	

	
	getContentPane().add(jSplit);
	commitPanel.addGraphSelectionListener(new GraphSelectionListener() {

		@Override
		public void valueChanged(GraphSelectionEvent e) {
		    if (e.getCell() instanceof DefaultGraphCell
			    && ((DefaultGraphCell) e.getCell()).getUserObject() instanceof Commit) {
			Commit c = (Commit) ((DefaultGraphCell) e.getCell()).getUserObject();
			try {
			    jsonPanel.setJson(c.getElement().toString(), UpdateType.REPLACE);
			}
			catch (JsonSyntaxException | UnsupportedEncodingException | StoreException e1) {
			    // TODO Auto-generated catch block
			    e1.printStackTrace();
			}
		    }

		}
	    });
	
	pack();
	new Thread() {
	    public void run() {
		setVisible(true);
	    }
	}.start();

	demo();
    }

    private void addDummyData() throws StoreException, IOException {
	String c1 = "[\"a\"]";
	String c2 = "[\"a\",\"b\"]";
	String c3 = "[\"a\",\"b\",\"c1\"]";
	String c4 = "[\"a\",\"b\",\"c2\",\"d\"]";
	String c5 = "[\"a\",\"b\",\"c2\",\"d\",\"e\"]";
	String c6 = "[\"a\",\"b\",\"c2\",\"d\",[\"f\"],\"f\"]";
	String c7 = "[\"a\",\"b1\",\"c2\",\"d\",[\"f\"],\"g\"]";

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

    public static void main(String args[]) throws JsonSyntaxException, PatchFailedException, StoreException,
	    IOException, InterruptedException {
	new CommitBuilder();
    }

    private void demo() throws JsonSyntaxException, StoreException, PatchFailedException, IOException,
	    InterruptedException {
	Thread.sleep(4000);

	jca1.merge(jca4);
	commitPanel.merge(new Commit[] { jca1.getCommit() });

	Thread.sleep(2000);

	jca4.write("hello").commit();

	commitPanel.merge(new Commit[] { jca4.getCommit() });
	System.out.println("x");
    }

    static class SmartJGraph extends JGraph {

	private MergeableDirectedGraph asGraphToRoots = new MergeableDirectedGraph(DefaultEdge.class);;

	SmartJGraph() {
	    super();
	    this.setModel(new JGraphModelAdapter<Commit, DefaultEdge>(asGraphToRoots));

	    

	}

	public void merge(Commit[] commits) throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	    asGraphToRoots.merge(Commit.getAsGraphToRoots(commits));
	    final JGraphFacade graphFacade = new JGraphFacade(this);
	    JGraphTreeLayout lay = new JGraphTreeLayout();
	    lay.run(graphFacade);
	    final Map nestedMap = graphFacade.createNestedMap(true, true);
	    getGraphLayoutCache().edit(nestedMap);
	}

    }
}
