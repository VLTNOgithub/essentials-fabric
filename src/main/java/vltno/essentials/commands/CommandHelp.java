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

public class CommandHelp {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> helpCmd = Commands.literal("help")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.help", 0))
            .executes(context -> executeHelp(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> helpCmdNode = dispatcher.register(helpCmd);
        dispatcher.register(Commands.literal("ehelp").requires(helpCmdNode.getRequirement()).redirect(helpCmdNode));


    }

    public static int executeHelp(CommandContext<CommandSourceStack> context) {
        java.io.File helpFile = new java.io.File("config/essentials-fabric/help.txt");
        if (helpFile.exists()) {
            try {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(helpFile.toPath());
                for (String line : lines) {
                    context.getSource().sendSystemMessage(Component.literal(line.replace("&", "\u00A7")));
                }
                return 1;
            } catch (Exception e) {
                context.getSource().sendSystemMessage(Component.literal("Failed to read help.txt").withStyle(net.minecraft.ChatFormatting.RED));
            }
        }
        // Fallback to basic message or vanilla help could still exist if we didn't fully override the root node
        context.getSource().sendSystemMessage(Component.literal("Help menus not configured."));
        return 1;
    }

}
