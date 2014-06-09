import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;
import javax.swing.JLabel;


public class TabComponentAddTabLayout extends JPanel {

	/**
	 * Create the panel.
	 */
	public TabComponentAddTabLayout() {
		setLayout(new MigLayout("", "[:40px:40px]", "[]"));
		
		JLabel label = new JLabel("+");
		add(label, "cell 0 0,alignx center");

	}

}
