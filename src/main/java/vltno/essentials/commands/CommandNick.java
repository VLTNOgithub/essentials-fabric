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

public class CommandNick {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> nickCmd = Commands.literal("nick")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.nick", 0))
            .then(Commands.argument("nickname", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeNick(context, context.getSource().getPlayerOrException(), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "nickname")))
            )
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .then(Commands.argument("nickname", com.mojang.brigadier.arguments.StringArgumentType.word())
                    .executes(context -> executeNick(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "nickname")))
                )
            );
        dispatcher.register(nickCmd);
        dispatcher.register(Commands.literal("enick").redirect(nickCmd.build()));
        dispatcher.register(Commands.literal("nickname").redirect(nickCmd.build()));
        dispatcher.register(Commands.literal("enickname").redirect(nickCmd.build()));

    }

    public static int executeNick(CommandContext<CommandSourceStack> context, ServerPlayer target, String nickname) {
        UserData data = UserCache.getUser(target);
        if (nickname.equalsIgnoreCase("off")) {
            data.nickname = null;
            context.getSource().sendSystemMessage(Component.literal("Reset nickname for " + target.getName().getString()));
        } else {
            data.nickname = nickname;
            context.getSource().sendSystemMessage(Component.literal("Set nickname for " + target.getName().getString() + " to " + nickname));
        }
        context.getSource().getServer().getPlayerList().broadcastAll(new net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket(net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME, target));
        return 1;
    }

}
