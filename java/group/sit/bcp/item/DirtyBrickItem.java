package group.sit.bcp.item;

import group.sit.bcp.block.SometimesDirtyBrick;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;

public class DirtyBrickItem extends BlockItem {

	public DirtyBrickItem(Block blockIn, Item.Properties builder) {
		super(blockIn, builder);
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		if(!(this.getBlock() instanceof SometimesDirtyBrick)) {
			return super.getStateForPlacement(context);
		}

		BlockState blockstate =  ( ( SometimesDirtyBrick )( this.getBlock() ) ).getDirtyState();
		return blockstate != null && this.canPlace(context, blockstate) ? blockstate : null;
	}
}
