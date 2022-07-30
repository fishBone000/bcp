package group.sit.bcp.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
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

public class MultiBlock extends HorizontalDirectionalBlock {   //   NOTE  SHOULDN'T BE MOVED BY PISTON OR ANYTHING ELSE

	private static final Logger LOGGER = LogUtils.getLogger();

	public static final EnumProperty<MultiBlockPart> PART = EnumProperty.create("part", MultiBlockPart.class);

	private final int widthStart, heightStart, depthStart, widthEnd, heightEnd, depthEnd;

	public MultiBlock(int width, int height, int depth, BlockBehaviour.Properties properties) {
		super(properties);
		widthStart = heightStart = depthStart = 0;
		this.widthEnd = width-1;
		this.heightEnd  = height-1;
		this.depthEnd  = depth-1;
	}

	public MultiBlock(int widthStart, int heightStart, int depthStart, int widthEnd, int heightEnd, int depthEnd, BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(PART, MultiBlockPart.MAIN));
		this.widthStart = widthStart;
		this.heightStart  = heightStart;
		this.depthStart  = depthStart;
		this.widthEnd = widthEnd;
		this.heightEnd  = heightEnd;
		this.depthEnd  = depthEnd;
	}

	@SuppressWarnings("deprecation")
	public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
		BlockEntity te = pLevel.getBlockEntity(pPos);
		if(!(pState.getBlock() instanceof MultiBlock)) {
			LOGGER.warn("Expected block at pos " + pPos.toString() + " type of MultiBlock sub block, but it is " + pState.toString() + ", this shoudln't happen!");
			super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
			return;
		}
		Block block = pState.getBlock();
		if(te != null) {
			if(te instanceof MainBlockPosBE) {
				BlockPos mainBlockPos = ((MainBlockPosBE)te).getMainBlockPos();
				if(block instanceof MultiBlock) {
					((MultiBlock)block).breakAllBlocks(pLevel, block, mainBlockPos, pLevel.getBlockState(mainBlockPos).getValue(FACING));
					return;
				}else 
					LOGGER.warn("Expected block at pos " + mainBlockPos.toString() + " type of MultiBlock main block, but it is " + block.toString() + ", this shouldn't happen!");
			}else 
				LOGGER.warn("Expected BlockEntity at pos " + pPos.toString() + " type of MainBlockPosBE, but it is " + te.toString() + ", this shouldn't happen!");
		}else if(pState.getValue(PART) == MultiBlockPart.MAIN) {
			this.breakAllBlocks(pLevel, block, pPos, pState.getValue(FACING));
		}else // This sub block has been marked as being removed by breakAllBlocks. Remove it normally to prevent calling breakAllBlocks again.
			super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
	}

	public BlockState getStateForPlacement(BlockPlaceContext pContext) {
		return this.defaultBlockState().setValue(PART, MultiBlockPart.MAIN).setValue(FACING, pContext.getNearestLookingDirection());
	}

	public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
		HorizontalRange range = new HorizontalRange(pState.getValue(FACING));
		for(int iSouth = range.southStart; iSouth <= range.southEnd; iSouth++) {
			for(int iEast = range.eastStart; iEast <= range.eastEnd; iEast++) {
				for(int iHeight = heightStart; iHeight <= heightEnd; iHeight++) {
					if(!pLevel.getBlockState(pPos.south(iSouth).east(iEast).above(iHeight)).is(Blocks.AIR) || !super.canSurvive(pState, pLevel, pPos)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {

		HorizontalRange range = new HorizontalRange(pState.getValue(FACING));

		for(int iSouth = range.southStart; iSouth <= range.southEnd; iSouth++) {
			for(int iEast = range.eastStart; iEast <= range.eastEnd; iEast++) {
				for(int iHeight = heightStart; iHeight <= heightEnd; iHeight++) {
					if(iSouth != 0 || iEast != 0 || iHeight != 0) {

						BlockPos iPos = pPos.south(iSouth).east(iEast).above(iHeight);
						pLevel.setBlock(iPos, pState.setValue(PART, MultiBlockPart.SUB), 3);

						BlockEntity te = pLevel.getBlockEntity(iPos);
						if(te instanceof MainBlockPosBE) {
							MainBlockPosBE mbpte = (MainBlockPosBE)te;
							mbpte.setMainBlockPos(pPos);
						}else
							LOGGER.warn("BlockEntity at position " + iPos + " is expected to be instance of MainBlockPosBE");

					}
				}
			}
		}
	}

	public void playerDestroy(Level pLevel, Player pPlayer, BlockPos pPos, BlockState pState, @Nullable BlockEntity pBlockEntity, ItemStack pTool) {
		HorizontalRange range = new HorizontalRange(pState.getValue(FACING));
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
	private void breakAllBlocks(Level pLevel, Block block, BlockPos mainBlockPos, Direction facing) {
		HorizontalRange range = new HorizontalRange(facing);
		for(int iSouth = range.southStart; iSouth <= range.southEnd; iSouth++) {
			for(int iEast = range.eastStart; iEast <= range.eastEnd; iEast++) {
				for(int iHeight = heightStart; iHeight <= heightEnd; iHeight++) {
					BlockPos iPos = mainBlockPos.south(iSouth).east(iEast).above(iHeight);
					if(pLevel.getBlockState(iPos).is(block)) {
						pLevel.removeBlockEntity(iPos); // Remove TE to mark it as being removed by this method.
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

	private class HorizontalRange{
		final int southStart, southEnd, eastStart, eastEnd;
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
		pBuilder.add(FACING, PART);
	}
}
