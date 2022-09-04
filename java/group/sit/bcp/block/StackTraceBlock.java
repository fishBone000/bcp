package group.sit.bcp.block;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;


public class StackTraceBlock extends Block {
	public static final BooleanProperty BOOL = BooleanProperty.create("bool");

	// private static final Logger LOGGER = LogUtils.getLogger();
	public StackTraceBlock() {
		super(BlockBehaviour.Properties
			.of(Material.STONE)
    		.strength(2.0F, 6.0F)
    		.requiresCorrectToolForDrops()
    	);
		Thread.dumpStack();
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		Thread.dumpStack();
		return super.getStateForPlacement(pContext);
	}

	@Override
	public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
		Thread.dumpStack();
		super.playerDestroy(pLevel, pPlayer, pPos, pState, pBlockEntity, pTool);
	}

	@Override
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		Thread.dumpStack();
		return super.canSurvive(pState, pLevel, pPos);
	}

	@Override
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		Thread.dumpStack();
		super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
	}

	@Override
	public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
		Thread.dumpStack();
		super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
	}

	@Override
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
		Thread.dumpStack();
		return super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
	}

}
