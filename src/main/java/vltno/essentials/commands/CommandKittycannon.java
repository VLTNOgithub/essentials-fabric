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

public class CommandKittycannon {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> kittycannonCmd = Commands.literal("kittycannon")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.kittycannon", 2))
            .executes(context -> executeKittycannon(context))
        ;
        dispatcher.register(kittycannonCmd);
        dispatcher.register(Commands.literal("ekittycannon").executes(kittycannonCmd.getCommand()).redirect(kittycannonCmd.build()));


    }

    public static int executeKittycannon(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.entity.Entity cat = net.minecraft.world.entity.EntityType.CAT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (cat != null) {
                cat.setPos(player.getX(), player.getEyeY(), player.getZ());
                cat.setDeltaMovement(player.getLookAngle().scale(2.0));
                player.level().addFreshEntity(cat);
                net.minecraft.world.entity.item.PrimedTnt tnt = net.minecraft.world.entity.EntityType.TNT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
                tnt.setPos(cat.getX(), cat.getY(), cat.getZ());
                tnt.startRiding(cat);
                tnt.setFuse(20);
                player.level().addFreshEntity(tnt);
                context.getSource().sendSystemMessage(Component.literal("Meow!"));
            }
            return 1;
        }

}
