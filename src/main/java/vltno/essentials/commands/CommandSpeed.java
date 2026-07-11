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

public class CommandSpeed {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("speed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("flyspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("eflyspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("fspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("efspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("espeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("walkspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("ewalkspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("wspeed")
            .executes(context -> executeSpeed(context))
        );
        dispatcher.register(Commands.literal("ewspeed")
            .executes(context -> executeSpeed(context))
        );

    }

    public static int executeSpeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.getAbilities().setFlyingSpeed(0.1F);
            player.getAbilities().setWalkingSpeed(0.2F);
            player.onUpdateAbilities();
            context.getSource().sendSystemMessage(Component.literal("Speed reset to defaults."));
            return 1;
        }

}
