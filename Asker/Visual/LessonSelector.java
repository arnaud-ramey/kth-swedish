package Asker.Visual;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import Kvtml.IO.IO;
import Kvtml.VocParser.Lessons.LessonSelection;
import Kvtml.VocParser.Lessons.LessonTree;

public class LessonSelector extends JPanel {
	private static final long serialVersionUID = 1L;

	/** the tree containing the lessons */
	private LessonSelection lessonSelection;

	/** the drawing of the tree */
	private JTree jtree;

	private JTextField infos = new JTextField("");
	private TreeSelectionListener listener = null;

	/**
	 * constructor
	 * 
	 * @param tree
	 */
	public LessonSelector(LessonTree tree) {
		this.lessonSelection = new LessonSelection(tree);
		jtree = new JTree(tree.getNode());
		buildPanel();
	}

	private void refreshSelection() {
		debug("refreshSelection()");
		debug("");
		jtree.removeTreeSelectionListener(listener);
		// jtree.setSelectionPaths(lessonSelection.getSelectionsPathes());
		refreshInfoButton();
		jtree.addTreeSelectionListener(listener);
	}

	private void refreshInfoButton() {
		String text = "";
		text += lessonSelection.getNbAllowedLessons() + " lessons,";
		text += lessonSelection.getNbAllowedWords() + " words";
		infos.setText(text);
	}

	class renderer extends JPanel implements TreeCellRenderer {
		private static final long serialVersionUID = 1L;
		/** swing components */
		protected JCheckBox check = new JCheckBox();
		protected JTextField field = new JTextField();

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean selected, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			LessonTree localTree = (LessonTree) node.getUserObject();
			String text = localTree.getLessonName();

			int depth = node.getLevel();
			int comp = 255 - 30 * depth;
			Color color = new Color(comp, 255, 255);
			field.setText(text);
			field.setBorder(null);
			field.setBackground(color);
			check.setSelected(lessonSelection.isSelected(localTree));

			this.setBackground(color);
			this.removeAll();
			this.setLayout(new GridBagLayout());
			GridBagConstraints c = newConstraints();
			c.gridx = 0;
			this.add(check, c);
			c.gridx = 1;
			this.add(field, c);
			return this;
		}

	}

	public void buildPanel() {
		// Where the tree is initialized:
		jtree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		// Listen for when the selection changes.
		final LessonSelector thisPtr = this;
		listener = new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				for (TreePath tp : e.getPaths()) {
					DefaultMutableTreeNode finalNode = (DefaultMutableTreeNode) tp
							.getLastPathComponent();
					LessonTree finalLesson = (LessonTree) finalNode
							.getUserObject();

					if (e.isAddedPath(tp)) {
						debug("Allowed:" + tp);
						lessonSelection.allowLessonTree(finalLesson);
					} else {
						debug("Forbidden:" + tp);
						lessonSelection.forbidLessonTree(finalLesson);
					}
				}// end loop paths
				thisPtr.refreshSelection();
			} // end valueChanged
		}; // end selection listener

		jtree.setCellRenderer(new renderer());

		/* make the info button */
		infos.setEditable(false);
		infos.setFont(new Font(Font.DIALOG, Font.ITALIC, 14));

		/* add everything */
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = newConstraints();
		c.gridy = 0;
		c.weighty = 1;
		this.add(infos, c);

		c.gridy++;
		c.weighty = 10;
		JScrollPane treeView = new JScrollPane(jtree);
		this.add(treeView, c);

		refreshSelection();
	}

	public static void debug(String s) {
		IO.debug("LessonSelector::" + s);
	}

	private static GridBagConstraints newConstraints() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		return c;
	}

	public static void main(String[] args) {
		LessonSelector ls = new LessonSelector(LessonTree.defaultLessonTree());
		JFrame jf = new JFrame("jframe");
		jf.add(ls);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setSize(800, 600);
		jf.setVisible(true);

	}
}
