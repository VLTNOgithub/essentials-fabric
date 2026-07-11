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

public class CommandThunder {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> thunderCmd = Commands.literal("thunder")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.thunder", 2))
            .executes(context -> executeThunder(context))
        ;
        dispatcher.register(thunderCmd);
        dispatcher.register(Commands.literal("ethunder").redirect(thunderCmd.build()));


    }

    public static int executeThunder(CommandContext<CommandSourceStack> context) {
            context.getSource().getServer().getLevel(net.minecraft.world.level.Level.OVERWORLD).setWeatherParameters(0, 6000, true, true);
            context.getSource().sendSystemMessage(Component.literal("Thunderstorm forced."));
            return 1;
        }

}
