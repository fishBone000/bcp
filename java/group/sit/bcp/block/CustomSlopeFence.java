package group.sit.bcp.block;

import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class CustomSlopeFence extends Block {

	public static final IntegerProperty NORTH = IntegerProperty.create("north", 0, 3);
	public static final IntegerProperty WEST = IntegerProperty.create("west", 0, 3);
	public static final IntegerProperty SOUTH = IntegerProperty.create("south", 0, 3);
	public static final IntegerProperty EAST = IntegerProperty.create("east", 0, 3);
	// TODO Check functions required to perform features of WATERLOGGED
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

	// An array of 16 Shapes
	// Index of each element can be obtained by ORing flags:
	// 1:	North connected, low or horizontal
	// 2:	West connected, low or horizontal
	// 4:	South connected, low or horizontal
	// 8:	East connected, low or horizontal
	// 16:	North connected, High
	// 32:	West connected, High
	// 64:	South connected, High
	// 128:	East connected, High
	protected VoxelShape shapes[];

	public CustomSlopeFence(float nodeWidth, float extensionWidth, float extensionHeight, float extraHeight, BlockBehaviour.Properties properties) {
	//public CustomSlopeFence(AbstractBlock.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(NORTH, 0).setValue(EAST, 0).setValue(SOUTH, 0).setValue(WEST, 0).setValue(WATERLOGGED, Boolean.valueOf(false)));
		shapes = makeShapes(nodeWidth, extensionWidth, extensionHeight, extraHeight);
		
	}

	protected VoxelShape[] makeShapes(float nodeWidth, float extensionWidth, float extensionHeight, float extraHeight) {
		VoxelShape result[] = new VoxelShape[16];
		VoxelShape node = Block.box(8.0F-nodeWidth, 0F, 8.0F-nodeWidth, 8.0F+nodeWidth, extensionHeight, 8.0F+nodeWidth);
		VoxelShape north = Block.box(8.0F-extensionWidth, 0F, 0F, 8.0F+extensionWidth, extensionHeight, 8.0F-nodeWidth);
		VoxelShape west = Block.box(0F, 0F, 8.0F-extensionWidth, 8.0F-nodeWidth, extensionHeight, 8.0F+extensionWidth);
		VoxelShape south = Block.box(8.0F-extensionWidth, 0F, 8.0F+nodeWidth, 8.0F+extensionWidth, extensionHeight, 16.0F);
		VoxelShape east = Block.box(8.0F+nodeWidth, 0F, 8.0F-extensionWidth, 16.0F, extensionHeight, 8.0F+extensionWidth);
		VoxelShape northHigh = Block.box(8.0F-extensionWidth, 0F, 0F, 8.0F+extensionWidth, extraHeight, 8.0F-nodeWidth);
		VoxelShape westHigh = Block.box(0F, 0F, 8.0F-extensionWidth, 8.0F-nodeWidth, extraHeight, 8.0F+extensionWidth);
		VoxelShape southHigh = Block.box(8.0F-extensionWidth, 0F, 8.0F+nodeWidth, 8.0F+extensionWidth, extraHeight, 16.0F);
		VoxelShape eastHigh = Block.box(8.0F+nodeWidth, 0F, 8.0F-extensionWidth, 16.0F, extraHeight, 8.0F+extensionWidth);
		for(int i = 0; i < 16; i++) {
			result[i] = node;
			if((i & 1) != 0)
				result[i] = Shapes.or(result[i], north);
			if((i & 2) != 0)
				result[i] = Shapes.or(result[i], west);
			if((i & 4) != 0)
				result[i] = Shapes.or(result[i], south);
			if((i & 8) != 0)
				result[i] = Shapes.or(result[i], east);
			if((i & 16) != 0)
				result[i] = Shapes.or(result[i], northHigh);
			if((i & 32) != 0)
				result[i] = Shapes.or(result[i], westHigh);
			if((i & 64) != 0)
				result[i] = Shapes.or(result[i], southHigh);
			if((i & 128) != 0)
				result[i] = Shapes.or(result[i], eastHigh);
		}
		return result;
	}

	private VoxelShape getShape(BlockState pState) {
		int flags = 0;
		if(pState.getValue(NORTH) != 0)
			flags += 1;
		if(pState.getValue(WEST) != 0)
			flags += 2;
		if(pState.getValue(SOUTH) != 0)
			flags += 4;
		if(pState.getValue(EAST) != 0)
			flags += 8;
		return shapes[flags];
	}

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return getShape(pState);
	}

   public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return getShape(pState);
   }

	public VoxelShape getRenderShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
		return getShape(pState);
	}

	// Just copy-pasted the code because I don't know how to solve:
	// Cannot make static reference blahblahblah
	public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
		if (pLevel.isClientSide) {
	         ItemStack itemstack = pPlayer.getItemInHand(pHand);
	         return itemstack.is(Items.LEAD) ? InteractionResult.SUCCESS : InteractionResult.PASS;
	      } else {
	         return LeadItem.bindPlayerMobs(pPlayer, pLevel, pPos);
	      }
	}

   /*public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return !state.getValue(WATERLOGGED);
   }
   */

	public FluidState getFluidState(BlockState pState) {
		return pState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(pState);
	}

	public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
		return false;
	}
}
