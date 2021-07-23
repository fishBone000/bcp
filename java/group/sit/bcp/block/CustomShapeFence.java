package group.sit.bcp.block;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

/*public class CustomShapeFence extends FenceBlock {
	//public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	
	private static Logger logger = LogManager.getLogger();

	public CustomShapeFence(AbstractBlock.Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState());//.with(HALF, DoubleBlockHalf.LOWER));
	}
	
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlockState(pos.up(), this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)));//.with(HALF, DoubleBlockHalf.UPPER), 3);
		logger.info("Test Fence placed by " + placer.toString());
	}
	
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		return true;
	}
}
*/

public class CustomShapeFence extends FourWayBlock {
	private final VoxelShape[] renderShapes;
	
	public CustomShapeFence(float nodeWidth, float extensionWidth, float nodeHeight, float extensionBottom, float extensionHeight, float collisionY, Properties properties) {
		
		super(nodeWidth, extensionWidth, nodeHeight, extensionHeight, collisionY, properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
		//this.renderShapes = this.makeShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
		this.renderShapes = this.makeShapes(nodeWidth, extensionWidth, nodeHeight, extensionBottom, extensionHeight);
	}
	
	public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
		return this.renderShapes[this.getIndex(state)];
	
	}
	
	public VoxelShape getRayTraceShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
		return this.getShape(state, reader, pos, context);
	}
	
	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}
	
	
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		World world = context.getWorld();
		IBlockReader iblockreader = world;
		BlockPos blockpos = context.getPos();
		FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
		BlockPos blockpos1 = blockpos.north();
		BlockPos blockpos2 = blockpos.east();
		BlockPos blockpos3 = blockpos.south();
		BlockPos blockpos4 = blockpos.west();
		BlockState blockstate1 = iblockreader.getBlockState(blockpos1);
		BlockState blockstate2 = iblockreader.getBlockState(blockpos2);
		BlockState blockstate3 = iblockreader.getBlockState(blockpos3);
		BlockState blockstate4 = iblockreader.getBlockState(blockpos4);
		/*return super.getStateForPlacement(context)
				.with(NORTH, Boolean.valueOf(this.canConnect(blockstate1, blockstate1.isSolidSide(iblockreader, blockpos1, Direction.SOUTH), Direction.SOUTH)))
				.with(EAST, Boolean.valueOf(this.canConnect(blockstate2, blockstate2.isSolidSide(iblockreader, blockpos2, Direction.WEST), Direction.WEST)))
				.with(SOUTH, Boolean.valueOf(this.canConnect(blockstate3, blockstate3.isSolidSide(iblockreader, blockpos3, Direction.NORTH), Direction.NORTH)))
				.with(WEST, Boolean.valueOf(this.canConnect(blockstate4, blockstate4.isSolidSide(iblockreader, blockpos4, Direction.EAST), Direction.EAST)))
				.with(WATERLOGGED, Boolean.valueOf(fluidstate.getFluid() == Fluids.WATER));
				*/
		return super.getStateForPlacement(context)
				.with(NORTH, Boolean.valueOf(canConnect(blockstate1, iblockreader, blockpos1, Direction.NORTH)))
				.with(EAST, Boolean.valueOf(canConnect(blockstate2, iblockreader, blockpos2, Direction.EAST)))
				.with(SOUTH, Boolean.valueOf(canConnect(blockstate3, iblockreader, blockpos3, Direction.SOUTH)))
				.with(WEST, Boolean.valueOf(canConnect(blockstate4, iblockreader, blockpos4, Direction.WEST)))
				.with(WATERLOGGED, Boolean.valueOf(fluidstate.getFluid() == Fluids.WATER));
	}
	
	protected boolean canConnect(BlockState stateIn, IBlockReader BlockReaderIn, BlockPos pos, Direction direction) {
		return stateIn.isIn(this);
	}
	
	@SuppressWarnings("deprecation")  // Because vanilla Minecraft's FenceBlock#updatePostPlacement used this deprecated method too.
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
		if (stateIn.get(WATERLOGGED)) {
			worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
		}

		return facing.getAxis().getPlane() == Direction.Plane.HORIZONTAL ?
				stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(
						canConnect(stateIn, worldIn, facingPos, facing)
				)) 
				: super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
	}
	
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote) {
			ItemStack itemstack = player.getHeldItem(handIn);
			return itemstack.getItem() == Items.LEAD ? ActionResultType.SUCCESS : ActionResultType.PASS;
		} else {
			return LeadItem.bindPlayerMobs(player, worldIn, pos);
		}
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
	}
}
