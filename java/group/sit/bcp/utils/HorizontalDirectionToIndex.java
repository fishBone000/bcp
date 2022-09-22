package group.sit.bcp.utils;

import net.minecraft.core.Direction;

public class HorizontalDirectionToIndex {

	public static int map(Direction direction) {
		switch(direction) {
			case NORTH:
				return 0;
			case EAST:
				return 1;
			case SOUTH:
				return 2;
			case WEST:
				return 3;
			default:
				throw new IllegalArgumentException("Horizontal direction only. ");
		}
	}

}
