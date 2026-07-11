package vltno.essentials.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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

public class CommandSethunger {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("sethunger")
            .requires(source -> {
                try {
                    return source.getEntity() == null || source.getServer().getPlayerList().isOp(source.getPlayerOrException().nameAndId());
                } catch (Exception e) {
                    return true;
                }
            })
            .then(Commands.argument("value", IntegerArgumentType.integer(0, 20))
                .executes(context -> executeSethunger(context, IntegerArgumentType.getInteger(context, "value"), Collections.singletonList(context.getSource().getPlayerOrException())))
                .then(Commands.argument("targets", EntityArgument.players())
                    .executes(context -> executeSethunger(context, IntegerArgumentType.getInteger(context, "value"), EntityArgument.getPlayers(context, "targets")))
                )
            )
        );
    }

    public static int executeSethunger(CommandContext<CommandSourceStack> context, int value, Collection<ServerPlayer> targets) {
        for (ServerPlayer target : targets) {
            target.getFoodData().setFoodLevel(value);
            // Optionally fill saturation too when hunger is full, but we'll stick to just setting the level exactly.
        }
        context.getSource().sendSystemMessage(Component.literal("Set hunger to " + value + " for " + targets.size() + " players."));
        return targets.size();
    }
}