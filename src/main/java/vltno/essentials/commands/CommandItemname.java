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

public class CommandItemname {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"itemname", "iname", "einame", "eitemname", "itemrename", "irename", "eitemrename", "eirename"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.itemname", 2))
            .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeItemname(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
            ));
        }

    }

    public static int executeItemname(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You are not holding an item."));
            return 0;
        }
        hand.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME, Component.literal(name.replace("&", "\u00A7")));
        context.getSource().sendSystemMessage(Component.literal("Item renamed."));
        return 1;
    }

}
