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

public class CommandTpr {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> tprCmd = Commands.literal("tpr")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.tpr", 0))
        .executes(context -> executeTpr(context))
    ;
        dispatcher.register(tprCmd);
        dispatcher.register(Commands.literal("etpr").redirect(tprCmd.build()));
        dispatcher.register(Commands.literal("tprandom").redirect(tprCmd.build()));
        dispatcher.register(Commands.literal("etprandom").redirect(tprCmd.build()));


    }

    public static int executeTpr(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.level.border.WorldBorder border = player.level().getWorldBorder();
            double minX = Math.max(border.getMinX(), -5000);
            double maxX = Math.min(border.getMaxX(), 5000);
            double minZ = Math.max(border.getMinZ(), -5000);
            double maxZ = Math.min(border.getMaxZ(), 5000);
            double x = minX + (player.getRandom().nextDouble() * (maxX - minX));
            double z = minZ + (player.getRandom().nextDouble() * (maxZ - minZ));
            int y = player.level().getMaxY() - 1;
            // Basic top-down scan to find surface (will just teleport to top block for simplicity)
            net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos((int)x, y, (int)z);
            while(y > player.level().getMinY() && player.level().getBlockState(pos).isAir()) {
                y--;
                pos = new net.minecraft.core.BlockPos((int)x, y, (int)z);
            }
            saveBackLocation(player);
            player.teleportTo(player.level(), x, y + 1.0, z, java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal(String.format("Randomly teleported to X: %.1f Z: %.1f", x, z)));
            return 1;
        }

}
