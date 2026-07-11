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

public class CommandPowertool {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("powertool")
            .executes(context -> executePowertool(context))
        );
        dispatcher.register(Commands.literal("epowertool")
            .executes(context -> executePowertool(context))
        );
        dispatcher.register(Commands.literal("pt")
            .executes(context -> executePowertool(context))
        );
        dispatcher.register(Commands.literal("ept")
            .executes(context -> executePowertool(context))
        );

    }

    public static int executePowertool(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Powertool tracking not implemented."));
            return 1;
        }

}
