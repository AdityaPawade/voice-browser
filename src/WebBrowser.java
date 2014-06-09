import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.sound.sampled.AudioFileFormat;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import net.miginfocom.swing.MigLayout;

import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.GoogleResponse;
import com.darkprograms.speech.recognizer.Recognizer;
import com.darkprograms.speech.synthesiser.Synthesiser;

import darkprograms.speech.util.AePlayWave;

public class WebBrowser implements ActionListener, ChangeListener {

	private JFrame frame;
	private final JTextField addressBar = new JTextField();
	private final JProgressBar progressBar = new JProgressBar();
	private final JTabbedPane TPViewPort = new JTabbedPane(JTabbedPane.TOP);
	private final JButton btnMenu = new JButton("Menu");
	private final JPopupMenu optionsMenu = new JPopupMenu();
	private final JMenuItem mntmNewTab = new JMenuItem("New Tab");
	private final JMenuItem mntmExit = new JMenuItem("Exit");
	private final JMenuItem mntmOptions = new JMenuItem("Options");
	private final JMenuItem mntmHelp = new JMenuItem("Help");
	private final Options options = new Options();
	int selectedIndex;
	boolean addTabFlag = true;
	JButton closeButton;
	private final JButton btnGo = new JButton("Go");
	ViewPortLayout activeViewPort;
	private URL url;
	private String urlVal;
	private ArrayList<URL> addresses;
	private final JButton btnVoice = new JButton("Voice");
	private final JButton btnSpeak = new JButton("Speak");
	private final JTextField textFieldSpoken = new JTextField();
	private String spokenText = "", responseText, languageCode = "en-US",
			fileLocation;
	Player playerSpeak;
	Synthesiser synthesiser;
	private final JLabel lblStatus = new JLabel("Status - ");
	private final JButton btnPlay = new JButton("Play");
	protected Microphone microphone = new Microphone(AudioFileFormat.Type.WAVE);
	protected AePlayWave aePlayWave;
	boolean canStop = false;
	String workingDir = System.getProperty("user.dir");

	String your_os;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					try {
						UIManager.setLookAndFeel(UIManager
								.getSystemLookAndFeelClassName());
					} catch (Exception unused) {
						;
						// Ignore exception because we can't do anything. Will
						// use default.
					}
					/*JEditorPane.registerEditorKitForContentType("text/html",
							"com.inet.html.InetHtmlEditorKit");*/
					WebBrowser window = new WebBrowser();
					window.frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public WebBrowser() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		textFieldSpoken.setColumns(10);

		options.setVisible(false);

		addressBar.setColumns(10);
		frame = new JFrame();
		frame.setBounds(100, 100, 900, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(
				new MigLayout("", "[][][][][grow][grow][][][50px:n:100px][]",
						"[][][grow][]"));

		frame.getContentPane().add(btnMenu, "cell 1 0,growx");

		addPopup(btnMenu, optionsMenu);

		optionsMenu.add(mntmNewTab);

		optionsMenu.add(mntmOptions);

		optionsMenu.add(mntmHelp);

		optionsMenu.add(mntmExit);
		addresses = new ArrayList<URL>();
		mntmOptions.addActionListener(this);
		mntmNewTab.addActionListener(this);

		frame.getContentPane().add(btnVoice, "cell 2 0");

		frame.getContentPane().add(btnSpeak, "cell 3 0");

		frame.getContentPane().add(textFieldSpoken, "cell 4 0,growx");

		frame.getContentPane().add(lblStatus, "cell 5 0,growx");

		frame.getContentPane().add(btnPlay, "cell 6 0");

		frame.getContentPane().add(addressBar, "cell 1 1 6 1,growx");

		frame.getContentPane().add(btnGo, "cell 7 1");

		frame.getContentPane().add(progressBar, "cell 8 1");

		frame.getContentPane().add(TPViewPort, "cell 1 2 8 1,grow");

		TabComponentLayout tab = new TabComponentLayout();
		closeButton = ((JButton) tab.getComponent(1));
		closeButton.addActionListener(this);
		TPViewPort.addTab("New Tab", new ViewPortLayout());
		TPViewPort.setTabComponentAt(0, tab);
		urlVal = "http://www.google.com";
		try {
			url = new URL(urlVal);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		addresses.add(url);
		addressBar.setText(urlVal);
		TPViewPort.addTab("Add Tab", null);
		TPViewPort.setTabComponentAt(1, new TabComponentAddTabLayout());
		TPViewPort.addChangeListener(this);
		btnGo.addActionListener(this);
		btnMenu.addActionListener(this);
		btnSpeak.addActionListener(this);
		btnVoice.addActionListener(this);
		btnPlay.addActionListener(this);
		your_os = System.getProperty("os.name").toLowerCase();
		if (your_os.indexOf("win") >= 0) {
			fileLocation = workingDir + "\\" + "temp.dat";
		} else if (your_os.indexOf("nix") >= 0 || your_os.indexOf("nux") >= 0) {
			fileLocation = workingDir + "/" + "temp.dat";
		} else {
			fileLocation = workingDir + "{others}" + "temp.dat";
		}
	}

	public void execCommand(String responseText) {
		if (responseText.contains("new tab")) {
			addTab(false);
		}
		if (responseText.contains("close tab")) {
			closeTab(true, null);
		}
		if (responseText.contains("goto")) {
			int index = responseText.indexOf("www");
			String urlVal = responseText.substring(index);

			try {
				if (!urlVal.contains("https://") || !urlVal.contains("http://"))
					urlVal = "http://" + urlVal;

				System.out.println(urlVal);
				System.out.println(selectedIndex);
				url = new URL(urlVal);

				if (addresses.size() < selectedIndex)
					addresses.add(url);
				else
					addresses.add(selectedIndex, url);

				activeViewPort = (ViewPortLayout) TPViewPort
						.getComponentAt(selectedIndex);
				activeViewPort.editorPane.setPage(url);
				// activeViewPort.editorPane.setText("<h1>Sorry The Page Can Not Be Displayed</h1>");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				activeViewPort = (ViewPortLayout) TPViewPort
						.getComponentAt(selectedIndex);
				activeViewPort.editorPane
						.setText("<h1>Sorry The Page Can Not Be Displayed</h1>");
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// System.out.println("Btn Click");
		if (arg0.getSource() == btnMenu) {
			optionsMenu.setVisible(true);
		} else if (arg0.getSource() == mntmOptions) {
			options.setVisible(true);
		} else if (arg0.getSource() == mntmNewTab) {
			addTab(true);
		} else if (arg0.getSource() == btnPlay) {
			aePlayWave = new AePlayWave(fileLocation);
			aePlayWave.start();
			new Thread(new PlayState()).start();
		} else if (arg0.getSource() == btnVoice) {
			if (canStop) {
				microphone.close();
				btnVoice.setText("Voice");
				// Recognize
				textFieldSpoken.setEditable(false);
				new Runnable() {
					@Override
					public void run() {
						Recognizer recognizer = new Recognizer();
						try {
							GoogleResponse googleResponse = recognizer
									.getRecognizedDataForWave(fileLocation,
											languageCode);
							responseText = googleResponse.getResponse();
							textFieldSpoken
									.setText("You Said: " + responseText);
							setStatus("Confidence "
									+ googleResponse.getConfidence());
							execCommand(responseText);

						} catch (Exception ex) {
							ex.printStackTrace();
						}
						canStop = false;
					}
				}.run();

			} else {
				canStop = true;
				try {
					microphone.captureAudioToFile(fileLocation);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				btnVoice.setText("Stop Recording");
			}

			textFieldSpoken.setEditable(true);
		} else if (arg0.getSource() == btnSpeak) {
			spokenText = textFieldSpoken.getText();
			synthesiser = new Synthesiser();
			setStatus("Speaking..");
			new Runnable() {

				@Override
				public void run() {
					try {

						playerSpeak = new Player(
								synthesiser.getMP3Data(spokenText));
						playerSpeak.play();

					} catch (JavaLayerException e) {
						e.printStackTrace();
					} catch (Exception e) {

						e.printStackTrace();
					}
					setStatus("");
				}
			}.run();

		} else if (arg0.getSource() == btnGo) {
			selectedIndex = TPViewPort.getSelectedIndex();
			if (selectedIndex == TPViewPort.getComponentCount())
				selectedIndex = selectedIndex - 1;
			urlVal = addressBar.getText();

			try {
				if (!urlVal.contains("https://") || !urlVal.contains("http://"))
					urlVal = "http://" + urlVal;

				System.out.println(urlVal);
				System.out.println(selectedIndex);
				url = new URL(urlVal);

				if (addresses.size() < selectedIndex)
					addresses.add(url);
				else
					addresses.add(selectedIndex, url);

				activeViewPort = (ViewPortLayout) TPViewPort
						.getComponentAt(selectedIndex);
				activeViewPort.editorPane.setPage(url);
				// activeViewPort.editorPane.setText("<h1>Sorry The Page Can Not Be Displayed</h1>");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				activeViewPort = (ViewPortLayout) TPViewPort
						.getComponentAt(selectedIndex);
				activeViewPort.editorPane
						.setText("<h1>Sorry The Page Can Not Be Displayed</h1>");
			}
		} else {
			// Close Button Has To be last for safety
			closeTab(false, arg0);
		}
	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}

	@Override
	public void stateChanged(ChangeEvent event) {
		JTabbedPane tabbedPane = (JTabbedPane) event.getSource();
		addressBar.setText("");
		selectedIndex = tabbedPane.getSelectedIndex();
		if (selectedIndex == tabbedPane.getComponentCount() - 1
				&& addTabFlag == true) {
			// System.out.println(addTabFlag);
			addTab(false);
		} else if (addresses.size() > selectedIndex) {
			System.out.println(selectedIndex);
			System.out.println(tabbedPane.getComponentCount() - 1);
			addressBar.setText(addresses.get(selectedIndex).toString());
		}

	}

	public void addTab(boolean button) {
		TabComponentLayout tab = new TabComponentLayout();
		closeButton = ((JButton) tab.getComponent(1));
		closeButton.addActionListener(this);
		System.out.println("Entering");
		if (button == true) {
			addTabFlag = false;
			TPViewPort.insertTab("New Tab", null, new ViewPortLayout(),
					"New Tab", TPViewPort.getComponentCount() - 1);
			TPViewPort.setTabComponentAt(TPViewPort.getComponentCount() - 2,
					tab);
			TPViewPort.setSelectedIndex(TPViewPort.getComponentCount() - 2);
			// System.out.println("1");
			addTabFlag = true;

		} else {
			addTabFlag = false;

			TPViewPort.insertTab("New Tab", null, new ViewPortLayout(),
					"New Tab", TPViewPort.getComponentCount() - 1);
			TPViewPort.setTabComponentAt(TPViewPort.getComponentCount() - 2,
					tab);
			TPViewPort.setSelectedIndex(TPViewPort.getComponentCount() - 2);
			addTabFlag = true;
			System.out.println("2   " + (TPViewPort.getComponentCount() - 1));
		}

	}

	public void closeTab(boolean button, ActionEvent arg0) {
		if (button == true) {
			if (selectedIndex != TPViewPort.getComponentCount() - 1
					&& TPViewPort.getComponentCount() != 2) {
				TPViewPort.remove(TPViewPort.getSelectedIndex());
				TPViewPort.setSelectedIndex(TPViewPort.getComponentCount() - 2);
			}
		} else {
			if (TPViewPort.getComponentCount() != 2) {
				closeButton = (JButton) arg0.getSource();
				closeButton.removeActionListener(this);
				TabComponentLayout component = ((TabComponentLayout) closeButton
						.getParent());
				TPViewPort.remove(TPViewPort.indexOfTabComponent(component));
				TPViewPort.setSelectedIndex(TPViewPort.getComponentCount() - 2);
			}
		}
	}

	public void setStatus(String status) {
		lblStatus.setText("Status - " + status);
	}

	protected class PlayState implements Runnable {

		@Override
		public void run() {
			while (aePlayWave.isAlive()) {
				try {
					Thread.sleep(200);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
			setStatus("Waiting...");
		}
	}
}
