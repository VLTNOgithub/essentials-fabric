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

public class CommandRules {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> rulesCmd = Commands.literal("rules")
            .executes(context -> executeRules(context))
        ;
        dispatcher.register(rulesCmd);
        dispatcher.register(Commands.literal("erules").redirect(rulesCmd.build()));


    }

    public static int executeRules(CommandContext<CommandSourceStack> context) {
        java.io.File rulesFile = new java.io.File("config/essentials-fabric/rules.txt");
        if (rulesFile.exists()) {
            try {
                java.util.List<String> lines = java.nio.file.Files.readAllLines(rulesFile.toPath());
                for (String line : lines) {
                    context.getSource().sendSystemMessage(Component.literal(line.replace("&", "\u00A7")));
                }
            } catch (Exception e) {
                context.getSource().sendSystemMessage(Component.literal("Failed to read rules.txt").withStyle(net.minecraft.ChatFormatting.RED));
            }
        } else {
            context.getSource().sendSystemMessage(Component.literal("1. Be nice."));
        }
        return 1;
    }

}
