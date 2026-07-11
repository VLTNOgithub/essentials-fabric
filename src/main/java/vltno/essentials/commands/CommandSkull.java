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

public class CommandSkull {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("skull")
            .executes(context -> executeSkull(context))
        );
        dispatcher.register(Commands.literal("eskull")
            .executes(context -> executeSkull(context))
        );
        dispatcher.register(Commands.literal("playerskull")
            .executes(context -> executeSkull(context))
        );
        dispatcher.register(Commands.literal("eplayerskull")
            .executes(context -> executeSkull(context))
        );
        dispatcher.register(Commands.literal("head")
            .executes(context -> executeSkull(context))
        );
        dispatcher.register(Commands.literal("ehead")
            .executes(context -> executeSkull(context))
        );

    }

    public static int executeSkull(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.item.ItemStack skull = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD, 1);
            // Adding profile component normally requires NBT handling, we just give the item here.
            if (!player.getInventory().add(skull)) player.drop(skull, false);
            context.getSource().sendSystemMessage(Component.literal("You received a player skull."));
            return 1;
        }

}
