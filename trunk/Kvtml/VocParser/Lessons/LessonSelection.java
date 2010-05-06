package Kvtml.VocParser.Lessons;

import java.util.Observable;
import java.util.Vector;

import javax.swing.tree.TreePath;

import Kvtml.IO.IO;
import Kvtml.VocParser.ListOfWords;
import Kvtml.VocParser.Word;

public class LessonSelection extends Observable {
	private LessonTree lessonTree;
	private Vector<Integer> allowedWords = new Vector<Integer>();
	private int nbAlllowedLessons = 0;

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

	public TreePath[] getSelectionsPathes() {
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
		debug("setLessonTree(" + tree + ", " + value + ")");
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

	public void switchLessonTree(LessonTree tree) {
		if (isSelected(tree))
			forbidLessonTree(tree);
		else
			allowLessonTree(tree);
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

	public boolean areAllLessonsForbidden() {
		return (getNbAllowedLessons() == 0);
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

	/**
	 * @return the number of allowed lessons
	 */
	public int getNbAllowedLessons() {
		return nbAlllowedLessons;
	}

	/**
	 * @return the size of words in the selection
	 */
	public int getNbAllowedWords() {
		return allowedWords.size();
	}

	/**
	 * @return all the allowed words
	 */
	public Vector<Word> getWords() {
		Vector<Word> ans = new Vector<Word>();
		for (int i : allowedWords)
			ans.add(words().getWord(i));
		return ans;
	}

	/**
	 * @return a random {@link Word} in the selection
	 */
	public Word getRandomWord() {
		int wordIdxInSelection = (int) (Math.random() * allowedWords.size());
		int wordIdxInKvtml = allowedWords.elementAt(wordIdxInSelection);
		return words().getWord(wordIdxInKvtml);
	}

	/**
	 * @param lessonName
	 *            a {@link String} contained in the name of the lessons to
	 *            change of status
	 */
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
	private void generateIndex() {
		debug("generateIndex()");
		setChanged();
		notifyObservers();

		// clear the vector
		allowedWords.clear();
		nbAlllowedLessons = 0;

		// recursively add all the words
		generateIndex_rec(lessonTree);
	}

	/**
	 * recusrsively generate the list of allowed words
	 */
	private void generateIndex_rec(LessonTree node) {
		if (isSelected(node)) {
			// System.out.println("Allowed :" + node.getWordsOfLesson().size());
			nbAlllowedLessons++;
			allowedWords.addAll(node.getWordsOfLesson());
		}

		for (LessonTree child : node.getChildren())
			generateIndex_rec(child);
	}

	/**
	 * a fancy toString() routine
	 */
	public String toString() {
		StringBuilder buffer = new StringBuilder();
		buffer.append("-> LessonSelection :\n");

		buffer.append("Allowed lessons : (" + getNbAllowedLessons() + ")\n");
		TreePath[] paths = getSelectionsPathes();
		for (TreePath tp : paths)
			buffer.append(" * " + tp + '\n');

		buffer.append("Allowed words : (" + getNbAllowedWords() + ")\n");
		// for (Word w : getWords())
		// buffer.append(" * " + w + '\n');

		return buffer.toString();
	}

	public static void debug(String s) {
		IO.debug("LessonSelection::" + s);
	}

	/**
	 * @param allowAll
	 *            if <code>true</code>, will allow all the lessons. if
	 *            <code>false</code>, will forbid all the lessons
	 * @return the default {@link LessonSelection}, obtained by parsing the
	 *         default kvtml file and selecting everything
	 */
	public static LessonSelection defaultLessonSelection(boolean allowAll) {
		LessonSelection lessonSelection = new LessonSelection(LessonTree
				.defaultLessonTree());
		if (allowAll)
			lessonSelection.allowAllLessons();
		return lessonSelection;
	}

	/**
	 * some tests
	 */
	public static void main(String[] args) {
		LessonSelection selection = defaultLessonSelection(false);
		selection.setLesson("Es", true);
		System.out.println(selection);
		for (int i = 0; i < 10; i++) {
			System.out.println(selection.getRandomWord().toString_onlyWords());
		}
	}
}
