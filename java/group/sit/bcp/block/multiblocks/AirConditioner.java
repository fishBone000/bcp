package group.sit.bcp.block.multiblocks;

import group.sit.bcp.utils.HorizontalAABBGenerator;
import group.sit.bcp.utils.HorizontalDirectionToIndex;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AirConditioner extends MultiBlock{

	public AirConditioner(BlockBehaviour.Properties properties) {
		super(properties);
	}

	protected int widthStart()	{ return 0; }
	protected int widthEnd()	{ return 1; }
	protected int heightStart()	{ return 0; }
	protected int heightEnd()	{ return 0; }
	protected int depthStart()	{ return 0; }
	protected int depthEnd()	{ return 0; }

	protected static final double AABB_PARAM[][] = {
			{ 1D, 3D, 10D, 16D, 13D, 16D }, 
			{ 0D, 3D, 10D, 15D, 13D, 16D }
	};

	protected static final VoxelShape SHAPES[][] = HorizontalAABBGenerator.generate(AABB_PARAM);

	public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
		return SHAPES[HorizontalDirectionToIndex.map(pState.getValue(FACING))][pState.getValue(getIndexProperty())];
	}

	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return super.getStateForPlacement(pContext)
				.setValue(FACING, pContext.getHorizontalDirection().getOpposite());
	}

}