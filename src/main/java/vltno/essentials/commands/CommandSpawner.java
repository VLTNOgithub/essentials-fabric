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

public class CommandSpawner {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> spawnerCmd = Commands.literal("spawner")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.spawner", 2))
            .then(Commands.argument("mob", net.minecraft.commands.arguments.ResourceArgument.resource(registryAccess, net.minecraft.core.registries.Registries.ENTITY_TYPE))
                .executes(context -> executeSpawner(context, net.minecraft.commands.arguments.ResourceArgument.getEntityType(context, "mob")))
            );
        dispatcher.register(spawnerCmd);
        dispatcher.register(Commands.literal("changems").redirect(spawnerCmd.build()));
        dispatcher.register(Commands.literal("echangems").redirect(spawnerCmd.build()));
        dispatcher.register(Commands.literal("espawner").redirect(spawnerCmd.build()));
        dispatcher.register(Commands.literal("mobspawner").redirect(spawnerCmd.build()));
        dispatcher.register(Commands.literal("emobspawner").redirect(spawnerCmd.build()));

    }

    public static int executeSpawner(CommandContext<CommandSourceStack> context, net.minecraft.core.Holder.Reference<net.minecraft.world.entity.EntityType<?>> entityType) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
        if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos();
            net.minecraft.world.level.block.entity.BlockEntity be = player.level().getBlockEntity(pos);
            if (be instanceof net.minecraft.world.level.block.entity.SpawnerBlockEntity spawner) {
                spawner.setEntityId(entityType.value(), player.level().getRandom());
                be.setChanged();
                player.level().sendBlockUpdated(pos, be.getBlockState(), be.getBlockState(), 3);
                context.getSource().sendSystemMessage(Component.literal("Spawner type changed."));
                return 1;
            }
        }
        context.getSource().sendSystemMessage(Component.literal("You must be looking at a spawner."));
        return 0;
    }

}
