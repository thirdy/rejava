/* Copyright (C) 2004-2007 Sami Koivu
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package net.sf.rej.gui.tab;

import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import net.sf.rej.files.FileSet;
import net.sf.rej.gui.Link;
import net.sf.rej.gui.SystemFacade;
import net.sf.rej.gui.editor.CaseInsensitiveMatcher;
import net.sf.rej.gui.editor.rendering.PlaintextSyntaxDrawer;
import net.sf.rej.gui.event.Event;
import net.sf.rej.gui.event.EventDispatcher;
import net.sf.rej.gui.event.EventObserver;
import net.sf.rej.gui.event.EventType;
import net.sf.rej.gui.serialized.rendering.SerializedRenderer;
import net.sf.rej.gui.serialized.rendering.SerializedTreeRenderer;
import net.sf.rej.java.serialized.Array;
import net.sf.rej.java.serialized.BlockData;
import net.sf.rej.java.serialized.ClassData;
import net.sf.rej.java.serialized.ClassDesc;
import net.sf.rej.java.serialized.ClassDescImpl;
import net.sf.rej.java.serialized.Content;
import net.sf.rej.java.serialized.ExceptionContent;
import net.sf.rej.java.serialized.FieldDesc;
import net.sf.rej.java.serialized.Null;
import net.sf.rej.java.serialized.SerializedObject;
import net.sf.rej.java.serialized.SerializedStream;
import net.sf.rej.java.serialized.StringContent;
import net.sf.rej.java.serialized.Value;

/**
 * <code>SerializedTab</code> is a GUI tab for displaying the contents of a
 * file containing objects serialized by an ObjectOutputStream.
 *
 * @author Sami Koivu
 */

public class SerializedTab extends JPanel implements Tabbable, EventObserver {
	DefaultMutableTreeNode root = new DefaultMutableTreeNode();

	DefaultTreeModel model = new DefaultTreeModel(root);

	JTree contentsTree = new JTree(model);

	TreeCellRenderer renderer = new SerializedTreeRenderer();

	JLabel noFileOpenLabel = new JLabel("No file open.", SwingConstants.CENTER);

	FileSet fileSet = null;

	CaseInsensitiveMatcher lastSearch = null;

	private String lastQueryString = "";

	EventDispatcher dispatcher;

	public SerializedTab() {
		try {
			this.contentsTree.setCellRenderer(renderer);
			this.noFileOpenLabel.setFont(this.noFileOpenLabel.getFont()
					.deriveFont(16.0f));
			this.setLayout(new BorderLayout());
			this.contentsTree.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getClickCount() == 2) {
						selectFile();
					}
				}
			});

			this.contentsTree.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						selectFile();
					}
				}
			});
			this.add(new JScrollPane(this.contentsTree), BorderLayout.CENTER);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getTabTitle() {
		return "Serialized Contents";
	}

	public void processEvent(Event event) {
		switch (event.getType()) {
		case INIT:
			this.dispatcher = event.getDispatcher();
			break;
		case PROJECT_UPDATE:
			break;
		case SERIALIZED_OPEN:
			repaint();
			break;
		case RAW_OPEN:
		case CLASS_OPEN:
		case CLASS_UPDATE:
		case CLASS_REPARSE:
		case CLASS_PARSE_ERROR:
		case DISPLAY_PARAMETER_UPDATE:
		case DEBUG_ATTACH:
		case DEBUG_DETACH:
		case DEBUG_RESUMED:
		case DEBUG_SUSPENDED:
		case DEBUG_THREAD_CHANGE_REQUESTED:
		case DEBUG_STEP_INTO_REQUESTED:
		case DEBUG_STEP_OUT_REQUESTED:
		case DEBUG_STEP_OVER_REQUESTED:
		case DEBUG_RESUME_REQUESTED:
		case DEBUG_STACK_FRAME_CHANGE_REQUESTED:
		case DEBUG_STACK_FRAME_CHANGED:
		case DEBUG_SUSPEND_REQUESTED:
		case DEBUG_THREAD_CHANGED:
			// do nothing
			break;
		}

		if (event.getType() == EventType.SERIALIZED_OPEN) {
			SerializedStream ss = event.getSerialized();

			this.root = new DefaultMutableTreeNode("Contents");
			this.model.setRoot(this.root);

			for (Content content : ss.getContents()) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(content);
				addChildren(node, content);
				root.add(node);
			}

			// expand the root
			this.contentsTree.expandPath(this.contentsTree.getPathForRow(0));
		}

	}

	private void addChildren(DefaultMutableTreeNode node, Content content) {
		if (content instanceof Array) {

		} else if (content instanceof BlockData) {
		} else if (content instanceof net.sf.rej.java.serialized.Enumeration) {
		} else if (content instanceof ExceptionContent) {
		} else if (content instanceof Null) {
		} else if (content instanceof SerializedObject) {
			SerializedObject so = (SerializedObject) content;

			Map<ClassDesc, ClassData> map = so.getSuperClassData();
			for (Entry<ClassDesc, ClassData> entry : map.entrySet()) {

				List<FieldDesc> fields = entry.getKey().getClassDescInfo().getFields();
				List<Value> values = entry.getValue().getValues();
				for (int i=0; i < fields.size(); i++) {
					FieldDesc field = fields.get(i);
					Value value = values.get(i);
					DefaultMutableTreeNode fieldNode = new DefaultMutableTreeNode(field);
					node.add(fieldNode);
					DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(value);
					fieldNode.add(valueNode);
					if (value.getValue() instanceof Content) {
						addChildren(valueNode, (Content) value.getValue());
					}
				}

				List<Content> contents = entry.getValue().getAnnotations();
				if (contents != null && contents.size() > 0) {
					DefaultMutableTreeNode annotationsNode = new DefaultMutableTreeNode("Annotations");
					node.add(annotationsNode);
					for (Content cnt : contents) {
						DefaultMutableTreeNode contentNode = new DefaultMutableTreeNode(cnt);
						annotationsNode.add(contentNode);
						addChildren(contentNode, cnt);
					}
				}

			}
		} else if (content instanceof StringContent) {
		} else if (content instanceof ClassDescImpl) {
		} else {
			System.out.println("Invalid content type: " + content.getClass());
		}
	}

	 private void selectFile() {
		try {
			TreePath path = this.contentsTree.getSelectionPath();
			DefaultMutableTreeNode node = null;
			if (path != null) {
				node = (DefaultMutableTreeNode) path.getLastPathComponent();
			}
			try {
			FileOutputStream fos = new FileOutputStream("temp");
			if (node != null) {
				Object userObject = node.getUserObject();
				if (userObject instanceof Value) {
					Value value = (Value) userObject;
					if (value.getValue() instanceof Array) {
						Array array = (Array) value.getValue();
						for (Value element : array.getElements()) {
							if (element.getValue() instanceof Byte) {
								fos.write((Byte)element.getValue());
							}
						}
					}
				}
			}
			fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} finally {
		}
	}

	public void redo() {
		SystemFacade.getInstance().performProjectRedo();
	}

	public void undo() {
		SystemFacade.getInstance().performProjectUndo();
	}

	public void insert() {
	}

	public void remove() {
	}

	public void goTo(Link link) {
	}

	public void find() {
		String query = (String) JOptionPane.showInputDialog(this,
				"Search for..", "Search", JOptionPane.QUESTION_MESSAGE, null,
				null, this.lastQueryString);
		if (query == null)
			return; // early return

		this.lastQueryString = query;
		this.lastSearch = new CaseInsensitiveMatcher(query);
		SerializedRenderer renderer = new SerializedRenderer();
		PlaintextSyntaxDrawer sd = new PlaintextSyntaxDrawer();
		java.util.Enumeration en = this.root.breadthFirstEnumeration();
		while (en.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) en
					.nextElement();
			Object obj = node.getUserObject();
			sd.clear();
			renderer.render(sd, obj);
			if (this.lastSearch.matches(sd.getText())) {
				TreePath tp = new TreePath(node.getPath());
				contentsTree.setSelectionPath(tp);
				contentsTree.startEditingAtPath(tp);
				SystemFacade.getInstance().setStatus("Found '" + query + "'.");
				return; // early return
			}
		}

		this.lastSearch = null;
		SystemFacade.getInstance().setStatus(
				"No occurances of '" + query + "' found.");
	}

	public void findNext() {
		if (this.lastSearch == null) {
			find();
		} else {
			java.util.Enumeration en = this.root.breadthFirstEnumeration();
			SerializedRenderer renderer = new SerializedRenderer();
			PlaintextSyntaxDrawer sd = new PlaintextSyntaxDrawer();
			boolean startSearching = false;
			while (en.hasMoreElements()) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) en
						.nextElement();
				if (!startSearching) {
					if (node.equals(this.contentsTree.getSelectionPath()
							.getLastPathComponent())) {
						startSearching = true;
					}
					continue;
				}
				Object obj = node.getUserObject();
				sd.clear();
				renderer.render(sd, obj);
				if (this.lastSearch.matches(sd.getText())) {
					TreePath tp = new TreePath(node.getPath());
					this.contentsTree.setSelectionPath(tp);
					this.contentsTree.startEditingAtPath(tp);
					SystemFacade.getInstance().setStatus("Found '" + this.lastQueryString + "'.");
					return; // early return
				}
			}
			SystemFacade.getInstance().setStatus(
					"No more occurances of '" + this.lastQueryString
							+ "' found.");
		}
	}

	public void outline() {
	}

	public void leavingTab() {
	}

	public void enteringTab() {
	}

}