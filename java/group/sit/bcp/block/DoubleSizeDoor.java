package group.sit.bcp.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoorHingeSide;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.authlib.properties.Property;


public class DoubleSizeDoor extends Block{
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
	public static final EnumProperty<DoorHingeSide> HINGE = BlockStateProperties.DOOR_HINGE;
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	protected static final VoxelShape SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 6.0D);
	protected static final VoxelShape NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 10.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape WEST_AABB = Block.makeCuboidShape(10.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	protected static final VoxelShape EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 6.0D, 16.0D, 16.0D);
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

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		Direction direction = state.get(FACING);
		boolean flag = !state.get(OPEN);
		boolean flag1 = state.get(HINGE) == DoorHingeSide.RIGHT;
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

	public DoubleSizeDoor(AbstractBlock.Properties builder) {
		super(builder);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(OPEN, Boolean.valueOf(false)).with(HINGE, DoorHingeSide.LEFT).with(POWERED, Boolean.valueOf(false)).with(PART, 3));
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(PART, FACING, OPEN, HINGE, POWERED);
	}

	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		Direction direction = getDirection(state);
		BlockPos iPos = pos.up();
		for(int i = 2; i >= 0; i--) {
			worldIn.setBlockState(iPos, state.with(PART, i));
			iPos = iPos.up();
		}
		iPos = iPos.down().offset(direction);
		for(int i = 4; i < 8; i++) {
			worldIn.setBlockState(iPos, state.with(PART, i));
			iPos = iPos.down();
		}
	}

	private Direction clockwiseAQuater(Direction direction) {
			switch(direction) {
			case NORTH:
				return Direction.EAST;
			case EAST:
				return Direction.SOUTH;
			case SOUTH:
				return Direction.WEST;
			case WEST:
				return Direction.NORTH;
			default:
				return direction;
		}
	}

	private Direction counterClockwiseAQuater(Direction direction) {
			switch(direction) {
			case NORTH:
				return Direction.WEST;
			case EAST:
				return Direction.NORTH;
			case SOUTH:
				return Direction.EAST;
			case WEST:
				return Direction.SOUTH;
			default:
				return direction;
		}
	}

	private boolean isDoorBlocked(IWorld world, BlockState state, BlockPos pos) {

		if(!(state.getBlock() instanceof DoubleSizeDoor))
			throw new RuntimeException("BlockState "+state.toString()+"at pos "+pos.toString()+" is not instance of DoubleSizeDoor. This shouldn't happen!");

		final int part = state.get(PART);
		final Direction direction = getDirection(state);
		if(state.get(OPEN)) {
			if(part < 4)
				pos = pos.offset(direction);
			pos = pos.offset( state.get(HINGE) == DoorHingeSide.LEFT ? direction.rotateY() : direction.rotateYCCW() );
		} else {
			if(part < 4)
				pos = pos.offset(direction);
			pos = pos.offset(state.get(FACING));
		}
		pos = pos.up(part%4);

		for(int j = 0; j < 2; j++) {
			for(int i = 0; i < 4; i++) {
				if(!world.getBlockState(pos).isIn(Blocks.AIR))
					return true;
				pos = pos.down();
			}
			pos = pos.up(4).offset(direction.getOpposite());
		}
		return false;

	}

	private Direction getDirection(BlockState state) {
		if(state.get(OPEN))
			return state.get(FACING);
		else {
			boolean flag = state.get(HINGE) == DoorHingeSide.RIGHT;
			switch(state.get(FACING)) {
				case NORTH:
					return flag ? Direction.WEST : Direction.EAST;
				case WEST:
					return flag ? Direction.SOUTH : Direction.NORTH;
				case SOUTH:
					return flag ? Direction.EAST : Direction.WEST;
				case EAST:
					return flag ? Direction.NORTH : Direction.SOUTH;
				default:
					throw new RuntimeException("Facing is not horizontal. This shouldn't happen! " + state.toString());
			}
		}
		
	}

	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		Direction direction = getDirection(state);
		final int part = state.get(PART);
		pos = pos.up(part % 4);
		if(part > 3)
			pos = pos.offset(direction.getOpposite());

		for(int i = 0; i < 8; i++) {
			BlockState iState = worldIn.getBlockState(pos);
			if(!iState.isIn(Blocks.AIR))
				return false;

			pos = i == 3 ? pos.offset(direction).up(3) : pos.down();
		}
		return true;
	}

	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {

		if(isDoorBlocked(worldIn, state, pos))
			return ActionResultType.PASS;

		BlockPos iPos;
		if(state.get(PART) > 3)
			iPos = pos.offset(getDirection(state).getOpposite());
		else iPos = pos;
		iPos = iPos.up(state.get(PART) % 4);
		LOGGER.debug("iPos set" + iPos.toString());

		Direction direction = Direction.NORTH;
		boolean flag = true;
		for(int i = 0; i < 4; i++) {
			LOGGER.debug(iPos.toString());
			BlockState iState = worldIn.getBlockState(iPos);
			if(iState.getBlock() instanceof DoubleSizeDoor) {
				BlockPos sidePos = iPos.offset(getDirection(iState));
				BlockState sideState = worldIn.getBlockState(sidePos);
				direction = getDirection(iState);
				if(sideState.isIn(state.getBlock()))
					if(getDirection(sideState) == direction && sideState.get(PART) == i+4)
						worldIn.setBlockState(sidePos, Blocks.AIR.getDefaultState());

				iState = worldIn.getBlockState(iPos).func_235896_a_(OPEN);
				LOGGER.debug(iState.toString());
				worldIn.setBlockState(iPos, iState);
				direction = getDirection(iState);
				flag = false;
			}else LOGGER.debug("Block is not instanceof DoubleSizeDoor. " + iState.toString());
			iPos = iPos.down();
		}
		if(flag)
			return ActionResultType.func_233537_a_(worldIn.isRemote);

		iPos = iPos.up(4).offset(direction);
		for(int i = 0; i < 4; i++) {
			if(worldIn.getBlockState(iPos).isIn(Blocks.AIR)) {
				BlockPos hingePos = iPos.offset(direction.getOpposite());
				BlockState hingeState = worldIn.getBlockState(hingePos);
				if(hingeState.isIn(this))
					if(getDirection(hingeState) == direction)
						worldIn.setBlockState(iPos, hingeState.with(PART, 4+i));
					else LOGGER.debug("Invalid hinge block direction at: " + hingePos);
				else LOGGER.debug("Missing hinge block at: " + hingePos);
			}
			iPos = iPos.down();
		}

		worldIn.playEvent(player, state.get(OPEN) ? 1006 : 1012, pos, 0);
		return ActionResultType.func_233537_a_(worldIn.isRemote);
	}

	private DoorHingeSide getHingeSide(BlockItemUseContext context) {
		IBlockReader iblockreader = context.getWorld();
		BlockPos blockpos = context.getPos();
		Direction direction = context.getPlacementHorizontalFacing();
		Direction direction1 = direction.rotateYCCW();
		BlockPos blockpos2 = blockpos.offset(direction1);
		BlockState blockstate = iblockreader.getBlockState(blockpos2);
		Direction direction2 = direction.rotateY();
		BlockPos blockpos4 = blockpos.offset(direction2);
		BlockState blockstate2 = iblockreader.getBlockState(blockpos4);

		int i = 0;
		BlockPos iPos = blockpos.offset(direction1);
		for(int j = -1; j < 2; j+=2) {
			for(int k = 0; k < 4; k++) {
				if(iblockreader.getBlockState(iPos).hasOpaqueCollisionShape(iblockreader, iPos))
					i+=j;
				iPos = iPos.up();
			}
			iPos = iPos.offset(direction2, 2).down(4);
		}
		//boolean flag = blockstate.isIn(this) && blockstate.get(HALF) == DoubleBlockHalf.LOWER;
		boolean flag = blockstate.isIn(this) && blockstate.get(PART)%4 == 3;
		boolean flag1 = blockstate2.isIn(this) && blockstate2.get(PART)%4 == 3;
		if ((!flag || flag1) && i <= 0) {
			if ((!flag1 || flag) && i >= 0) {
			int j = direction.getXOffset();
			int k = direction.getZOffset();
			Vector3d vector3d = context.getHitVec();
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
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos blockpos = context.getPos();
		boolean flag = context.getWorld().getBlockState(blockpos.up()).isReplaceable(context) &&
					context.getWorld().getBlockState(blockpos.up(2)).isReplaceable(context) &&
					context.getWorld().getBlockState(blockpos.up(3)).isReplaceable(context);
		if (blockpos.getY() < 253 && flag) {
			World world = context.getWorld();
			flag = world.isBlockPowered(blockpos) || 
				world.isBlockPowered(blockpos.up()) ||
				world.isBlockPowered(blockpos.up(2)) ||
				world.isBlockPowered(blockpos.up(3));
			return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing()).with(HINGE, this.getHingeSide(context)).with(POWERED, Boolean.valueOf(flag)).with(OPEN, Boolean.valueOf(flag)).with(PART, 3);
		} else {
			return null;
		}
	}

	private void destroy(IWorld worldIn, BlockPos pos, BlockState state) {
		if(!(state.getBlock() instanceof DoubleSizeDoor))
			throw new RuntimeException("BlockState " + state.toString() + " at pos " + pos.toString() + "is not instance of DoubleSizeDoor, this shouldn't happen!");

		Direction direction = getDirection(state);
		final int part = state.get(PART);
		pos = pos.up(part % 4);
		if(part > 3)
			pos = pos.offset(direction.getOpposite());

		for(int i = 0; i < 8; i++) {
			BlockState iState = worldIn.getBlockState(pos);
			if(iState.isIn(state.getBlock())) {
				if(	iState.get(PART) == i && 
					iState.get(OPEN) == state.get(OPEN) &&
					iState.get(HINGE) == state.get(HINGE) &&
					iState.get(FACING) == state.get(FACING) &&
					iState.get(POWERED) == state.get(POWERED)
				) {
					worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				}
			}

			pos = i == 3 ? pos.offset(direction).up(3) : pos.down();
		}
	}

	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
		destroy(worldIn, pos, state);
	}

	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		final int part = stateIn.get(PART);
		switch(part) {
			case 3:
			case 7:
				if(facing == Direction.DOWN && !facingState.isSolidSide(worldIn, facingPos, Direction.UP)) {
					destroy(worldIn, currentPos, stateIn);
					return Blocks.AIR.getDefaultState();
				}
			default:
				if(!facingState.isIn(stateIn.getBlock())) {
					if(facingState.isIn(Blocks.AIR))
						return stateIn;
					switch(facing) {
						case UP:
							if(part != 0 && part != 4)
								destroy(worldIn, currentPos, stateIn);
							break;
						case DOWN:
							if(part != 3 && part != 7)
								destroy(worldIn, currentPos, stateIn);
							break;
						default:
							if(facing == getDirection(stateIn)) {
								if(part==0 || part==1 || part==2 || part==3)
									destroy(worldIn, currentPos, stateIn);
							}else if(facing == getDirection(stateIn).getOpposite()) {
								if(part==4 || part==5 || part==6 || part==7)
									destroy(worldIn, currentPos, stateIn);
							}else return stateIn;
					}
					return Blocks.AIR.getDefaultState();
				}
				if(part < 4) // If the facing block is the same block as stateIn, in this situation, none of my business.
							 // But...what if it's a power change? TODO
					return stateIn;
				Direction direction = getDirection(stateIn);
				if(direction == facing.getOpposite())
					if(getDirection(facingState) == direction)
						return facingState.with(PART, part);
					else return Blocks.AIR.getDefaultState();
				return stateIn;
		}
	}


}
