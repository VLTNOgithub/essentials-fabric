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

public class CommandSetwarp {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> setwarpCmd = Commands.literal("setwarp")
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeSetwarp(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    ;
        dispatcher.register(setwarpCmd);
        dispatcher.register(Commands.literal("createwarp").redirect(setwarpCmd.build()));
        dispatcher.register(Commands.literal("ecreatewarp").redirect(setwarpCmd.build()));
        dispatcher.register(Commands.literal("esetwarp").redirect(setwarpCmd.build()));


    }

    public static int executeSetwarp(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /setwarp <name>")); return 0; }

    public static int executeSetwarp(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            HomePosition pos = new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.level().dimension().identifier().toString());
            WARPS.put(name.toLowerCase(), pos);
            saveJailsWarps();
            context.getSource().sendSystemMessage(Component.literal("Warp '" + name + "' set."));
            return 1;
        }

}
