package vltno.essentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Collections;

public class CommandGethunger {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("gethunger")
            .executes(context -> executeGethunger(context, Collections.singletonList(context.getSource().getPlayerOrException())))
            .then(Commands.argument("targets", EntityArgument.players())
                .requires(source -> {
                    try {
                        return source.getEntity() == null || source.getServer().getPlayerList().isOp(source.getPlayerOrException().nameAndId());
                    } catch (Exception e) {
                        return true;
                    }
                })
                .executes(context -> executeGethunger(context, EntityArgument.getPlayers(context, "targets")))
            )
        );
    }

    public static int executeGethunger(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets) {
        for (ServerPlayer target : targets) {
            int foodLevel = target.getFoodData().getFoodLevel();
            float saturation = target.getFoodData().getSaturationLevel();
            context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + "'s hunger: " + foodLevel + " / 20 (Saturation: " + String.format("%.1f", saturation) + ")"));
        }
        return targets.size();
    }
}