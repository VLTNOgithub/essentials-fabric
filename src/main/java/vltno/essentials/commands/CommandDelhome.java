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

public class CommandDelhome {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("delhome")
        .executes(context -> executeDelhome(context))
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeDelhome(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    );
        dispatcher.register(Commands.literal("edelhome")
            .executes(context -> executeDelhome(context))
        );
        dispatcher.register(Commands.literal("remhome")
            .executes(context -> executeDelhome(context))
        );
        dispatcher.register(Commands.literal("eremhome")
            .executes(context -> executeDelhome(context))
        );
        dispatcher.register(Commands.literal("rmhome")
            .executes(context -> executeDelhome(context))
        );
        dispatcher.register(Commands.literal("ermhome")
            .executes(context -> executeDelhome(context))
        );

    }

    public static int executeDelhome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeDelhome(context, "home"); }

    public static int executeDelhome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            UserData data = UserCache.getUser(player);
            if (data.homes.remove(name.toLowerCase()) != null) {
                UserCache.saveUser(player.getUUID());
                context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' deleted."));
                return 1;
            }
            context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' does not exist."));
            return 0;
        }

}
