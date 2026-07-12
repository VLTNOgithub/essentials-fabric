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

public class CommandTpoffline {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"tpoffline", "otp", "offlinetp", "tpoff", "etpoffline"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.tpoffline", 2))
        .then(Commands.argument("target", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeTpoffline(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "target")))
        ));
        }

    }

    public static int executeTpoffline(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /tpoffline <uuid>")); return 0; }

    public static int executeTpoffline(CommandContext<CommandSourceStack> context, String targetName) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            HomePosition pos = offlinePositions.get(targetName.toLowerCase());
            if (pos == null) {
                context.getSource().sendSystemMessage(Component.literal("No offline location recorded for " + targetName + " since the server started."));
                return 0;
            }
            net.minecraft.resources.Identifier dimLoc = net.minecraft.resources.Identifier.parse(pos.dimension);
            net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimLoc);
            net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
            if (targetLevel != null) {
                saveBackLocation(player);
            player.teleportTo(targetLevel, pos.x, pos.y, pos.z, java.util.Collections.emptySet(), pos.yaw, pos.pitch, false);
                context.getSource().sendSystemMessage(Component.literal("Teleported to " + targetName + "'s last known offline location."));
                return 1;
            }
            return 0;
        }

}
