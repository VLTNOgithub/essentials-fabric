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

public class CommandWarp {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> warpCmd = Commands.literal("warp")
        .executes(context -> executeWarp(context, ""))
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeWarp(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    ;
        dispatcher.register(warpCmd);
        dispatcher.register(Commands.literal("ewarp").redirect(warpCmd.build()));
        dispatcher.register(Commands.literal("warps").redirect(warpCmd.build()));
        dispatcher.register(Commands.literal("ewarps").redirect(warpCmd.build()));


    }

    public static int executeWarp(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeWarp(context, ""); }

    public static int executeWarp(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
            if (name.isEmpty()) {
                context.getSource().sendSystemMessage(Component.literal("Warps: " + String.join(", ", WARPS.keySet())));
                return 1;
            }
            HomePosition warpPos = WARPS.get(name.toLowerCase());
            if (warpPos == null) {
                context.getSource().sendSystemMessage(Component.literal("Warp '" + name + "' not found."));
                return 0;
            }
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, net.minecraft.resources.Identifier.parse(warpPos.dimension));
            net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
            if (targetLevel != null) {
                saveBackLocation(player);
                player.teleportTo(targetLevel, warpPos.x, warpPos.y, warpPos.z, java.util.Collections.emptySet(), warpPos.yaw, warpPos.pitch, false);
            }
            context.getSource().sendSystemMessage(Component.literal("Warped to " + name));
            return 1;
        }

}
