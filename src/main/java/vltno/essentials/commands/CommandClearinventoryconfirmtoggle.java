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
        dispatcher.register(Commands.literal("clearinventoryconfirmtoggle")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("eclearinventoryconfirmtoggle")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("clearinventoryconfirmoff")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("eclearinventoryconfirmoff")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("clearconfirmoff")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("eclearconfirmoff")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("clearconfirmon")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("eclearconfirmon")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("clearconfirm")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("eclearconfirm")
            .executes(context -> executeClearinventoryconfirmtoggle(context))
        );

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
