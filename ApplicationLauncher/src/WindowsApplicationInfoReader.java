import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.VerRsrc.VS_FIXEDFILEINFO;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class WindowsApplicationInfoReader {
	public static Pointer allocateBuffer(int size) {
		byte[] bufferarray = new byte[size];
		Pointer buffer = new Memory(bufferarray.length);

		return buffer;
	}

	public static void log(Object obj) {
		System.out.println(String.format("%s: %s", DateTime.getUtcNowAsString(), obj.toString()));
	}

	public static void main(String[] args) throws IOException {

		log("Application Started");
		String systemRoot = System.getenv("SystemRoot");
        File notepad = new File(systemRoot + "\\notepad.exe");
        
		String applicationPath ="C:\\Program Files\\Internet Explorer\\iexplore.exe";


		IntByReference dwDummy = new IntByReference(0);
		int fileVersionInfoSize = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfoSize(applicationPath, dwDummy);

		SemanticVersion version = getFileVersion(applicationPath, fileVersionInfoSize);

		log(version);

		String translation = getTranslation(applicationPath, fileVersionInfoSize);

		String[] strVersionInfo = new String[8];
		strVersionInfo[0] = "CompanyName";
		strVersionInfo[1] = "FileDescription";
		strVersionInfo[2] = "FileVersion";
		strVersionInfo[3] = "InternalName";
		strVersionInfo[4] = "LegalCopyright";
		strVersionInfo[5] = "OriginalFileName";
		strVersionInfo[6] = "ProductName";
		strVersionInfo[7] = "ProductVersion";

		for (String itemName : strVersionInfo) {
			String value = getFilePropertyValue(applicationPath, fileVersionInfoSize, translation, itemName);
			log(itemName + ": '" + value + "'");
		}

		promptAndWait();

		log("Thank you and Goodbye!");
	}

	private static void promptAndWait() throws IOException {
		System.out.print("Press any key to quit...");
		System.in.read();
	}

	private static String getTranslation(String applicationPath, int fileVersionInfoSize) {
		Pointer lpData = allocateBuffer(fileVersionInfoSize);

		boolean fileInfoStatus = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfo(applicationPath, 0, fileVersionInfoSize, lpData);

		//log("fileInfoStatus-ver:" + fileInfoStatus);

		String queryPath = "\\VarFileInfo\\Translation";

		PointerByReference lplpBuffer = new PointerByReference();
		IntByReference puLen = new IntByReference();

		boolean verQueryVal = ExecuteQuery(lpData, queryPath, lplpBuffer, puLen);

		//log("verQueryVal-ver:" + verQueryVal);
		//log("puLen = " + puLen.getValue());

		LANGANDCODEPAGE lplpBufStructure = new LANGANDCODEPAGE(lplpBuffer.getValue());
		lplpBufStructure.read();

		//log("Code Page: " + lplpBufStructure.wCodePage);
		//log("Language: " + lplpBufStructure.wLanguage);

		StringBuilder hexBuilder = new StringBuilder();

		String languageAsHex = String.format("%04x", lplpBufStructure.wLanguage);
		String codePageAsHex = String.format("%04x", lplpBufStructure.wCodePage);

		hexBuilder.append(languageAsHex);
		hexBuilder.append(codePageAsHex);

		//log("Hex builder output: " + hexBuilder.toString());

		return hexBuilder.toString();
	}

	/**
	 * Extracts the information item with key @propertyKey from the application properties found in @applicationPath
	 * @param applicationPath
	 * @param fileVersionInfoSize
	 * @param translation
	 * @param propertyKey
	 * @return
	 */
	private static String getFilePropertyValue(String applicationPath, int fileVersionInfoSize, String translation, String propertyKey) {
		Pointer lpData = allocateBuffer(fileVersionInfoSize);

		boolean fileInfoStatus = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfo(applicationPath, 0, fileVersionInfoSize, lpData);

		String queryPath = "\\StringFileInfo\\" + translation + "\\" + propertyKey;

		PointerByReference lplpBuffer = new PointerByReference();
		IntByReference puLen = new IntByReference();

		boolean verQueryVal = ExecuteQuery(lpData, queryPath, lplpBuffer, puLen);

		int descLength = puLen.getValue();

		Pointer pointer = lplpBuffer.getValue();
		char[] buffer = pointer.getCharArray(0, descLength);

		String value = new String(buffer);

		return value;
	}

	private static SemanticVersion getFileVersion(String applicationPath, int fileVersionInfoSize) {
		Pointer lpData = allocateBuffer(fileVersionInfoSize);

		boolean fileInfoStatus = com.sun.jna.platform.win32.Version.INSTANCE.GetFileVersionInfo(applicationPath, 0, fileVersionInfoSize, lpData);

		//log("fileInfoStatus-ver:" + fileInfoStatus);

		String queryPath = "\\";

		PointerByReference lplpBuffer = new PointerByReference();
		IntByReference puLen = new IntByReference();

		boolean verQueryVal = ExecuteQuery(lpData, queryPath, lplpBuffer, puLen);

		//log("verQueryVal-ver:" + verQueryVal);

		return extractVersionInfo(lplpBuffer);
	}

	private static SemanticVersion extractVersionInfo(PointerByReference lplpBuffer) {
		VS_FIXEDFILEINFO lplpBufStructure = new VS_FIXEDFILEINFO(lplpBuffer.getValue());
		lplpBufStructure.read();

		short major = (short) (lplpBufStructure.dwFileVersionMS.intValue() >> 16);
		short minor = (short) (lplpBufStructure.dwFileVersionMS.intValue() & 0xffff);
		short revision = (short) (lplpBufStructure.dwFileVersionLS.intValue() >> 16);
		short build = (short) (lplpBufStructure.dwFileVersionLS.intValue() & 0xffff);

		SemanticVersion version = new SemanticVersion(major, minor, revision, build);
		return version;
	}

	public static boolean ExecuteQuery(Pointer lpData, String lpSubBlock, PointerByReference lplpBuffer, IntByReference puLen) {
		return com.sun.jna.platform.win32.Version.INSTANCE.VerQueryValue(lpData, lpSubBlock, lplpBuffer, puLen);
	}

	public static class DateTime {
		public static String getUtcNowAsString() {
			final SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			final String utcTime = dateFormat.format(new Date());

			return utcTime;
		}

		static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss:SSS zzz";

		public static Date GetUTCdatetimeAsDate() {
			return StringDateToDate(getUtcNowAsString());
		}

		public static Date StringDateToDate(String StrDate) {
			Date dateToReturn = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

			try {
				dateToReturn = (Date) dateFormat.parse(StrDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}

			return dateToReturn;
		}
	}

}