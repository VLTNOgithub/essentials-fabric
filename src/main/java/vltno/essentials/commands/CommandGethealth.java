package vltno.essentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Collections;

public class CommandGethealth {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("gethealth")
            .executes(context -> executeGethealth(context, Collections.singletonList(context.getSource().getPlayerOrException())))
            .then(Commands.argument("targets", EntityArgument.entities())
                .requires(source -> {
                    try {
                        return source.getEntity() == null || source.getServer().getPlayerList().isOp(source.getPlayerOrException().nameAndId());
                    } catch (Exception e) {
                        return true;
                    }
                })
                .executes(context -> executeGethealth(context, EntityArgument.getEntities(context, "targets")))
            )
        );
    }

    public static int executeGethealth(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) {
        int count = 0;
        for (net.minecraft.world.entity.Entity target : targets) {
            if (target instanceof LivingEntity living) {
                float health = living.getHealth();
                float maxHealth = living.getMaxHealth();
                context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + "'s health: " + String.format("%.1f", health) + " / " + String.format("%.1f", maxHealth)));
                count++;
            }
        }
        if (count == 0) {
            context.getSource().sendSystemMessage(Component.literal("No living entities found.").withStyle(net.minecraft.ChatFormatting.RED));
        }
        return count;
    }
}