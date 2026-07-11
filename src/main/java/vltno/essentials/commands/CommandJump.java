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

public class CommandJump {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> jumpCmd = Commands.literal("jump")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.jump", 0))
            .executes(context -> executeJump(context))
        ;
        dispatcher.register(jumpCmd);
        dispatcher.register(Commands.literal("j").executes(jumpCmd.getCommand()).redirect(jumpCmd.build()));
        dispatcher.register(Commands.literal("ej").executes(jumpCmd.getCommand()).redirect(jumpCmd.build()));
        dispatcher.register(Commands.literal("ejump").executes(jumpCmd.getCommand()).redirect(jumpCmd.build()));
        dispatcher.register(Commands.literal("jumpto").executes(jumpCmd.getCommand()).redirect(jumpCmd.build()));
        dispatcher.register(Commands.literal("ejumpto").executes(jumpCmd.getCommand()).redirect(jumpCmd.build()));


    }

    public static int executeJump(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
            if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
                net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos();
                player.teleportTo(player.level(), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
                context.getSource().sendSystemMessage(Component.literal("Jumped!"));
            } else {
                context.getSource().sendSystemMessage(Component.literal("No block in sight."));
            }
            return 1;
        }

}
