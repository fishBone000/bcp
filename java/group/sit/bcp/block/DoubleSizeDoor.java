package group.sit.bcp.block;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;


public class DoubleSizeDoor extends Block{
	private static final Logger LOGGER = LogUtils.getLogger();
	
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 6.0D);
	protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 10.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_AABB = Block.box(10.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 6.0D, 16.0D, 16.0D);
	public static final IntegerProperty PART = IntegerProperty.create("part", 0, 7);
	/*
	 *   ___________
	 *   |    |    |
	 *   | 4  |  0 |
	 *   -----------
	 *   |    |   [|  H
	 *   | 5  |  1 |  i
	 *   -----------  n
	 *   |    |    |  g
	 *   | 6  |  2[|  e
	 *   -----------
	 *   |    |    |
	 *   | 7  |  3 |
	 *   -----------
	 */

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		Direction direction = pState.getValue(FACING);
		boolean flag = !pState.getValue(OPEN);
		boolean flag1 = pState.getValue(HINGE) == DoorHingeSide.RIGHT;
		switch(direction) {
			case EAST:
			default:
			return flag ? EAST_AABB : (flag1 ? NORTH_AABB : SOUTH_AABB);
			case SOUTH:
			return flag ? SOUTH_AABB : (flag1 ? EAST_AABB : WEST_AABB);
			case WEST:
			return flag ? WEST_AABB : (flag1 ? SOUTH_AABB : NORTH_AABB);
			case NORTH:
			return flag ? NORTH_AABB : (flag1 ? WEST_AABB : EAST_AABB);
		}
	}

	public DoubleSizeDoor(BlockBehaviour.Properties builder) {
		super(builder);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(OPEN, Boolean.valueOf(false)).setValue(HINGE, DoorHingeSide.LEFT).setValue(POWERED, Boolean.valueOf(false)).setValue(PART, 3));
	}

	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
		Direction direction = getDirection(pState);
		BlockPos iPos = pPos.above();
		for(int i = 2; i >= 0; i--) {
			pLevel.setBlock(iPos, pState.setValue(PART, i), 3);
			iPos = iPos.above();
		}
		iPos = iPos.below().relative(direction);
		for(int i = 4; i < 8; i++) {
			pLevel.setBlock(iPos, pState.setValue(PART, i), 3);
			iPos = iPos.below();
		}
	}

	private boolean isDoorBlocked(BlockGetter blockgetter, BlockState pState, BlockPos pPos) {

		if(!(pState.getBlock() instanceof DoubleSizeDoor))
			throw new RuntimeException("BlockState "+pState.toString()+"at pos "+pPos.toString()+" is not instance of DoubleSizeDoor. This shouldn't happen!");

		final int part = pState.getValue(PART);
		final Direction direction = getDirection(pState);
		if(pState.getValue(OPEN)) {
			if(part < 4)
				pPos = pPos.relative(direction);
			pPos = pPos.relative( (pState.getValue(HINGE) == DoorHingeSide.LEFT ? direction.getClockWise() : direction.getCounterClockWise()) );
		} else {
			if(part < 4)
				pPos = pPos.relative(direction);
			pPos = pPos.relative(pState.getValue(FACING));
		}
		pPos = pPos.above(part%4);

		for(int j = 0; j < 2; j++) {
			for(int i = 0; i < 4; i++) {
				if(!blockgetter.getBlockState(pPos).is(Blocks.AIR))
					return true;
				pPos = pPos.below();
			}
			pPos = pPos.above(4).relative(direction.getOpposite());
		}
		return false;

	}

	private Direction getDirection(BlockState pState) {
		if(pState.getValue(OPEN))
			return pState.getValue(FACING);
		else {
			boolean flag = pState.getValue(HINGE) == DoorHingeSide.RIGHT;
			switch(pState.getValue(FACING)) {
				case NORTH:
					return flag ? Direction.WEST : Direction.EAST;
				case WEST:
					return flag ? Direction.SOUTH : Direction.NORTH;
				case SOUTH:
					return flag ? Direction.EAST : Direction.WEST;
				case EAST:
					return flag ? Direction.NORTH : Direction.SOUTH;
				default:
					throw new RuntimeException("Facing is not horizontal. This shouldn't happen! " + pState.toString());
			}
		}
		
	}

	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		Direction direction = getDirection(pState);
		final int part = pState.getValue(PART);
		pPos = pPos.above(part % 4);
		if(part > 3)
			pPos = pPos.relative(direction.getOpposite());

		for(int i = 0; i < 8; i++) {
			BlockState iState = pLevel.getBlockState(pPos);
			if(!iState.is(Blocks.AIR))
				return false;

			pPos = i == 3 ? pPos.relative(direction).above(3) : pPos.below();
		}
		return true;
	}

	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player player, InteractionHand handIn, BlockHitResult hit) {

		if(isDoorBlocked(pLevel, pState, pPos))
			return InteractionResult.PASS;

		BlockPos iPos;
		if(pState.getValue(PART) > 3)
			iPos = pPos.relative(getDirection(pState).getOpposite());
		else iPos = pPos;
		iPos = iPos.above(pState.getValue(PART) % 4);
		LOGGER.debug("iPos set" + iPos.toString());

		Direction direction = Direction.NORTH;
		boolean flag = true;
		for(int i = 0; i < 4; i++) {
			LOGGER.debug(iPos.toString());
			BlockState iState = pLevel.getBlockState(iPos);
			if(iState.getBlock() instanceof DoubleSizeDoor) {
				BlockPos sidePos = iPos.relative(getDirection(iState));
				BlockState sideState = pLevel.getBlockState(sidePos);
				direction = getDirection(iState);
				if(sideState.is(pState.getBlock()))
					if(getDirection(sideState) == direction && sideState.getValue(PART) == i+4)
						pLevel.setBlock(sidePos, Blocks.AIR.defaultBlockState(), 3);

				iState = pLevel.getBlockState(iPos).cycle(OPEN);
				LOGGER.debug(iState.toString());
				pLevel.setBlock(iPos, iState, 3);
				direction = getDirection(iState);
				flag = false;
			}else LOGGER.debug("Block is not instanceof DoubleSizeDoor. " + iState.toString());
			iPos = iPos.below();
		}
		if(flag)
			return InteractionResult.sidedSuccess(pLevel.isClientSide);

		iPos = iPos.above(4).relative(direction);
		for(int i = 0; i < 4; i++) {
			if(pLevel.getBlockState(iPos).is(Blocks.AIR)) {
				BlockPos hingePos = iPos.relative(direction.getOpposite());
				BlockState hingeState = pLevel.getBlockState(hingePos);
				if(hingeState.is(this))
					if(getDirection(hingeState) == direction)
						pLevel.setBlock(iPos, hingeState.setValue(PART, 4+i), 3);
					else LOGGER.debug("Invalid hinge block direction at: " + hingePos);
				else LOGGER.debug("Missing hinge block at: " + hingePos);
			}
			iPos = iPos.below();
		}

		pLevel.levelEvent(player, pState.getValue(OPEN) ? 1006 : 1012, pPos, 0);
		pLevel.gameEvent(player, pState.getValue(OPEN) ? GameEvent.BLOCK_OPEN : GameEvent.BLOCK_CLOSE, pPos);
		return InteractionResult.sidedSuccess(pLevel.isClientSide);
	}

	private DoorHingeSide getHingeSide(BlockPlaceContext pContext) {
		BlockGetter blockgetter = pContext.getLevel();
		BlockPos blockpos = pContext.getClickedPos();
		Direction direction = pContext.getHorizontalDirection();
		Direction direction1 = direction.getCounterClockWise();
		BlockPos blockpos2 = blockpos.relative(direction1);
		BlockState blockstate = blockgetter.getBlockState(blockpos2);
		Direction direction2 = direction.getClockWise();
		BlockPos blockpos4 = blockpos.relative(direction2);
		BlockState blockstate2 = blockgetter.getBlockState(blockpos4);

		int i = 0;
		BlockPos iPos = blockpos.relative(direction1);
		for(int j = -1; j < 2; j+=2) {
			for(int k = 0; k < 4; k++) {
				if(blockgetter.getBlockState(iPos).isCollisionShapeFullBlock(blockgetter, iPos))
					i+=j;
				iPos = iPos.above();
			}
			iPos = iPos.relative(direction2).relative(direction2).below(4);
		}
		//boolean flag = blockstate.is(this) && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER;
		boolean flag = blockstate.is(this) && blockstate.getValue(PART)%4 == 3;
		boolean flag1 = blockstate2.is(this) && blockstate2.getValue(PART)%4 == 3;
		if ((!flag || flag1) && i <= 0) {
			if ((!flag1 || flag) && i >= 0) {
			int j = direction.getStepX();
			int k = direction.getStepZ();
			Vec3 vector3d = pContext.getClickLocation();
			double d0 = vector3d.x - (double)blockpos.getX();
			double d1 = vector3d.z - (double)blockpos.getZ();
			return (j >= 0 || !(d1 < 0.5D)) && (j <= 0 || !(d1 > 0.5D)) && (k >= 0 || !(d0 > 0.5D)) && (k <= 0 || !(d0 < 0.5D)) ? DoorHingeSide.LEFT : DoorHingeSide.RIGHT;
			} else {
			return DoorHingeSide.LEFT;
			}
		} else {
			return DoorHingeSide.RIGHT;
		}
	}

	@Nullable
	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockPos blockpos = pContext.getClickedPos();
		boolean flag = pContext.getLevel().getBlockState(blockpos.above()).canBeReplaced(pContext) &&
					pContext.getLevel().getBlockState(blockpos.above(2)).canBeReplaced(pContext) &&
					pContext.getLevel().getBlockState(blockpos.above(3)).canBeReplaced(pContext);
		if (blockpos.getY() < 253 && flag) {
			Level level = pContext.getLevel();
			flag = level.hasNeighborSignal(blockpos) || 
				level.hasNeighborSignal(blockpos.above()) ||
				level.hasNeighborSignal(blockpos.above(2)) ||
				level.hasNeighborSignal(blockpos.above(3));
			return this.defaultBlockState().setValue(FACING, pContext.getHorizontalDirection()).setValue(HINGE, this.getHingeSide(pContext)).setValue(POWERED, Boolean.valueOf(flag)).setValue(OPEN, Boolean.valueOf(flag)).setValue(PART, 3);
		} else {
			return null;
		}
	}

	private void destroy(Level pLevel, BlockPos pPos, BlockState pState) {
		if(!(pState.getBlock() instanceof DoubleSizeDoor))
			throw new RuntimeException("BlockState " + pState.toString() + " at pos " + pPos.toString() + "is not instance of DoubleSizeDoor, this shouldn't happen!");

		Direction direction = getDirection(pState);
		final int part = pState.getValue(PART);
		pPos = pPos.above(part % 4);
		if(part > 3)
			pPos = pPos.relative(direction.getOpposite());

		for(int i = 0; i < 8; i++) {
			BlockState iState = pLevel.getBlockState(pPos);
			if(iState.is(pState.getBlock())) {
				if(	iState.getValue(PART) == i && 
					iState.getValue(OPEN) == pState.getValue(OPEN) &&
					iState.getValue(HINGE) == pState.getValue(HINGE) &&
					iState.getValue(FACING) == pState.getValue(FACING) &&
					iState.getValue(POWERED) == pState.getValue(POWERED)
				) {
					pLevel.setBlock(pPos, Blocks.AIR.defaultBlockState(), 3);
				}
			}

			pPos = i == 3 ? pPos.relative(direction).above(3) : pPos.below();
		}
	}

	public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
		destroy(pLevel, pPos, pState);
	}

	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
		final int part = pState.getValue(PART);
		switch(part) {
			case 3:
			case 7:
				if(pDirection == Direction.DOWN && !pNeighborState.isFaceSturdy(pLevel, pNeighborPos, Direction.UP)) {
					destroy(pLevel, pCurrentPos, pState);
					return Blocks.AIR.defaultBlockState();
				}
			default:
				if(!pNeighborState.is(pState.getBlock())) {
					if(pNeighborState.is(Blocks.AIR))
						return pState;
					switch(pDirection) {
						case UP:
							if(part != 0 && part != 4)
								destroy(pLevel, pCurrentPos, pState);
							break;
						case DOWN:
							if(part != 3 && part != 7)
								destroy(pLevel, pCurrentPos, pState);
							break;
						default:
							if(pDirection == getDirection(pState)) {
								if(part==0 || part==1 || part==2 || part==3)
									destroy(pLevel, pCurrentPos, pState);
							}else if(pDirection == getDirection(pState).getOpposite()) {
								if(part==4 || part==5 || part==6 || part==7)
									destroy(pLevel, pCurrentPos, pState);
							}else return pState;
					}
					return Blocks.AIR.defaultBlockState();
				}
				if(part < 4) // If the facing block is the same block as pState, in this situation, none of my business.
							 // But...what if it's a power change? TODO
					return pState;
				Direction direction = getDirection(pState);
				if(direction == pDirection.getOpposite())
					if(getDirection(pNeighborState) == direction)
						return pNeighborState.setValue(PART, part);
					else return Blocks.AIR.defaultBlockState();
				return pState;
		}
	}


}
