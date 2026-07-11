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

public class CommandMotd {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> motdCmd = Commands.literal("motd")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.motd", 0))
            .executes(context -> executeMotd(context))
        ;
        dispatcher.register(motdCmd);
        dispatcher.register(Commands.literal("emotd").redirect(motdCmd.build()));


    }

    public static int executeMotd(CommandContext<CommandSourceStack> context) {
        java.io.File motdFile = new java.io.File("config/essentials-fabric/motd.txt");
        if (motdFile.exists()) {
            try {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(motdFile.toPath());
                for (String line : lines) {
                    context.getSource().sendSystemMessage(Component.literal(line.replace("&", "\u00A7")));
                }
            } catch (Exception e) {
                context.getSource().sendSystemMessage(Component.literal("Failed to read motd.txt").withStyle(net.minecraft.ChatFormatting.RED));
            }
        } else {
            context.getSource().sendSystemMessage(Component.literal("Welcome to the server!"));
        }
        return 1;
    }

}
