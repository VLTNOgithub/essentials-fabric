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

public class CommandBigtree {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> bigtreeCmd = Commands.literal("bigtree")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.bigtree", 0))
            .executes(context -> executeBigtree(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> bigtreeCmdNode = dispatcher.register(bigtreeCmd);
        dispatcher.register(Commands.literal("ebigtree").requires(bigtreeCmdNode.getRequirement()).redirect(bigtreeCmdNode));
        dispatcher.register(Commands.literal("largetree").requires(bigtreeCmdNode.getRequirement()).redirect(bigtreeCmdNode));
        dispatcher.register(Commands.literal("elargetree").requires(bigtreeCmdNode.getRequirement()).redirect(bigtreeCmdNode));


    }

    public static int executeBigtree(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
            if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
                net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos().above();
                player.level().setBlock(pos, net.minecraft.world.level.block.Blocks.OAK_SAPLING.defaultBlockState(), 3);
                context.getSource().sendSystemMessage(Component.literal("Tree spawned."));
                return 1;
            }
            return 0;
        }

}
