package group.sit.bcp.properties;

import net.minecraft.util.IStringSerializable;

public enum MultiBlockPart implements IStringSerializable {
	MAIN("main"),
	SUB("sub");

	private final String name;

	private MultiBlockPart(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	public String getString() {
		return this.name;
	}
}
