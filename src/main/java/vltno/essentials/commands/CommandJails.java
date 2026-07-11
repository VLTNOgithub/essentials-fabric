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

public class CommandJails {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("jails").executes(context -> executeJails(context)));
        dispatcher.register(Commands.literal("ejails")
            .executes(context -> executeJails(context))
        );

    }

    public static int executeJails(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Jails: " + String.join(", ", JAILS.keySet())));
            return 1;
        }

}
