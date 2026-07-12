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

public class CommandSeen {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"seen", "eseen", "ealts", "alts"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.seen", 2))
            .then(Commands.argument("target", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeSeen(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "target")))
            ));
        }

    }

    public static int executeSeen(CommandContext<CommandSourceStack> context, String targetName) {
        // Check online players first
        ServerPlayer p = context.getSource().getServer().getPlayerList().getPlayerByName(targetName);
        if (p != null) {
            context.getSource().sendSystemMessage(Component.literal(p.getName().getString() + " is online now!"));
            return 1;
        }

        // Check offline positions cache as a heuristic for last logout
        HomePosition pos = offlinePositions.get(targetName.toLowerCase());
        if (pos != null) {
            context.getSource().sendSystemMessage(Component.literal(targetName + " was last seen logging out at " + pos.dimension));
            return 1;
        }

        // Finally, look in UserCache for any saved data
        for (java.util.Map.Entry<java.util.UUID, UserData> entry : UserCache.getLoadedUsers().entrySet()) {
            if (entry.getValue().nickname != null && entry.getValue().nickname.equalsIgnoreCase(targetName)) {
                context.getSource().sendSystemMessage(Component.literal(targetName + " is offline. (Matched by nickname)"));
                return 1;
            }
        }

        context.getSource().sendSystemMessage(Component.literal("Player not found or has never joined."));
        return 0;
    }

}
