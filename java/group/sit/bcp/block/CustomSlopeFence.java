package group.sit.bcp.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.item.LeadItem;
import net.minecraft.pathfinding.PathType;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;

public class CustomSlopeFence extends Block {

	public static final IntegerProperty NORTH = IntegerProperty.create("north", 0, 3);
	public static final IntegerProperty WEST = IntegerProperty.create("west", 0, 3);
	public static final IntegerProperty SOUTH = IntegerProperty.create("south", 0, 3);
	public static final IntegerProperty EAST = IntegerProperty.create("east", 0, 3);
	// TODO Check functions required to perform features of WATERLOGGED
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	// An array of 16 VoxelShapes
	// Index of each element can be obtained by ORing flags:
	// 1: North connected
	// 2: West connected
	// 4: South connected
	// 8: East connected
	protected VoxelShape shapes[];

	public CustomSlopeFence(float nodeWidth, float extensionWidth, float extensionHeight, AbstractBlock.Properties properties) {
	//public CustomSlopeFence(AbstractBlock.Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, 0).with(EAST, 0).with(SOUTH, 0).with(WEST, 0).with(WATERLOGGED, Boolean.valueOf(false)));
		shapes = makeShapes(nodeWidth, extensionWidth, extensionHeight);
		
	}

	protected VoxelShape[] makeShapes(float nodeWidth, float extensionWidth, float extensionHeight) {
		VoxelShape result[] = new VoxelShape[16];
		VoxelShape node = Block.makeCuboidShape(8.0F-nodeWidth, 0F, 8.0F-nodeWidth, 8.0F+nodeWidth, extensionHeight, 8.0F+nodeWidth);
		VoxelShape north = Block.makeCuboidShape(8.0F-nodeWidth, 0F, 0F, 8.0F+nodeWidth, extensionHeight, 8.0F-nodeWidth);
		VoxelShape west = Block.makeCuboidShape(0F, 0F, 8.0F-nodeWidth, 8.0F-nodeWidth, extensionHeight, 8.0F+nodeWidth);
		VoxelShape south = Block.makeCuboidShape(8.0F-nodeWidth, 0F, 8.0F+nodeWidth, 8.0F+nodeWidth, extensionHeight, 16.0F);
		VoxelShape east = Block.makeCuboidShape(8.0F+nodeWidth, 0F, 8.0F-nodeWidth, 16.0F, extensionHeight, 8.0F+nodeWidth);
		for(int i = 0; i < 16; i++) {
			result[i] = node;
			if((i & 1) != 0)
				result[i] = VoxelShapes.or(result[i], north);
			if((i & 2) != 0)
				result[i] = VoxelShapes.or(result[i], west);
			if((i & 4) != 0)
				result[i] = VoxelShapes.or(result[i], south);
			if((i & 8) != 0)
				result[i] = VoxelShapes.or(result[i], east);
		}
		return result;
		
	}

	private VoxelShape getShape(BlockState state) {
		int flags = 0;
		if(state.get(NORTH) != 0)
			flags += 1;
		if(state.get(WEST) != 0)
			flags += 2;
		if(state.get(SOUTH) != 0)
			flags += 4;
		if(state.get(EAST) != 0)
			flags += 8;
		return shapes[flags];
	}

	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return getShape(state);
	}

   public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return getShape(state);
   }

	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return getShape(state);
	}

	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote) {
			ItemStack itemstack = player.getHeldItem(handIn);
			return itemstack.getItem() == Items.LEAD ? ActionResultType.SUCCESS : ActionResultType.PASS;
		} else {
			return LeadItem.bindPlayerMobs(player, worldIn, pos);
		}
	}

   /*public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
      return !state.get(WATERLOGGED);
   }
   */

   public FluidState getFluidState(BlockState state) {
      return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }

	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}
}
