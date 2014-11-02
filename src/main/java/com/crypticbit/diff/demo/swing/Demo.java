package com.crypticbit.diff.demo.swing;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Demo implements OKCancelListener {

    private JSONEditFrame theFrame;

    private Demo() {
	theFrame = new JSONEditFrame(this, "Testing JSONEditFrame", "{}");
    }

    @Override
    public void onFrameAction(Action action, JFrame frame) {
	if (theFrame == frame) {
	    String json = theFrame.getJson();
	    theFrame.closeWindow();
	    if (action.equals(Action.OK)) {
		JOptionPane.showMessageDialog(null, json, "Resulting JSON", JOptionPane.INFORMATION_MESSAGE);
	    }
	    else if (action.equals(Action.DEFAULT_CLOSE)) {
		JOptionPane.showMessageDialog(null, "The default close button was clicked", "User Hit Default Close",
			JOptionPane.INFORMATION_MESSAGE);
	    }
	    else {
		JOptionPane.showMessageDialog(null, "The cancel button was clicked", "User Hit Cancel",
			JOptionPane.INFORMATION_MESSAGE);
	    }
	}
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
	Logger LOG = Logger.getLogger("com.crypticbit.javelin.diff");
	ConsoleHandler handler = new ConsoleHandler();
	handler.setLevel(Level.ALL);
	LOG.addHandler(handler);
	LOG.setLevel(Level.ALL);

	new Demo();

    }

}
