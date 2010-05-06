package Kvtml.VocParser.Lessons;

import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import Kvtml.IO.IO;
import Kvtml.VocParser.ListOfWords;
import Kvtml.VocParser.Word;

public class LessonTree {
	private static final long serialVersionUID = 1L;

	/**
	 * the user object of the node is in fact a pointer to "this" (the
	 * {@link LessonTree})
	 */
	private DefaultMutableTreeNode node;

	/**
	 * the list of words
	 */
	private ListOfWords words;
	private String name;

	protected boolean isSelected;

	/**
	 * constructors
	 * 
	 * @param words
	 *            the list of words where we apply the Tree
	 */
	public LessonTree(ListOfWords words, String name) {
		this(words, name, true);
	}

	private LessonTree(ListOfWords words, String name, boolean needParse) {
		this.words = words;
		this.name = name;
		node = new DefaultMutableTreeNode(this);
		if (needParse)
			parse();
	}

	private void parse() {
		debug("parse()");
		boolean isStarted = false;
		DefaultMutableTreeNode localPositionInTree = this.node;

		for (int curr_line_index = 0; curr_line_index < words.nbLines(); curr_line_index++) {
			String l = words.getLine(curr_line_index);
			if (l.contains("<lessons>"))
				isStarted = true;
			if (l.contains("</lessons>"))
				break;
			if (!isStarted)
				continue;

			// beginning of lesson -> push the name in the tree
			if (l.contains("<name>")) {
				String lessonName = Word.extractFieldFromAnchors(l, "name");
				debug("new lesson : '" + lessonName + "'");
				LessonTree son = new LessonTree(words, lessonName, false);
				localPositionInTree.add(son.node);
				localPositionInTree = son.node;
			}
			// end of lesson -> remove the name
			if (l.contains("</container>")) {
				debug("end of the lesson.");
				localPositionInTree = (DefaultMutableTreeNode) localPositionInTree
						.getParent();
			}
			// word id -> push the lesson
			if (l.contains("<entry")) {
				String word_id_string = Word.extractFieldFromQuotes(l, "id");
				int word_id = Integer.parseInt(word_id_string);
				Word word = words.getWord(word_id);
				LessonTree localTree = (LessonTree) localPositionInTree
						.getUserObject();
				word.lessonTree = localTree;
				debug("adding word #" + word_id + " to lesson '"
						+ localTree.getLessonFullName() + "'");
			}
		} // end loop on lines
	} // end parse

	/**
	 * @return the lesson name
	 */
	public String getLessonName() {
		return name;
	}

	protected LessonTree getFather() {
		DefaultMutableTreeNode nodeFather = (DefaultMutableTreeNode) node
				.getParent();
		return (LessonTree) nodeFather.getUserObject();
	}

	protected Vector<LessonTree> getChildren() {
		Vector<LessonTree> ans = new Vector<LessonTree>();
		for (int child_idx = 0; child_idx < node.getChildCount(); child_idx++) {
			DefaultMutableTreeNode son = (DefaultMutableTreeNode) node
					.getChildAt(child_idx);
			ans.add((LessonTree) son.getUserObject());
		}
		return ans;
	}
	
	protected ListOfWords getWords() {
		return words;
	}

	protected DefaultMutableTreeNode getNode() {
		return node;
	}

	/**
	 * @return the lesson full name (with the parents name before)
	 */
	public String getLessonFullName() {
		if (node.getParent() == null)
			return getLessonName();
		else
			return getFather().getLessonFullName() + "/" + getLessonName();
	}

	public String toString() {
		return getLessonName();
	}

	public static void debug(String s) {
		IO.debug("LessonTree::" + s);
	}

	public TreePath getPath() {
		return new TreePath( node.getPath() );
	}

	public static void testTrees() {
		DefaultMutableTreeNode tree = new DefaultMutableTreeNode();
		DefaultMutableTreeNode son = new DefaultMutableTreeNode();
		tree.add(son);
		System.out.println(tree.getDepth());
	}

	public void TreeDemo() {
		final JTree tree = new JTree(node);
		// Where the tree is initialized:
		tree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				for (TreePath tp : e.getPaths()) {
					DefaultMutableTreeNode finalNode = (DefaultMutableTreeNode) tp
							.getLastPathComponent();
					LessonTree finalLesson = (LessonTree) finalNode
							.getUserObject();

					if (e.isAddedPath(tp)) {
						System.out.println("Added:" + tp);

					} else {
						System.out.println("Removed:" + tp);
					}

					System.out.println("Final lesson:" + finalLesson);
				}
			}
		});

		JScrollPane treeView = new JScrollPane(tree);
		JFrame jf = new JFrame("jframe");
		jf.add(treeView);
		jf.pack();
		jf.setVisible(true);
	}

	/**
	 * some tests
	 */
	public static void main(String[] args) {
		// testTrees();

		LessonTree tree = new LessonTree(ListOfWords.defaultListOfWords(),
				"root");
		System.out.println(tree);
		// tree.TreeDemo();
	}

}
