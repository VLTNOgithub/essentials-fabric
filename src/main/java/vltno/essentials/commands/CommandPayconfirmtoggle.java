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

public class CommandPayconfirmtoggle {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> payconfirmtoggleCmd = Commands.literal("payconfirmtoggle")
            .executes(context -> executePayconfirmtoggle(context))
        ;
        dispatcher.register(payconfirmtoggleCmd);
        dispatcher.register(Commands.literal("epayconfirmtoggle").redirect(payconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("payconfirmoff").redirect(payconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("epayconfirmoff").redirect(payconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("payconfirmon").redirect(payconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("epayconfirmon").redirect(payconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("payconfirm").redirect(payconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("epayconfirm").redirect(payconfirmtoggleCmd.build()));


    }

    public static int executePayconfirmtoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        data.payConfirmToggle = !data.payConfirmToggle;
        UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Payment confirmation toggle set to: " + data.payConfirmToggle));
        return 1;
    }

}
