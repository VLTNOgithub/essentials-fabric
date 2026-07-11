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
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> vanishCmd = Commands.literal("vanish")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.vanish", 2))
            .executes(context -> executeVanish(context))
        ;
        dispatcher.register(vanishCmd);
        dispatcher.register(Commands.literal("v").redirect(vanishCmd.build()));
        dispatcher.register(Commands.literal("ev").redirect(vanishCmd.build()));
        dispatcher.register(Commands.literal("evanish").redirect(vanishCmd.build()));


    }

    public static int executeVanish(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.setInvisible(!player.isInvisible());
            context.getSource().sendSystemMessage(Component.literal("Vanish toggled to: " + player.isInvisible()));
            return 1;
        }

}
