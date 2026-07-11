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

public class CommandClearinventoryconfirmtoggle {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> clearinventoryconfirmtoggleCmd = Commands.literal("clearinventoryconfirmtoggle")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.clearinventoryconfirmtoggle", 0))
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        ;
        dispatcher.register(clearinventoryconfirmtoggleCmd);
        dispatcher.register(Commands.literal("eclearinventoryconfirmtoggle").executes(clearinventoryconfirmtoggleCmd.getCommand()).redirect(clearinventoryconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("clearinventoryconfirmoff").executes(clearinventoryconfirmtoggleCmd.getCommand()).redirect(clearinventoryconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("eclearinventoryconfirmoff").executes(clearinventoryconfirmtoggleCmd.getCommand()).redirect(clearinventoryconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("clearconfirmoff").executes(clearinventoryconfirmtoggleCmd.getCommand()).redirect(clearinventoryconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("eclearconfirmoff").executes(clearinventoryconfirmtoggleCmd.getCommand()).redirect(clearinventoryconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("clearconfirmon").executes(clearinventoryconfirmtoggleCmd.getCommand()).redirect(clearinventoryconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("eclearconfirmon").executes(clearinventoryconfirmtoggleCmd.getCommand()).redirect(clearinventoryconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("clearconfirm").executes(clearinventoryconfirmtoggleCmd.getCommand()).redirect(clearinventoryconfirmtoggleCmd.build()));
        dispatcher.register(Commands.literal("eclearconfirm").executes(clearinventoryconfirmtoggleCmd.getCommand()).redirect(clearinventoryconfirmtoggleCmd.build()));


    }

    public static int executeClearinventoryconfirmtoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            UserData data = UserCache.getUser(player);
            data.clearInventoryConfirmToggle = !data.clearInventoryConfirmToggle;
            UserCache.saveUser(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Clear inventory confirmation toggle set to: " + data.clearInventoryConfirmToggle));
            return 1;
        }

}
