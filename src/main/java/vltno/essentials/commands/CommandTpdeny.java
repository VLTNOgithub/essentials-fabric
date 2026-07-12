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

public class CommandTpdeny {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"tpdeny", "etpdeny", "tpno", "etpno"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.tpdeny", 0))
        .executes(context -> executeTpdeny(context))
    );
        }


    }

    public static int executeTpdeny(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            TeleportRequest req = pendingRequests.remove(player.getUUID());
            if (req == null) {
                context.getSource().sendSystemMessage(Component.literal("You do not have any pending teleport requests."));
                return 0;
            }
            ServerPlayer sender = context.getSource().getServer().getPlayerList().getPlayer(req.sender);
            if (sender != null) {
                sender.sendSystemMessage(Component.literal(player.getName().getString() + " denied your teleport request."));
            }
            context.getSource().sendSystemMessage(Component.literal("Teleport request denied."));
            return 1;
        }

}
