package filefilters;
import java.io.File;

import javax.swing.filechooser.FileFilter;


public class ChainedFileFilter extends javax.swing.filechooser.FileFilter {

	private String name;
	private FileFilter[] filters;

	public ChainedFileFilter(String name, FileFilter... filters) {
		this.name = name;
		this.filters = filters;
	}

	public boolean accept(File file) {
		boolean acceptable = false;
		for (FileFilter filter : filters) {
			acceptable = filter.accept(file);
			if (acceptable)
				return acceptable;
		}
		return acceptable;
	}

	public String getDescription() {
		return name;
	}
}