package group.sit.bcp;

import net.minecraft.tileentity.TileEntityType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.AbstractBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import group.sit.bcp.block.*;
import group.sit.bcp.item.*;
import group.sit.bcp.tileentity.MainBlockPosTE;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("bcp")
public class bcp
{
	public static final String MODID = "bcp";
	public static final String VERSION = "0.1.5 alpha";
	
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    //  ===============================   BLOCK / ITEM INSTANCE START   ==============================
    private static final StackTraceBlock stackTraceBlock = new StackTraceBlock();
    private static final BlockItem stackTraceBlockItem = new BlockItem(stackTraceBlock, new Item.Properties());

    private static final Block testBrick = new Block(AbstractBlock.Properties
    		.create(Material.ROCK, MaterialColor.RED)
    		.hardnessAndResistance(2.0F, 6.0F)
    		.setRequiresTool()
    		);
    private static final BlockItem testBrickItem = new BlockItem(testBrick, new Item.Properties());
    
    private static final SometimesDirtyBrick yellowBricks = new SometimesDirtyBrick(AbstractBlock.Properties
    		.create(Material.ROCK)
    		.hardnessAndResistance(2.0F, 6.0F)
    		.setRequiresTool()
    		);
    private static final BlockItem yellowBricksItem = new BlockItem(yellowBricks, new Item.Properties());
    private static final DirtyBrickItem yellowBricksDirtyItem = new DirtyBrickItem(yellowBricks, new Item.Properties());

    private static final Block yellowWall = new Block(AbstractBlock.Properties
    		.create(Material.ROCK)
    		.hardnessAndResistance(2.0F, 6.0F)
    		.setRequiresTool()
    		);
    private static final BlockItem yellowWallItem = new BlockItem(yellowWall, new Item.Properties());
    
    private static HoleyFenceNode HoleyFenceNode = new HoleyFenceNode(AbstractBlock.Properties
    		.create(Material.IRON)
    		.hardnessAndResistance(2.0F, 6.0F)
    		.setRequiresTool()
    		);
    private static BlockItem HoleyFenceNodeItem = new BlockItem(HoleyFenceNode, new Item.Properties());
    
    private static HoleyFenceExtension HoleyFenceExtension = new HoleyFenceExtension(AbstractBlock.Properties
    		.create(Material.IRON)
    		.hardnessAndResistance(2.0F, 6.0F)
    		.setRequiresTool()
    		);
    private static final BlockItem HoleyFenceExtensionItem = new BlockItem(HoleyFenceExtension, new Item.Properties());

    private static final MultiBlock testMultiBlock = new MultiBlock(2, 2, 3, AbstractBlock.Properties
    		.create(Material.IRON)
    		.hardnessAndResistance(2.0F, 6.0F)
    		.setRequiresTool());
    private static final BlockItem testMultiBlockItem = new BlockItem(testMultiBlock, new Item.Properties());
    // ==================================   BLOCK / ITEM INSTANCE END   =================================
    public static TileEntityType<MainBlockPosTE> mainBlockPosTEType =  TileEntityType.Builder.create(MainBlockPosTE::new, testMultiBlock).build(null);
    

    public bcp() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        // do something that can only be done on the client
        LOGGER.info("Got game settings {}", event.getMinecraftSupplier().get().gameSettings);
        RenderTypeLookup.setRenderLayer(HoleyFenceNode, RenderType.getCutoutMipped());
        RenderTypeLookup.setRenderLayer(HoleyFenceExtension, RenderType.getCutoutMipped());
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
                map(m->m.getMessageSupplier().get()).
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
    	public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> teTypeRegistryEvent) {
    		teTypeRegistryEvent.getRegistry().register(mainBlockPosTEType.setRegistryName(new ResourceLocation("bcp:main_block_pos_te_type")));
    	}

        @SubscribeEvent
        public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
            // register a new block here
            LOGGER.info("HELLO from Register Block");
            blockRegistryEvent.getRegistry().registerAll(
            		testBrick.setRegistryName(new ResourceLocation("bcp:test_brick_block")),
            		HoleyFenceNode.setRegistryName(new ResourceLocation("bcp:holey_fence_node")),
            		HoleyFenceExtension.setRegistryName(new ResourceLocation("bcp:holey_fence_extension")),
            		yellowBricks.setRegistryName(new ResourceLocation("bcp:yellow_bricks")),
            		yellowWall.setRegistryName(new ResourceLocation("bcp:yellow_wall")),
            		testMultiBlock.setRegistryName(new ResourceLocation("bcp:test_multi_block")),
            		stackTraceBlock.setRegistryName(new ResourceLocation("bcp:stack_trace_block"))
            		);
        }
        @SubscribeEvent
        public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
        	LOGGER.info("HELLO from Register Item");
        	itemRegistryEvent.getRegistry().registerAll(
        			testBrickItem.setRegistryName(new ResourceLocation("bcp:test_brick_item")),
        			HoleyFenceNodeItem.setRegistryName(new ResourceLocation("bcp:holey_fence_node_item")),
        			HoleyFenceExtensionItem.setRegistryName(new ResourceLocation("bcp:holey_fence_extension_item")),
        			yellowBricksItem.setRegistryName(new ResourceLocation("bcp:yellow_bricks_item")),
        			yellowBricksDirtyItem.setRegistryName(new ResourceLocation("bcp:yellow_bricks_dirty_item")),
        			yellowWallItem.setRegistryName(new ResourceLocation("bcp:yellow_wall_item")),
        			testMultiBlockItem.setRegistryName(new ResourceLocation("bcp:test_multi_block_item")),
        			stackTraceBlockItem.setRegistryName(new ResourceLocation("bcp:stack_trace_block_item"))
        			);
        }
    }
}
