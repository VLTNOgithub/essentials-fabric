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

public class CommandMsgtoggle {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("msgtoggle")
            .executes(context -> executeMsgtoggle(context))
        );
        dispatcher.register(Commands.literal("emsgtoggle")
            .executes(context -> executeMsgtoggle(context))
        );

    }

    public static int executeMsgtoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            UserData data = UserCache.getUser(player.getUUID());
            data.msgtoggle = !data.msgtoggle;
            UserCache.saveUser(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Message toggle set to: " + data.msgtoggle));
            return 1;
        }

}
