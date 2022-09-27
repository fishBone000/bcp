package group.sit.bcp.block.multiblocks;

import group.sit.bcp.utils.HorizontalAABBGenerator;
import group.sit.bcp.utils.HorizontalDirectionToIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CylinderBin extends MultiBlock {

	public CylinderBin(BlockBehaviour.Properties properties) {
		super(properties);
	}

	protected int widthStart()	{ return 0; }
	protected int widthEnd()	{ return 1; }
	protected int heightStart()	{ return 0; }
	protected int heightEnd()	{ return 1; }
	protected int depthStart()	{ return 0; }
	protected int depthEnd()	{ return 1; }

	protected static final double AABB_PARAMETERS[][] = {
			{8D, 0D, 0D, 16D, 16D, 8D}, 
			{0D, 0D, 0D, 8D, 16D, 8D}, 
			{8D, 0D, 8D, 16D, 16D, 16D}, 
			{0D, 0D, 8D, 8D, 16D, 16D}, 
			{7D, 0D, 0D, 16D, 6D, 9D}, 
			{0D, 0D, 0D, 9D, 6D, 9D}, 
			{7D, 0D, 7D, 16D, 6D, 16D}, 
			{0D, 0D, 9D, 9D, 6D, 16D}
	};
	protected static final VoxelShape SHAPES[][] = HorizontalAABBGenerator.generate(AABB_PARAMETERS);

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return SHAPES[HorizontalDirectionToIndex.map(pState.getValue(FACING))][pState.getValue(getIndexProperty())];
	}
}
