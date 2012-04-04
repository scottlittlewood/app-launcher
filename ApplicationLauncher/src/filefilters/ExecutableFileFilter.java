package filefilters;

import java.io.File;

public class ExecutableFileFilter extends javax.swing.filechooser.FileFilter {
	public boolean accept(File file) {
		String filename = file.getName();
		return filename.endsWith(".exe");
	}

	public String getDescription() {
		return "*.exe";
	}
}