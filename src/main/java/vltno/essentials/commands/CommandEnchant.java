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
                for (String alias : new String[]{"enchant", "eenchant", "enchantment", "eenchantment"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.enchant", 2))
        .then(Commands.argument("enchantment", net.minecraft.commands.arguments.ResourceArgument.resource(registryAccess, net.minecraft.core.registries.Registries.ENCHANTMENT))
            .executes(context -> executeEnchantItem(context, net.minecraft.commands.arguments.ResourceArgument.getEnchantment(context, "enchantment"), 1))
            .then(Commands.argument("level", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
                .executes(context -> executeEnchantItem(context, net.minecraft.commands.arguments.ResourceArgument.getEnchantment(context, "enchantment"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "level")))
            )
        ));
        }
    }

    public static int executeEnchantItem(CommandContext<CommandSourceStack> context, net.minecraft.core.Holder.Reference<net.minecraft.world.item.enchantment.Enchantment> enchant, int level) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You must hold an item to enchant it."));
            return 0;
        }
        if (level == 0) {
            // Remove enchantment
            net.minecraft.world.item.enchantment.ItemEnchantments enchants = hand.getOrDefault(net.minecraft.core.component.DataComponents.ENCHANTMENTS, net.minecraft.world.item.enchantment.ItemEnchantments.EMPTY);
            net.minecraft.world.item.enchantment.ItemEnchantments.Mutable mutable = new net.minecraft.world.item.enchantment.ItemEnchantments.Mutable(enchants);
            mutable.set(enchant, 0);
            hand.set(net.minecraft.core.component.DataComponents.ENCHANTMENTS, mutable.toImmutable());
            context.getSource().sendSystemMessage(Component.literal("Removed enchantment."));
        } else {
            hand.enchant(enchant, level);
            context.getSource().sendSystemMessage(Component.literal("Enchantment applied successfully."));
        }
        return 1;
    }

}
