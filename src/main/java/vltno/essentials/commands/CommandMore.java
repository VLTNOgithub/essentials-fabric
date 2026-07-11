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

public class CommandMore {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> moreCmd = Commands.literal("more")
            .executes(context -> executeMore(context))
        ;
        dispatcher.register(moreCmd);
        dispatcher.register(Commands.literal("emore").redirect(moreCmd.build()));


    }

    public static int executeMore(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
            if (hand.isEmpty()) {
                context.getSource().sendSystemMessage(Component.literal("You are not holding an item."));
                return 0;
            }
            hand.setCount(hand.getMaxStackSize());
            context.getSource().sendSystemMessage(Component.literal("Filled item stack to maximum."));
            return 1;
        }

}
