package group.sit.bcp.tileentity;


import net.minecraft.util.math.BlockPos;

import group.sit.bcp.bcp;
import net.minecraft.tileentity.TileEntity;

public class MainBlockPosTE extends TileEntity {

	private BlockPos mainBlockPos;

	public MainBlockPosTE() {
		super(bcp.mainBlockPosTEType);
	}

	public BlockPos getMainBlockPos() {
		return mainBlockPos;
	}

	public void setMainBlockPos(BlockPos pos) {
		mainBlockPos = pos;
	}

}
