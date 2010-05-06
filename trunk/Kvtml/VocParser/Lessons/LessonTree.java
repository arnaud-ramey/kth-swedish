package Kvtml.VocParser.Lessons;

import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

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

	private Vector<Integer> wordsOfLesson;

	protected boolean isSelected;

	/**
	 * constructors
	 * 
	 * @param words
	 *            the list of words where we apply the Tree
	 * @param name
	 *            the name of the lesson
	 */
	public LessonTree(ListOfWords words, String name) {
		this(words, name, true);
	}

	/**
	 * constructors
	 * 
	 * @param words
	 *            the list of words where we apply the Tree
	 * @param name
	 *            the name of the lesson
	 * @param needParse
	 *            <code>true</code> if we need to parse the kvtml file
	 */
	private LessonTree(ListOfWords words, String name, boolean needParse) {
		this.words = words;
		this.name = name;
		node = new DefaultMutableTreeNode(this);
		wordsOfLesson = new Vector<Integer>();
		if (needParse)
			parse();
	}

	/**
	 * parse the kvtml file to find the name of the lessons and the words they
	 * contain
	 */
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

				debug("adding word #" + word_id + " to lesson '"
						+ localTree.getLessonFullName() + "'");
				word.setLessonTree(localTree);
				localTree.wordsOfLesson.add(word.getIndex());
			}
		} // end loop on lines
	} // end parse

	/**
	 * @return the lesson name
	 */
	public String getLessonName() {
		return name;
	}

	/**
	 * @return the tree which is the father
	 */
	protected LessonTree getFather() {
		DefaultMutableTreeNode nodeFather = (DefaultMutableTreeNode) node
				.getParent();
		return (LessonTree) nodeFather.getUserObject();
	}

	/**
	 * @return the {@link Vector} containing all the children
	 */
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

	protected Vector<Integer> getWordsOfLesson() {
		return wordsOfLesson;
	}

	public DefaultMutableTreeNode getNode() {
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

	/**
	 * @return the path from the root to this node
	 */
	public TreePath getPath() {
		return new TreePath(node.getPath());
	}

	public static void debug(String s) {
		IO.debug("LessonTree::" + s);
	}

	// public void TreeDemo() {
	// final JTree tree = new JTree(node);
	// // Where the tree is initialized:
	// tree.getSelectionModel().setSelectionMode(
	// TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	//
	// // Listen for when the selection changes.
	// tree.addTreeSelectionListener(new TreeSelectionListener() {
	// public void valueChanged(TreeSelectionEvent e) {
	// for (TreePath tp : e.getPaths()) {
	// DefaultMutableTreeNode finalNode = (DefaultMutableTreeNode) tp
	// .getLastPathComponent();
	// LessonTree finalLesson = (LessonTree) finalNode
	// .getUserObject();
	//
	// if (e.isAddedPath(tp)) {
	// System.out.println("Added:" + tp);
	//
	// } else {
	// System.out.println("Removed:" + tp);
	// }
	//
	// System.out.println("Final lesson:" + finalLesson);
	// }
	// }
	// });
	//
	// JScrollPane treeView = new JScrollPane(tree);
	// JFrame jf = new JFrame("jframe");
	// jf.add(treeView);
	// jf.pack();
	// jf.setVisible(true);
	// }

	/**
	 * @return the {@link LessonTree} obtained by parsing the default kvtml file
	 */
	public static LessonTree defaultLessonTree() {
		return ListOfWords.defaultListOfWords().getLessonTree();
	}

	/**
	 * some tests
	 */
	public static void main(String[] args) {
		LessonTree tree = defaultLessonTree();
		System.out.println(tree);
	}

}
