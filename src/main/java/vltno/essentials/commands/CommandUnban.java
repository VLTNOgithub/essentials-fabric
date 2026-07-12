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

public class CommandUnban {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"unban", "pardon", "eunban", "epardon"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.unban", 2))
        .executes(context -> executeUnban(context, Collections.emptyList()))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.GameProfileArgument.gameProfile())
            .executes(context -> executeUnban(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets")))
        )
    );
        }


    }

    public static int executeUnban(CommandContext<CommandSourceStack> context, Collection<net.minecraft.server.players.NameAndId> targets) {
            if (targets.isEmpty()) {
                context.getSource().sendSystemMessage(Component.literal("Please specify a player to unban."));
                return 0;
            }
            net.minecraft.server.players.UserBanList banList = context.getSource().getServer().getPlayerList().getBans();
            for (net.minecraft.server.players.NameAndId profile : targets) {
                banList.remove(profile);
            }
            context.getSource().sendSystemMessage(Component.literal("Unbanned " + targets.size() + " players."));
            return targets.size();
        }

}
