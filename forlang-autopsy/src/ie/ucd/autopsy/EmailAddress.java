package ie.ucd.autopsy;

import org.apache.commons.lang3.Validate;

public final class EmailAddress {

	private String address = null;
	private String name = null;

	public EmailAddress() {
		super();
	}

	public EmailAddress(String address, String name) {
		super();
		setAddress(address);
		setName(name);
	}

	public final String getAddress() {
		return address;
	}

	public final String getName() {
		return name;
	}

	public final void setAddress(String address) {
		Validate.notEmpty(address, "email address must have a value");
		this.address = address;
	}

	public final void setName(String name) {
		this.name = name;
	}

	@Override
	public final String toString() {
		return (name == null) ? address : new StringBuilder(name).append(" <").append(address).append(">").toString();
	}
}