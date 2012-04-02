
// Structure used to store enumerated languages and code pages.
public class LANGANDCODEPAGE extends com.sun.jna.Structure {
	public short wLanguage;
	public short wCodePage;

	public LANGANDCODEPAGE(com.sun.jna.Pointer p) {
		super(p);
	}
}