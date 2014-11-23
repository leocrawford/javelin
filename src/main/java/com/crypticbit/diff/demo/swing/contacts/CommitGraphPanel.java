package com.crypticbit.diff.demo.swing.contacts;

import java.io.IOException;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import com.crypticbit.javelin.merkle.Commit;
import com.crypticbit.javelin.merkle.CorruptTreeException;
import com.crypticbit.javelin.merkle.MerkleTree;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.view.mxGraphSelectionModel;

import difflib.PatchFailedException;

public class CommitGraphPanel extends mxGraphComponent implements mxIEventListener {

    private MergeableDirectedGraph asGraphToRoots = new MergeableDirectedGraph(DefaultEdge.class);

    public CommitGraphPanel(Commit... commits) throws JsonSyntaxException, StoreException, CorruptTreeException {

	super(getAdapter(commits));

	// this.getAdapter().addListener(null, this);
	this.getGraph().getSelectionModel().addListener("change", this);
    }

    public CommitGraphPanel(MerkleTree jca) throws StoreException, IOException, JsonSyntaxException,
	    PatchFailedException, InterruptedException, CorruptTreeException {

	this(new Commit[] { jca.getCommit() });
    }

    public void show(Commit[] commits) {
	try {
	    setGraph(getAdapter(commits));
	}
	catch (Exception e) {
	    // FIXME: handle exception
	    e.printStackTrace();
	}

    }

    private static JGraphXAdapter getAdapter(Commit... commits) throws JsonSyntaxException, StoreException,
	    CorruptTreeException {
	JGraphXAdapter adapter = new JGraphXAdapter(Commit.getAsGraphToRoots(commits));
	adapter.setCellsDisconnectable(false);
	adapter.setCellsEditable(false);
	adapter.setCellsLocked(true);

	/*
	 * xa.getSelectionModel().addListener(mxEvent.SELECT, new mxIEventListener() {
	 * @Override public void invoke(Object arg0, mxEventObject e) { // tCommit c = (Commit) ((DefaultGraphCell)
	 * e.getCell()) // .getUserObject(); System.out.println(e); } });
	 */

	new mxHierarchicalLayout(adapter).execute(adapter.getDefaultParent());
	new mxParallelEdgeLayout(adapter).execute(adapter.getDefaultParent());
	return adapter;

    }

    public static final class MergeableDirectedGraph extends ListenableDirectedGraph<Commit, DefaultEdge> {
	public MergeableDirectedGraph(Class<? extends DefaultEdge> edgeClass) {
	    super(edgeClass);

	}

	public void merge(DirectedGraph<Commit, DefaultEdge> asGraphToRoots1) {
	    for (DefaultEdge x : asGraphToRoots1.edgeSet()) {
		Commit edgeSource = asGraphToRoots1.getEdgeSource(x);
		Commit edgeTarget = asGraphToRoots1.getEdgeTarget(x);
		if (!containsVertex(edgeSource)) {
		    addVertex(edgeSource);
		}
		if (!containsVertex(edgeTarget)) {
		    addVertex(edgeTarget);
		}
		addEdge(edgeSource, edgeTarget);
	    }

	}
    }

    @Override
    public void invoke(Object sender, mxEventObject evt) {
	mxGraphSelectionModel sm = (mxGraphSelectionModel) sender;
	mxCell cell = (mxCell) sm.getCell();
	if (cell.isVertex())
	    System.out.println(((Commit) cell.getValue()).getAsElement());
    }

}
