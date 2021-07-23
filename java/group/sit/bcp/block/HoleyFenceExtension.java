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
		super(properties);
	}
	
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		LOGGER.debug(this.toString() + "'s isValidPosition is called at pos : " + pos.toString());
		return super.isValidPosition(state, worldIn, pos) && hasNeighborNode(state, worldIn, pos);
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
		if(hasNeighborNode(stateIn, worldIn, currentPos))
			return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		else
			return Blocks.AIR.getDefaultState();
	}
}