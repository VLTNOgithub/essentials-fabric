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

public class CommandCustomtext {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("customtext")
            .executes(context -> executeCustomtext(context))
        );

    }

    public static int executeCustomtext(CommandContext<CommandSourceStack> context) {
        // In Essentials, custom text reads from a file like custom.txt or similar.
        // Since we don't have bukkit.yml aliases here, this command just reads customtext.txt
        java.io.File file = new java.io.File("config/essentials-fabric/customtext.txt");
        if (file.exists()) {
            try {
                for (String line : java.nio.file.Files.readAllLines(file.toPath())) {
                    context.getSource().sendSystemMessage(Component.literal(line.replace("&", "\u00A7")));
                }
                return 1;
            } catch (Exception e) {
                context.getSource().sendSystemMessage(Component.literal("Failed to read customtext.txt").withStyle(net.minecraft.ChatFormatting.RED));
            }
        } else {
            context.getSource().sendSystemMessage(Component.literal("Custom text file not found."));
        }
        return 1;
    }

}
