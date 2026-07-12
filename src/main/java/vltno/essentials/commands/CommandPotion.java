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

public class CommandPotion {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"potion", "epotion", "elixer", "eelixer"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.potion", 0))
            .then(Commands.argument("effect", net.minecraft.commands.arguments.ResourceArgument.resource(registryAccess, net.minecraft.core.registries.Registries.MOB_EFFECT))
                .executes(context -> executePotion(context, net.minecraft.commands.arguments.ResourceArgument.getMobEffect(context, "effect"), 600, 1))
                .then(Commands.argument("duration", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
                    .executes(context -> executePotion(context, net.minecraft.commands.arguments.ResourceArgument.getMobEffect(context, "effect"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "duration"), 1))
                    .then(Commands.argument("amplifier", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
                        .executes(context -> executePotion(context, net.minecraft.commands.arguments.ResourceArgument.getMobEffect(context, "effect"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "duration"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "amplifier")))
                    )
                )
            ));
        }

    }

    public static int executePotion(CommandContext<CommandSourceStack> context, net.minecraft.core.Holder.Reference<net.minecraft.world.effect.MobEffect> effect, int duration, int amplifier) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();

        if (hand.is(net.minecraft.world.item.Items.POTION) || hand.is(net.minecraft.world.item.Items.SPLASH_POTION) || hand.is(net.minecraft.world.item.Items.LINGERING_POTION)) {
            net.minecraft.world.item.alchemy.PotionContents contents = hand.getOrDefault(net.minecraft.core.component.DataComponents.POTION_CONTENTS, net.minecraft.world.item.alchemy.PotionContents.EMPTY);
            net.minecraft.world.effect.MobEffectInstance instance = new net.minecraft.world.effect.MobEffectInstance(effect, duration, amplifier - 1);
            hand.set(net.minecraft.core.component.DataComponents.POTION_CONTENTS, contents.withEffectAdded(instance));
            context.getSource().sendSystemMessage(Component.literal("Added custom effect to the potion."));
        } else {
            player.addEffect(new net.minecraft.world.effect.MobEffectInstance(effect, duration, amplifier - 1));
            context.getSource().sendSystemMessage(Component.literal("Applied effect to yourself."));
        }
        return 1;
    }

}
