package com.crypticbit.diff.demo.swing.contacts;

import java.awt.Container;
import java.awt.Dimension;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.crypticbit.javelin.js.Commit;
import com.crypticbit.javelin.js.JsonCasAdapter;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

import difflib.PatchFailedException;

public class Main extends JFrame {

    private String lastPath = null;
    private NamePanel namePanel;
    private CommitGraphPanel commitGraphPanel;
    private JsonCasAdapter jsonStore;

    public Main() throws StoreException, JsonSyntaxException, PatchFailedException, IOException, InterruptedException {
	super("Contacts");
	Container content = getContentPane();
	JSplitPane navAndViewJSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	JSplitPane commitAndEditJSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
	content.add(navAndViewJSplit);

	jsonStore = new JsonCasAdapter(new StorageFactory().createMemoryCas());
	jsonStore.write("{people:[{name:\"Leo\"},{name:\"John\"},{name:\"Caroline\"}]}").commit().commit();

	final ContactEditPanel contactEditPanel = new ContactEditPanel();
	namePanel = new NamePanel(jsonStore, new JsonElementSelectionListener() {

	    @Override
	    public void jsonElementSelected(String path, Object element) {
		System.out.println(path + "->" + element + "," + element.getClass());
		lastPath = path;
		contactEditPanel.setJson(element.toString());

	    }
	});
	contactEditPanel.addJsonChangeListener(new JsonChangeListener() {

	    @Override
	    public void notify(String json) {
		try {
		    jsonStore.write(lastPath, json);
		    commitChange();
		}
		catch (Exception e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }

	});

	navAndViewJSplit.add(namePanel);
	navAndViewJSplit.add(commitAndEditJSplit);
	commitAndEditJSplit.add(contactEditPanel);
	commitGraphPanel = new CommitGraphPanel(jsonStore);
	commitAndEditJSplit.add(new JScrollPane(commitGraphPanel));

	setPreferredSize(new Dimension(680, 480));
	pack();
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setLocationRelativeTo(null);
	setVisible(true);

    }

    private void commitChange() throws JsonSyntaxException, UnsupportedEncodingException, StoreException {
	namePanel.refresh();
	commitGraphPanel.show(new Commit[]{jsonStore.getCommit()});
    }

    /**
     * Call this when finished using this frame.
     */
    public void closeWindow() {
	setVisible(false);
	dispose();
    }

    /**
     * @param args
     * @throws StoreException
     */
    public static void main(String[] args) throws Exception {
	new Main();

    }

}