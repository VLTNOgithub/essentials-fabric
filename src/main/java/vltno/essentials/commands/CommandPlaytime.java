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

public class CommandPlaytime {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> playtimeCmd = Commands.literal("playtime")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.playtime", 0))
            .executes(context -> executePlaytime(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> playtimeCmdNode = dispatcher.register(playtimeCmd);
        dispatcher.register(Commands.literal("eplaytime").requires(playtimeCmdNode.getRequirement()).redirect(playtimeCmdNode));


    }

    public static int executePlaytime(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            int ticks = player.getStats().getValue(net.minecraft.stats.Stats.CUSTOM.get(net.minecraft.stats.Stats.PLAY_TIME));
            context.getSource().sendSystemMessage(Component.literal("Playtime: " + (ticks / 20 / 60) + " minutes"));
            return 1;
        }

}
