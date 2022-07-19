package group.sit.bcp.block;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class HoleyFenceExtension extends HoleyFenceNode {
	
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public HoleyFenceExtension(BlockBehaviour.Properties properties) {
		super(0F, 1.0F, 16.0F, 25.0F, properties);
	}
	
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		LOGGER.debug(this.toString() + "'s canSurvive is called at pos : " + pPos.toString() + ", with state: " + pState.toString());
		return pState.getValue(NORTH) != 0
				|| pState.getValue(WEST) != 0
				|| pState.getValue(SOUTH) != 0
				|| pState.getValue(EAST) != 0;
	}
	
	/* boolean hasNeighborNode(BlockState pState, IBlockGetterReader pLevel, BlockPos pPos) {
		BlockPos [] neighborPos = {pPos.north(), pPos.west(), pPos.south(), pPos.east()};
		DoubleBlockHalf half = pState.getValue(HALF);
		for(BlockPos thisPos: neighborPos) {
			if(pLevel.getBlockState(thisPos).getBlock() instanceof HoleyFenceNode)
				if(pLevel.getBlockState(thisPos).getValue(HALF) == half)
					return true;
		}
		return false;
	} */
	
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
		LOGGER.debug(this.toString() + "'s updateShape is called at pos : " + pCurrentPos.toString());
		BlockState state = super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
		if(state.getBlock() instanceof HoleyFenceNode) {
			if(state.getValue(NORTH) != 0
					|| state.getValue(WEST) != 0
					|| state.getValue(SOUTH) != 0
					|| state.getValue(EAST) != 0
			)
				return state;
		}
		return Blocks.AIR.defaultBlockState();
	}
}
