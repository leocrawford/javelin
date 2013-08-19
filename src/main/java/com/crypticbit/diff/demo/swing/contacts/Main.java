package com.crypticbit.diff.demo.swing.contacts;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import com.crypticbit.javelin.js.JsonCasAdapter;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

public class Main extends JFrame {

    private String lastPath = null;
    
    public Main() throws StoreException {
	super("Contacts");
	Container content = getContentPane();
	JSplitPane jSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	content.add(jSplit);

	final JsonCasAdapter jsonStore = new JsonCasAdapter(new StorageFactory().createMemoryCas());
	jsonStore.write("{people:[{name:\"Leo\"},{name:\"John\"},{name:\"Caroline\"}]}").commit();

	final ContactEditPanel contactEditPanel = new ContactEditPanel();
	NamePanel namePanel = new NamePanel(jsonStore, new JsonElementSelectionListener(){

	    @Override
	    public void jsonElementSelected(String path, Object element) {
		System.out.println(path+"->"+element+","+element.getClass());
		lastPath = path;
		contactEditPanel.setJson(element.toString());
		
	    }});
	contactEditPanel.addJsonChangeListener(new JsonChangeListener() {
	    
	    @Override
	    public void notify(String json) {
		System.out.println("Setting "+lastPath+" as "+json+","+json.getClass());
		try {
		    jsonStore.write(lastPath, json);
		}
		catch (JsonSyntaxException | StoreException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	});
	
	jSplit.add(namePanel);
	jSplit.add(contactEditPanel);
	

	setPreferredSize(new Dimension(680, 480));
	pack();
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setLocationRelativeTo(null);
	setVisible(true);

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
    public static void main(String[] args) throws StoreException {
	new Main();

    }

}
