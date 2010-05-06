package Kvtml.VocParser.Lessons;

import java.util.Vector;

import javax.swing.tree.TreePath;

import Kvtml.IO.IO;
import Kvtml.VocParser.ListOfWords;
import Kvtml.VocParser.Word;

public class LessonSelection {
	private LessonTree lessonTree;
	private Vector<Integer> allowedWords = new Vector<Integer>();

	/**
	 * constructors
	 * 
	 * @param words
	 *            the list of words where we apply the selection
	 */
	public LessonSelection(LessonTree tree) {
		this.lessonTree = tree;
		// deselect all the lessons
		forbidAllLessons();
	}

	private ListOfWords words() {
		return lessonTree.getWords();
	}

	public TreePath[] getSelectionsPath() {
		Vector<TreePath> ans = new Vector<TreePath>();
		getSelectionsPath_rec(lessonTree, ans);
		TreePath[] array = new TreePath[0];
		return ans.toArray(array);
	}

	private void getSelectionsPath_rec(LessonTree node, Vector<TreePath> paths) {
		if (isSelected(node))
			paths.add(node.getPath());
		for (LessonTree son : node.getChildren())
			getSelectionsPath_rec(son, paths);
	}

	private void setLessonTree(LessonTree tree, boolean value) {
		// debug("setLessonTree(" + tree + ", " + value + ")");
		tree.isSelected = value;
		for (LessonTree son : tree.getChildren())
			setLessonTree(son, value);
	}

	public void allowLessonTree(LessonTree tree) {
		debug("allowLessonTree(" + tree + ")");
		setLessonTree(tree, true);
		generateIndex();
	}

	public void forbidLessonTree(LessonTree tree) {
		debug("forbidLessonTree(" + tree + ")");
		setLessonTree(tree, false);
		generateIndex();
	}

	public void allowAllLessons() {
		debug("allowAllLessons()");
		allowLessonTree(lessonTree);
	}

	public void forbidAllLessons() {
		debug("forbidAllLessons()");
		forbidLessonTree(lessonTree);
	}

	public boolean isSelected(LessonTree lesson) {
		return lesson.isSelected;
	}

	// private Vector<LessonTree> allLessons() {
	// Vector<LessonTree> ans = new Vector<LessonTree>();
	// allLessons(lessonTree, ans);
	// return ans;
	// }
	//
	// private void allLessons(LessonTree node, Vector<LessonTree> ans) {
	// ans.add(node);
	// for (LessonTree child : node.getChildren())
	// allLessons(child, ans);
	// }

	public void setLesson(String lessonName, boolean value) {
		debug("setLesson( \"" + lessonName + "\", " + value + ")");
		setLesson_rec(lessonTree, lessonName, value);
		generateIndex();
	}

	private void setLesson_rec(LessonTree node, String lessonName, boolean value) {
		TreePath path = node.getPath();
		if (path.toString().contains(lessonName)) {
			setLessonTree(node, value);
		} else {
			for (LessonTree child : node.getChildren())
				setLesson_rec(child, lessonName, value);
		}
	}

	/**
	 * generate the list of allowed words
	 */
	public void generateIndex() {
		debug("generateIndex()");
		// clear the vector
		allowedWords.clear();

		// loop through all the words
		for (Word w : words().getWords()) {
			if (isSelected(w.lessonTree))
				allowedWords.add(w.getIndex());
		}
	}

	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("-> LessonSelection :\n");

		TreePath[] paths = getSelectionsPath();
		buffer.append("Allowed lessons : (" + paths.length + ")\n");
		for (TreePath tp : paths)
			buffer.append(" * " + tp + '\n');
		
		buffer.append("Allowed words : (" + allowedWords.size() + ")\n");
		// for (int word_idx : allowedWords)
		// buffer.append(" * " + words().getWord(word_idx) + '\n');
		
		return buffer.toString();
	}

	public static void debug(String s) {
		IO.debug("LessonSelection::" + s);
	}

	/**
	 * some tests
	 */
	public static void main(String[] args) {
		LessonTree tree = new LessonTree(ListOfWords.defaultListOfWords(),
				"root");
		LessonSelection selection = new LessonSelection(tree);
		selection.setLesson("Es", true);
		System.out.println(selection);
	}
}
