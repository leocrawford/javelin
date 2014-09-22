package com.crypticbit.javelin.js;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;

import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultEdge;

import com.crypticbit.javelin.js.convert.VisitorException;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;

import difflib.PatchFailedException;

public class TestUtils {

	void enableLog(String path, Level level) {
		Logger LOG = Logger.getLogger(path);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(level);
		LOG.addHandler(handler);
		LOG.setLevel(level);
	}

	void show(Commit... commits) throws JsonSyntaxException, StoreException,
			PatchFailedException, IOException, VisitorException {
		enableLog("com.crypticbit.javelin.js", Level.FINEST);

		JGraphModelAdapter<Commit, DefaultEdge> model = new JGraphModelAdapter<Commit, DefaultEdge>(
				Commit.getAsGraphToRoots(commits));


		JGraphXAdapter xa = new JGraphXAdapter(Commit.getAsGraphToRoots(commits));
		mxGraphComponent graphComponent = new mxGraphComponent(xa);
		
/*		xa.getSelectionModel().addListener(mxEvent.SELECT, new mxIEventListener() {
			
			@Override
			public void invoke(Object arg0, mxEventObject e) {
				//	tCommit c = (Commit) ((DefaultGraphCell) e.getCell())
//							.getUserObject();
				System.out.println(e);
			}
		}); */
		
		
		new mxHierarchicalLayout(xa).execute(xa
				.getDefaultParent());
		new mxParallelEdgeLayout(xa).execute(xa
				.getDefaultParent());

		JDialog frame = new JDialog(); 
		frame.setModal(true);
		frame.getContentPane().add(graphComponent, BorderLayout.CENTER); 
		frame.pack(); 
		frame.setVisible(true);

	}

}