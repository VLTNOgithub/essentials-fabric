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

public class CommandEnchant {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("enchant")
            .executes(context -> executeEnchant(context))
        );
        dispatcher.register(Commands.literal("eenchant")
            .executes(context -> executeEnchant(context))
        );
        dispatcher.register(Commands.literal("enchantment")
            .executes(context -> executeEnchant(context))
        );
        dispatcher.register(Commands.literal("eenchantment")
            .executes(context -> executeEnchant(context))
        );

    }

    public static int executeEnchant(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            context.getSource().sendSystemMessage(Component.literal("Usage: /enchant <enchantment> <level>"));
            return 0;
        }

}
