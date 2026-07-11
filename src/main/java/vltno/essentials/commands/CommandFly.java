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

public class CommandFly {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("fly")
            .executes(context -> executeFly(context))
        );
        dispatcher.register(Commands.literal("efly")
            .executes(context -> executeFly(context))
        );

    }

    public static int executeFly(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            boolean isFlying = player.getAbilities().mayfly;
            player.getAbilities().mayfly = !isFlying;
            if (isFlying) {
                player.getAbilities().flying = false;
            }
            player.onUpdateAbilities();
            context.getSource().sendSystemMessage(Component.literal("Set fly mode to " + (!isFlying ? "enabled" : "disabled") + " for " + player.getName().getString() + "."));
            return 1;
        }

}
