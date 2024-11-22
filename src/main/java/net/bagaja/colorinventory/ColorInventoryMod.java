package net.bagaja.colorinventory;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraft.client.Minecraft;

@Mod("colorinventory")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE)
public class ColorInventoryMod {
    public static final String MODID = "colorinventory";
    private static int inventoryColor = 0xFF0000; // Default red color
    private static final ResourceLocation INVENTORY_LOCATION =
            new ResourceLocation("textures/gui/container/inventory.png");
    private static final ResourceLocation RECIPE_BUTTON_LOCATION =
            new ResourceLocation("textures/gui/recipe_button.png");

    public static final KeyMapping COLOR_PICKER_KEY = new KeyMapping(
            "key.colorinventory.picker",
            GLFW.GLFW_KEY_P,
            "key.categories.colorinventory"
    );

    public ColorInventoryMod() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onGuiRender(ScreenEvent.Render.Post event) {  // Changed to Post to render after items
        if (event.getScreen() instanceof InventoryScreen) {
            GuiGraphics graphics = event.getGuiGraphics();
            InventoryScreen screen = (InventoryScreen) event.getScreen();

            int x = (screen.width - 176) / 2;
            int y = (screen.height - 166) / 2;

            // Store the current color state
            float[] prevColor = RenderSystem.getShaderColor().clone();

            // Setup rendering state
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            // Draw the colored inventory background
            RenderSystem.setShaderColor(
                    ((inventoryColor >> 16) & 0xFF) / 255.0F,
                    ((inventoryColor >> 8) & 0xFF) / 255.0F,
                    (inventoryColor & 0xFF) / 255.0F,
                    0.7F  // Added some transparency
            );

            // Bind and render the inventory texture
            RenderSystem.setShaderTexture(0, INVENTORY_LOCATION);
            graphics.blit(INVENTORY_LOCATION, x, y, 0, 0, 176, 166);

            // Restore the previous color state
            RenderSystem.setShaderColor(prevColor[0], prevColor[1], prevColor[2], prevColor[3]);

            // Reset blend state
            RenderSystem.disableBlend();
        }
    }

    public static void setInventoryColor(int color) {
        inventoryColor = color;
    }

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(COLOR_PICKER_KEY);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft minecraft = Minecraft.getInstance();
            if (COLOR_PICKER_KEY.consumeClick() && minecraft.player != null) {
                minecraft.setScreen(new ColorPickerScreen(minecraft.screen));
            }
        }
    }
}