import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import sun.awt.UNIXToolkit;

public class IconLoader {

	public ImageIcon loadIcon() {
		ImageIcon icon = null;

		try {
			int widgetType = -1;
			String stockId = "gtk-help";
			int direction = 1; // NONE=0 ; LTR=1; RTL=2;
			String detail = null;
			int iconSize = com.sun.java.swing.plaf.gtk.GTKConstants.IconSize.SMALL_TOOLBAR
					.ordinal();

			UNIXToolkit utk = (UNIXToolkit) Toolkit.getDefaultToolkit();
			BufferedImage img = utk.getStockIcon(widgetType, stockId, iconSize,
					direction, detail);
			icon = new ImageIcon(img); // Will throw if img==null

		} catch (NoClassDefFoundError e) {
			// Ok, the API is not available at all.
		} catch (Exception e) {
			// We are not on a GTK system
		} finally {
			// Load a bundled icon here
		}
		return icon;
	}
}