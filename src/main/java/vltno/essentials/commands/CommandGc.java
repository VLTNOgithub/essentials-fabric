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

public class CommandGc {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> gcCmd = Commands.literal("gc")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.gc", 2))
            .executes(context -> executeGc(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> gcCmdNode = dispatcher.register(gcCmd);
        dispatcher.register(Commands.literal("lag").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));
        dispatcher.register(Commands.literal("elag").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));
        dispatcher.register(Commands.literal("egc").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));
        dispatcher.register(Commands.literal("mem").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));
        dispatcher.register(Commands.literal("emem").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));
        dispatcher.register(Commands.literal("memory").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));
        dispatcher.register(Commands.literal("ememory").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));
        dispatcher.register(Commands.literal("uptime").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));
        dispatcher.register(Commands.literal("euptime").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));
        dispatcher.register(Commands.literal("tps").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));
        dispatcher.register(Commands.literal("etps").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));
        dispatcher.register(Commands.literal("entities").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));
        dispatcher.register(Commands.literal("eentities").requires(gcCmdNode.getRequirement()).redirect(gcCmdNode));


    }

    public static int executeGc(CommandContext<CommandSourceStack> context) {
            long maxMem = Runtime.getRuntime().maxMemory() / 1024 / 1024;
            long totalMem = Runtime.getRuntime().totalMemory() / 1024 / 1024;
            long freeMem = Runtime.getRuntime().freeMemory() / 1024 / 1024;
            long usedMem = totalMem - freeMem;
            context.getSource().sendSystemMessage(Component.literal("Max Memory: " + maxMem + " MB"));
            context.getSource().sendSystemMessage(Component.literal("Allocated Memory: " + totalMem + " MB"));
            context.getSource().sendSystemMessage(Component.literal("Free Memory: " + freeMem + " MB"));
            context.getSource().sendSystemMessage(Component.literal("Used Memory: " + usedMem + " MB"));
            return 1;
        }

}
