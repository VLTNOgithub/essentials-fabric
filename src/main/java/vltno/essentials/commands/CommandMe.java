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

public class CommandMe {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"me", "action", "eaction", "describe", "edescribe", "eme"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.me", 0))
        .then(Commands.argument("action", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeMe(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "action")))
        ));
        }

    }

    public static int executeMe(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /me <action>")); return 0; }

    public static int executeMe(CommandContext<CommandSourceStack> context, String action) {
            context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal(" * " + context.getSource().getTextName() + " " + action), false);
            return 1;
        }

}
