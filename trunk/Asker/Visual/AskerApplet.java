package Asker.Visual;

import java.applet.Applet;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import Asker.Visual.VisualAsker;

public class AskerApplet extends Applet {
	private static final long serialVersionUID = 1L;

	public void init() {
		super.init();

		VisualAsker panel = new VisualAsker();

		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;

		this.add(panel, c);
		this.setSize(800, 300);
		this.repaint();
	}
}
