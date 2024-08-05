package net.p3pp3rf1y.sophisticatedbackpacks.compat.chipped;

import earth.terrarium.chipped.Chipped;
import earth.terrarium.chipped.recipe.ChippedRecipe;
import earth.terrarium.chipped.registry.ModRecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import net.p3pp3rf1y.sophisticatedbackpacks.Config;
import net.p3pp3rf1y.sophisticatedbackpacks.SophisticatedBackpacks;
import net.p3pp3rf1y.sophisticatedbackpacks.client.gui.SBPButtonDefinitions;
import net.p3pp3rf1y.sophisticatedbackpacks.compat.jei.SBPPlugin;
import net.p3pp3rf1y.sophisticatedbackpacks.init.ModItems;
import net.p3pp3rf1y.sophisticatedcore.client.gui.StorageScreenBase;
import net.p3pp3rf1y.sophisticatedcore.client.gui.UpgradeGuiManager;
import net.p3pp3rf1y.sophisticatedcore.client.gui.utils.Position;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerRegistry;
import net.p3pp3rf1y.sophisticatedcore.common.gui.UpgradeContainerType;
import net.p3pp3rf1y.sophisticatedcore.compat.CompatModIds;
import net.p3pp3rf1y.sophisticatedcore.compat.ICompat;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeContainer;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeItem;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeTab;
import net.p3pp3rf1y.sophisticatedcore.compat.chipped.BlockTransformationUpgradeWrapper;

import java.util.function.Supplier;

public class ThirstCompat implements ICompat {
/*
	public static final RegistryObject<BlockTransformationUpgradeItem> ADVANCE_DRINKING_UPGRADE = ModItems.ITEMS.register("thirst/advance_drinking_upgrade",
			() -> new BlockTransformationUpgradeItem(SophisticatedBackpacks.ITEM_GROUP, ModRecipeTypes.BOTANIST_WORKBENCH_TYPE, Config.SERVER.maxUpgradesPerStorage));
	public static final RegistryObject<BlockTransformationUpgradeItem> GLASSBLOWER_WORKBENCH_UPGRADE = ModItems.ITEMS.register("thirst/drinking_upgrade",
			() -> new BlockTransformationUpgradeItem(SophisticatedBackpacks.ITEM_GROUP, ModRecipeTypes.GLASSBLOWER_TYPE, Config.SERVER.maxUpgradesPerStorage));

	@Override
	public void init() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		modBus.addGenericListener(MenuType.class, this::registerContainers);

		if (ModList.get().isLoaded(CompatModIds.JEI)) {
			((Supplier<Runnable>) () -> () -> SBPPlugin.setAdditionalCatalystRegistrar(registration -> {
				registration.addRecipeCatalyst(new ItemStack(BOTANIST_WORKBENCH_UPGRADE.get()), new RecipeType<>(new ResourceLocation(Chipped.MOD_ID, "botanist_workbench"), ChippedRecipe.class));
				registration.addRecipeCatalyst(new ItemStack(GLASSBLOWER_WORKBENCH_UPGRADE.get()), new RecipeType<>(new ResourceLocation(Chipped.MOD_ID, "glassblower"), ChippedRecipe.class));

			})).get().run();
		}
	}

	public void registerContainers(RegistryEvent.Register<MenuType<?>> evt) {
		registerUpgradeContainer(BOTANIST_WORKBENCH_UPGRADE);
		registerUpgradeContainer(GLASSBLOWER_WORKBENCH_UPGRADE);
		registerUpgradeContainer(CARPENTER_WORKBENCH_UPGRADE);
		registerUpgradeContainer(SHEPHERD_WORKBENCH_UPGRADE);
		registerUpgradeContainer(MASON_WORKBENCH_UPGRADE);
		registerUpgradeContainer(PHILOSOPHER_WORKBENCH_UPGRADE);
		registerUpgradeContainer(TINKERER_WORKBENCH_UPGRADE);
	}

	private void registerUpgradeContainer(RegistryObject<BlockTransformationUpgradeItem> item) {
		UpgradeContainerType<BlockTransformationUpgradeWrapper, BlockTransformationUpgradeContainer> containerType = new UpgradeContainerType<>(BlockTransformationUpgradeContainer::new);
		UpgradeContainerRegistry.register(item.getId(), containerType);
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> UpgradeGuiManager.registerTab(containerType, (BlockTransformationUpgradeContainer upgradeContainer, Position position, StorageScreenBase<?> screen) -> {
			String itemName = item.getId().getPath();
			return new BlockTransformationUpgradeTab(upgradeContainer, position, screen, SBPButtonDefinitions.SHIFT_CLICK_TARGET, itemName.replace('/', '_').substring(0, itemName.length() - "_upgrade".length()));
		}));
	}

	@Override
	public void setup() {
		//noop
	}*/
}
