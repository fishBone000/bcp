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
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class HoleyFenceNode extends CustomShapeFence {
	private Logger LOGGER = LogManager.getLogger();
	
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	
	public HoleyFenceNode(AbstractBlock.Properties properties) {
		this(2.0F, 2.0F, 16.0F, 6.0F, 16.0F, 16.0F, properties);
	}
	
	public HoleyFenceNode(float nodeWidth, float extensionWidth, float nodeHeight, float extensionBottom, float extensionHeight, float collisionY, AbstractBlock.Properties properties) {
		super(nodeWidth, extensionWidth, nodeHeight, extensionBottom, extensionHeight,  collisionY, properties);
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(NORTH, false)
				.with(SOUTH, false)
				.with(WEST, false)
				.with(EAST, false)
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
		BlockPos blockpos1 = blockpos.north();
		BlockPos blockpos2 = blockpos.east();
		BlockPos blockpos3 = blockpos.south();
		BlockPos blockpos4 = blockpos.west();
		BlockState blockstate1 = iblockreader.getBlockState(blockpos1);
		BlockState blockstate2 = iblockreader.getBlockState(blockpos2);
		BlockState blockstate3 = iblockreader.getBlockState(blockpos3);
		BlockState blockstate4 = iblockreader.getBlockState(blockpos4);
		return this.getDefaultState()
				.with(NORTH, Boolean.valueOf(canConnect(blockstate1, iblockreader, DoubleBlockHalf.LOWER, blockpos1, Direction.NORTH)))
				.with(EAST, Boolean.valueOf(canConnect(blockstate2, iblockreader, DoubleBlockHalf.LOWER, blockpos2, Direction.EAST)))
				.with(SOUTH, Boolean.valueOf(canConnect(blockstate3, iblockreader, DoubleBlockHalf.LOWER, blockpos3, Direction.SOUTH)))
				.with(WEST, Boolean.valueOf(canConnect(blockstate4, iblockreader, DoubleBlockHalf.LOWER, blockpos4, Direction.WEST)))
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

		stateIn= stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(
						canConnect(facingState, worldIn, stateIn.get(HALF), facingPos, facing)
				));
		if(stateIn.get(HALF) == DoubleBlockHalf.UPPER)
			worldIn.setBlockState(currentPos.down(), stateIn.with(HALF, DoubleBlockHalf.LOWER), 18);
		else
			worldIn.setBlockState(currentPos.up(), stateIn.with(HALF, DoubleBlockHalf.UPPER), 18);
		return stateIn;
	}
	
	protected boolean canConnect(BlockState stateIn, IBlockReader blockReaderIn, Enum<DoubleBlockHalf> half, BlockPos pos, Direction direction) {
		boolean flag = false;
		if(stateIn.getBlock() instanceof HoleyFenceNode)
			flag = stateIn.get(HALF) == half;
		boolean result = !cannotAttach(stateIn.getBlock()) 
				&& stateIn.isSolidSide(blockReaderIn, pos, direction.getOpposite())
				&&  (
						half == DoubleBlockHalf.UPPER?
						blockReaderIn.getBlockState(pos.down()).isSolidSide(blockReaderIn, pos.down(), direction.getOpposite())
						:blockReaderIn.getBlockState(pos.up()).isSolidSide(blockReaderIn, pos.up(), direction.getOpposite())
					)
				|| flag;
		LOGGER.debug("HoleyFenceNode#canConnect is called at " + pos.toString() + ", stateIn: " + stateIn.toString() + ", with result of" + result);
		return result;
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED, HALF);
	}
}
