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

public class CommandTpall {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"tpall", "etpall"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.tpall", 0))
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpall(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        ));
        }

    }

    public static int executeTpall(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpall <player>")); return 0; }

    public static int executeTpall(CommandContext<CommandSourceStack> context, ServerPlayer target) {
            int count = 0;
            for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
                if (player != target) {
                    player.teleportTo(target.level(), target.getX(), target.getY(), target.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
                    count++;
                }
            }
            context.getSource().sendSystemMessage(Component.literal("Teleported " + count + " players to " + target.getName().getString() + "."));
            return count;
        }

}
