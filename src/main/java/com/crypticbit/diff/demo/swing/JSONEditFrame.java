package com.crypticbit.diff.demo.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import com.crypticbit.javelin.diff.DifferFactory;
import com.crypticbit.javelin.diff.ThreeWayDiff;
import com.crypticbit.javelin.diff.js.JsonElementArray;
import com.crypticbit.javelin.diff.js.JsonElementMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

/**
 * JSON Editor Frame This class is not thread safe.
 *
 * @author Stephen Owens
 *         <p>
 *         Copyright 2011 Stephen P. Owens : steve@doitnext.com
 *         </p>
 *         <p>
 *         Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 *         compliance with the License. You may obtain a copy of the License at
 *         </p>
 *         <p>
 *         http://www.apache.org/licenses/LICENSE-2.0
 *         </p>
 *         <p>
 *         Unless required by applicable law or agreed to in writing, software distributed under the License is
 *         distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 *         the License for the specific language governing permissions and limitations under the License.
 *         </p>
 */
public class JSONEditFrame extends JFrame implements TreeSelectionListener {

    /**
     * Using default serial version id.
     */
    private static final long serialVersionUID = 1L;

    private JTextArea jsonTextArea;
    private JSONEditPanel[] treeView = new JSONEditPanel[3];
    private Map<JSONEditPanel.AllowedOps, JButton> treeChangeButtons = new HashMap<JSONEditPanel.AllowedOps, JButton>();
    private int currentIndex = 0;

    /**
     * Constructs the JSONEditFrame. If a parent is specified this component will behave like a dialog, otherwise it
     * will behave like a main application. This affects the default close operation and whether or not the OK,CANCEL
     * buttons are displayed.
     * 
     * @param parent
     *            - parent component of this frame (if applicable may be null)
     * @param title
     *            - title to put in the frame title bar
     * @param initialJson
     *            - initial Json to intialize the frame with (may be null)
     */
    public JSONEditFrame(OKCancelListener parent, String title, String initialJson) {
	super(title);
	Container content = getContentPane();
	content.setLayout(new BorderLayout());

	JPanel northPanel = new JPanel();
	BoxLayout topFillerLayout = new BoxLayout(northPanel, BoxLayout.Y_AXIS);
	northPanel.setLayout(topFillerLayout);
	northPanel.add(Box.createRigidArea(new Dimension(10, 10)));
	content.add(northPanel, BorderLayout.NORTH);

	JPanel eastPanel = new JPanel();
	BoxLayout leftFillerLayout = new BoxLayout(eastPanel, BoxLayout.X_AXIS);
	eastPanel.setLayout(leftFillerLayout);
	eastPanel.add(Box.createRigidArea(new Dimension(10, 10)));
	content.add(eastPanel, BorderLayout.EAST);

	JPanel westPanel = new JPanel();
	BoxLayout rightFillerLayout = new BoxLayout(westPanel, BoxLayout.X_AXIS);
	westPanel.setLayout(rightFillerLayout);
	westPanel.add(Box.createRigidArea(new Dimension(10, 10)));
	content.add(westPanel, BorderLayout.WEST);

	JPanel newCentrePane = new JPanel();
	newCentrePane.setLayout(new BoxLayout(newCentrePane, BoxLayout.X_AXIS));

	for (int loop = 0; loop < 3; loop++) {

	    // Edit panel is the center panel
	    JPanel centerPanel = new JPanel();
	    BoxLayout centerPanelLayout = new BoxLayout(centerPanel, BoxLayout.Y_AXIS);
	    centerPanel.setLayout(centerPanelLayout);
	    JLabel label = new JLabel("JSON Tree View:", SwingConstants.LEFT);
	    label.setAlignmentX(LEFT_ALIGNMENT);
	    centerPanel.add(label);
	    treeView[loop] = new JSONEditPanel();
	    treeView[loop].setJson(initialJson, JSONEditPanel.UpdateType.REPLACE);
	    treeView[loop].setAlignmentX(LEFT_ALIGNMENT);
	    treeView[loop].addTreeSelectionListener(this);
	    centerPanel.add(treeView[loop]);
	    newCentrePane.add(centerPanel, BorderLayout.CENTER);
	}

	content.add(newCentrePane, BorderLayout.CENTER);

	JPanel bottomPanelWrapper = new JPanel();
	BoxLayout wrapperLayout = new BoxLayout(bottomPanelWrapper, BoxLayout.X_AXIS);
	bottomPanelWrapper.setLayout(wrapperLayout);

	JPanel bottomPanel = new JPanel();
	BoxLayout bottomPanelLayout = new BoxLayout(bottomPanel, BoxLayout.Y_AXIS);
	bottomPanel.setLayout(bottomPanelLayout);
	Component rigid = Box.createRigidArea(new Dimension(0, 10));
	bottomPanel.add(rigid);

	JPanel taPanel = new JPanel();
	taPanel.setLayout(new BoxLayout(taPanel, BoxLayout.Y_AXIS));
	taPanel.setAlignmentX(RIGHT_ALIGNMENT);
	JLabel label = new JLabel("JSON Text:", SwingConstants.LEFT);
	label.setAlignmentX(LEFT_ALIGNMENT);
	taPanel.add(label);
	JPanel scrollWrapper = new JPanel();
	scrollWrapper.setLayout(new BoxLayout(scrollWrapper, BoxLayout.X_AXIS));
	jsonTextArea = new JTextArea();
	jsonTextArea.setText(initialJson);
	jsonTextArea.setPreferredSize(new Dimension(600, 100));
	JScrollPane textScroller = new JScrollPane(jsonTextArea);
	textScroller.setAlignmentX(LEFT_ALIGNMENT);
	taPanel.add(textScroller);
	bottomPanel.add(taPanel);
	bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));

	// Add the Buttons
	JPanel buttonPanel = new JPanel();
	BoxLayout horizontalLayout = new BoxLayout(buttonPanel, BoxLayout.X_AXIS);
	buttonPanel.setLayout(horizontalLayout);
	buttonPanel.setAlignmentX(RIGHT_ALIGNMENT);
	JButton button = new JButton(new CopyJsonAction(Direction.REPLACE, treeView, jsonTextArea));
	buttonPanel.add(button);
	treeChangeButtons.put(JSONEditPanel.AllowedOps.REPLACE, button);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

	button = new JButton(new CopyJsonAction(Direction.INSERT, treeView, jsonTextArea));
	treeChangeButtons.put(JSONEditPanel.AllowedOps.INSERT, button);
	buttonPanel.add(button);
	bottomPanel.add(buttonPanel);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

	button = new JButton(new CopyJsonAction(Direction.APPEND, treeView, jsonTextArea));
	treeChangeButtons.put(JSONEditPanel.AllowedOps.APPEND, button);
	buttonPanel.add(button);
	bottomPanel.add(buttonPanel);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

	button = new JButton(new CopyJsonAction(Direction.AS_CHILD, treeView, jsonTextArea));
	treeChangeButtons.put(JSONEditPanel.AllowedOps.AS_CHILD, button);
	buttonPanel.add(button);
	bottomPanel.add(buttonPanel);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

	button = new JButton(new CopyJsonAction(Direction.RENAME, treeView, jsonTextArea));
	buttonPanel.add(button);
	treeChangeButtons.put(JSONEditPanel.AllowedOps.RENAME, button);
	bottomPanel.add(buttonPanel);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

	button = new JButton(new CopyJsonAction(Direction.DELETE, treeView, jsonTextArea));
	buttonPanel.add(button);
	treeChangeButtons.put(JSONEditPanel.AllowedOps.DELETE, button);
	bottomPanel.add(buttonPanel);
	buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

	button = new JButton(new CopyJsonAction(Direction.GET, treeView, jsonTextArea));
	treeChangeButtons.put(JSONEditPanel.AllowedOps.GET_JSON, button);
	buttonPanel.add(button);
	bottomPanel.add(buttonPanel);
	bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));

	if (parent != null) {
	    // Add second row of buttons to bottomPanel
	    buttonPanel = new JPanel();
	    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
	    buttonPanel.setAlignmentX(RIGHT_ALIGNMENT);

	    button = new JButton(new OkCancelAction(this, parent, OKCancelListener.Action.CANCEL));
	    buttonPanel.add(button);
	    buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));

	    button = new JButton(new OkCancelAction(this, parent, OKCancelListener.Action.OK));
	    buttonPanel.add(button);

	    // Add second row to bottomPanel
	    bottomPanel.add(buttonPanel);
	    bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));
	}

	bottomPanelWrapper.add(Box.createRigidArea(new Dimension(10, 10)));
	bottomPanelWrapper.add(bottomPanel);
	bottomPanelWrapper.add(Box.createRigidArea(new Dimension(10, 10)));

	content.add(bottomPanelWrapper, BorderLayout.SOUTH);

	setPreferredSize(new Dimension(680, 480));
	pack();

	if (parent == null) {
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	else {
	    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
	    setAlwaysOnTop(true);
	    addWindowListener(new ExitListener(new OkCancelAction(this, parent, OKCancelListener.Action.DEFAULT_CLOSE)));
	}
	setLocationRelativeTo(null);
	setVisible(true);
	updateButtonStates();
    }

    /**
     * Call this when finished using this frame.
     */
    public void closeWindow() {
	setVisible(false);
	dispose();
    }

    /**
     * Allows the caller to get the JSON from the Tree View.
     * 
     * @return the JSON as represented in the Tree View within the frame.
     */
    public String getJson() {
	return treeView[currentIndex].getJson();
    }

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
	currentIndex = getIndex(treeSelectionEvent);
	System.out.println(currentIndex);
	updateButtonStates();
    }

    private int getIndex(TreeSelectionEvent treeSelectionEvent) {
	int loop = 0;
	for (JSONEditPanel tv : treeView) {
	    if (tv.jTree == treeSelectionEvent.getSource()) {
		return loop;
	    }
	    else {
		loop++;
	    }
	}
	return -1;
    }

    private void updateButtonStates() {
	// Update the button controls to enable and disable tree manipulation
	// operations
	List<JSONEditPanel.AllowedOps> allowedOps = treeView[currentIndex].getAllowedOperations();
	for (Entry<JSONEditPanel.AllowedOps, JButton> entry : treeChangeButtons.entrySet()) {
	    if (allowedOps.contains(entry.getKey())) {
		entry.getValue().setEnabled(true);
	    }
	    else {
		entry.getValue().setEnabled(false);
	    }
	}
    }

    public enum Direction {
	INSERT("Insert", "Insert a node using json in text area to create the node before the selected node."), APPEND(
		"Append", "Put a new node using the json in the text area immediately after selected node."), AS_CHILD(
		"New Child", "Put a new child node into the child list of the node selected."), REPLACE("Replace",
		"Push tree view or selected node with the JSON in the text area."), GET("Get",
		"Get JSON from the tree view or selected tree node into text area."), RENAME("Rename",
		"Rename selected node."), DELETE("Delete", "Delete selected node.");

	final String shortName;
	final String description;

	private Direction(String shortName, String description) {
	    this.shortName = shortName;
	    this.description = description;
	}
    }

    private class CopyJsonAction extends AbstractAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private final Direction direction;
	private final JSONEditPanel[] jEditPanel;
	private final JTextArea jTextArea;

	private final JsonParser parser = new JsonParser();

	private final ThreeWayDiff<JsonElement> twd = new ThreeWayDiff<>(parser.parse("{}"), new DifferFactory() {
	    {
		addApplicator(new JsonElementArray());
		addApplicator(new JsonElementMap());
	    }
	});

	public CopyJsonAction(Direction direction, JSONEditPanel[] treeView, JTextArea jTextArea) {
	    super(direction.shortName);
	    putValue(SHORT_DESCRIPTION, direction.description);
	    this.direction = direction;
	    this.jEditPanel = treeView;
	    this.jTextArea = jTextArea;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	    switch (direction) {
	    case AS_CHILD:
		jEditPanel[currentIndex].setJson(jTextArea.getText(), JSONEditPanel.UpdateType.AS_CHILD);
		break;
	    case APPEND:
		jEditPanel[currentIndex].setJson(jTextArea.getText(), JSONEditPanel.UpdateType.APPEND);
		break;
	    case INSERT:
		jEditPanel[currentIndex].setJson(jTextArea.getText(), JSONEditPanel.UpdateType.INSERT);
		break;
	    case REPLACE:
		jEditPanel[currentIndex].setJson(jTextArea.getText(), JSONEditPanel.UpdateType.REPLACE);
		break;
	    case GET:
		jTextArea.setText(jEditPanel[currentIndex].getJson());
		break;
	    case RENAME:
		jEditPanel[currentIndex].renameNode();
		break;
	    case DELETE:
		jEditPanel[currentIndex].deleteNode();
		break;
	    }

	    jEditPanel[2].setJson(mergeJson(jEditPanel[currentIndex].getJson(), currentIndex),
		    JSONEditPanel.UpdateType.REPLACE);
	}

	private String mergeJson(String json, int index) {
	    twd.addBranchSnapshot(parser.parse(json), index);
	    System.out.println(twd);
	    return new Gson().toJson(twd.apply());
	}
    }

    private static class ExitListener extends WindowAdapter {
	private final OkCancelAction action;

	public ExitListener(OkCancelAction action) {
	    this.action = action;
	}

	@Override
	public void windowClosing(WindowEvent event) {
	    action.actionPerformed(null);
	}
    }

    private static class OkCancelAction extends AbstractAction {
	/**
	 * Default serial version id
	 */
	private static final long serialVersionUID = 1L;

	private final OKCancelListener listener;
	private final OKCancelListener.Action action;
	private final JFrame frame;

	public OkCancelAction(JFrame frame, OKCancelListener listener, OKCancelListener.Action action) {
	    super(action.name());
	    this.action = action;
	    this.listener = listener;
	    this.frame = frame;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	    listener.onFrameAction(action, frame);
	}
    };

}
