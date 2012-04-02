
public class SemanticVersion {
	private short major;
	private short minor;
	private short revision;
	private short build;

	public SemanticVersion(short major, short minor, short revision, short build) {
		this.major = major;
		this.minor = minor;
		this.revision = revision;
		this.build = build;
	}

	public short getMajor() {
		return major;
	}

	public short getMinor() {
		return minor;
	}

	public short getRevision() {
		return revision;
	}

	public short getBuild() {
		return build;
	}
	
	@Override
	public String toString() {
		return "Version [major=" + major + ", minor=" + minor + ", revision=" + revision + ", build=" + build + "]";
	}
}