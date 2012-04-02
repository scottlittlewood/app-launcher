package filefilters;
import java.io.File;


public class DirectoryFileFilter extends javax.swing.filechooser.FileFilter {
	public boolean accept(File file) {
		return (file.isDirectory());
	}

	public String getDescription() {
		return "Directory";
	}
}