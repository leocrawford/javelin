package com.crypticbit.diff.demo.swing.contacts;

import com.crypticbit.javelin.convert.VisitorException;
import com.crypticbit.javelin.merkle.Commit;
import com.crypticbit.javelin.merkle.MerkleTree;
import com.crypticbit.javelin.merkle.MerkleTree.MergeType;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

import difflib.PatchFailedException;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.Socket;

public class Main extends JFrame {

	private String lastPath = null;
	private NamePanel namePanel;
	private CommitGraphPanel commitGraphPanel;
	private MerkleTree jsonStore;

	public Main() throws StoreException, JsonSyntaxException,
			PatchFailedException, IOException, InterruptedException,
			VisitorException {
		super("Contacts");

		final Server.StreamCallback exported = new Server.StreamCallback() {
			@Override
			public void callback(InputStream is) {
				try {
					jsonStore.importAll(is, MergeType.MERGE);
					jsonStore.checkout();
					commitChange();
					System.out.println(jsonStore.read());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void callback(OutputStream os) {
				try {
					jsonStore.exportAll(os);
					System.out.println("Exported");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menu.add(new JMenuItem(new AbstractAction("Export") {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					File f = File.createTempFile(jsonStore.getCommit()
							.getUser(), "commit");

					jsonStore.exportAll(new FileOutputStream(f));
					System.out.println("Exported to: " + f + ".");
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}));
		menu.add(new JMenuItem(new AbstractAction("Import") {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					JFileChooser chooser = new JFileChooser();
					chooser.showOpenDialog(Main.this);
					File f = chooser.getSelectedFile();

					jsonStore
							.importAll(new FileInputStream(f), MergeType.MERGE);
					jsonStore.checkout();
					commitChange();
					System.out.println(jsonStore.read());

				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}));

		menu.add(new JMenuItem(new AbstractAction("Start Listening") {
			Server server = null;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (server == null) {
					server = new Server(exported);

					putValue(Action.NAME, "Stop Listening (" + server.getPort()
							+ ")");
				} else {
					server.halt();
					server = null;
					putValue(Action.NAME, "Start Listening");
				}
			}
		}));

		menu.add(new JMenuItem(new AbstractAction("Sync") {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Socket socket = new Socket("127.0.0.1", 8000);
					exported.callback(socket.getOutputStream());

				} catch (Exception ee) {
					ee.printStackTrace();
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

		jsonStore = new MerkleTree(new StorageFactory().createMemoryCas());
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

	/**
	 * Call this when finished using this frame.
	 */
	public void closeWindow() {
		setVisible(false);
		dispose();
	}

	private void commitChange() throws JsonSyntaxException,
			UnsupportedEncodingException, StoreException, VisitorException {
		namePanel.refresh();
		commitGraphPanel.show(new Commit[] { jsonStore.getCommit() });
	}

	/**
	 * @param args
	 * @throws StoreException
	 */
	public static void main(String[] args) throws Exception {
		new Main();

	}

}
