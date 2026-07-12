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

public class CommandSpawnmob {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"spawnmob", "mob", "emob", "spawnentity", "espawnentity", "espawnmob"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.spawnmob", 2))
            .then(Commands.argument("mob", net.minecraft.commands.arguments.ResourceArgument.resource(registryAccess, net.minecraft.core.registries.Registries.ENTITY_TYPE))
                .executes(context -> executeSpawnmob(context, net.minecraft.commands.arguments.ResourceArgument.getEntityType(context, "mob"), 1))
                .then(Commands.argument("amount", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1))
                    .executes(context -> executeSpawnmob(context, net.minecraft.commands.arguments.ResourceArgument.getEntityType(context, "mob"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "amount")))
                )
            ));
        }

    }

    public static int executeSpawnmob(CommandContext<CommandSourceStack> context, net.minecraft.core.Holder.Reference<net.minecraft.world.entity.EntityType<?>> entityType, int amount) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
        net.minecraft.core.BlockPos pos = hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK ? ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos().above() : player.blockPosition();
        for (int i = 0; i < amount; i++) {
            net.minecraft.world.entity.Entity entity = entityType.value().create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            if (entity != null) {
                entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                player.level().addFreshEntity(entity);
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Spawned " + amount + " mobs."));
        return amount;
    }

}
