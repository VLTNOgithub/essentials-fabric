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

public class CommandWorld {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> worldCmd = Commands.literal("world")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.world", 2))
            .then(Commands.argument("world", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeWorld(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "world")))
            );
        dispatcher.register(worldCmd);
        dispatcher.register(Commands.literal("eworld").redirect(worldCmd.build()));

    }

    public static int executeWorld(CommandContext<CommandSourceStack> context, String worldName) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> targetDimension = null;

        for (net.minecraft.server.level.ServerLevel level : context.getSource().getServer().getAllLevels()) {
            if (level.dimension().identifier().getPath().equalsIgnoreCase(worldName) || level.dimension().identifier().toString().equalsIgnoreCase(worldName)) {
                targetDimension = level.dimension();
                break;
            }
        }

        if (targetDimension == null) {
            if (worldName.equalsIgnoreCase("nether")) targetDimension = net.minecraft.world.level.Level.NETHER;
            else if (worldName.equalsIgnoreCase("end")) targetDimension = net.minecraft.world.level.Level.END;
            else if (worldName.equalsIgnoreCase("overworld")) targetDimension = net.minecraft.world.level.Level.OVERWORLD;
        }

        if (targetDimension != null) {
            net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(targetDimension);
            if (targetLevel != null) {
                if (player.level() == targetLevel) {
                    context.getSource().sendSystemMessage(Component.literal("You are already in that world.").withStyle(net.minecraft.ChatFormatting.RED));
                    return 0;
                }
                vltno.essentials.EssentialsCommands.saveBackLocation(player);
                // Teleport to world spawn
                net.minecraft.core.BlockPos spawnPos = targetLevel.getRespawnData().pos();
                player.teleportTo(targetLevel, spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
                context.getSource().sendSystemMessage(Component.literal("Teleported to world '" + targetDimension.identifier().toString() + "'."));
                return 1;
            }
        }

        context.getSource().sendSystemMessage(Component.literal("World not found.").withStyle(net.minecraft.ChatFormatting.RED));
        return 0;
    }

}
