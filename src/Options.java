import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;

import net.miginfocom.swing.MigLayout;

public class Options extends JFrame {

	private JPanel contentPane;
	private final JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);

	/**
	 * Create the frame.
	 */
	public Options() {
		initGUI();
	}

	private void initGUI() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][grow][]", "[][grow][]"));

		contentPane.add(tabbedPane, "cell 1 1,grow");

		tabbedPane.add("General", new General());
	}

}
