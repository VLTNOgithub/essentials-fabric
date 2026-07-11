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

public class CommandHat {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("hat")
            .executes(context -> executeHat(context))
        );
        dispatcher.register(Commands.literal("ehat")
            .executes(context -> executeHat(context))
        );
        dispatcher.register(Commands.literal("head")
            .executes(context -> executeHat(context))
        );
        dispatcher.register(Commands.literal("ehead")
            .executes(context -> executeHat(context))
        );

    }

    public static int executeHat(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
            if (hand.isEmpty()) {
                context.getSource().sendSystemMessage(Component.literal("You must be holding an item."));
                return 0;
            }
            net.minecraft.world.item.ItemStack head = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
            player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, hand.copy());
            player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, head);
            context.getSource().sendSystemMessage(Component.literal("Enjoy your new hat!"));
            return 1;
        }

}
