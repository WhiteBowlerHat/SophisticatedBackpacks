package net.p3pp3rf1y.sophisticatedbackpacks.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemEntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.api.CapabilityBackpackWrapper;
import net.p3pp3rf1y.sophisticatedbackpacks.backpack.BackpackItem;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.BackpackScreen;
import net.p3pp3rf1y.sophisticatedbackpacks.client.init.ModBlockColors;
import net.p3pp3rf1y.sophisticatedbackpacks.client.init.ModItemColors;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackBlockEntityRenderer;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackDynamicModel;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackLayerRenderer;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.BackpackModel;
import net.p3pp3rf1y.sophisticatedbackpacks.client.render.ClientBackpackContentsTooltip;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModBlocks;
import net.p3pp3rf1y.sophisticatedbackpacks.network.BackpackInsertMessage;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import static net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems.EVERLASTING_BACKPACK_ITEM_ENTITY;

public class ClientEventHandler {
	private ClientEventHandler() {}

	private static final String BACKPACK_REG_NAME = "backpack";
	public static final ModelLayerLocation BACKPACK_LAYER = new ModelLayerLocation(new ResourceLocation(SophisticatedBackpacks.MOD_ID, BACKPACK_REG_NAME), "main");

	public static void registerHandlers() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addListener(ClientEventHandler::loadComplete);
		modBus.addListener(ClientEventHandler::clientSetup);
		modBus.addListener(ClientEventHandler::onModelRegistry);
		modBus.addListener(ClientEventHandler::registerLayer);
		modBus.addListener(ClientEventHandler::registerEntityRenderers);
		modBus.addListener(ClientEventHandler::registerReloadListener);
		IEventBus eventBus = MinecraftForge.EVENT_BUS;
		eventBus.addListener(ClientEventHandler::onDrawScreen);
		eventBus.addListener(EventPriority.HIGH, ClientEventHandler::onRightClick);
		eventBus.addListener(ClientBackpackContentsTooltip::onWorldLoad);
	}

	private static void onDrawScreen(ScreenEvent.DrawScreenEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		Screen gui = mc.screen;
		if (!(gui instanceof AbstractContainerScreen<?> containerGui) || gui instanceof CreativeModeInventoryScreen || mc.player == null) {
			return;
		}
		AbstractContainerMenu menu = containerGui.getMenu();
		ItemStack held = menu.getCarried();
		if (!held.isEmpty() && !(held.getItem() instanceof BackpackItem)) {
			Slot under = containerGui.getSlotUnderMouse();
			PoseStack poseStack = event.getPoseStack();

			for (Slot s : menu.slots) {
				ItemStack stack = s.getItem();
				if (!s.mayPickup(mc.player) || stack.getCount() != 1) {
					continue;
				}

				stack.getCapability(CapabilityBackpackWrapper.getCapabilityInstance()).ifPresent(backpackWrapper -> {
					if (s == under) {
						int x = event.getMouseX();
						int y = event.getMouseY();
						poseStack.pushPose();
						poseStack.translate(0, 0, containerGui instanceof BackpackScreen ? -100 : 100);
						containerGui.renderTooltip(poseStack, Collections.singletonList(new TranslatableComponent("gui.sophisticatedbackpacks.tooltip.right_click_to_add_to_backpack")), Optional.of(new BackpackItem.BackpackContentsTooltip(stack)), x, y, mc.font);
						poseStack.popPose();
					} else {
						int x = containerGui.getGuiLeft() + s.x;
						int y = containerGui.getGuiTop() + s.y;

						poseStack.pushPose();
						poseStack.translate(0, 0, containerGui instanceof BackpackScreen ? 100 : 499);

						mc.font.drawShadow(poseStack, "+", (float) x + 10, (float) y + 8, 0xFFFF00);
						poseStack.popPose();
					}
				});
			}

		}
	}

	private static void onRightClick(ScreenEvent.MouseReleasedEvent.Pre event) {
		Minecraft mc = Minecraft.getInstance();
		Screen screen = mc.screen;
		if (screen instanceof AbstractContainerScreen<?> container && !(screen instanceof CreativeModeInventoryScreen) && event.getButton() == 1) {
			Slot under = container.getSlotUnderMouse();
			ItemStack held = container.getMenu().getCarried();

			if (under != null && !held.isEmpty() && mc.player != null && under.mayPickup(mc.player)) {
				ItemStack stack = under.getItem();
				if (stack.getItem() instanceof BackpackItem && stack.getCount() == 1) {
					SophisticatedBackpacks.PACKET_HANDLER.sendToServer(new BackpackInsertMessage(under.index));
					screen.mouseReleased(0, 0, -1);
					event.setCanceled(true);
				}
			}
		}
	}

	private static void loadComplete(FMLLoadCompleteEvent event) {
		event.enqueueWork(() -> {
			ModItemColors.init();
			ModBlockColors.init();
		});
	}

	private static void onModelRegistry(ModelRegistryEvent event) {
		ModelLoaderRegistry.registerLoader(SophisticatedBackpacks.getRL(BACKPACK_REG_NAME), BackpackDynamicModel.Loader.INSTANCE);
	}

	private static void clientSetup(FMLClientSetupEvent event) {
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.BACKPACK.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.IRON_BACKPACK.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.GOLD_BACKPACK.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.DIAMOND_BACKPACK.get(), RenderType.cutout());
		ItemBlockRenderTypes.setRenderLayer(ModBlocks.NETHERITE_BACKPACK.get(), RenderType.cutout());
	}

	public static void registerReloadListener(ParticleFactoryRegisterEvent event) {
		((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener((ResourceManagerReloadListener) resourceManager -> registerBackpackLayer());
	}

	private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(EVERLASTING_BACKPACK_ITEM_ENTITY.get(), ItemEntityRenderer::new);
		event.registerBlockEntityRenderer(ModBlocks.BACKPACK_TILE_TYPE.get(), BackpackBlockEntityRenderer::new);
	}

	public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {
		event.registerLayerDefinition(BACKPACK_LAYER, BackpackModel::createBodyLayer);
	}

	@SuppressWarnings("java:S3740") //explanation below
	private static void registerBackpackLayer() {
		EntityRenderDispatcher renderManager = Minecraft.getInstance().getEntityRenderDispatcher();
		Map<String, EntityRenderer<? extends Player>> skinMap = renderManager.getSkinMap();
		for (EntityRenderer<? extends Player> renderer : skinMap.values()) {
			if (renderer instanceof LivingEntityRenderer livingEntityRenderer) {
				//noinspection rawtypes ,unchecked - this is not going to fail as the LivingRenderer makes sure the types are right, but there doesn't seem to be a way to us inference here
				livingEntityRenderer.addLayer(new BackpackLayerRenderer(livingEntityRenderer));
			}
		}
		renderManager.renderers.forEach((e, r) -> {
			if (r instanceof LivingEntityRenderer livingEntityRenderer) {
				//noinspection rawtypes ,unchecked - this is not going to fail as the LivingRenderer makes sure the types are right, but there doesn't seem to be a way to us inference here
				livingEntityRenderer.addLayer(new BackpackLayerRenderer(livingEntityRenderer));
			}
		});
	}
}