package group.sit.bcp.tileentity;


import net.minecraft.util.math.BlockPos;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import group.sit.bcp.bcp;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

public class MainBlockPosTE extends TileEntity {

	private static Logger LOGGER = LogManager.getLogger();

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

	public CompoundNBT write(CompoundNBT nbt) {
		nbt = super.write(nbt);
		if(mainBlockPos != null) {
			nbt.putInt("mBPx", mainBlockPos.getX());
			nbt.putInt("mBPy", mainBlockPos.getY());
			nbt.putInt("mBPz", mainBlockPos.getZ());
			LOGGER.debug("Writing CompoundNBT: " + nbt.toString());
			return nbt;
		}else
			throw new RuntimeException("MainBlockTE's mainBlockPos is null, which is at pos " + this.getPos().toString() + ". This is a bug!");
	}

	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		if(nbt.contains("mBPx") && nbt.contains("mBPy") && nbt.contains("mBPz")) {
			mainBlockPos = new BlockPos(nbt.getInt("mBPx"), nbt.getInt("mBPy"), nbt.getInt("mBPz"));
		}else
			throw new RuntimeException(nbt.toString() + "doesn't has desired mainBlockPos. This is a bug!");
	}

}
