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

public class CommandItemlore {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"itemlore", "lore", "elore", "ilore", "eilore", "eitemlore"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.itemlore", 2))
            .then(Commands.literal("add")
                .then(Commands.argument("text", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                    .executes(context -> executeItemlore(context, "add", com.mojang.brigadier.arguments.StringArgumentType.getString(context, "text")))
                )
            )
            .then(Commands.literal("clear")
                .executes(context -> executeItemlore(context, "clear", ""))
            ));
        }

    }

    public static int executeItemlore(CommandContext<CommandSourceStack> context, String action, String text) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You are not holding an item."));
            return 0;
        }
        net.minecraft.world.item.component.ItemLore lore = hand.getOrDefault(net.minecraft.core.component.DataComponents.LORE, net.minecraft.world.item.component.ItemLore.EMPTY);
        if (action.equals("clear")) {
            hand.remove(net.minecraft.core.component.DataComponents.LORE);
            context.getSource().sendSystemMessage(Component.literal("Lore cleared."));
        } else if (action.equals("add")) {
            java.util.List<Component> lines = new java.util.ArrayList<>(lore.lines());
            lines.add(Component.literal(text.replace("&", "\u00A7")).withStyle(net.minecraft.ChatFormatting.ITALIC));
            hand.set(net.minecraft.core.component.DataComponents.LORE, new net.minecraft.world.item.component.ItemLore(lines));
            context.getSource().sendSystemMessage(Component.literal("Lore added."));
        }
        return 1;
    }

}
