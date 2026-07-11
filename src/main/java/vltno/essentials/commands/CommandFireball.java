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

public class CommandFireball {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> fireballCmd = Commands.literal("fireball")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.fireball", 2))
            .executes(context -> executeFireball(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> fireballCmdNode = dispatcher.register(fireballCmd);
        dispatcher.register(Commands.literal("efireball").requires(fireballCmdNode.getRequirement()).redirect(fireballCmdNode));
        dispatcher.register(Commands.literal("fireentity").requires(fireballCmdNode.getRequirement()).redirect(fireballCmdNode));
        dispatcher.register(Commands.literal("efireentity").requires(fireballCmdNode.getRequirement()).redirect(fireballCmdNode));
        dispatcher.register(Commands.literal("fireskull").requires(fireballCmdNode.getRequirement()).redirect(fireballCmdNode));
        dispatcher.register(Commands.literal("efireskull").requires(fireballCmdNode.getRequirement()).redirect(fireballCmdNode));


    }

    public static int executeFireball(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.entity.Entity fireball = net.minecraft.world.entity.EntityType.FIREBALL.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (fireball != null) {
                if (fireball instanceof net.minecraft.world.entity.projectile.Projectile proj) {
                    proj.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
                }
            }
            fireball.setPos(player.getX(), player.getEyeY(), player.getZ());
            player.level().addFreshEntity(fireball);
            context.getSource().sendSystemMessage(Component.literal("Fireball away!"));
            return 1;
        }

}
