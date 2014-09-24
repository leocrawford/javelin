package com.crypticbit.diff.demo.swing.contacts;

import java.io.IOException;

import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import com.crypticbit.javelin.convert.VisitorException;
import com.crypticbit.javelin.merkle.Commit;
import com.crypticbit.javelin.merkle.MerkleTree;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;

import difflib.PatchFailedException;

public class CommitGraphPanel extends mxGraphComponent {

	public CommitGraphPanel(Commit... commits) throws JsonSyntaxException,
			StoreException, VisitorException {

		super(getAdapter(commits));

	}

	private static JGraphXAdapter getAdapter(Commit... commits)
			throws JsonSyntaxException, StoreException, VisitorException {
		JGraphXAdapter adapter = new JGraphXAdapter(
				Commit.getAsGraphToRoots(commits));
		/*
		 * xa.getSelectionModel().addListener(mxEvent.SELECT, new
		 * mxIEventListener() {
		 * 
		 * @Override public void invoke(Object arg0, mxEventObject e) { //
		 * tCommit c = (Commit) ((DefaultGraphCell) e.getCell()) //
		 * .getUserObject(); System.out.println(e); } });
		 */

		new mxHierarchicalLayout(adapter).execute(adapter.getDefaultParent());
		new mxParallelEdgeLayout(adapter).execute(adapter.getDefaultParent());
		return adapter;

	}

	private MergeableDirectedGraph asGraphToRoots = new MergeableDirectedGraph(
			DefaultEdge.class);

	public CommitGraphPanel(MerkleTree jca) throws StoreException,
			IOException, JsonSyntaxException, PatchFailedException,
			InterruptedException, VisitorException {

		this(new Commit[] { jca.getCommit() });
	}

	public static final class MergeableDirectedGraph extends
			ListenableDirectedGraph<Commit, DefaultEdge> {
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

	public void show(Commit[] commits) {
		try {
			setGraph(getAdapter(commits));
		} catch (Exception e) {
			// FIXME: handle exception
			e.printStackTrace();
		}

	}

}
