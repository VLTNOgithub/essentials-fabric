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
            .requires(vltno.essentials.EssentialsCommands.require("essentials.payconfirmtoggle", 0))
            .executes(context -> executePayconfirmtoggle(context))
        ;
        dispatcher.register(payconfirmtoggleCmd);
        dispatcher.register(Commands.literal("epayconfirmtoggle").executes(payconfirmtoggleCmd.getCommand()).redirect(payconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("payconfirmoff").executes(payconfirmtoggleCmd.getCommand()).redirect(payconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("epayconfirmoff").executes(payconfirmtoggleCmd.getCommand()).redirect(payconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("payconfirmon").executes(payconfirmtoggleCmd.getCommand()).redirect(payconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("epayconfirmon").executes(payconfirmtoggleCmd.getCommand()).redirect(payconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("payconfirm").executes(payconfirmtoggleCmd.getCommand()).redirect(payconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("epayconfirm").executes(payconfirmtoggleCmd.getCommand()).redirect(payconfirmtoggleCmd.build()));


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
