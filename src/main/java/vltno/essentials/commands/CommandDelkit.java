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

public class CommandDelkit {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> delkitCmd = Commands.literal("delkit")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.delkit", 2))
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeDelkit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> delkitCmdNode = dispatcher.register(delkitCmd);
        dispatcher.register(Commands.literal("edelkit").requires(delkitCmdNode.getRequirement()).redirect(delkitCmdNode));
        dispatcher.register(Commands.literal("remkit").requires(delkitCmdNode.getRequirement()).redirect(delkitCmdNode));
        dispatcher.register(Commands.literal("eremkit").requires(delkitCmdNode.getRequirement()).redirect(delkitCmdNode));
        dispatcher.register(Commands.literal("rmkit").requires(delkitCmdNode.getRequirement()).redirect(delkitCmdNode));
        dispatcher.register(Commands.literal("ermkit").requires(delkitCmdNode.getRequirement()).redirect(delkitCmdNode));
        dispatcher.register(Commands.literal("deletekit").requires(delkitCmdNode.getRequirement()).redirect(delkitCmdNode));
        dispatcher.register(Commands.literal("edeletekit").requires(delkitCmdNode.getRequirement()).redirect(delkitCmdNode));

    }

    public static int executeDelkit(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /delkit <name>")); return 0; }

    public static int executeDelkit(CommandContext<CommandSourceStack> context, String name) {
            if (KITS.remove(name.toLowerCase()) != null) {
                saveKits(); saveJailsWarps();
                context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' deleted."));
                return 1;
            }
            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));
            return 0;
        }

}
