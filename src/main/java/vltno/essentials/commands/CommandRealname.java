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

public class CommandRealname {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> realnameCmd = Commands.literal("realname")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.realname", 0))
            .then(Commands.argument("nick", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeRealname(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "nick")))
            );
        dispatcher.register(realnameCmd);
        dispatcher.register(Commands.literal("erealname").redirect(realnameCmd.build()));

    }

    public static int executeRealname(CommandContext<CommandSourceStack> context, String nickname) {
        boolean found = false;
        for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
            UserData data = UserCache.getUser(player);
            if (data.nickname != null && data.nickname.equalsIgnoreCase(nickname)) {
                context.getSource().sendSystemMessage(Component.literal(nickname + " is " + player.getName().getString()));
                found = true;
            }
        }
        if (!found) {
            context.getSource().sendSystemMessage(Component.literal("No player found with that nickname."));
        }
        return 1;
    }

}
