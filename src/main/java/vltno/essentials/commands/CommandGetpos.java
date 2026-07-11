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

public class CommandGetpos {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("getpos")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("coords")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("egetpos")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("position")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("eposition")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("whereami")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("ewhereami")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("getlocation")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("egetlocation")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("getloc")
            .executes(context -> executeGetpos(context))
        );
        dispatcher.register(Commands.literal("egetloc")
            .executes(context -> executeGetpos(context))
        );

    }

    public static int executeGetpos(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.phys.Vec3 pos = player.position();
            context.getSource().sendSystemMessage(Component.literal(String.format("Location: X: %.2f Y: %.2f Z: %.2f Pitch: %.1f Yaw: %.1f", pos.x, pos.y, pos.z, player.getXRot(), player.getYRot())));
            return 1;
        }

}
