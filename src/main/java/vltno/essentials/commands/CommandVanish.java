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
                for (String alias : new String[]{"vanish", "v", "ev", "evanish"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.vanish", 2))
            .executes(context -> executeVanish(context))
        );
        }


    }

    public static int executeVanish(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        data.isVanished = !data.isVanished;
        UserCache.saveUser(player.getUUID());

        if (data.isVanished) {
            // Tell everyone who doesn't have OP to remove this player from the tab list.
            net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket packet = new net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket(Collections.singletonList(player.getUUID()));
            for (ServerPlayer other : context.getSource().getServer().getPlayerList().getPlayers()) {
                if (other != player && !context.getSource().getServer().getPlayerList().isOp(other.nameAndId())) {
                    other.connection.send(packet);
                }
            }
            // Force server tracker to reload the entity visibility to clients
            player.level().getChunkSource().removeEntity(player);
            player.level().getChunkSource().addEntity(player);

            context.getSource().sendSystemMessage(Component.literal("You are now vanished. (Hidden from non-ops)"));
        } else {
            // Re-add them to the tab list for everyone
            net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket packet = net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.createPlayerInitializing(Collections.singletonList(player));
            for (ServerPlayer other : context.getSource().getServer().getPlayerList().getPlayers()) {
                if (other != player && !context.getSource().getServer().getPlayerList().isOp(other.nameAndId())) {
                    other.connection.send(packet);
                }
            }
            // Force server tracker to reload the entity visibility to clients
            player.level().getChunkSource().removeEntity(player);
            player.level().getChunkSource().addEntity(player);
            context.getSource().sendSystemMessage(Component.literal("You are no longer vanished."));
        }

        return 1;
    }

}
