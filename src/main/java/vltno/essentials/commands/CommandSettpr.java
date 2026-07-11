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

public class CommandSettpr {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> settprCmd = Commands.literal("settpr")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.settpr", 2))
            .executes(context -> executeSettpr(context))
        ;
        dispatcher.register(settprCmd);
        dispatcher.register(Commands.literal("esettpr").executes(settprCmd.getCommand()).redirect(settprCmd.build()));
        dispatcher.register(Commands.literal("settprandom").executes(settprCmd.getCommand()).redirect(settprCmd.build()));
        dispatcher.register(Commands.literal("esettprandom").executes(settprCmd.getCommand()).redirect(settprCmd.build()));


    }

    public static int executeSettpr(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("TPR variables set."));
            return 1;
        }

}
