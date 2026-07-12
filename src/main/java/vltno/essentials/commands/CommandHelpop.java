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

public class CommandHelpop {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"helpop", "ac", "eac", "amsg", "eamsg", "ehelpop"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.helpop", 0))
            .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeHelpop(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
            ));
        }

    }

    public static int executeHelpop(CommandContext<CommandSourceStack> context, String message) {
        Component msg = Component.literal("[HelpOp] " + context.getSource().getTextName() + ": " + message).withStyle(net.minecraft.ChatFormatting.DARK_RED);
        int sent = 0;
        for (ServerPlayer p : context.getSource().getServer().getPlayerList().getPlayers()) {
            if (context.getSource().getServer().getPlayerList().isOp(p.nameAndId())) {
                p.sendSystemMessage(msg);
                sent++;
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Your message has been sent to " + sent + " online operators."));
        return 1;
    }

}
