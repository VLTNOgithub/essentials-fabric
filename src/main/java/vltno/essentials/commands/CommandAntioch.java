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

public class CommandAntioch {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> antiochCmd = Commands.literal("antioch")
        .executes(context -> executeAntioch(context, null))
        .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeAntioch(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
        );
        dispatcher.register(antiochCmd);
        dispatcher.register(Commands.literal("eantioch").redirect(antiochCmd.build()));
        dispatcher.register(Commands.literal("grenade").redirect(antiochCmd.build()));
        dispatcher.register(Commands.literal("egrenade").redirect(antiochCmd.build()));
        dispatcher.register(Commands.literal("tnt").redirect(antiochCmd.build()));
        dispatcher.register(Commands.literal("etnt").redirect(antiochCmd.build()));

    }

    public static int executeAntioch(CommandContext<CommandSourceStack> context, String message) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            if (message != null && !message.isEmpty()) {
                context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal("...lobbest thou thy Holy Hand Grenade of Antioch towards thy foe,"), false);
                context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal("who being naughty in My sight, shall snuff it."), false);
            }
            net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
            net.minecraft.core.BlockPos pos = hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK ? ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos() : player.blockPosition();
            net.minecraft.world.entity.item.PrimedTnt tnt = net.minecraft.world.entity.EntityType.TNT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (tnt != null) {
                tnt.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                tnt.setFuse(40);
                player.level().addFreshEntity(tnt);
            }
            return 1;
        }

}
