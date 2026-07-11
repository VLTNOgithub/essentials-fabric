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

public class CommandPotion {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("potion")
            .executes(context -> executePotion(context))
        );
        dispatcher.register(Commands.literal("epotion")
            .executes(context -> executePotion(context))
        );
        dispatcher.register(Commands.literal("elixer")
            .executes(context -> executePotion(context))
        );
        dispatcher.register(Commands.literal("eelixer")
            .executes(context -> executePotion(context))
        );

    }

    public static int executePotion(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /potion <effect> [duration]"));
            return 0;
        }

}
