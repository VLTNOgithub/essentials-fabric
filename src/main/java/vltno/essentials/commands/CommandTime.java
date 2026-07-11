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

public class CommandTime {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> timeCmd = Commands.literal("time")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.time", 2))
            .then(Commands.literal("day").executes(context -> executeTime(context, 1000)))
            .then(Commands.literal("night").executes(context -> executeTime(context, 13000)));
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> timeCmdNode = dispatcher.register(timeCmd);
        dispatcher.register(Commands.literal("day").executes(context -> executeTime(context, 1000)));
        dispatcher.register(Commands.literal("eday").executes(context -> executeTime(context, 1000)));
        dispatcher.register(Commands.literal("night").executes(context -> executeTime(context, 13000)));
        dispatcher.register(Commands.literal("enight").executes(context -> executeTime(context, 13000)));
        dispatcher.register(Commands.literal("etime").requires(timeCmdNode.getRequirement()).redirect(timeCmdNode));

    }

    public static int executeTime(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /time <day|night>")); return 0; }

    public static int executeTime(CommandContext<CommandSourceStack> context, int time) {
            context.getSource().getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD).setDayTime(time);
            context.getSource().sendSystemMessage(Component.literal("Time set to " + time + "."));
            return 1;
        }

}
