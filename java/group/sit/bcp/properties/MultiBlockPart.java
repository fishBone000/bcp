package group.sit.bcp.properties;

import net.minecraft.util.StringRepresentable;

public enum MultiBlockPart implements StringRepresentable{
	MAIN("main"),
	SUB("sub");

	private final String name;

	private MultiBlockPart(String pName) {
		this.name = pName;
	}

	public String toString() {
		return this.name;
	}

	public String getSerializedName() {
		return this.name;
	}
}
