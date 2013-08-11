package com.crypticbit.diff.demo.swing.contacts;

import java.awt.Container;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import com.crypticbit.javelin.js.JsonCasAdapter;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;

public class Main extends JFrame {

    public Main() throws StoreException {
	super("Contacts");
	Container content = getContentPane();
	JSplitPane jSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	content.add(jSplit);

	JsonCasAdapter jsonStore = new JsonCasAdapter(new StorageFactory().createMemoryCas());
	jsonStore.write("{people:[{name:\"Leo\"},{name:\"John\"},{name:\"Caroline\"}]}").commit();

	final ContactEditPanel contactEditPanel = new ContactEditPanel();
	NamePanel namePanel = new NamePanel(jsonStore, new JsonElementSelectionListener(){

	    @Override
	    public void jsonElementSelected(Object element) {
		System.out.println(element);
		contactEditPanel.setJson(element.toString());
		
	    }});
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
