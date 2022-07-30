package group.sit.bcp.block;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;

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
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public HoleyFenceNode(float nodeWidth, float extensionWidth, float extensionHeight, float extraHeight, BlockBehaviour.Properties properties) {
		super(nodeWidth, extensionWidth, extensionHeight, extraHeight, properties);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(NORTH, 0)
				.setValue(SOUTH, 0)
				.setValue(WEST, 0)
				.setValue(EAST, 0)
				.setValue(WATERLOGGED, false)
				.setValue(HALF, DoubleBlockHalf.LOWER));
	}
	
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		boolean result = pLevel.getBlockState(pPos.above()).is(Blocks.AIR);
		LOGGER.debug("HoleyFenceNode#canSurvive called at " + pPos.toString() + " with result of " + result);
		return result;
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockGetter blockgetter = pContext.getLevel();
		BlockPos blockpos = pContext.getClickedPos();
		LOGGER.debug("HoleyFenceNode#getStateForPlacement is called at " + blockpos.toString());
		FluidState fluidstate = pContext.getLevel().getFluidState(pContext.getClickedPos());
		return this.defaultBlockState()
				.setValue(NORTH, Integer.valueOf(connectResult(blockgetter, DoubleBlockHalf.LOWER, blockpos, Direction.NORTH)))
				.setValue(EAST, Integer.valueOf(connectResult(blockgetter, DoubleBlockHalf.LOWER, blockpos, Direction.EAST)))
				.setValue(SOUTH, Integer.valueOf(connectResult(blockgetter, DoubleBlockHalf.LOWER, blockpos, Direction.SOUTH)))
				.setValue(WEST, Integer.valueOf(connectResult(blockgetter, DoubleBlockHalf.LOWER, blockpos, Direction.WEST)))
				.setValue(HALF, DoubleBlockHalf.LOWER)
				.setValue(WATERLOGGED, Boolean.valueOf(fluidstate.getType() == Fluids.WATER));
	}
	
	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
		LOGGER.debug("setPlacedBy is called at " + pPos.toString() + " with HALF: " + pState.getValue(HALF).toString());
		pLevel.setBlock(pPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER), 2);
	}
	
	public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
		LOGGER.debug("playerWillDestroy is called at " + pPos.toString());
		pLevel.setBlock(pState.getValue(HALF) == DoubleBlockHalf.UPPER?pPos.below():pPos.above(), Blocks.AIR.defaultBlockState(), 3);
	}
	
	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
		LOGGER.debug("updateShape is called at " + pCurrentPos.toString() + ", pNeighborPos: " + pNeighborPos.toString());
		
		if (pState.getValue(WATERLOGGED)) {
		 pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
		}

		if(pDirection.getAxis().isVertical()) {
			if(		(pDirection == Direction.UP && pState.getValue(HALF) == DoubleBlockHalf.LOWER 
				||	pDirection == Direction.DOWN && pState.getValue(HALF) == DoubleBlockHalf.UPPER)
				&&	!pLevel.getBlockState(pNeighborPos).is(pState.getBlock()))
				return Blocks.AIR.defaultBlockState();
			else
				return pState;
		}

		pState = pState.setValue(facingToProperty(pDirection), Integer.valueOf(
						connectResult(pLevel, pState.getValue(HALF), pCurrentPos, pDirection)
				));
		if(pState.getValue(HALF) == DoubleBlockHalf.UPPER)
			pLevel.setBlock(pCurrentPos.below(), pState.setValue(HALF, DoubleBlockHalf.LOWER), 18);
		else
			pLevel.setBlock(pCurrentPos.above(), pState.setValue(HALF, DoubleBlockHalf.UPPER), 18);
		return pState;
	}

	private IntegerProperty facingToProperty(Direction pDirection) {
		switch(pDirection) {
			case NORTH:
				return NORTH;
			case WEST:
				return WEST;
			case SOUTH:
				return SOUTH;
			case EAST:
				return EAST;
			default:
				throw new RuntimeException("A Direction class argument with value " + pDirection.toString() + " has passed to HoleyFenceNode#facingToProperty. This shouldn't happen!");
		}
	}
	
	protected int connectResult(BlockGetter blockReaderIn, Enum<DoubleBlockHalf> thisHalf, BlockPos thisPos, Direction direction) {
		int result;
		BlockPos pos = thisPos.relative(direction).above(2);
		BlockState state = Blocks.AIR.defaultBlockState();
		for(result = 3; result > 0; result--) {
			pos = pos.below();
			state = blockReaderIn.getBlockState(pos);
			if(state.getBlock() instanceof HoleyFenceNode) {
				Enum<DoubleBlockHalf> half = state.getValue(HALF);
				Enum<DoubleBlockHalf> opHalf = half == DoubleBlockHalf.UPPER?DoubleBlockHalf.LOWER:DoubleBlockHalf.UPPER;
				if(half == thisHalf) {
					BlockPos pos1 = half == DoubleBlockHalf.UPPER?pos.below():pos.above();
					LOGGER.debug("connectResult: pos1: " + pos1.toString());
					BlockState state1 = blockReaderIn.getBlockState(pos1);
					LOGGER.debug("connectResult: state: " + state1.toString());
					if(state1.getBlock() instanceof HoleyFenceNode)
						if(state1.getValue(HALF) == opHalf)
							break;
				}
			}
		}
		if(result!=0) {
			LOGGER.debug("HoleyFenceNode#connectResult is called at " + thisPos.toString() + ", connected to " + pos.toString() + ", state: " + state.toString() + ", with result of" + result);
			return result;
		}

		state = blockReaderIn.getBlockState(thisPos.relative(direction));
		pos = thisPos.relative(direction);
		if( !isExceptionForConnection(state)
			&& state.isFaceSturdy(blockReaderIn, pos, direction.getOpposite())
			&&  (
					thisHalf == DoubleBlockHalf.UPPER?
					blockReaderIn.getBlockState(pos.below()).isFaceSturdy(blockReaderIn, pos.below(), direction.getOpposite())
					:blockReaderIn.getBlockState(pos.above()).isFaceSturdy(blockReaderIn, pos.above(), direction.getOpposite())
				)
		)
			result = 2;
		LOGGER.debug("HoleyFenceNode#connectResult is called at " + thisPos.toString() + ", state: " + state.toString() + ", with result of" + result);
		return result;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(NORTH, EAST, WEST, SOUTH, HALF, WATERLOGGED);
	}
}
