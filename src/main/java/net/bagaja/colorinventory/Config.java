package net.bagaja.colorinventory;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid = ColorInventoryMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue INVENTORY_COLOR = BUILDER
            .comment("The color of the inventory overlay (in hex format without alpha)")
            .defineInRange("inventoryColor", 0xFF0000, 0x000000, 0xFFFFFF);

    private static final ForgeConfigSpec.BooleanValue COLOR_OVERLAY_ENABLED = BUILDER
            .comment("Whether the color overlay is enabled")
            .define("colorOverlayEnabled", true);

    private static final ForgeConfigSpec.DoubleValue INVENTORY_ALPHA = BUILDER
            .comment("The alpha (transparency) value of the inventory overlay (0.0 - 1.0)")
            .defineInRange("inventoryAlpha", 0.7, 0.0, 1.0);

    public static float inventoryAlpha;

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

        inventoryAlpha = INVENTORY_ALPHA.get().floatValue();
        ColorInventoryMod.setInventoryAlpha(inventoryAlpha);
    }

    public static void saveColor(int color) {
        INVENTORY_COLOR.set(color);
        inventoryColor = color;
    }

    public static void saveOverlayEnabled(boolean enabled) {
        COLOR_OVERLAY_ENABLED.set(enabled);
        colorOverlayEnabled = enabled;
    }

    public static void saveAlpha(float alpha) {
        INVENTORY_ALPHA.set((double)alpha);
        inventoryAlpha = alpha;
    }
}