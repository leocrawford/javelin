package com.crypticbit.diff.demo.swing.contacts;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.crypticbit.javelin.js.JsonCasAdapter;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

public class NamePanel extends JPanel {

    public NamePanel(JsonCasAdapter jca, final JsonElementSelectionListener jsonElementSelectionListener)
	    throws JsonSyntaxException, StoreException {
	this.setLayout(new BorderLayout());
	final JsonListModelAdapter dataModel = new JsonListModelAdapter(jca, "people", "name");
	final JList list = new JList(dataModel);
//	list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	this.add(list, BorderLayout.CENTER);
	this.add(new JButton("Add"), BorderLayout.SOUTH);
	list.addListSelectionListener(new ListSelectionListener() {

	    
	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
		    int selectedIndex = list.getSelectedIndex();
		    jsonElementSelectionListener.jsonElementSelected("people["+ selectedIndex+"]", dataModel
			    .getJsonElementAt(selectedIndex));
		}
	    }
	});
    }
}
