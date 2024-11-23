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

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static int inventoryColor;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        inventoryColor = INVENTORY_COLOR.get();
        // Set the color when config loads
        ColorInventoryMod.setInventoryColor(inventoryColor);
    }

    public static void saveColor(int color) {
        INVENTORY_COLOR.set(color);
        inventoryColor = color;
    }
}