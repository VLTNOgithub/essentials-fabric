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

public class CommandTop {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("top")
            .executes(context -> executeTop(context))
        );
        dispatcher.register(Commands.literal("etop")
            .executes(context -> executeTop(context))
        );

    }

    public static int executeTop(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            int topY = player.level().getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, player.getBlockX(), player.getBlockZ());
            player.teleportTo(player.level(), player.getX(), topY, player.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal("Teleported to top."));
            return 1;
        }

}
