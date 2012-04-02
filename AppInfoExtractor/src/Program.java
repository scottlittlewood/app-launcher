import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

class WindowEventHandler extends WindowAdapter {
	public void windowClosing(WindowEvent evt) {
		System.exit(0);
	}
}

public class Program extends JFrame {

	public Program(String title) {
		super(title);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Program program = new Program("Spike Program");

		final JButton searchButton = new JButton("Search");
		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(searchButton, BorderLayout.NORTH);
		
		program.add(panel);
		
		IconLoader loader = new IconLoader();
		
		showIcon(panel, loader.loadIcon(), "Some text");
		
		searchButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.showDialog(searchButton, "Select an application");
				File selectedFile = chooser.getSelectedFile();
				String path = selectedFile.getAbsolutePath();
				Icon icon =  chooser.getIcon(selectedFile);
				
				showIcon(panel, icon, path);
			}
		});

		program.addWindowListener(new WindowEventHandler());
		program.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		program.setSize(350, 200); // width=350, height=200
		program.setVisible(true); // Display the frame
	}
	
	public static void showIcon(Container container, Icon icon, String text)
	{
		JLabel displayLabel = new JLabel(text,icon, SwingConstants.LEFT);
		container.add(displayLabel, BorderLayout.SOUTH);
	}
}
