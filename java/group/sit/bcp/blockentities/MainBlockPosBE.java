package group.sit.bcp.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;

import group.sit.bcp.bcp;

public class MainBlockPosBE extends BlockEntity {

	// private static Logger LOGGER = LogManager.getLogger();

	private BlockPos mainBlockPos;

	public MainBlockPosBE(BlockPos pPos, BlockState pState) {
		super(bcp.mainBlockPosBEType, pPos, pState);
	}

	public BlockPos getMainBlockPos() {
		return mainBlockPos;
	}

	public void setMainBlockPos(BlockPos pos) {
		mainBlockPos = pos;
	}

	protected void saveAdditional(CompoundTag nbt) {
		super.saveAdditional(nbt);
		if(mainBlockPos != null) {
			nbt.putInt("mBPx", mainBlockPos.getX());
			nbt.putInt("mBPy", mainBlockPos.getY());
			nbt.putInt("mBPz", mainBlockPos.getZ());
		}else
			throw new RuntimeException("MainBlockTE's mainBlockPos is null, which is at pos " + this.getBlockPos().toString() + ". This is a bug!");
	}

	public void load(CompoundTag nbt) {
		super.load(nbt);
		if(nbt.contains("mBPx") && nbt.contains("mBPy") && nbt.contains("mBPz")) {
			mainBlockPos = new BlockPos(nbt.getInt("mBPx"), nbt.getInt("mBPy"), nbt.getInt("mBPz"));
		}else
			throw new RuntimeException(nbt.toString() + "doesn't has desired mainBlockPos. This is a bug!");
	}

}
