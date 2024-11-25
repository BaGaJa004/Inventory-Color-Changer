package net.bagaja.colorinventory;

import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Renderable;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
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
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import java.util.Iterator;

import static net.bagaja.colorinventory.KeyBindings.COLOR_PICKER_KEY;
import static net.bagaja.colorinventory.KeyBindings.TOGGLE_OVERLAY_KEY;

@Mod("colorinventory")
@EventBusSubscriber(bus = EventBusSubscriber.Bus.FORGE)
public class ColorInventoryMod {
    public static final String MODID = "colorinventory";
    private static int inventoryColor = 0xFF0000; // Default red color
    private static boolean overlayEnabled = true; // Default enabled
    private static float inventoryAlpha = 0.7f; // Default alpha value
    private static boolean useTransparentInventory = false;

    private static final ResourceLocation INVENTORY_LOCATION =
            new ResourceLocation("textures/gui/container/inventory.png");
    private static final ResourceLocation RECIPE_BUTTON_LOCATION =
            new ResourceLocation("colorinventory", "textures/gui/container/button_transparent.png");
    private static final ResourceLocation EMPTY_ARMOR_SLOT_HELMET =
            new ResourceLocation("textures/item/empty_armor_slot_helmet.png");
    private static final ResourceLocation EMPTY_ARMOR_SLOT_CHESTPLATE =
            new ResourceLocation("textures/item/empty_armor_slot_chestplate.png");
    private static final ResourceLocation EMPTY_ARMOR_SLOT_LEGGINGS =
            new ResourceLocation("textures/item/empty_armor_slot_leggings.png");
    private static final ResourceLocation EMPTY_ARMOR_SLOT_BOOTS =
            new ResourceLocation("textures/item/empty_armor_slot_boots.png");
    private static final ResourceLocation EMPTY_SLOT_SHIELD =
            new ResourceLocation("textures/item/empty_armor_slot_shield.png");
    private static final ResourceLocation TRANSPARENT_INVENTORY_LOCATION =
            new ResourceLocation("colorinventory", "textures/gui/container/inventory_transparent.png");


    public ColorInventoryMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }

    public static int getInventoryColor() {
        return inventoryColor;
    }

    public static boolean isOverlayEnabled() {
        return overlayEnabled;
    }

    public static float getInventoryAlpha() {
        return inventoryAlpha;
    }

    public static void setInventoryAlpha(float alpha) {
        inventoryAlpha = alpha;
        Config.saveAlpha(alpha);
    }

    public static void setUseTransparentInventory(boolean useTransparent) {
        useTransparentInventory = useTransparent;
        Config.saveUseTransparentInventory(useTransparent);
    }

    public static boolean isUsingTransparentInventory() {
        return useTransparentInventory;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onGuiPreRender(ScreenEvent.Render.Pre event) {
        if (!overlayEnabled) return;

        if (event.getScreen() instanceof InventoryScreen inventoryScreen) {
            if (useTransparentInventory) {
                event.setCanceled(true);
            }

            // Remove the vanilla recipe book button
            Iterator<Renderable> iterator = inventoryScreen.renderables.iterator();
            while (iterator.hasNext()) {
                Renderable renderable = iterator.next();
                if (renderable instanceof ImageButton) {
                    ImageButton button = (ImageButton) renderable;
                    int x = (inventoryScreen.width - 176) / 2;
                    int y = (inventoryScreen.height - 166) / 2;
                    if (button.getX() == x + 104 && button.getY() == y + 61) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onGuiRender(ScreenEvent.Render.Post event) {
        if (!overlayEnabled) return; // Skip rendering if overlay is disabled

        if (event.getScreen() instanceof InventoryScreen inventoryScreen) {
            GuiGraphics graphics = event.getGuiGraphics();

            // Get recipe book component
            RecipeBookComponent recipeBook = inventoryScreen.getRecipeBookComponent();

            // Calculate the x position based on recipe book state
            int xOffset = recipeBook.isVisible() ? 77 : 0;
            int x = (inventoryScreen.width - 176) / 2 + xOffset;
            int y = (inventoryScreen.height - 166) / 2;

            // Store the current color and blend state
            float[] prevColor = RenderSystem.getShaderColor().clone();

            // Setup rendering state
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            // Render colored overlay
            RenderSystem.setShaderColor(
                    ((inventoryColor >> 16) & 0xFF) / 255.0F,
                    ((inventoryColor >> 8) & 0xFF) / 255.0F,
                    (inventoryColor & 0xFF) / 255.0F,
                    inventoryAlpha  // Use the configurable alpha value instead of hardcoded 0.7F
            );

            // Main inventory area
            ResourceLocation textureToUse = useTransparentInventory ?
                    TRANSPARENT_INVENTORY_LOCATION : INVENTORY_LOCATION;
            RenderSystem.setShaderTexture(0, textureToUse);
            graphics.blit(textureToUse, x, y, 0, 0, 176, 166);

            // Restore original color state
            RenderSystem.setShaderColor(prevColor[0], prevColor[1], prevColor[2], prevColor[3]);

            // Re-render the armor slot icons above the colored overlay
            int armorX = x + 8;
            int armorY = y + 8;

            if (Minecraft.getInstance().player != null) {
                // Helmet slot
                if (Minecraft.getInstance().player.getInventory().getArmor(3).isEmpty()) {
                    RenderSystem.setShaderTexture(0, EMPTY_ARMOR_SLOT_HELMET);
                    graphics.blit(EMPTY_ARMOR_SLOT_HELMET, armorX, armorY, 0, 0, 16, 16, 16, 16);
                }

                // Chestplate slot
                if (Minecraft.getInstance().player.getInventory().getArmor(2).isEmpty()) {
                    RenderSystem.setShaderTexture(0, EMPTY_ARMOR_SLOT_CHESTPLATE);
                    graphics.blit(EMPTY_ARMOR_SLOT_CHESTPLATE, armorX, armorY + 18, 0, 0, 16, 16, 16, 16);
                }

                // Leggings slot
                if (Minecraft.getInstance().player.getInventory().getArmor(1).isEmpty()) {
                    RenderSystem.setShaderTexture(0, EMPTY_ARMOR_SLOT_LEGGINGS);
                    graphics.blit(EMPTY_ARMOR_SLOT_LEGGINGS, armorX, armorY + 36, 0, 0, 16, 16, 16, 16);
                }

                // Boots slot
                if (Minecraft.getInstance().player.getInventory().getArmor(0).isEmpty()) {
                    RenderSystem.setShaderTexture(0, EMPTY_ARMOR_SLOT_BOOTS);
                    graphics.blit(EMPTY_ARMOR_SLOT_BOOTS, armorX, armorY + 54, 0, 0, 16, 16, 16, 16);
                }

                // Offhand slot
                if (Minecraft.getInstance().player.getOffhandItem().isEmpty()) {
                    RenderSystem.setShaderTexture(0, EMPTY_SLOT_SHIELD);
                    graphics.blit(EMPTY_SLOT_SHIELD, x + 77, y + 62, 0, 0, 16, 16, 16, 16);
                }
            }

            // Re-render the recipe book button on top
            RenderSystem.setShaderTexture(0, RECIPE_BUTTON_LOCATION);
            int buttonX = x + 104;
            int buttonY = y + 61;
            graphics.blit(RECIPE_BUTTON_LOCATION, buttonX, buttonY, 0, 0, 20, 18, 20, 18);

            // Re-render the "Crafting" text
            Component craftingText = Component.translatable("container.crafting");
            graphics.drawString(Minecraft.getInstance().font, craftingText,
                    x + 97, y + 6, 0x404040, // Default text color
                    false); // Don't use drop shadow

            // Reset blend state
            RenderSystem.disableBlend();
        }
    }

    public static void setInventoryColor(int color) {
        inventoryColor = color;
        // Save the color to config when it changes
        Config.saveColor(color);
    }

    public static void setOverlayEnabled(boolean enabled) {
        overlayEnabled = enabled;
        Config.saveOverlayEnabled(enabled);
    }

    public static void toggleOverlay() {
        setOverlayEnabled(!overlayEnabled);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft minecraft = Minecraft.getInstance();
            if (KeyBindings.COLOR_PICKER_KEY.consumeClick() && minecraft.player != null) {
                minecraft.setScreen(new ColorPickerScreen(minecraft.screen));
            }
            if (KeyBindings.TOGGLE_OVERLAY_KEY.consumeClick() && minecraft.player != null) {
                toggleOverlay();
            }
        }
    }
}

// tool bar behind color layer
// add transparency