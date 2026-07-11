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

public class CommandTpauto {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("tpauto")
        .executes(context -> executeTpauto(context))
    );
        dispatcher.register(Commands.literal("etpauto")
            .executes(context -> executeTpauto(context))
        );

    }

    public static int executeTpauto(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            if (tpAutoPlayers.contains(player.getUUID())) {
                tpAutoPlayers.remove(player.getUUID());
                context.getSource().sendSystemMessage(Component.literal("Auto-accept teleport requests disabled."));
            } else {
                tpAutoPlayers.add(player.getUUID());
                context.getSource().sendSystemMessage(Component.literal("Auto-accept teleport requests enabled."));
            }
            return 1;
        }

}
