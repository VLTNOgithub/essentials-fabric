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

public class CommandUnbanip {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("unbanip")
            .then(Commands.argument("ip", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeUnbanip(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "ip")))
            )
        );
        dispatcher.register(Commands.literal("eunbanip")
            .then(Commands.argument("ip", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeUnbanip(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "ip")))
            )
        );
        dispatcher.register(Commands.literal("pardonip")
            .then(Commands.argument("ip", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeUnbanip(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "ip")))
            )
        );
        dispatcher.register(Commands.literal("epardonip")
            .then(Commands.argument("ip", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeUnbanip(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "ip")))
            )
        );

    }

    public static int executeUnbanip(CommandContext<CommandSourceStack> context, String ip) {
        net.minecraft.server.players.IpBanList banList = context.getSource().getServer().getPlayerList().getIpBans();
        if (banList.isBanned(ip)) {
            banList.remove(ip);
            context.getSource().sendSystemMessage(Component.literal("Unbanned IP: " + ip));
        } else {
            context.getSource().sendSystemMessage(Component.literal("That IP is not banned."));
        }
        return 1;
    }

}
