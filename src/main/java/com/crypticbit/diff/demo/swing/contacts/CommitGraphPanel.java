package com.crypticbit.diff.demo.swing.contacts;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.ListenableDirectedGraph;

import com.crypticbit.javelin.js.Commit;
import com.crypticbit.javelin.js.DataStructure;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.tree.JGraphTreeLayout;

import difflib.PatchFailedException;

public class CommitGraphPanel extends JGraph {

    private MergeableDirectedGraph asGraphToRoots = new MergeableDirectedGraph(DefaultEdge.class);

    public CommitGraphPanel(DataStructure jca) throws StoreException, IOException, JsonSyntaxException,
	    PatchFailedException, InterruptedException {
	
	System.out.println(asGraphToRoots.edgeSet().size());
	
	show(new Commit[] { jca.getCommit() });
	setModel(new JGraphModelAdapter<Commit, DefaultEdge>(asGraphToRoots));

	System.out.println(asGraphToRoots.edgeSet().size());
	addGraphSelectionListener(new GraphSelectionListener() {

	    @Override
	    public void valueChanged(GraphSelectionEvent e) {
		if (e.getCell() instanceof DefaultGraphCell
			&& ((DefaultGraphCell) e.getCell()).getUserObject() instanceof Commit) {
		    Commit c = (Commit) ((DefaultGraphCell) e.getCell()).getUserObject();
		    // do something

		}

	    }
	});
    }

    public void show(Commit[] commits) throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	System.out.println(Commit.getAsGraphToRoots(commits));
	asGraphToRoots.merge(Commit.getAsGraphToRoots(commits));
	final JGraphFacade graphFacade = new JGraphFacade(this);
	JGraphTreeLayout lay = new JGraphTreeLayout();
	lay.run(graphFacade);
	final Map nestedMap = graphFacade.createNestedMap(true, true);
	getGraphLayoutCache().edit(nestedMap);
    }

    public static final class MergeableDirectedGraph extends ListenableDirectedGraph<Commit, DefaultEdge> {
	public MergeableDirectedGraph(Class<? extends DefaultEdge> edgeClass) {
	    super(edgeClass);

	}

	public void merge(DirectedGraph<Commit, DefaultEdge> asGraphToRoots1) {
	    for (DefaultEdge x : asGraphToRoots1.edgeSet()) {
		System.out.println("y");
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

}
