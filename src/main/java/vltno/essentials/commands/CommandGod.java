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

public class CommandGod {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("god")
            .executes(context -> executeGod(context))
        );
        dispatcher.register(Commands.literal("egod")
            .executes(context -> executeGod(context))
        );
        dispatcher.register(Commands.literal("godmode")
            .executes(context -> executeGod(context))
        );
        dispatcher.register(Commands.literal("egodmode")
            .executes(context -> executeGod(context))
        );
        dispatcher.register(Commands.literal("tgm")
            .executes(context -> executeGod(context))
        );
        dispatcher.register(Commands.literal("etgm")
            .executes(context -> executeGod(context))
        );

    }

    public static int executeGod(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            boolean isGod = player.isInvulnerable();
            player.setInvulnerable(!isGod);
            context.getSource().sendSystemMessage(Component.literal("God mode " + (!isGod ? "enabled" : "disabled") + "."));
            return 1;
        }

}
