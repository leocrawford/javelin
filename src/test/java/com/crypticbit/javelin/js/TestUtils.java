package com.crypticbit.javelin.js;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.jgraph.JGraph;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.DefaultGraphCell;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;

import com.crypticbit.javelin.js.convert.VisitorException;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.hierarchical.JGraphHierarchicalLayout;

import difflib.PatchFailedException;

public class TestUtils {

    void enableLog(String path, Level level) {
	Logger LOG = Logger.getLogger(path);
	ConsoleHandler handler = new ConsoleHandler();
	handler.setLevel(level);
	LOG.addHandler(handler);
	LOG.setLevel(level);
    }

    void show(Commit... commits) throws JsonSyntaxException, StoreException, PatchFailedException, IOException,
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

}