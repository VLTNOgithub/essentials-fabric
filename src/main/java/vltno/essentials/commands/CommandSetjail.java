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

public class CommandSetjail {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> setjailCmd = Commands.literal("setjail")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.setjail", 2))
            .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeSetjail(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> setjailCmdNode = dispatcher.register(setjailCmd);
        dispatcher.register(Commands.literal("esetjail").requires(setjailCmdNode.getRequirement()).redirect(setjailCmdNode));
        dispatcher.register(Commands.literal("createjail").requires(setjailCmdNode.getRequirement()).redirect(setjailCmdNode));
        dispatcher.register(Commands.literal("ecreatejail").requires(setjailCmdNode.getRequirement()).redirect(setjailCmdNode));

    }

    public static int executeSetjail(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /setjail <name>")); return 0; }

    public static int executeSetjail(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            HomePosition pos = new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.level().dimension().identifier().toString());
            JAILS.put(name.toLowerCase(), pos);
            saveJailsWarps();
            context.getSource().sendSystemMessage(Component.literal("Jail '" + name + "' set."));
            return 1;
        }

}
