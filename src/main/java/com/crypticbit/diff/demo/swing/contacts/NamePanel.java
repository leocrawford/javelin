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

    private JList list;
    private JsonCasAdapter jca;
    private JsonListModelAdapter dataModel;

    public NamePanel(JsonCasAdapter jca, final JsonElementSelectionListener jsonElementSelectionListener)
	    throws JsonSyntaxException, StoreException {
	this.jca = jca;
	this.setLayout(new BorderLayout());
	JsonListModelAdapter dataModel = updateModel(jca);
	list = new JList(dataModel);
	// list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	this.add(list, BorderLayout.CENTER);
	this.add(new JButton("Add"), BorderLayout.SOUTH);
	list.addListSelectionListener(new ListSelectionListener() {

	    @Override
	    public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
		    int selectedIndex = list.getSelectedIndex();
		    if (selectedIndex >= 0)
			jsonElementSelectionListener.jsonElementSelected("people[" + selectedIndex + "]", getModel()
				.getJsonElementAt(selectedIndex));
		}
	    }
	});
    }

    private JsonListModelAdapter getModel() {
	return dataModel;
    }

    private JsonListModelAdapter updateModel(JsonCasAdapter jca) throws StoreException {
	dataModel = new JsonListModelAdapter(jca, "people", "name");
	return dataModel;
    }

    public void refresh() {
	try {
	    list.setModel(updateModel(jca));
	}
	catch (StoreException e) {
	    // FIXME
	    throw new Error(e);
	}
    }
}
