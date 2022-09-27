package group.sit.bcp.block.multiblocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import group.sit.bcp.utils.HorizontalAABBGenerator;
import group.sit.bcp.utils.HorizontalDirectionToIndex;

public class ACExternalUnit extends MultiBlock {

	protected static final double[][] AABB_PARAMETERS = {
				{4D, 0D, 5D, 16D, 15D, 15D},
				{0D, 0D, 5D, 12D, 15D, 15D}
		};
	protected static final VoxelShape SHAPES[][] = HorizontalAABBGenerator.generate(AABB_PARAMETERS); 

	public ACExternalUnit(BlockBehaviour.Properties properties) {
		super(properties);
	}

	protected int widthStart()	{ return 0; }
	protected int widthEnd()	{ return 1; }
	protected int heightStart()	{ return 0; }
	protected int heightEnd()	{ return 0; }
	protected int depthStart()	{ return 0; }
	protected int depthEnd()	{ return 0; }

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return SHAPES[HorizontalDirectionToIndex.map(pState.getValue(FACING))][pState.getValue(getIndexProperty())%3];
	}

	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return super.getStateForPlacement(pContext)
				.setValue(FACING, pContext.getHorizontalDirection().getOpposite());
	}
}
