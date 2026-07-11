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

public class CommandJailedplayers {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> jailedplayersCmd = Commands.literal("jailedplayers")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.jailedplayers", 2))
            .executes(context -> executeJailedplayers(context))
        ;
        dispatcher.register(jailedplayersCmd);
        dispatcher.register(Commands.literal("ejailedplayers").executes(jailedplayersCmd.getCommand()).redirect(jailedplayersCmd.build()));
        dispatcher.register(Commands.literal("ejailed").executes(jailedplayersCmd.getCommand()).redirect(jailedplayersCmd.build()));
        dispatcher.register(Commands.literal("ejp").executes(jailedplayersCmd.getCommand()).redirect(jailedplayersCmd.build()));


    }

    public static int executeJailedplayers(CommandContext<CommandSourceStack> context) {
        java.util.List<String> jailedNames = new java.util.ArrayList<>();
        for (java.util.Map.Entry<java.util.UUID, vltno.essentials.UserData> entry : vltno.essentials.UserCache.getLoadedUsers().entrySet()) {
            if (entry.getValue().jail != null) {
                // Try to find online name, otherwise use offline nickname if available
                net.minecraft.server.level.ServerPlayer p = context.getSource().getServer().getPlayerList().getPlayer(entry.getKey());
                if (p != null) {
                    jailedNames.add(p.getName().getString() + " (" + entry.getValue().jail + ")");
                } else if (entry.getValue().nickname != null) {
                    jailedNames.add(entry.getValue().nickname + " (" + entry.getValue().jail + ")");
                }
            }
        }
        if (jailedNames.isEmpty()) {
            context.getSource().sendSystemMessage(net.minecraft.network.chat.Component.literal("There are no jailed players."));
        } else {
            context.getSource().sendSystemMessage(net.minecraft.network.chat.Component.literal("Jailed Players: " + String.join(", ", jailedNames)));
        }
        return 1;
    }

}
