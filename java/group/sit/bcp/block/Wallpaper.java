package group.sit.bcp.block;


import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.item.context.BlockPlaceContext;

public class Wallpaper extends Block {
	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final BooleanProperty UP = BlockStateProperties.UP;
	//public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
	private final VoxelShape NORTH_SHAPE, SOUTH_SHAPE, EAST_SHAPE, WEST_SHAPE, UP_SHAPE; // , DOWN_SHAPE;

	public Wallpaper(float thickness, BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, true).setValue(EAST, true).setValue(SOUTH, true).setValue(WEST, true).setValue(UP, true));//.setValue(DOWN, true));
		this.NORTH_SHAPE = Block.box(0, 0, 0, 16, 16, thickness);
		this.SOUTH_SHAPE = Block.box(0, 0, 16F-thickness, 16, 16, 16);
		this.EAST_SHAPE = Block.box(16F-thickness, 0, 0, 16, 16, 16);
		this.WEST_SHAPE = Block.box(0, 0, 0, thickness, 16, 16);
		this.UP_SHAPE = Block.box(0, 16F-thickness, 0, 16, 16, 16);
		//this.DOWN_SHAPE = Block.box(0, 0, 0, 16, thickness, 16);
	}

	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		BlockGetter blockgetter = pContext.getLevel();
		BlockPos pos = pContext.getClickedPos();
		BlockState northBS = blockgetter.getBlockState(pos.north());
		BlockState southBS = blockgetter.getBlockState(pos.south());
		BlockState eastBS = blockgetter.getBlockState(pos.east());
		BlockState westBS = blockgetter.getBlockState(pos.west());
		BlockState upBS = blockgetter.getBlockState(pos.above());
		// BlockState downBS = world.getBlockState(pos.down());
		return this.defaultBlockState()
				.setValue(NORTH, northBS.isFaceSturdy(blockgetter, pos.north(), Direction.SOUTH) && !northBS.is(this))
				.setValue(SOUTH, southBS.isFaceSturdy(blockgetter, pos.south(), Direction.NORTH) && !southBS.is(this))
				.setValue(EAST, eastBS.isFaceSturdy(blockgetter, pos.east(), Direction.WEST) && !eastBS.is(this))
				.setValue(WEST, westBS.isFaceSturdy(blockgetter, pos.west(), Direction.EAST) && !westBS.is(this))
				.setValue(UP, upBS.isFaceSturdy(blockgetter, pos.above(), Direction.DOWN) && !upBS.is(this));
				// .setValue(DOWN, downBS.isFaceSturdy(world, pos.down(), Direction.UP) && !downBS.is(this));
	}

	public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pDirectionState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pDirectionPos) {
		if(pDirection == Direction.DOWN)
			return pState;
		if(canSurvive(this.defaultBlockState(), pLevel, pCurrentPos))
			return pState.setValue(directionToProperty(pDirection), pDirectionState.isFaceSturdy(pLevel, pDirectionPos, pDirection.getOpposite()) && !pDirectionState.is(this));
		else
			return Blocks.AIR.defaultBlockState();
	}

	/** Vanilla ordering index for D-U-N-S-W-E */
	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		BlockState northBS = pLevel.getBlockState(pPos.north());
		BlockState southBS = pLevel.getBlockState(pPos.south());
		BlockState eastBS = pLevel.getBlockState(pPos.east());
		BlockState westBS = pLevel.getBlockState(pPos.west());
		BlockState upBS = pLevel.getBlockState(pPos.above());
		// BlockState downBS = pLevel.getBlockState(pPos.down());
		if(northBS.isFaceSturdy(pLevel, pPos.north(), Direction.SOUTH) && !northBS.is(this)) return true;
		if(southBS.isFaceSturdy(pLevel, pPos.south(), Direction.NORTH) && !southBS.is(this)) return true;
		if(eastBS.isFaceSturdy(pLevel, pPos.east(), Direction.WEST) && !eastBS.is(this)) return true;
		if(westBS.isFaceSturdy(pLevel, pPos.west(), Direction.EAST) && !westBS.is(this)) return true;
		if(upBS.isFaceSturdy(pLevel, pPos.above(), Direction.DOWN) && !upBS.is(this)) return true;
		// if(downBS.isFaceSturdy(BlockGetter, pPos.down(), Direction.UP) && !downBS.is(this)) return true;
		return false;
	}

	private BooleanProperty directionToProperty(Direction direction) {
		switch(direction) {
			default:
			case NORTH:
				return this.NORTH;
			case SOUTH:
				return this.SOUTH;
			case EAST:
				return this.EAST;
			case WEST:
				return this.WEST;
			case UP:
				return this.UP;
			//case DOWN:
			//	return this.DOWN;
		}
	}

	public VoxelShape getShape(BlockState pState, BlockGetter BlockGetter, BlockPos pPos, CollisionContext context) {
		VoxelShape shape = Shapes.empty();
		boolean flag = false;
		if(pState.getValue(NORTH)) {
			shape = Shapes.or(shape, NORTH_SHAPE);
			flag = true;
		}
		if(pState.getValue(SOUTH)) {
			shape = Shapes.or(shape, SOUTH_SHAPE);
			flag = true;
		}
		if(pState.getValue(WEST)) {
			shape = Shapes.or(shape, WEST_SHAPE);
			flag = true;
		}
		if(pState.getValue(EAST)) {
			shape = Shapes.or(shape, EAST_SHAPE);
			flag = true;
		}
		if(pState.getValue(UP)) {
			shape = Shapes.or(shape, UP_SHAPE);
			flag = true;
		}
		/*if(pState.getValue(DOWN)) {
			shape = Shapes.or(shape, DOWN_SHAPE);
			flag = true;
		} */
		return flag ? shape : Shapes.block();
	}

	public boolean propagatesSkylightDown(BlockState pState, BlockGetter reader, BlockPos pPos) {
		return true;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
	      pBuilder.add(NORTH, WEST, SOUTH, EAST, UP);
	   }
}
