package group.sit.bcp.block;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;

public class SixVarientsBlock extends Block {
	private final Logger LOGGER = LogManager.getLogger();
	public static final IntegerProperty VARIENT_05 = IntegerProperty.create("varient_05", 0, 5);
	/*
	 * 0 00
	 * 1 01
	 * 2 02
	 * 3 10
	 * 4 11
	 * 5 12
	 */

	public SixVarientsBlock(AbstractBlock.Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState()
				.with(VARIENT_05, 0)
				);
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(VARIENT_05);
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		//                                                          So that the brick has a 1/8 chance to be dirty
		BlockState result = this.getDefaultState().with(VARIENT_05, Math.random()<0.85?0:(int)(Math.random()*6));
		LOGGER.debug("yellow_brick placed with state " + result.toString());
		return result;
	}

}
