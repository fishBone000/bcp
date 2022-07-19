package group.sit.bcp;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import group.sit.bcp.block.*;
import group.sit.bcp.blockentities.MainBlockPosBE;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("bcp")
public class bcp
{
	public static final String MODID = "bcp";
	public static final String VERSION = "0.2.2 alpha";
	
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final BlockBehaviour.Properties ROCK = BlockBehaviour.Properties
    		.of(Material.STONE)
    		.strength(2.0F, 6.0F)
    		.requiresCorrectToolForDrops();

    private static final BlockBehaviour.Properties IRON = BlockBehaviour.Properties
    		.of(Material.STONE)
    		.strength(2.0F, 6.0F)
    		.requiresCorrectToolForDrops();

    private static final CreativeModeTab TAB = (new CreativeModeTab("BCP"){
    	@OnlyIn(Dist.CLIENT)
    	public ItemStack makeIcon() {
    		return new ItemStack(creamNoiseBlock);
    	}
    });

    private static final Item.Properties PROP = new Item.Properties().tab(TAB);

    private static boolean isntSolid(BlockState pState, BlockGetter reader, BlockPos pPos) {
    	return false;
    }

    //  ===============================   BLOCK / ITEM INSTANCE START   ==============================
    private static final DoubleSizeDoor woodenDoubleSizeDoor = new DoubleSizeDoor(IRON);
    private static final BlockItem woodenDoubleSizeDoorItem = new BlockItem(woodenDoubleSizeDoor, PROP);

    private static final Block creamNoiseBlock = new Block(ROCK);
    private static final BlockItem creamNoiseBlockItem = new BlockItem(creamNoiseBlock, PROP);

    private static final StairBlock creamNoiseStairs = new StairBlock(() -> creamNoiseBlock.defaultBlockState(), ROCK);
    private static final BlockItem creamNoiseStairsItem = new BlockItem(creamNoiseStairs, PROP);

    private static final SlabBlock creamNoiseSlab = new SlabBlock(ROCK);
    private static final BlockItem creamNoiseSlabItem = new BlockItem(creamNoiseSlab, PROP);

    private static final Block greyNoiseBlock = new Block(ROCK);
    private static final BlockItem greyNoiseBlockItem = new BlockItem(greyNoiseBlock, PROP);

    private static final SlabBlock greyNoiseSlab = new SlabBlock(ROCK);
    private static final BlockItem greyNoiseSlabItem = new BlockItem(greyNoiseSlab, PROP);

    private static final StairBlock greyNoiseStairs = new StairBlock(() -> greyNoiseBlock.defaultBlockState(), ROCK);
    private static final BlockItem greyNoiseStairsItem = new BlockItem(greyNoiseStairs, PROP);

    private static final StackTraceBlock stackTraceBlock = new StackTraceBlock();
    private static final BlockItem stackTraceBlockItem = new BlockItem(stackTraceBlock, new Item.Properties());

    private static final Block testBrick = new Block(ROCK);
    private static final BlockItem testBrickItem = new BlockItem(testBrick, new Item.Properties());
    
    private static final Block creamBricks = new Block(ROCK);
    private static final BlockItem creamBricksItem = new BlockItem(creamBricks, PROP);
    //private static final DirtyBrickItem creamBricksDirtyItem = new DirtyBrickItem(creamBricks, PROP);

    private static final StairBlock creamBrickStairs = new StairBlock(() -> creamBricks.defaultBlockState(), ROCK);
    private static final BlockItem creamBrickStairsItem = new BlockItem(creamBrickStairs, PROP);

    private static final SlabBlock creamBrickSlab = new SlabBlock(ROCK);
    private static final BlockItem creamBrickSlabItem = new BlockItem(creamBrickSlab, PROP);

    private static final Block greyBricks = new Block(ROCK);
    private static final BlockItem greyBricksItem = new BlockItem(greyBricks, PROP);

    private static final StairBlock greyBrickStairs = new StairBlock(() -> greyBricks.defaultBlockState(), ROCK);
    private static final BlockItem greyBrickStairsItem = new BlockItem(greyBrickStairs, PROP);

    private static final SlabBlock greyBrickSlab = new SlabBlock(ROCK);
    private static final BlockItem greyBrickSlabItem = new BlockItem(greyBrickSlab, PROP);

    private static final Block creamWall = new Block(ROCK);
    private static final BlockItem creamWallItem = new BlockItem(creamWall, PROP);
    
    private static final HoleyFenceNode HoleyFenceNode = new HoleyFenceNode(1.0F, 1.0F, 16.0F, 25.0F, IRON);
    private static final BlockItem HoleyFenceNodeItem = new BlockItem(HoleyFenceNode, PROP);
    
    private static final HoleyFenceExtension HoleyFenceExtension = new HoleyFenceExtension(IRON);
    private static final BlockItem HoleyFenceExtensionItem = new BlockItem(HoleyFenceExtension, PROP);

    private static final MultiBlock testMultiBlock = new MultiBlock(2, 2, 3, IRON);
    private static final BlockItem testMultiBlockItem = new BlockItem(testMultiBlock, new Item.Properties());

    private static final Wallpaper whiteWallpaper= new Wallpaper(0.3F, ROCK.noCollission());
    private static final BlockItem whiteWallpaperBlockItem = new BlockItem(whiteWallpaper, PROP);
    // ==================================   BLOCK / ITEM INSTANCE END   =================================
    public static BlockEntityType<MainBlockPosBE> mainBlockPosBEType =  BlockEntityType.Builder.of(MainBlockPosBE::new, testMultiBlock).build(null);
    
    public bcp() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.getValue().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.getValue().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.getValue().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.getValue().getModEventBus().addListener(this::doClientStuff);
        

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setabove(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().getValue().gameSettings);
        RenderTypeLookup.setRenderLayer(HoleyFenceNode, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(HoleyFenceExtension, RenderType.getCutoutMipped());
        //RenderTypeLookup.setRenderLayer(whiteWallpaper, RenderType.getCutoutMipped());
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("examplemod", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.getMessageSupplier().getValue()).
                collect(Collectors.toList()));
    }
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    	@SubscribeEvent
    	public static void onBlockEntityRegistry(final RegistryEvent.Register<BlockEntityType<?>> teTypeRegistryEvent) {
    		teTypeRegistryEvent.getRegistry().register(mainBlockPosBEType.setRegistryName(new ResourceLocation("bcp:main_block_pos_be")));
    	}

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
            blockRegistryEvent.getRegistry().registerAll(
            		testBrick.setRegistryName(new ResourceLocation("bcp:test_brick_block")),
            		HoleyFenceNode.setRegistryName(new ResourceLocation("bcp:holey_fence_node")),
            		HoleyFenceExtension.setRegistryName(new ResourceLocation("bcp:holey_fence_extension")),
            		creamBricks.setRegistryName(new ResourceLocation("bcp:cream_bricks")),
            		creamBrickSlab.setRegistryName(new ResourceLocation("bcp:cream_brick_slab")),
            		creamBrickStairs.setRegistryName(new ResourceLocation("bcp:cream_brick_stairs")),
            		greyBricks.setRegistryName(new ResourceLocation("bcp:grey_bricks")),
            		greyBrickSlab.setRegistryName(new ResourceLocation("bcp:grey_brick_slab")),
            		greyBrickStairs.setRegistryName(new ResourceLocation("bcp:grey_brick_stairs")),
            		creamWall.setRegistryName(new ResourceLocation("bcp:cream_wall")),
            		testMultiBlock.setRegistryName(new ResourceLocation("bcp:test_multi_block")),
            		stackTraceBlock.setRegistryName(new ResourceLocation("bcp:stack_trace_block")),
            		creamNoiseBlock.setRegistryName(new ResourceLocation("bcp:cream_noise_block")),
            		creamNoiseSlab.setRegistryName(new ResourceLocation("bcp:cream_noise_slab")),
            		creamNoiseStairs.setRegistryName(new ResourceLocation("bcp:cream_noise_stairs")),
            		greyNoiseBlock.setRegistryName(new ResourceLocation("bcp:grey_noise_block")),
            		greyNoiseSlab.setRegistryName(new ResourceLocation("bcp:grey_noise_slab")),
            		greyNoiseStairs.setRegistryName(new ResourceLocation("bcp:grey_noise_stairs")),
            		woodenDoubleSizeDoor.setRegistryName(new ResourceLocation("bcp:wooden_double_size_door")),
            		whiteWallpaper.setRegistryName(new ResourceLocation("bcp:white_wallpaper"))
            		);
        }
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
        	LOGGER.info("HELLO from Register Item");
        	itemRegistryEvent.getRegistry().registerAll(
        			testBrickItem.setRegistryName(new ResourceLocation("bcp:test_brick_item")),
        			HoleyFenceNodeItem.setRegistryName(new ResourceLocation("bcp:holey_fence_node_item")),
        			HoleyFenceExtensionItem.setRegistryName(new ResourceLocation("bcp:holey_fence_extension_item")),
        			creamBricksItem.setRegistryName(new ResourceLocation("bcp:cream_bricks_item")),
            		creamBrickSlabItem.setRegistryName(new ResourceLocation("bcp:cream_brick_slab_item")),
            		creamBrickStairsItem.setRegistryName(new ResourceLocation("bcp:cream_brick_stairs_item")),
        			greyBricksItem.setRegistryName(new ResourceLocation("bcp:grey_bricks_item")),
            		greyBrickSlabItem.setRegistryName(new ResourceLocation("bcp:grey_brick_slab_item")),
            		greyBrickStairsItem.setRegistryName(new ResourceLocation("bcp:grey_brick_stairs_item")),
        			//greyBricksDirtyItem.setRegistryName(new ResourceLocation("bcp:grey_bricks_dirty_item")),
        			creamWallItem.setRegistryName(new ResourceLocation("bcp:cream_wall_item")),
        			testMultiBlockItem.setRegistryName(new ResourceLocation("bcp:test_multi_block_item")),
        			stackTraceBlockItem.setRegistryName(new ResourceLocation("bcp:stack_trace_block_item")),
            		creamNoiseBlockItem.setRegistryName(new ResourceLocation("bcp:cream_noise_block_item")),
            		creamNoiseSlabItem.setRegistryName(new ResourceLocation("bcp:cream_noise_slab_item")),
            		creamNoiseStairsItem.setRegistryName(new ResourceLocation("bcp:cream_noise_stairs_item")),
            		greyNoiseBlockItem.setRegistryName(new ResourceLocation("bcp:grey_noise_block_item")),
            		greyNoiseSlabItem.setRegistryName(new ResourceLocation("bcp:grey_noise_slab_item")),
            		greyNoiseStairsItem.setRegistryName(new ResourceLocation("bcp:grey_noise_stairs_item")),
            		woodenDoubleSizeDoorItem.setRegistryName(new ResourceLocation("bcp:wooden_double_size_door_item")),
            		whiteWallpaperBlockItem.setRegistryName(new ResourceLocation("bcp:white_wallpaper_item"))
        			);
        }
    }
}
