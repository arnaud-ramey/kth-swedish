package Asker.Visual;

import java.applet.Applet;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class AskerApplet extends Applet{
	private static final long serialVersionUID = 1L;

	public VisualAsker panel = new VisualAsker();

	public void init() {
		super.init();
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.fill = GridBagConstraints.BOTH;
		this.add(panel,c);
		
		this.setSize(600, 200);
	}
}
