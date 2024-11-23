package net.bagaja.colorinventory;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ColorInventoryMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue INVENTORY_COLOR = BUILDER
            .comment("The color of the inventory overlay (in hex format without alpha)")
            .defineInRange("inventoryColor", 0xFF0000, 0x000000, 0xFFFFFF);

    private static final ForgeConfigSpec.BooleanValue COLOR_OVERLAY_ENABLED = BUILDER
            .comment("Whether the color overlay is enabled")
            .define("colorOverlayEnabled", true);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int inventoryColor;
    public static boolean colorOverlayEnabled;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        inventoryColor = INVENTORY_COLOR.get();
        colorOverlayEnabled = COLOR_OVERLAY_ENABLED.get();
        // Set the color when config loads
        ColorInventoryMod.setInventoryColor(inventoryColor);
        ColorInventoryMod.setOverlayEnabled(colorOverlayEnabled);
    }

    public static void saveColor(int color) {
        INVENTORY_COLOR.set(color);
        inventoryColor = color;
    }

    public static void saveOverlayEnabled(boolean enabled) {
        COLOR_OVERLAY_ENABLED.set(enabled);
        colorOverlayEnabled = enabled;
    }
}