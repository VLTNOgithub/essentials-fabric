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

public class CommandRenamehome {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> renamehomeCmd = Commands.literal("renamehome")
        .then(Commands.argument("oldName", com.mojang.brigadier.arguments.StringArgumentType.word())
            .then(Commands.argument("newName", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeRenamehome(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "oldName"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "newName")))
            )
        )
    ;
        dispatcher.register(renamehomeCmd);
        dispatcher.register(Commands.literal("erenamehome").redirect(renamehomeCmd.build()));


    }

    public static int executeRenamehome(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /renamehome <old> <new>")); return 0; }

    public static int executeRenamehome(CommandContext<CommandSourceStack> context, String oldName, String newName) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            java.util.Map<String, HomePosition> homes = playerHomes.get(player.getUUID());
            if (homes == null || !homes.containsKey(oldName.toLowerCase())) {
                context.getSource().sendSystemMessage(Component.literal("Home '" + oldName + "' does not exist."));
                return 0;
            }
            if (homes.containsKey(newName.toLowerCase())) {
                context.getSource().sendSystemMessage(Component.literal("A home named '" + newName + "' already exists."));
                return 0;
            }
            HomePosition home = homes.remove(oldName.toLowerCase());
            homes.put(newName.toLowerCase(), home);
            context.getSource().sendSystemMessage(Component.literal("Successfully renamed home '" + oldName + "' to '" + newName + "'."));
            return 1;
        }

}
