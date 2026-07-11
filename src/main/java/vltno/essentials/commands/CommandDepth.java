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

public class CommandDepth {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> depthCmd = Commands.literal("depth")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.depth", 0))
            .executes(context -> executeDepth(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> depthCmdNode = dispatcher.register(depthCmd);
        dispatcher.register(Commands.literal("edepth").requires(depthCmdNode.getRequirement()).redirect(depthCmdNode));
        dispatcher.register(Commands.literal("height").requires(depthCmdNode.getRequirement()).redirect(depthCmdNode));
        dispatcher.register(Commands.literal("eheight").requires(depthCmdNode.getRequirement()).redirect(depthCmdNode));


    }

    public static int executeDepth(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            int depth = player.getBlockY() - player.level().getMinY();
            context.getSource().sendSystemMessage(Component.literal("You are " + depth + " blocks above minimum depth."));
            return 1;
        }

}
