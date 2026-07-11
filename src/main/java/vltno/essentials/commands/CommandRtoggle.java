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

public class CommandRtoggle {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> rtoggleCmd = Commands.literal("rtoggle")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.rtoggle", 0))
            .executes(context -> executeRtoggle(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> rtoggleCmdNode = dispatcher.register(rtoggleCmd);
        dispatcher.register(Commands.literal("ertoggle").requires(rtoggleCmdNode.getRequirement()).redirect(rtoggleCmdNode));
        dispatcher.register(Commands.literal("replytoggle").requires(rtoggleCmdNode.getRequirement()).redirect(rtoggleCmdNode));
        dispatcher.register(Commands.literal("ereplytoggle").requires(rtoggleCmdNode.getRequirement()).redirect(rtoggleCmdNode));


    }

    public static int executeRtoggle(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Reply toggle changed."));
            return 1;
        }

}
