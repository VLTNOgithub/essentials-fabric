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

public class CommandPayconfirmtoggle {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("payconfirmtoggle")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("epayconfirmtoggle")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("payconfirmoff")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("epayconfirmoff")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("payconfirmon")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("epayconfirmon")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("payconfirm")
            .executes(context -> executePayconfirmtoggle(context))
        );
        dispatcher.register(Commands.literal("epayconfirm")
            .executes(context -> executePayconfirmtoggle(context))
        );

    }

    public static int executePayconfirmtoggle(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Command payconfirmtoggle is not fully implemented yet!"));
            return 1;
        }

}
