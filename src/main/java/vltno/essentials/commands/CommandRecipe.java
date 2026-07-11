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

public class CommandRecipe {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("recipe")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("formula")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("eformula")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("method")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("emethod")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("erecipe")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("recipes")
            .executes(context -> executeRecipe(context))
        );
        dispatcher.register(Commands.literal("erecipes")
            .executes(context -> executeRecipe(context))
        );

    }

    public static int executeRecipe(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Usage: /recipe <item>"));
            return 0;
        }

}
