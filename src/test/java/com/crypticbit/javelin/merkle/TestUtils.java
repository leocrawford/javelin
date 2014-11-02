package com.crypticbit.javelin.merkle;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JDialog;

import com.crypticbit.diff.demo.swing.contacts.CommitGraphPanel;
import com.crypticbit.javelin.store.StoreException;
import com.google.gson.JsonSyntaxException;

import difflib.PatchFailedException;

public class TestUtils {

	void enableLog(String path, Level level) {
		Logger LOG = Logger.getLogger(path);
		ConsoleHandler handler = new ConsoleHandler();
		handler.setLevel(level);
		LOG.addHandler(handler);
		LOG.setLevel(level);
	}

	void show(Commit... commits) throws JsonSyntaxException, StoreException,
			PatchFailedException, IOException, CorruptTreeException {
		enableLog("com.crypticbit.javelin.js", Level.FINEST);
		JDialog frame = new JDialog();
		frame.setModal(true);
		frame.getContentPane().add(new CommitGraphPanel(commits),
				BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);

	}

}
