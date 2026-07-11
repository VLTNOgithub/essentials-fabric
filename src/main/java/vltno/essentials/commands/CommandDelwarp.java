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

public class CommandDelwarp {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> delwarpCmd = Commands.literal("delwarp")
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeDelwarp(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        );
        dispatcher.register(delwarpCmd);
        dispatcher.register(Commands.literal("edelwarp").redirect(delwarpCmd.build()));
        dispatcher.register(Commands.literal("remwarp").redirect(delwarpCmd.build()));
        dispatcher.register(Commands.literal("eremwarp").redirect(delwarpCmd.build()));
        dispatcher.register(Commands.literal("rmwarp").redirect(delwarpCmd.build()));
        dispatcher.register(Commands.literal("ermwarp").redirect(delwarpCmd.build()));

    }

    public static int executeDelwarp(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /delwarp <name>")); return 0; }

    public static int executeDelwarp(CommandContext<CommandSourceStack> context, String name) {
            if (WARPS.remove(name.toLowerCase()) != null) {
                saveJailsWarps();
                context.getSource().sendSystemMessage(Component.literal("Warp '" + name + "' deleted."));
                return 1;
            }
            context.getSource().sendSystemMessage(Component.literal("Warp '" + name + "' not found."));
            return 0;
        }

}
