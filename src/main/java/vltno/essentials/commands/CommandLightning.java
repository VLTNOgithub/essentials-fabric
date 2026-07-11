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

public class CommandLightning {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> lightningCmd = Commands.literal("lightning")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.lightning", 2))
            .executes(context -> executeLightning(context))
        ;
        dispatcher.register(lightningCmd);
        dispatcher.register(Commands.literal("elightning").executes(lightningCmd.getCommand()).redirect(lightningCmd.build()));
        dispatcher.register(Commands.literal("shock").executes(lightningCmd.getCommand()).redirect(lightningCmd.build()));
        dispatcher.register(Commands.literal("eshock").executes(lightningCmd.getCommand()).redirect(lightningCmd.build()));
        dispatcher.register(Commands.literal("smite").executes(lightningCmd.getCommand()).redirect(lightningCmd.build()));
        dispatcher.register(Commands.literal("esmite").executes(lightningCmd.getCommand()).redirect(lightningCmd.build()));
        dispatcher.register(Commands.literal("strike").executes(lightningCmd.getCommand()).redirect(lightningCmd.build()));
        dispatcher.register(Commands.literal("estrike").executes(lightningCmd.getCommand()).redirect(lightningCmd.build()));
        dispatcher.register(Commands.literal("thor").executes(lightningCmd.getCommand()).redirect(lightningCmd.build()));
        dispatcher.register(Commands.literal("ethor").executes(lightningCmd.getCommand()).redirect(lightningCmd.build()));


    }

    public static int executeLightning(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
            if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
                net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos();
                net.minecraft.world.entity.LightningBolt bolt = net.minecraft.world.entity.EntityType.LIGHTNING_BOLT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
                if (bolt != null) {
                    bolt.setPos(net.minecraft.world.phys.Vec3.atBottomCenterOf(pos));
                    player.level().addFreshEntity(bolt);
                    context.getSource().sendSystemMessage(Component.literal("Smite!"));
                }
            }
            return 1;
        }

}
