package net.bagaja.colorinventory;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;

@Mod("colorinventory")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE)
public class ColorInventoryMod {
    public static final String MODID = "colorinventory";
    private static int inventoryColor = 0xFF0000; // Default red color
    private static final ResourceLocation INVENTORY_LOCATION =
            new ResourceLocation("textures/gui/container/inventory.png");

    public ColorInventoryMod() {
        // Constructor
    }

    @SubscribeEvent
    public static void onGuiRender(ScreenEvent.Render event) {
        if (event.getScreen() instanceof InventoryScreen) {
            GuiGraphics graphics = event.getGuiGraphics();
            InventoryScreen screen = (InventoryScreen) event.getScreen();

            // Get the screen coordinates
            int x = (screen.width - 176) / 2;
            int y = (screen.height - 166) / 2;

            // Apply color overlay
            RenderSystem.setShaderColor(
                    ((inventoryColor >> 16) & 0xFF) / 255.0F,
                    ((inventoryColor >> 8) & 0xFF) / 255.0F,
                    (inventoryColor & 0xFF) / 255.0F,
                    1.0F
            );

            // Render the inventory background with color
            graphics.blit(INVENTORY_LOCATION, x, y, 0, 0, 176, 166);

            // Reset color
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    // Method to change inventory color
    public static void setInventoryColor(int color) {
        inventoryColor = color;
    }
}
