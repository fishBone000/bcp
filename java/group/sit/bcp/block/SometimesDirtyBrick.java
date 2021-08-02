package group.sit.bcp.block;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraftforge.fml.RegistryObject;

public class SometimesDirtyBrick extends Block {
	public static final IntegerProperty VARIENT_05 = IntegerProperty.create("varient_05", 0, 5);
	/*
	 * 0 00
	 * 1 01
	 * 2 02
	 * 3 10
	 * 4 11
	 * 5 12
	 */

	public SometimesDirtyBrick(AbstractBlock.Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(VARIENT_05, 0)
				);
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(VARIENT_05);
	}

	public BlockState getDirtyState() {
		//                                                So that the brick is always dirty
		return this.getDefaultState().with(VARIENT_05, (int)(Math.random()*5)+1);
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		//                                                          So that the brick has a 1/8 chance to be dirty
		return this.getDefaultState().with(VARIENT_05, Math.random()<0.85?0:(int)(Math.random()*6));
	}

}
