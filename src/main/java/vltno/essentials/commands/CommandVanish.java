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

public class CommandVanish {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("vanish")
            .executes(context -> executeVanish(context))
        );
        dispatcher.register(Commands.literal("v")
            .executes(context -> executeVanish(context))
        );
        dispatcher.register(Commands.literal("ev")
            .executes(context -> executeVanish(context))
        );
        dispatcher.register(Commands.literal("evanish")
            .executes(context -> executeVanish(context))
        );

    }

    public static int executeVanish(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.setInvisible(!player.isInvisible());
            context.getSource().sendSystemMessage(Component.literal("Vanish toggled to: " + player.isInvisible()));
            return 1;
        }

}
