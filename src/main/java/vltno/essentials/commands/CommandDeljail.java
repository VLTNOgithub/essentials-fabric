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

public class CommandDeljail {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("deljail")
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeDeljail(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    );
        dispatcher.register(Commands.literal("edeljail")
            .executes(context -> executeDeljail(context))
        );
        dispatcher.register(Commands.literal("remjail")
            .executes(context -> executeDeljail(context))
        );
        dispatcher.register(Commands.literal("eremjail")
            .executes(context -> executeDeljail(context))
        );
        dispatcher.register(Commands.literal("rmjail")
            .executes(context -> executeDeljail(context))
        );
        dispatcher.register(Commands.literal("ermjail")
            .executes(context -> executeDeljail(context))
        );

    }

    public static int executeDeljail(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /deljail <name>")); return 0; }

    public static int executeDeljail(CommandContext<CommandSourceStack> context, String name) {
            if (JAILS.remove(name.toLowerCase()) != null) {
                saveJailsWarps();
                context.getSource().sendSystemMessage(Component.literal("Jail '" + name + "' deleted."));
                return 1;
            }
            context.getSource().sendSystemMessage(Component.literal("Jail '" + name + "' not found."));
            return 0;
        }

}
