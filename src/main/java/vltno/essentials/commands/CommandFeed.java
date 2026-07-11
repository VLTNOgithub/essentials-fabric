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

public class CommandFeed {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("feed")
            .executes(context -> executeFeed(context))
        );
        dispatcher.register(Commands.literal("eat")
            .executes(context -> executeFeed(context))
        );
        dispatcher.register(Commands.literal("eeat")
            .executes(context -> executeFeed(context))
        );
        dispatcher.register(Commands.literal("efeed")
            .executes(context -> executeFeed(context))
        );

    }

    public static int executeFeed(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20.0F);
            context.getSource().sendSystemMessage(Component.literal("You have been fed."));
            return 1;
        }

}
