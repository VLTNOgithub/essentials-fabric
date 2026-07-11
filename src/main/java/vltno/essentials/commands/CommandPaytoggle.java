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

public class CommandPaytoggle {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> paytoggleCmd = Commands.literal("paytoggle")
            .executes(context -> executePaytoggle(context))
        ;
        dispatcher.register(paytoggleCmd);
        dispatcher.register(Commands.literal("epaytoggle").redirect(paytoggleCmd.build()));
        dispatcher.register(Commands.literal("payoff").redirect(paytoggleCmd.build()));
        dispatcher.register(Commands.literal("epayoff").redirect(paytoggleCmd.build()));
        dispatcher.register(Commands.literal("payon").redirect(paytoggleCmd.build()));
        dispatcher.register(Commands.literal("epayon").redirect(paytoggleCmd.build()));


    }

    public static int executePaytoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            UserData data = UserCache.getUser(player);
            data.payToggle = !data.payToggle;
            UserCache.saveUser(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Accepting payments set to: " + data.payToggle));
            return 1;
        }

}
