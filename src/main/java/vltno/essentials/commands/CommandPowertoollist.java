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

public class CommandPowertoollist {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> powertoollistCmd = Commands.literal("powertoollist")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.powertoollist", 2))
            .executes(context -> executePowertoollist(context))
        ;
        dispatcher.register(powertoollistCmd);
        dispatcher.register(Commands.literal("epowertoollist").executes(powertoollistCmd.getCommand()).redirect(powertoollistCmd.build()));
        dispatcher.register(Commands.literal("ptlist").executes(powertoollistCmd.getCommand()).redirect(powertoollistCmd.build()));
        dispatcher.register(Commands.literal("eptlist").executes(powertoollistCmd.getCommand()).redirect(powertoollistCmd.build()));


    }

    public static int executePowertoollist(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        net.minecraft.server.level.ServerPlayer player = context.getSource().getPlayerOrException();
        vltno.essentials.UserData data = vltno.essentials.UserCache.getUser(player);
        if (data.powertools.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You have no active powertools."));
        } else {
            context.getSource().sendSystemMessage(Component.literal("Your Powertools:"));
            for (java.util.Map.Entry<String, String> entry : data.powertools.entrySet()) {
                context.getSource().sendSystemMessage(Component.literal(" - " + entry.getKey() + ": /" + entry.getValue()));
            }
        }
        return 1;
    }

}
