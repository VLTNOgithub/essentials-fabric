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

public class CommandEnderchest {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("enderchest")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("echest")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("eechest")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("eenderchest")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("endersee")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("eendersee")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("ec")
            .executes(context -> executeEnderchest(context))
        );
        dispatcher.register(Commands.literal("eec")
            .executes(context -> executeEnderchest(context))
        );

    }

    public static int executeEnderchest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
                return net.minecraft.world.inventory.ChestMenu.threeRows(id, inventory, player.getEnderChestInventory());
            }, Component.literal("Ender Chest")));
            return 1;
        }

}
