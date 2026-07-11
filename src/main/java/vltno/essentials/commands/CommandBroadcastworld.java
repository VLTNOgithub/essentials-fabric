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

public class CommandBroadcastworld {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("broadcastworld")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("bcw")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("ebcw")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("bcastw")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("ebcastw")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("ebroadcastworld")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("shoutworld")
            .executes(context -> executeBroadcastworld(context))
        );
        dispatcher.register(Commands.literal("eshoutworld")
            .executes(context -> executeBroadcastworld(context))
        );

    }

    public static int executeBroadcastworld(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /broadcastworld <world> <message>"));
            return 0;
        }

}
