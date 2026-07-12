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
                for (String alias : new String[]{"getpos", "coords", "egetpos", "position", "eposition", "whereami", "ewhereami", "getlocation", "egetlocation", "getloc", "egetloc"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.getpos", 2))
            .executes(context -> executeGetpos(context, context.getSource().getPlayerOrException()))
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .executes(context -> executeGetpos(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
            ));
        }
    }

    public static int executeGetpos(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        net.minecraft.world.phys.Vec3 pos = target.position();
        context.getSource().sendSystemMessage(Component.literal(String.format(target.getName().getString() + "'s Location: X: %.2f Y: %.2f Z: %.2f Pitch: %.1f Yaw: %.1f", pos.x, pos.y, pos.z, target.getXRot(), target.getYRot())));
        return 1;
    }
}