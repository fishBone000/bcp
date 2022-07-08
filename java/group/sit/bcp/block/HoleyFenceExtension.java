package group.sit.bcp.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HoleyFenceExtension extends HoleyFenceNode {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public HoleyFenceExtension(AbstractBlock.Properties properties) {
		super(0F, 1.0F, 16.0F, 25.0F, properties);
	}
	
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		LOGGER.debug(this.toString() + "'s isValidPosition is called at pos : " + pos.toString() + ", with state: " + state.toString());
		return state.get(NORTH) != 0
				|| state.get(WEST) != 0
				|| state.get(SOUTH) != 0
				|| state.get(EAST) != 0;
	}
	
	boolean hasNeighborNode(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockPos [] neighborPos = {pos.north(), pos.west(), pos.south(), pos.east()};
		DoubleBlockHalf half = state.get(HALF);
		for(BlockPos thisPos: neighborPos) {
			if(worldIn.getBlockState(thisPos).getBlock() instanceof HoleyFenceNode)
				if(worldIn.getBlockState(thisPos).get(HALF) == half)
					return true;
		}
		return false;
	}
	
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		LOGGER.debug(this.toString() + "'s updatePostPlacement is called at pos : " + currentPos.toString());
		BlockState state = super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		if(state.getBlock() instanceof HoleyFenceNode) {
			if(state.get(NORTH) != 0
					|| state.get(WEST) != 0
					|| state.get(SOUTH) != 0
					|| state.get(EAST) != 0
			)
				return state;
		}
		return Blocks.AIR.getDefaultState();
	}
}