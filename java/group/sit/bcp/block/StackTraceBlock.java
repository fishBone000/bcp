package group.sit.bcp.block;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class StackTraceBlock extends Block {
	public static final BooleanProperty BOOL = BooleanProperty.create("bool");

	//private static final Logger LOGGER = LogManager.getLogger();
	public StackTraceBlock() {
		super(AbstractBlock.Properties
			.create(Material.ROCK)
    		.hardnessAndResistance(2.0F, 6.0F)
    		.setRequiresTool()
    	);
		Thread.dumpStack();
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Thread.dumpStack();
		return super.getStateForPlacement(context);
	}

	@Override
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
		Thread.dumpStack();
		super.onPlayerDestroy(worldIn, pos, state);
	}

	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		Thread.dumpStack();
		return super.isValidPosition(state, worldIn, pos);
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		Thread.dumpStack();
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		Thread.dumpStack();
		return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}

	@Override
	public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(BOOL);
	}
}
