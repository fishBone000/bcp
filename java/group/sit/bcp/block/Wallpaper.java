package group.sit.bcp.block;


import group.sit.bcp.bcp;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
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
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.item.BlockItemUseContext;

public class Wallpaper extends Block {
	private final float thickness;
	public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
	public static final BooleanProperty EAST = BlockStateProperties.EAST;
	public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
	public static final BooleanProperty WEST = BlockStateProperties.WEST;
	public static final BooleanProperty UP = BlockStateProperties.UP;
	public static final BooleanProperty DOWN = BlockStateProperties.DOWN;

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getWorld();
		BlockPos pos = context.getPos();
		return this.getDefaultState()
				.with(NORTH, world.getBlockState(pos.north()).isSolidSide(world, pos.north(), Direction.SOUTH))
				.with(SOUTH, world.getBlockState(pos.south()).isSolidSide(world, pos.south(), Direction.NORTH))
				.with(EAST, world.getBlockState(pos.east()).isSolidSide(world, pos.east(), Direction.WEST))
				.with(WEST, world.getBlockState(pos.west()).isSolidSide(world, pos.west(), Direction.EAST))
				.with(UP, world.getBlockState(pos.up()).isSolidSide(world, pos.up(), Direction.DOWN))
				.with(DOWN, world.getBlockState(pos.down()).isSolidSide(world, pos.down(), Direction.UP));
	}

	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		return stateIn.with(directionToProperty(facing), worldIn.getBlockState(currentPos.north()).isSolidSide(worldIn, currentPos.north(), Direction.SOUTH));
	}

	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		if(!state.get(NORTH) && !state.get(SOUTH) && !state.get(WEST) && !state.get(EAST) && !state.get(UP) && !state.get(DOWN))
			return false;
		return true;
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
			case DOWN:
				return this.DOWN;
		}
	}

	public Wallpaper(float thickness, AbstractBlock.Properties properties) {
		super(properties);
		this.setDefaultState(this.getStateContainer().getBaseState().with(NORTH, true).with(EAST, true).with(SOUTH, true).with(WEST, true).with(UP, true).with(DOWN, true));
		this.thickness = thickness;
	}

	public void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}

}
