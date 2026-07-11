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

public class CommandBackup {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> backupCmd = Commands.literal("backup")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.backup", 2))
            .executes(context -> executeBackup(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> backupCmdNode = dispatcher.register(backupCmd);
        dispatcher.register(Commands.literal("ebackup").requires(backupCmdNode.getRequirement()).redirect(backupCmdNode));

    }

    public static int executeBackup(CommandContext<CommandSourceStack> context) {
            context.getSource().getServer().saveEverything(true, true, false);
            context.getSource().sendSystemMessage(Component.literal("Backup (Save-All) complete."));
            return 1;
        }

}
