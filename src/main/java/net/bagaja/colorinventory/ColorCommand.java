package net.bagaja.colorinventory;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ColorCommand {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("inventorycolor")
                        .then(Commands.argument("color", IntegerArgumentType.integer(0, 0xFFFFFF))
                                .executes(context -> {
                                    int color = IntegerArgumentType.getInteger(context, "color");
                                    ColorInventoryMod.setInventoryColor(color);
                                    context.getSource().sendSuccess(() ->
                                            Component.literal("Inventory color set to: " +
                                                    String.format("#%06X", color)), false);
                                    return Command.SINGLE_SUCCESS;
                                }))
        );
    }
}