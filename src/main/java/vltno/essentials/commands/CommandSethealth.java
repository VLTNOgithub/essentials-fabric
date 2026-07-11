package vltno.essentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.FloatArgumentType;
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

public class CommandSethealth {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("sethealth")
            .requires(source -> {
                try {
                    return source.getEntity() == null || source.getServer().getPlayerList().isOp(source.getPlayerOrException().nameAndId());
                } catch (Exception e) {
                    return true;
                }
            })
            .then(Commands.argument("value", FloatArgumentType.floatArg(0.0f))
                .executes(context -> executeSethealth(context, FloatArgumentType.getFloat(context, "value"), Collections.singletonList(context.getSource().getPlayerOrException())))
                .then(Commands.argument("targets", EntityArgument.entities())
                    .executes(context -> executeSethealth(context, FloatArgumentType.getFloat(context, "value"), EntityArgument.getEntities(context, "targets")))
                )
            )
        );
    }

    public static int executeSethealth(CommandContext<CommandSourceStack> context, float value, Collection<? extends net.minecraft.world.entity.Entity> targets) {
        int count = 0;
        for (net.minecraft.world.entity.Entity target : targets) {
            if (target instanceof LivingEntity living) {
                // Ensure we don't exceed the entity's maximum health if we want, or we can just set it. 
                // Vanilla caps it internally up to MaxHealth attribute, but we can set it.
                living.setHealth(value);
                count++;
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Set health to " + value + " for " + count + " entities."));
        return count;
    }
}