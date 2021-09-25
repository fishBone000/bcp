package group.sit.bcp.block;


import group.sit.bcp.bcp;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.FourWayBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.LeadItem;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.item.BlockItemUseContext;

public class Wallpaper extends Block {
	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final BooleanProperty UP = BlockStateProperties.UP;
	//public static final BooleanProperty DOWN = BlockStateProperties.DOWN;
	private final VoxelShape NORTH_SHAPE, SOUTH_SHAPE, EAST_SHAPE, WEST_SHAPE, UP_SHAPE; // , DOWN_SHAPE;

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		BlockState northBS = world.getBlockState(pos.north());
		BlockState southBS = world.getBlockState(pos.south());
		BlockState eastBS = world.getBlockState(pos.east());
		BlockState westBS = world.getBlockState(pos.west());
		BlockState upBS = world.getBlockState(pos.up());
		// BlockState downBS = world.getBlockState(pos.down());
		return this.getDefaultState()
				.with(NORTH, northBS.isSolidSide(world, pos.north(), Direction.SOUTH) && !northBS.isIn(this))
				.with(SOUTH, southBS.isSolidSide(world, pos.south(), Direction.NORTH) && !southBS.isIn(this))
				.with(EAST, eastBS.isSolidSide(world, pos.east(), Direction.WEST) && !eastBS.isIn(this))
				.with(WEST, westBS.isSolidSide(world, pos.west(), Direction.EAST) && !westBS.isIn(this))
				.with(UP, upBS.isSolidSide(world, pos.up(), Direction.DOWN) && !upBS.isIn(this));
				// .with(DOWN, downBS.isSolidSide(world, pos.down(), Direction.UP) && !downBS.isIn(this));
	}

	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if(facing == Direction.DOWN)
			return stateIn;
		if(isValidPosition(this.getDefaultState(), worldIn, currentPos))
			return stateIn.with(directionToProperty(facing), facingState.isSolidSide(worldIn, facingPos, facing.getOpposite()) && !facingState.isIn(this));
		else
			return Blocks.AIR.getDefaultState();
	}

	/** Vanilla ordering index for D-U-N-S-W-E */
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		BlockState northBS = worldIn.getBlockState(pos.north());
		BlockState southBS = worldIn.getBlockState(pos.south());
		BlockState eastBS = worldIn.getBlockState(pos.east());
		BlockState westBS = worldIn.getBlockState(pos.west());
		BlockState upBS = worldIn.getBlockState(pos.up());
		// BlockState downBS = worldIn.getBlockState(pos.down());
		if(northBS.isSolidSide(worldIn, pos.north(), Direction.SOUTH) && !northBS.isIn(this)) return true;
		if(southBS.isSolidSide(worldIn, pos.south(), Direction.NORTH) && !southBS.isIn(this)) return true;
		if(eastBS.isSolidSide(worldIn, pos.east(), Direction.WEST) && !eastBS.isIn(this)) return true;
		if(westBS.isSolidSide(worldIn, pos.west(), Direction.EAST) && !westBS.isIn(this)) return true;
		if(upBS.isSolidSide(worldIn, pos.up(), Direction.DOWN) && !upBS.isIn(this)) return true;
		// if(downBS.isSolidSide(worldIn, pos.down(), Direction.UP) && !downBS.isIn(this)) return true;
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

	public Wallpaper(float thickness, AbstractBlock.Properties properties) {
		super(properties);
		this.setDefaultState(this.getStateContainer().getBaseState().with(NORTH, true).with(EAST, true).with(SOUTH, true).with(WEST, true).with(UP, true));//.with(DOWN, true));
		this.NORTH_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 16, thickness);
		this.SOUTH_SHAPE = Block.makeCuboidShape(0, 0, 16F-thickness, 16, 16, 16);
		this.EAST_SHAPE = Block.makeCuboidShape(16F-thickness, 0, 0, 16, 16, 16);
		this.WEST_SHAPE = Block.makeCuboidShape(0, 0, 0, thickness, 16, 16);
		this.UP_SHAPE = Block.makeCuboidShape(0, 16F-thickness, 0, 16, 16, 16);
		//this.DOWN_SHAPE = Block.makeCuboidShape(0, 0, 0, 16, thickness, 16);
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		VoxelShape shape = VoxelShapes.empty();
		boolean flag = false;
		if(state.get(NORTH)) {
			shape = VoxelShapes.or(shape, NORTH_SHAPE);
			flag = true;
		}
		if(state.get(SOUTH)) {
			shape = VoxelShapes.or(shape, SOUTH_SHAPE);
			flag = true;
		}
		if(state.get(WEST)) {
			shape = VoxelShapes.or(shape, WEST_SHAPE);
			flag = true;
		}
		if(state.get(EAST)) {
			shape = VoxelShapes.or(shape, EAST_SHAPE);
			flag = true;
		}
		if(state.get(UP)) {
			shape = VoxelShapes.or(shape, UP_SHAPE);
			flag = true;
		}
		/*if(state.get(DOWN)) {
			shape = VoxelShapes.or(shape, DOWN_SHAPE);
			flag = true;
		} */
		return flag ? shape : VoxelShapes.fullCube();
	}

	public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP);
	}

	@OnlyIn(Dist.CLIENT)
	public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return 1.0F;
	}

	public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
		return true;
	}

}
