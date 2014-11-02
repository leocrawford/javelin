package com.crypticbit.diff.demo.swing;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JSplitPane;

import com.crypticbit.diff.demo.swing.contacts.CommitGraphPanel;
import com.crypticbit.javelin.merkle.Commit;
import com.crypticbit.javelin.merkle.CorruptTreeException;
import com.crypticbit.javelin.merkle.MergeException;
import com.crypticbit.javelin.merkle.MerkleTree;
import com.crypticbit.javelin.store.StorageFactory;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

import difflib.PatchFailedException;

public class CommitBuilder extends JFrame {

	private MerkleTree jca1, jca2, jca3, jca4;

	private CommitGraphPanel commitPanel;

	public CommitBuilder() throws StoreException, IOException,
			JsonSyntaxException, PatchFailedException, InterruptedException, MergeException, CorruptTreeException {
		addDummyData();

		commitPanel = new CommitGraphPanel(jca4.getCommit(), jca1.getCommit());
		JSplitPane jSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		final JSONEditPanel jsonPanel = new JSONEditPanel();

		jSplit.add(commitPanel);
		jSplit.add(jsonPanel);

		getContentPane().add(jSplit);
		/*
		 * commitPanel.addGraphSelectionListener(new GraphSelectionListener() {
		 * 
		 * @Override public void valueChanged(GraphSelectionEvent e) { if
		 * (e.getCell() instanceof DefaultGraphCell && ((DefaultGraphCell)
		 * e.getCell()).getUserObject() instanceof Commit) { Commit c = (Commit)
		 * ((DefaultGraphCell) e.getCell()).getUserObject(); try {
		 * jsonPanel.setJson(c.getElement().toString(), UpdateType.REPLACE); }
		 * catch (JsonSyntaxException | StoreException | VisitorException e1) {
		 * // TODO Auto-generated catch block e1.printStackTrace(); } }
		 * 
		 * } });
		 */
		pack();
		new Thread() {
			@Override
			public void run() {
				setVisible(true);
			}
		}.start();

		demo();
	}

	private void addDummyData() throws StoreException, IOException, CorruptTreeException {
		String c1 = "[\"a\"]";
		String c2 = "[\"a\",\"b\"]";
		String c3 = "[\"a\",\"b\",\"c1\"]";
		String c4 = "[\"a\",\"b\",\"c2\",\"d\"]";
		String c5 = "[\"a\",\"b\",\"c2\",\"d\",\"e\"]";
		String c6 = "[\"a\",\"b\",\"c2\",\"d\",[\"f\"],\"f\"]";
		String c7 = "[\"a\",\"b1\",\"c2\",\"d\",[\"f\"],\"g\"]";

		jca1 = new MerkleTree(new StorageFactory().createMemoryCas());
		jca1.write(c1).commit().write(c2).commit();
		jca2 = jca1.branch();
		jca1.write(c3).commit();
		jca2.write(c4).commit();
		jca3 = jca2.branch();
		jca3.write(c5).commit().write(c6);
		jca4 = jca2.branch();
		jca4.write(c7).commit();
	}

	private void demo() throws JsonSyntaxException, StoreException,
			PatchFailedException, IOException, InterruptedException, MergeException, CorruptTreeException {
		Thread.sleep(4000);

		jca1.merge(jca4);
		commitPanel.show(new Commit[] { jca1.getCommit() });

		Thread.sleep(2000);

		jca4.write("hello").commit();

		commitPanel.show(new Commit[] { jca4.getCommit() });
		System.out.println("x");
	}

	public static void main(String args[]) throws JsonSyntaxException,
			PatchFailedException, StoreException, IOException,
			InterruptedException, MergeException, CorruptTreeException {
		new CommitBuilder();
	}

}
