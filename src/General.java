import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class General extends JPanel {
	private final JLabel lblNewLabel = new JLabel("Home Page");
	private final JTextField textField = new JTextField();
	private final JLabel lblPreferredLanguage = new JLabel("Preferred Language");
	private final JComboBox comboBox = new JComboBox();

	/**
	 * Create the panel.
	 */
	public General() {
		textField.setColumns(10);

		initGUI();
	}

	private void initGUI() {
		setLayout(new MigLayout("", "[][][grow][]", "[][][]"));

		add(lblNewLabel, "cell 1 1,alignx left");

		add(textField, "cell 2 1,growx");

		add(lblPreferredLanguage, "cell 1 2,alignx trailing");

		add(comboBox, "cell 2 2,growx");
	}

}
