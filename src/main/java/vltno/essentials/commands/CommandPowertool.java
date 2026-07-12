package vltno.essentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Collections;
import vltno.essentials.UserCache;
import vltno.essentials.UserData;
import vltno.essentials.EssentialsCommands;
import static vltno.essentials.EssentialsCommands.*;

public class CommandPowertool {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"powertool", "epowertool", "pt", "ept"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.powertool", 2))
            .executes(context -> executePowertool(context, ""))
            .then(Commands.argument("command", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executePowertool(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "command")))
            ));
        }

    }

    public static int executePowertool(CommandContext<CommandSourceStack> context, String commandStr) throws CommandSyntaxException {
        net.minecraft.server.level.ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You must hold an item to assign a powertool to it.").withStyle(net.minecraft.ChatFormatting.RED));
            return 0;
        }
        String itemId = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(hand.getItem()).toString();
        vltno.essentials.UserData data = vltno.essentials.UserCache.getUser(player);
        if (commandStr.isEmpty()) {
            if (data.powertools.remove(itemId) != null) {
                context.getSource().sendSystemMessage(Component.literal("Powertool removed from " + itemId));
            } else {
                context.getSource().sendSystemMessage(Component.literal("No powertool assigned to " + itemId));
            }
        } else {
            data.powertools.put(itemId, commandStr);
            context.getSource().sendSystemMessage(Component.literal("Powertool assigned to " + itemId + ": /" + commandStr));
        }
        vltno.essentials.UserCache.saveUser(player.getUUID());
        return 1;
    }

}
