package com.crypticbit.diff.demo.swing.contacts;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.crypticbit.javelin.js.Commit;
import com.crypticbit.javelin.js.DataStructure;
import com.crypticbit.javelin.js.convert.VisitorException;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

import difflib.PatchFailedException;

public class Main extends JFrame {

	private String lastPath = null;
	private NamePanel namePanel;
	private CommitGraphPanel commitGraphPanel;
	private DataStructure jsonStore;

	public Main() throws StoreException, JsonSyntaxException,
			PatchFailedException, IOException, InterruptedException,
			VisitorException {
		super("Contacts");

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menu.add(new JMenuItem(new AbstractAction("Export") {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File f = File.createTempFile(jsonStore.getCommit().getUser(), "commit");

					jsonStore.exportAll(new FileOutputStream(f));
					System.out.println("Exported to: " + f);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}));
		menuBar.add(menu);
		setJMenuBar(menuBar);

		
		Container content = getContentPane();
		JSplitPane navAndViewJSplit = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		JSplitPane commitAndEditJSplit = new JSplitPane(
				JSplitPane.VERTICAL_SPLIT);
		content.add(navAndViewJSplit);

		jsonStore = new DataStructure(new StorageFactory().createMemoryCas());
		jsonStore
				.write("{people:[{name:\"Leo\"},{name:\"John\"},{name:\"Caroline\"}]}")
				.commit().commit();

		final ContactEditPanel contactEditPanel = new ContactEditPanel();
		namePanel = new NamePanel(jsonStore,
				new JsonElementSelectionListener() {

					@Override
					public void jsonElementSelected(String path, Object element) {
						System.out.println(path + "->" + element + ","
								+ element.getClass());
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
				} catch (Exception e) {
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

	private void commitChange() throws JsonSyntaxException,
			UnsupportedEncodingException, StoreException, VisitorException {
		namePanel.refresh();
		commitGraphPanel.show(new Commit[] { jsonStore.getCommit() });
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
