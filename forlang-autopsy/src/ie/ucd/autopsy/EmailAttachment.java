package ie.ucd.autopsy;

public final class EmailAttachment {

	private long aTime = 0L;
	private long crTime = 0L;
	private long cTime = 0L;
	private String localPath = "";
	private long mTime = 0L;
	private String name = "";
	private long size = 0L;

	public final long getaTime() {
		return aTime;
	}

	public final long getCrTime() {
		return crTime;
	}

	public final long getcTime() {
		return cTime;
	}

	public final String getLocalPath() {
		return localPath;
	}

	public final long getmTime() {
		return mTime;
	}

	public final String getName() {
		return name;
	}

	public final long getSize() {
		return size;
	}

	public final void setaTime(long aTime) {
		this.aTime = aTime;
	}

	public final void setCrTime(long crTime) {
		this.crTime = crTime;
	}

	public final void setcTime(long cTime) {
		this.cTime = cTime;
	}

	public final void setLocalPath(String localPath) {
		this.localPath = localPath;
	}

	public final void setmTime(long mTime) {
		this.mTime = mTime;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final void setSize(long size) {
		this.size = size;
	}
}