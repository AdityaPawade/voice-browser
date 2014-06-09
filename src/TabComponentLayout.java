import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JButton;


public class TabComponentLayout extends JPanel {

	/**
	 * Create the panel.
	 */
	public TabComponentLayout() {
		setLayout(new MigLayout("", "[grow][]", "[]"));
		
		JLabel lblNewTab = new JLabel("New Tab");
		add(lblNewTab, "cell 0 0,alignx center");
		
		JButton btnX = new JButton("X");
		add(btnX, "cell 1 0");

	}

}
