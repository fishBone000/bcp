package group.sit.bcp.block;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import group.sit.bcp.bcp;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class HoleyFenceNode extends CustomSlopeFence {
	private Logger LOGGER = LogManager.getLogger();
	
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

	// Value of 0~3 represents the type of connection between this fence and the connected object (Another fence or a wall with 2 blocks of height).
	// Note that a wall can only be connected horizontally (value of 2).
	// 3: High.			The connected object's position is 1 block higher than this fence.
	// 2: Horizontal.	The connected object is on the same level with this fence.
	// 1: Low.			The connected object's position is 1 block lower than this fence.
	// 0: Unconnected.	No object is connected in this direction.
	public static final IntegerProperty NORTH = IntegerProperty.create("north", 0, 3);
	public static final IntegerProperty WEST = IntegerProperty.create("west", 0, 3);
	public static final IntegerProperty SOUTH = IntegerProperty.create("south", 0, 3);
	public static final IntegerProperty EAST = IntegerProperty.create("east", 0, 3);
	
	public HoleyFenceNode(AbstractBlock.Properties properties) {
		this(2.0F, 2.0F, 16.0F, 6.0F, 16.0F, 16.0F, properties);
	}
	
	public HoleyFenceNode(float nodeWidth, float extensionWidth, float nodeHeight, float extensionBottom, float extensionHeight, float collisionY, AbstractBlock.Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(NORTH, 0)
				.with(SOUTH, 0)
				.with(WEST, 0)
				.with(EAST, 0)
				.with(WATERLOGGED, false)
				.with(HALF, DoubleBlockHalf.LOWER));
	}
	
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		boolean result = worldIn.getBlockState(pos.up()).isIn(Blocks.AIR);
		LOGGER.debug("HoleyFenceNode#isValidPosition is called at " + pos.toString() + " with result of " + result);
		return result;
	}
	
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getWorld();
		IBlockReader iblockreader = world;
		BlockPos blockpos = context.getPos();
		LOGGER.debug("HoleyFenceNode#getStateForPlacement is called at " + blockpos.toString());
		FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
		return this.getDefaultState()
				.with(NORTH, Integer.valueOf(connectResult(iblockreader, DoubleBlockHalf.LOWER, blockpos, Direction.NORTH)))
				.with(EAST, Integer.valueOf(connectResult(iblockreader, DoubleBlockHalf.LOWER, blockpos, Direction.EAST)))
				.with(SOUTH, Integer.valueOf(connectResult(iblockreader, DoubleBlockHalf.LOWER, blockpos, Direction.SOUTH)))
				.with(WEST, Integer.valueOf(connectResult(iblockreader, DoubleBlockHalf.LOWER, blockpos, Direction.WEST)))
				.with(HALF, DoubleBlockHalf.LOWER)
				.with(WATERLOGGED, Boolean.valueOf(fluidstate.getFluid() == Fluids.WATER));
	}
	
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		LOGGER.debug("onBlockPlacedBy is called at " + pos.toString() + " with HALF: " + state.get(HALF).toString());
		worldIn.setBlockState(pos.up(), state.with(HALF, DoubleBlockHalf.UPPER), 2);
	}
	
	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
		LOGGER.debug("onPlayerDestroy is called at " + pos.toString());
		worldIn.setBlockState(state.get(HALF) == DoubleBlockHalf.UPPER?pos.down():pos.up(), Blocks.AIR.getDefaultState(), 3);
	}
	
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		LOGGER.debug("updatePostPlacement is called at " + currentPos.toString() + ", facingPos: " + facingPos.toString());
		
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}
		

		if(facing.getAxis().isVertical()) {
			if(		(facing == Direction.UP && stateIn.get(HALF) == DoubleBlockHalf.LOWER 
				||	facing == Direction.DOWN && stateIn.get(HALF) == DoubleBlockHalf.UPPER)
				&&	!worldIn.getBlockState(facingPos).isIn(stateIn.getBlock()))
				return Blocks.AIR.getDefaultState();
			else
				return stateIn;
		}

		stateIn = stateIn.with(facingToProperty(facing), Integer.valueOf(
						connectResult(worldIn, stateIn.get(HALF), currentPos, facing)
				));
		if(stateIn.get(HALF) == DoubleBlockHalf.UPPER)
			worldIn.setBlockState(currentPos.down(), stateIn.with(HALF, DoubleBlockHalf.LOWER), 18);
		else
			worldIn.setBlockState(currentPos.up(), stateIn.with(HALF, DoubleBlockHalf.UPPER), 18);
		return stateIn;
	}

	private IntegerProperty facingToProperty(Direction facing) {
		switch(facing) {
			case NORTH:
				return NORTH;
			case WEST:
				return WEST;
			case SOUTH:
				return SOUTH;
			case EAST:
				return EAST;
			default:
				throw new RuntimeException("A Direction class argument with value " + facing.toString() + " has passed to HoleyFenceNode#facingToProperty. This shouldn't happen!");
		}
	}
	
	protected int connectResult(IBlockReader blockReaderIn, Enum<DoubleBlockHalf> thisHalf, BlockPos thisPos, Direction direction) {
		int result;
		BlockPos pos = thisPos.offset(direction).up(2);
		BlockState state = Blocks.AIR.getDefaultState();
		for(result = 3; result > 0; result--) {
			pos = pos.down();
			state = blockReaderIn.getBlockState(pos);
			if(state.getBlock() instanceof HoleyFenceNode)
				if(state.get(HALF) == thisHalf)
					break;
		}
		if(result!=0) {
			LOGGER.debug("HoleyFenceNode#connectResult is called at " + thisPos.toString() + ", connected to " + pos.toString() + ", state: " + state.toString() + ", with result of" + result);
			return result;
		}

		state = blockReaderIn.getBlockState(thisPos.offset(direction));
		pos = thisPos.offset(direction);
		if( !cannotAttach(state.getBlock()) 
			&& state.isSolidSide(blockReaderIn, pos, direction.getOpposite())
			&&  (
					thisHalf == DoubleBlockHalf.UPPER?
					blockReaderIn.getBlockState(pos.down()).isSolidSide(blockReaderIn, pos.down(), direction.getOpposite())
					:blockReaderIn.getBlockState(pos.up()).isSolidSide(blockReaderIn, pos.up(), direction.getOpposite())
				)
		)
			result = 2;
		LOGGER.debug("HoleyFenceNode#connectResult is called at " + thisPos.toString() + ", state: " + state.toString() + ", with result of" + result);
		return result;
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED, HALF);
	}
}
