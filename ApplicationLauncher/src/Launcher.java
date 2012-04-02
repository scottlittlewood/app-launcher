import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;

import filefilters.ChainedFileFilter;
import filefilters.DirectoryFileFilter;
import filefilters.ExecutableFileFilter;

public class Launcher extends JPanel implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -387007384661880362L;

	static private final String newline = String.format("%n");

	JButton selectApplication, selectFile, launch;
	JTextArea log;
	JFileChooser fc;
	JPanel applicationsPanel;

	String applicationPath;
	String filePath;

	public Launcher() {
		super(new BorderLayout());

		log = new JTextArea(5, 20);
		log.setMargin(new Insets(5, 5, 5, 5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);

		fc = new JFileChooser();
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(new ChainedFileFilter("Apps and dirs", new ExecutableFileFilter(), new DirectoryFileFilter()));

		selectApplication = new JButton("Select Application...");
		selectApplication.addActionListener(this);

		selectFile = new JButton("Select File...");
		selectFile.addActionListener(this);

		launch = new JButton("Launch Application");
		launch.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(selectApplication);
		buttonPanel.add(selectFile);
		buttonPanel.add(launch);

		applicationsPanel = new JPanel();
		applicationsPanel.setLayout(new BoxLayout(applicationsPanel, BoxLayout.PAGE_AXIS));

		JScrollPane appsScrollPane = new JScrollPane(applicationsPanel);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, logScrollPane, appsScrollPane);

		add(buttonPanel, BorderLayout.PAGE_START);
		add(splitPane, BorderLayout.CENTER);

	}

	public void launchApplication(String command, String... parameters) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		StringBuffer commandBuilder = new StringBuffer(command);

		for (String parameter : parameters) {
			commandBuilder.append(" ");
			commandBuilder.append(parameter);
		}

		runtime.exec(commandBuilder.toString());
	}

	public Icon getSystemIconFor(File file) {
		Icon icon = FileSystemView.getFileSystemView().getSystemIcon(file);

		return icon;
	}

	public void actionPerformed(ActionEvent e) {

		// Handle open button action.
		if (e.getSource() == selectApplication) {
			int returnVal = fc.showDialog(this, "Use this application");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				applicationPath = file.getAbsolutePath();

				Icon icon = getSystemIconFor(file);

				JLabel display = new JLabel(file.getAbsolutePath(), icon, SwingConstants.LEFT);
				applicationsPanel.add(display);
				applicationsPanel.revalidate();

				log.append("Chose application: " + file.getName() + "." + newline);
			} else {
				log.append("No Application chosen by user." + newline);
			}
			// Handle save button action.
		} else if (e.getSource() == selectFile) {
			int returnVal = fc.showSaveDialog(this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				log.append("Saving: " + file.getName() + "." + newline);
			} else {
				log.append("Save command cancelled by user." + newline);
			}
		} else if (e.getSource() == launch) {
			log.append("Launching Application: " + applicationPath + " with parameters: " + newline);

			try {
				launchApplication(applicationPath);
			} catch (IOException e1) {
				log.append(e1.getMessage());
			}
		}
		log.setCaretPosition(log.getDocument().getLength());
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	private static void createAndShowGUI() {
		// Create and set up the window.
		final JFrame frame = new JFrame("FileChooserDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		Launcher launcher = new Launcher();

		// Add content to the window.
		frame.add(launcher);

		// Display the window.
		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				UIManager.put("swing.boldMetal", Boolean.FALSE);
				createAndShowGUI();
			}
		});
	}
}
