package group.sit.bcp.block;

import net.minecraft.block.Block;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import group.sit.bcp.properties.MultiBlockPart;
import group.sit.bcp.tileentity.MainBlockPosTE;


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

public class MultiBlock extends HorizontalBlock {   //   NOTE  SHOULDN'T BE MOVED BY PISTON OR ANYTHING ELSE

	private static final Logger LOGGER = LogManager.getLogger();

	public static final EnumProperty<MultiBlockPart> PART = EnumProperty.create("part", MultiBlockPart.class);

	private final int widthStart, heightStart, depthStart, widthEnd, heightEnd, depthEnd;

	public MultiBlock(int width, int height, int depth, AbstractBlock.Properties properties) {
		super(properties);
		widthStart = heightStart = depthStart = 0;
		this.widthEnd = width-1;
		this.heightEnd  = height-1;
		this.depthEnd  = depth-1;
	}

	public MultiBlock(int widthStart, int heightStart, int depthStart, int widthEnd, int heightEnd, int depthEnd, AbstractBlock.Properties properties) {
		super(properties);
		this.setDefaultState(this.getStateContainer().getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(PART, MultiBlockPart.MAIN));
		this.widthStart = widthStart;
		this.heightStart  = heightStart;
		this.depthStart  = depthStart;
		this.widthEnd = widthEnd;
		this.heightEnd  = heightEnd;
		this.depthEnd  = depthEnd;
	}

	@SuppressWarnings("deprecation")
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity te = worldIn.getTileEntity(pos);
		if(!(state.getBlock() instanceof MultiBlock)) {
			LOGGER.warn("Expected block at pos " + pos.toString() + " type of MultiBlock sub block, but it is " + state.toString() + ", this shoudln't happen!");
			super.onReplaced(state, worldIn, pos, newState, isMoving);
			return;
		}
		if(te != null) {
			if(te instanceof MainBlockPosTE) {
				BlockPos mainBlockPos = ((MainBlockPosTE)te).getMainBlockPos();
				Block block = worldIn.getBlockState(mainBlockPos).getBlock();
				if(block instanceof MultiBlock) {
					((MultiBlock)block).breakAllBlocks(worldIn, state, mainBlockPos, worldIn.getBlockState(mainBlockPos).get(HORIZONTAL_FACING));
					return;
				}else 
					LOGGER.warn("Expected block at pos " + mainBlockPos.toString() + " type of MultiBlock main block, but it is " + block.toString() + ", this shouldn't happen!");
			}else 
				LOGGER.warn("Expected TileEntity at pos " + pos.toString() + " type of MainBlockPosTE, but it is " + te.toString() + ", this shouldn't happen!");
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getStateContainer().getBaseState().with(PART, MultiBlockPart.MAIN).with(HORIZONTAL_FACING, context.getPlacementHorizontalFacing());
	}

	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		HorizontalRange range = new HorizontalRange(state.get(HORIZONTAL_FACING));
		for(int iSouth = range.southStart; iSouth <= range.southEnd; iSouth++) {
			for(int iEast = range.eastStart; iEast <= range.eastEnd; iEast++) {
				for(int iHeight = heightStart; iHeight <= heightEnd; iHeight++) {
					if(!worldIn.getBlockState(pos.south(iSouth).east(iEast).up(iHeight)).isIn(Blocks.AIR)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {

		HorizontalRange range = new HorizontalRange(state.get(HORIZONTAL_FACING));

		for(int iSouth = range.southStart; iSouth <= range.southEnd; iSouth++) {
			for(int iEast = range.eastStart; iEast <= range.eastEnd; iEast++) {
				for(int iHeight = heightStart; iHeight <= heightEnd; iHeight++) {
					if(iSouth != 0 || iEast != 0 || iHeight != 0) {

						BlockPos iPos = pos.south(iSouth).east(iEast).up(iHeight);
						worldIn.setBlockState(iPos, state.with(PART, MultiBlockPart.SUB));

						TileEntity te = worldIn.getTileEntity(iPos);
						if(te instanceof MainBlockPosTE) {
							MainBlockPosTE mbpte = (MainBlockPosTE)te;
							mbpte.setMainBlockPos(pos);
						}else
							LOGGER.warn("TileEntity at position " + iPos + " is expected to be instance of MainBlockPosTE");

					}
				}
			}
		}
	}

	public void onPlayerDestroy(IWorld worldIn, BlockPos pos, BlockState state) {
		HorizontalRange range = new HorizontalRange(state.get(HORIZONTAL_FACING));
		for(int iSouth = range.southStart; iSouth <= range.southEnd; iSouth++) {
			for(int iEast = range.eastStart; iEast <= range.eastEnd; iEast++) {
				for(int iHeight = heightStart; iHeight <= heightEnd; iHeight++) {
					if(worldIn.getBlockState(pos.south(iSouth).east(iEast).up(iHeight)).isIn(worldIn.getBlockState(pos).getBlock())) {
						worldIn.setBlockState(pos.south(iSouth).east(iEast).up(iHeight), Blocks.AIR.getDefaultState(), 3);
					}
				}
			}
		}
	}
	private void breakAllBlocks(World worldIn, BlockState state, BlockPos mainBlockPos, Direction facing) {
		HorizontalRange range = new HorizontalRange(facing);
		for(int iSouth = range.southStart; iSouth <= range.southEnd; iSouth++) {
			for(int iEast = range.eastStart; iEast <= range.eastEnd; iEast++) {
				for(int iHeight = heightStart; iHeight <= heightEnd; iHeight++) {
					BlockPos iPos = mainBlockPos.south(iSouth).east(iEast).up(iHeight);
					if(worldIn.getBlockState(iPos).isIn(worldIn.getBlockState(mainBlockPos).getBlock())) {
						worldIn.removeTileEntity(iPos);
						worldIn.setBlockState(iPos, Blocks.AIR.getDefaultState(), 3);
					}
				}
			}
		}
	}

	// TODO onExplosionDestroy

	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new MainBlockPosTE();
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(PART, HORIZONTAL_FACING);
	}

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
}
