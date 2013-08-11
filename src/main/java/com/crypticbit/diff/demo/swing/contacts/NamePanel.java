package com.crypticbit.diff.demo.swing.contacts;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.crypticbit.javelin.js.JsonCasAdapter;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

public class NamePanel extends JPanel {

    public NamePanel(JsonCasAdapter jca, final JsonElementSelectionListener jsonElementSelectionListener) throws JsonSyntaxException, StoreException {
	this.setLayout(new BorderLayout());
	final JsonListModelAdapter dataModel = new JsonListModelAdapter(jca, "people","name");
	JList list = new JList(dataModel);
	this.add(list,BorderLayout.CENTER);
	this.add(new JButton("Add"),BorderLayout.SOUTH);
	list.addListSelectionListener(new ListSelectionListener() {
	    
	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		jsonElementSelectionListener.jsonElementSelected(dataModel.getJsonElementAt(e.getFirstIndex()));	
	    }
	});
    }
}
