// Create a new class called KeyBindings.java
package net.bagaja.colorinventory;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = ColorInventoryMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KeyBindings {
    public static final KeyMapping COLOR_PICKER_KEY = new KeyMapping(
            "key.colorinventory.picker",
            GLFW.GLFW_KEY_K,
            "category.colorinventory"
    );

    public static final KeyMapping TOGGLE_OVERLAY_KEY = new KeyMapping(
            "key.colorinventory.toggle",
            GLFW.GLFW_KEY_O,
            "category.colorinventory"
    );

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(COLOR_PICKER_KEY);
        event.register(TOGGLE_OVERLAY_KEY);
    }
}