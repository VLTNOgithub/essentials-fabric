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

public class CommandItemdb {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"itemdb", "dura", "edura", "durability", "edurability", "eitemdb", "itemno", "eitemno"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.itemdb", 0))
            .executes(context -> executeItemdb(context))
        );
        }


    }

    public static int executeItemdb(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
            if (hand.isEmpty()) {
                context.getSource().sendSystemMessage(Component.literal("You are not holding an item."));
                return 0;
            }
            context.getSource().sendSystemMessage(Component.literal("Item: " + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(hand.getItem()).toString()));
            return 1;
        }

}
