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

public class CommandInfo {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> infoCmd = Commands.literal("info")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.info", 0))
            .executes(context -> executeInfo(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> infoCmdNode = dispatcher.register(infoCmd);
        dispatcher.register(Commands.literal("about").requires(infoCmdNode.getRequirement()).redirect(infoCmdNode));
        dispatcher.register(Commands.literal("eabout").requires(infoCmdNode.getRequirement()).redirect(infoCmdNode));
        dispatcher.register(Commands.literal("ifo").requires(infoCmdNode.getRequirement()).redirect(infoCmdNode));
        dispatcher.register(Commands.literal("eifo").requires(infoCmdNode.getRequirement()).redirect(infoCmdNode));
        dispatcher.register(Commands.literal("einfo").requires(infoCmdNode.getRequirement()).redirect(infoCmdNode));
        dispatcher.register(Commands.literal("inform").requires(infoCmdNode.getRequirement()).redirect(infoCmdNode));
        dispatcher.register(Commands.literal("einform").requires(infoCmdNode.getRequirement()).redirect(infoCmdNode));
        dispatcher.register(Commands.literal("news").requires(infoCmdNode.getRequirement()).redirect(infoCmdNode));
        dispatcher.register(Commands.literal("enews").requires(infoCmdNode.getRequirement()).redirect(infoCmdNode));


    }

    public static int executeInfo(CommandContext<CommandSourceStack> context) {
        java.io.File infoFile = new java.io.File("config/essentials-fabric/info.txt");
        if (infoFile.exists()) {
            try {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(infoFile.toPath());
                for (String line : lines) {
                    context.getSource().sendSystemMessage(Component.literal(line.replace("&", "\u00A7")));
                }
            } catch (Exception e) {
                context.getSource().sendSystemMessage(Component.literal("Failed to read info.txt").withStyle(net.minecraft.ChatFormatting.RED));
            }
        } else {
            context.getSource().sendSystemMessage(Component.literal("Server Info not configured."));
        }
        return 1;
    }

}
