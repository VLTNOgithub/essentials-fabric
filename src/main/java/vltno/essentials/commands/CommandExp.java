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

public class CommandExp {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> expCmd = Commands.literal("exp")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.exp", 0))
            .executes(context -> executeExpShow(context, context.getSource().getPlayerOrException()))
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .executes(context -> executeExpShow(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
            )
            .then(Commands.literal("show")
                .executes(context -> executeExpShow(context, context.getSource().getPlayerOrException()))
                .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                    .executes(context -> executeExpShow(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
                )
            )
            .then(Commands.literal("set")
                .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                    .then(Commands.argument("amount", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
                        .executes(context -> executeExpSet(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "amount")))
                    )
                )
            )
            .then(Commands.literal("give")
                .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                    .then(Commands.argument("amount", com.mojang.brigadier.arguments.IntegerArgumentType.integer())
                        .executes(context -> executeExpGive(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "amount")))
                    )
                )
            );
        dispatcher.register(expCmd);
        dispatcher.register(Commands.literal("eexp").redirect(expCmd.build()));
        dispatcher.register(Commands.literal("xp").redirect(expCmd.build()));

    }

    public static int executeExpShow(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + " has " + target.experienceLevel + " levels and " + (int)(target.experienceProgress * target.getXpNeededForNextLevel()) + " exp."));
        return 1;
    }

    public static int executeExpSet(CommandContext<CommandSourceStack> context, ServerPlayer target, int amount) {
        target.setExperienceLevels(amount);
        target.setExperiencePoints(0);
        context.getSource().sendSystemMessage(Component.literal("Set " + target.getName().getString() + "'s exp to " + amount + " levels."));
        return 1;
    }

    public static int executeExpGive(CommandContext<CommandSourceStack> context, ServerPlayer target, int amount) {
        target.giveExperienceLevels(amount);
        context.getSource().sendSystemMessage(Component.literal("Gave " + amount + " exp levels to " + target.getName().getString() + "."));
        return 1;
    }

}
