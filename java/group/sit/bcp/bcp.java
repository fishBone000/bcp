package group.sit.bcp;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

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
	
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final BlockBehaviour.Properties ROCK = BlockBehaviour.Properties
    		.of(Material.STONE)
    		.strength(2.0F, 6.0F)
    		.requiresCorrectToolForDrops();

    private static final BlockBehaviour.Properties DSD_PROP = BlockBehaviour.Properties
    		.of(Material.WOOD)
    		.strength(6.0F)
    		.sound(SoundType.WOOD);

    private static final BlockBehaviour.Properties METAL = BlockBehaviour.Properties
    		.of(Material.METAL)
    		.strength(2.0F, 6.0F)
    		.requiresCorrectToolForDrops();

    private static final CreativeModeTab TAB = (new CreativeModeTab("BCP"){
    	@OnlyIn(Dist.CLIENT)
    	public ItemStack makeIcon() {
    		return new ItemStack(creamNoiseBlock.get());
    	}
    });

    private static final Item.Properties PROP = new Item.Properties().tab(TAB);

    private static boolean isntSolid(BlockState pState, BlockGetter reader, BlockPos pPos) {
    	return false;
    }

    //  ===============================   BLOCK / ITEM INSTANCE START   ==============================
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);

    private static final RegistryObject<DoubleSizeDoor> woodenDoubleSizeDoor = BLOCKS.register("wooden_double_size_door", () -> new DoubleSizeDoor(DSD_PROP));
    private static final RegistryObject<BlockItem> woodenDoubleSizeDoorItem = ITEMS.register("wooden_double_size_door_item", () -> new BlockItem(woodenDoubleSizeDoor.get(), PROP));

    private static final RegistryObject<Block> creamNoiseBlock = BLOCKS.register("cream_noise_block", () -> new Block(ROCK));
    private static final RegistryObject<BlockItem> creamNoiseBlockItem = ITEMS.register("cream_noise_block_item", () -> new BlockItem(creamNoiseBlock.get(), PROP));

    private static final RegistryObject<StairBlock> creamNoiseStairs = BLOCKS.register("cream_noise_stairs", () -> new StairBlock(() -> creamNoiseBlock.get().defaultBlockState(), ROCK));
    private static final RegistryObject<BlockItem> creamNoiseStairsItem = ITEMS.register("cream_noise_stairs_item", () -> new BlockItem(creamNoiseStairs.get(), PROP));

    private static final RegistryObject<SlabBlock> creamNoiseSlab = BLOCKS.register("cream_noise_slab", () -> new SlabBlock(ROCK));
    private static final RegistryObject<BlockItem> creamNoiseSlabItem = ITEMS.register("cream_noise_slab_item", () -> new BlockItem(creamNoiseSlab.get(), PROP));

    private static final RegistryObject<Block> greyNoiseBlock = BLOCKS.register("grey_noise_block", () -> new Block(ROCK));
    private static final RegistryObject<BlockItem> greyNoiseBlockItem = ITEMS.register("grey_noise_block_item", () -> new BlockItem(greyNoiseBlock.get(), PROP));

    private static final RegistryObject<SlabBlock> greyNoiseSlab = BLOCKS.register("grey_noise_slab", () -> new SlabBlock(ROCK));
    private static final RegistryObject<BlockItem> greyNoiseSlabItem = ITEMS.register("grey_noise_slab_item", () -> new BlockItem(greyNoiseSlab.get(), PROP));

    private static final RegistryObject<StairBlock> greyNoiseStairs = BLOCKS.register("grey_noise_stairs", () -> new StairBlock(() -> greyNoiseBlock.get().defaultBlockState(), ROCK));
    private static final RegistryObject<BlockItem> greyNoiseStairsItem = ITEMS.register("grey_noise_stairs_item", () -> new BlockItem(greyNoiseStairs.get(), PROP));

    private static final RegistryObject<StackTraceBlock> stackTraceBlock = BLOCKS.register("stack_trace_block", () -> new StackTraceBlock());
    private static final RegistryObject<BlockItem> stackTraceBlockItem = ITEMS.register("stack_trace_block_item", () -> new BlockItem(stackTraceBlock.get(), new Item.Properties()));

    private static final RegistryObject<Block> testBrick = BLOCKS.register("test_brick", () -> new Block(ROCK));
    private static final RegistryObject<BlockItem> testBrickItem = ITEMS.register("test_brick_item", () -> new BlockItem(testBrick.get(), new Item.Properties()));
    
    private static final RegistryObject<Block> creamBricks = BLOCKS.register("cream_bricks", () -> new Block(ROCK));
    private static final RegistryObject<BlockItem> creamBricksItem = ITEMS.register("cream_bricks_item", () -> new BlockItem(creamBricks.get(), PROP));
    //private static final DirtyBrickItem creamBricksDirtyItem = new DirtyBrickItem(creamBricks, PROP);

    private static final RegistryObject<StairBlock> creamBrickStairs = BLOCKS.register("cream_brick_stairs", () -> new StairBlock(() -> creamBricks.get().defaultBlockState(), ROCK));
    private static final RegistryObject<BlockItem> creamBrickStairsItem = ITEMS.register("cream_brick_stairs_item", () -> new BlockItem(creamBrickStairs.get(), PROP));

    private static final RegistryObject<SlabBlock> creamBrickSlab = BLOCKS.register("cream_brick_slab", () -> new SlabBlock(ROCK));
    private static final RegistryObject<BlockItem> creamBrickSlabItem = ITEMS.register("cream_brick_slab_item", () -> new BlockItem(creamBrickSlab.get(), PROP));

    private static final RegistryObject<Block> greyBricks = BLOCKS.register("grey_bricks", () -> new Block(ROCK));
    private static final RegistryObject<BlockItem> greyBricksItem = ITEMS.register("grey_bricks_item", () -> new BlockItem(greyBricks.get(), PROP));

    private static final RegistryObject<StairBlock> greyBrickStairs = BLOCKS.register("grey_brick_stairs", () -> new StairBlock(() -> greyBricks.get().defaultBlockState(), ROCK));
    private static final RegistryObject<BlockItem> greyBrickStairsItem = ITEMS.register("grey_brick_stairs_item", () -> new BlockItem(greyBrickStairs.get(), PROP));

    private static final RegistryObject<SlabBlock> greyBrickSlab = BLOCKS.register("grey_brick_slab", () -> new SlabBlock(ROCK));
    private static final RegistryObject<BlockItem> greyBrickSlabItem = ITEMS.register("grey_brick_slab_item", () -> new BlockItem(greyBrickSlab.get(), PROP));

    private static final RegistryObject<Block> creamWall = BLOCKS.register("cream_wall", () -> new Block(ROCK));
    private static final RegistryObject<BlockItem> creamWallItem = ITEMS.register("cream_wall_item", () -> new BlockItem(creamWall.get(), PROP));
    
    private static final RegistryObject<HoleyFenceNode> HoleyFenceNode = BLOCKS.register("holey_fence_node", () -> new HoleyFenceNode(1.0F, 1.0F, 16.0F, 25.0F, METAL));
    private static final RegistryObject<BlockItem> HoleyFenceNodeItem = ITEMS.register("holey_fence_node_item", () -> new BlockItem(HoleyFenceNode.get(), PROP));
    
    private static final RegistryObject<HoleyFenceExtension> HoleyFenceExtension = BLOCKS.register("holey_fence_extension", () -> new HoleyFenceExtension(METAL));
    private static final RegistryObject<BlockItem> HoleyFenceExtensionItem = ITEMS.register("holey_fence_extension_item", () -> new BlockItem(HoleyFenceExtension.get(), PROP));

    private static final RegistryObject<MultiBlock> testHugeBlock = BLOCKS.register("test_huge_block", () -> new HugeBlock(METAL));
    private static final RegistryObject<BlockItem> testHugeBlockItem = ITEMS.register("test_huge_block_item", () -> new BlockItem(testHugeBlock.get(), new Item.Properties()));

    private static final RegistryObject<Wallpaper> whiteWallpaper= BLOCKS.register("white_wallpaper", () -> new Wallpaper(0.3F, ROCK.noCollission()));
    private static final RegistryObject<BlockItem> whiteWallpaperBlockItem = ITEMS.register("white_wallpaper_item", () -> new BlockItem(whiteWallpaper.get(), PROP));
    // ==================================   BLOCK / ITEM INSTANCE END   =================================
    public static final RegistryObject<BlockEntityType<MainBlockPosBE>> mainBlockPosBEType = BLOCK_ENTITIES.register("main_block_pos_be_type", () -> BlockEntityType.Builder.of(MainBlockPosBE::new, testHugeBlock.get()).build(null));
    
    public bcp()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    	LOGGER.info("Hello from doClientStuff");
    	ItemBlockRenderTypes.setRenderLayer(HoleyFenceNode.get(), RenderType.cutout());
    	ItemBlockRenderTypes.setRenderLayer(HoleyFenceExtension.get(), RenderType.cutout());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    /*private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().getValue().gameSettings);
        RenderTypeLookup.setRenderLayer(HoleyFenceNode, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(HoleyFenceExtension, RenderType.getCutoutMipped());
        //RenderTypeLookup.setRenderLayer(whiteWallpaper, RenderType.getCutoutMipped());
    }
    */

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        // some example code to dispatch IMC to another mod
        InterModComms.sendTo("bcp", "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        // Some example code to receive and process InterModComms from other mods
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.messageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
    }
}
