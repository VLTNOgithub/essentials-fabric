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

public class CommandKitreset {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> kitResetCmd = Commands.literal("kitreset")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.kitreset", 2))
            .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
                .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
                    .executes(context -> executeKitreset(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
                )
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> kitResetCmdNode = dispatcher.register(kitResetCmd);
        dispatcher.register(Commands.literal("ekitreset").requires(kitResetCmdNode.getRequirement()).redirect(kitResetCmdNode));
        dispatcher.register(Commands.literal("kitr").requires(kitResetCmdNode.getRequirement()).redirect(kitResetCmdNode));
        dispatcher.register(Commands.literal("ekitr").requires(kitResetCmdNode.getRequirement()).redirect(kitResetCmdNode));
        dispatcher.register(Commands.literal("resetkit").requires(kitResetCmdNode.getRequirement()).redirect(kitResetCmdNode));
        dispatcher.register(Commands.literal("eresetkit").requires(kitResetCmdNode.getRequirement()).redirect(kitResetCmdNode));

    }

    public static int executeKitreset(CommandContext<CommandSourceStack> context, net.minecraft.server.level.ServerPlayer target, String kitname) {
        vltno.essentials.UserData data = vltno.essentials.UserCache.getUser(target);
        if (data.kitCooldowns.remove(kitname.toLowerCase()) != null) {
            vltno.essentials.UserCache.saveUser(target.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Kit cooldown reset for " + target.getName().getString() + " on kit '" + kitname + "'."));
            return 1;
        }
        context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + " has no active cooldown for kit '" + kitname + "'."));
        return 0;
    }

}
