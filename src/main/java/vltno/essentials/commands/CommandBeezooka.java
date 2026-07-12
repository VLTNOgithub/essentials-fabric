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

public class CommandBeezooka {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"beezooka", "ebeezooka", "beecannon", "ebeecannon"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.beezooka", 0))
            .executes(context -> executeBeezooka(context))
        );
        }


    }

    public static int executeBeezooka(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.entity.Entity bee = net.minecraft.world.entity.EntityType.BEE.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (bee != null) {
                bee.setPos(player.getX(), player.getEyeY(), player.getZ());
                net.minecraft.world.phys.Vec3 look = player.getLookAngle().scale(2.0);
                bee.setDeltaMovement(look);
                player.level().addFreshEntity(bee);
                net.minecraft.world.entity.item.PrimedTnt tnt = net.minecraft.world.entity.EntityType.TNT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
                tnt.setPos(bee.getX(), bee.getY(), bee.getZ());
                tnt.startRiding(bee);
                tnt.setFuse(20);
                player.level().addFreshEntity(tnt);
                context.getSource().sendSystemMessage(Component.literal("Bzzz!"));
            }
            return 1;
        }

}
