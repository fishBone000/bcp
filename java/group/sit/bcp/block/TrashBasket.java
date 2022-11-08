package group.sit.bcp.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrashBasket extends Block {

	private static final VoxelShape NORTH_SHAPE = box(5, 0, 4, 11, 10, 5);
	private static final VoxelShape SOUTH_SHAPE = box(5, 0, 11, 11, 10, 12);
	private static final VoxelShape WEST_SHAPE = box(4, 0, 4, 5, 10, 12);
	private static final VoxelShape EAST_SHAPE = box(11, 0, 4, 12, 10, 12);
	private static final VoxelShape BOTTOM_SHAPE = box(5, 0, 5, 11, 10, 11);
	private static final VoxelShape SHAPE = Shapes.or(NORTH_SHAPE, SOUTH_SHAPE, WEST_SHAPE, EAST_SHAPE, BOTTOM_SHAPE);

	public TrashBasket(BlockBehaviour.Properties properties) {
		super(properties);
	}

	public VoxelShape getShape(BlockState pState, BlockGetter BlockGetter, BlockPos pPos, CollisionContext context) {
		return SHAPE;
	}
}