package group.sit.bcp.block.multiblocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import javax.annotation.Nullable;

import group.sit.bcp.blockentities.MainBlockPosBE;
import group.sit.bcp.properties.MultiBlockPart;


/*
 * ====================    MAP    ======================
 *                      
 *                         NORTH
 *                           ^
 *                           |
 *                                 depthEnd
 *
 *
 *
 *                 widthStart                 widthEnd
 *                            ------------------------> X
 *                            |
 *                            |
 *                            |    depthStart
 *                            |
 *                            |
 *                            |
 *                            |
 *                            |
 *                            |
 *                            V
 *                            Z
 *
 */

public abstract class MultiBlock extends HorizontalDirectionalBlock implements EntityBlock {   //   NOTE  SHOULDN'T BE MOVED BY PISTON OR ANYTHING ELSE

	private static final Logger LOGGER = LogUtils.getLogger();

	public static final EnumProperty<MultiBlockPart> PART = EnumProperty.create("part", MultiBlockPart.class);

	protected abstract int widthStart();
	protected abstract int widthEnd();
	protected abstract int heightStart();
	protected abstract int heightEnd();
	protected abstract int depthStart();
	protected abstract int depthEnd();
	protected IntegerProperty getIndexProperty() {
		return IntegerProperty.create("index", 0, (widthEnd() - widthStart()+1) * (heightEnd() - heightStart()+1) * (depthEnd() - depthStart()+1) - 1);
	}

	public MultiBlock(BlockBehaviour.Properties properties) {
		super(properties);
	}

	public void playerWillDestroy(Level pLevel, BlockPos pPos, BlockState pState, Player pPlayer) {
		BlockEntity be = pLevel.getBlockEntity(pPos);
		Block block = pState.getBlock();
		if(pState.getValue(PART) == MultiBlockPart.MAIN) {
			this.breakOtherBlocks(pLevel, pPos, pPos, pState.getValue(FACING));
			pLevel.removeBlockEntity(pPos);
			super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
		}else if(be != null) {
			if(be instanceof MainBlockPosBE) {
				BlockPos mainBlockPos = ((MainBlockPosBE)be).getMainBlockPos();
				if(mainBlockPos != null) {
					((MultiBlock)block).breakOtherBlocks(pLevel, mainBlockPos, pPos, pState.getValue(FACING));
					pLevel.removeBlockEntity(pPos);
					super.playerWillDestroy(pLevel, pPos, pState, pPlayer);
					return;
				}else
					LOGGER.warn("Main block pos in MainBlockPosBE is null! BE: " + be.toString());
			}else 
				LOGGER.warn("Expected BlockEntity at pos " + pPos.toString() + " type of MainBlockPosBE, but it is " + be.toString() + ", this shouldn't happen!");
		}
	}

	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		int index = getIndex(0, 0, 0);
		return this.defaultBlockState()
				.setValue(PART, index>0 ? MultiBlockPart.SUB : MultiBlockPart.MAIN)
				.setValue(FACING, pContext.getHorizontalDirection())
				.setValue(getIndexProperty(), index);
	}

	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		HorizontalRange range = new HorizontalRange(pState.getValue(FACING));
		int heightStart = heightStart(), heightEnd = heightEnd();
		for(int iSouth = range.southStart; iSouth <= range.southEnd; iSouth++) {
			for(int iEast = range.eastStart; iEast <= range.eastEnd; iEast++) {
				for(int iHeight = heightStart; iHeight <= heightEnd; iHeight++) {
					if(!pLevel.getBlockState(pPos.south(iSouth).east(iEast).above(iHeight)).is(Blocks.AIR)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	protected int getIndex(int iWidth, int iHeight, int iDepth) {
		int width = widthEnd()-widthStart()+1;
		int depth = depthEnd()-depthStart()+1;
		iWidth -= widthStart();
		iHeight -= heightStart();
		iDepth -= depthStart();
		return iWidth + width*iDepth + width*depth*iHeight;
	}

	protected Rotation facingToRotation(BlockState pState) {
		switch(pState.getValue(FACING)) {
			default:
			case NORTH:
				return Rotation.NONE;
			case EAST:
				return Rotation.CLOCKWISE_90;
			case SOUTH:
				return Rotation.CLOCKWISE_180;
			case WEST:
				return Rotation.COUNTERCLOCKWISE_90;
		}
	}

	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
		Rotation rotation = facingToRotation(pState);

		int heightStart = heightStart(), heightEnd = heightEnd();
		int depthStart = depthStart(), depthEnd = depthEnd();
		int widthStart = widthStart(), widthEnd = widthEnd();
		for(int iHeight = heightStart; iHeight <= heightEnd; iHeight++)
			for(int iDepth = depthStart; iDepth <= depthEnd; iDepth++)
				for(int iWidth = widthStart; iWidth <= widthEnd; iWidth++) {
					BlockPos offset = new BlockPos(iWidth, iHeight, -iDepth).rotate(rotation);
					BlockPos iPos = pPos.offset(offset);
					int index = getIndex(iWidth, iHeight, iDepth);
					if(iPos != pPos)
						pLevel.setBlock(iPos, pState.setValue(PART, MultiBlockPart.SUB).setValue(getIndexProperty(), index), 3);

					BlockEntity te = pLevel.getBlockEntity(iPos);
					if(te instanceof MainBlockPosBE) {
						MainBlockPosBE mbpte = (MainBlockPosBE)te;
						mbpte.setMainBlockPos(pPos);
					}else {
						LOGGER.warn("BlockEntity at position " + iPos + " is expected to be instance of MainBlockPosBE when trying to set main block pos. ");
						if(te == null)
							LOGGER.warn("BlockEntity is null!");
						else
							LOGGER.warn("BlockEntity is: " + te.toString());
					}
				}
	}

	/*public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
		HorizontalRange range = new HorizontalRange(pState.getValue(FACING));
		int heightStart = heightStart(), heightEnd = heightEnd();
		for(int iSouth = range.southStart; iSouth <= range.southEnd; iSouth++) {
			for(int iEast = range.eastStart; iEast <= range.eastEnd; iEast++) {
				for(int iHeight = heightStart; iHeight <= heightEnd; iHeight++) {
					if(pLevel.getBlockState(pPos.south(iSouth).east(iEast).above(iHeight)).is(pLevel.getBlockState(pPos).getBlock())) {
						pLevel.setBlock(pPos.south(iSouth).east(iEast).above(iHeight), Blocks.AIR.defaultBlockState(), 3);
					}
				}
			}
		}
	}
	*/
	private void breakOtherBlocks(Level pLevel, BlockPos mainBlockPos, BlockPos thisPos, Direction facing) { // Break all blocks EXCEPT main block
		HorizontalRange range = new HorizontalRange(facing);
		int heightStart = heightStart(), heightEnd = heightEnd();
		for(int iSouth = range.southStart; iSouth <= range.southEnd; iSouth++) {
			for(int iEast = range.eastStart; iEast <= range.eastEnd; iEast++) {
				for(int iHeight = heightStart; iHeight <= heightEnd; iHeight++) {
					BlockPos iPos = mainBlockPos.south(iSouth).east(iEast).above(iHeight);
					if(iPos != thisPos) {
						pLevel.removeBlockEntity(iPos); // Remove BE to mark it as being removed by this method.
						pLevel.setBlock(iPos, Blocks.AIR.defaultBlockState(), 3);
					}
				}
			}
		}
	}

	// TODO onExplosionDestroy

	/*
	public boolean hasBlockEntity(BlockState state) {
		return state.getValue(PART) != MultiBlockPart.MAIN;
	}

	public BlockEntity createBlockEntity(BlockState state, BlockGetter world) {
		return new MainBlockPosBE();
	}
	*/

	public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
		LOGGER.debug("newBlockEntity is called, pPos: " + pPos.toString() + ", pState: " + pState.toString());
		return new MainBlockPosBE(pPos, pState);
	}

	private class HorizontalRange{
		final int southStart, southEnd, eastStart, eastEnd;
		int depthStart = depthStart(), depthEnd = depthEnd();
		int widthStart = widthStart(), widthEnd = widthEnd();
		HorizontalRange(Direction facing){
			switch(facing) {
				default:
				case NORTH:
					southStart = -depthEnd;
					southEnd = -depthStart;
					eastStart = widthStart;
					eastEnd = widthEnd;
					break;
				case SOUTH:
					southStart = depthStart;
					southEnd = depthEnd;
					eastStart = -widthEnd;
					eastEnd = -widthStart;
					break;
				case WEST:
					southStart = -widthEnd;
					southEnd = -widthStart;
					eastStart = -depthEnd;
					eastEnd = -depthStart;
					break;
				case EAST:
					southStart = widthStart;
					southEnd = widthEnd;
					eastStart = depthStart;
					eastEnd = depthEnd;
					break;
			}
		}
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
		pBuilder.add(FACING, PART, getIndexProperty());
	}
}
