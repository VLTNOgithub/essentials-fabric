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

public class CommandBreak {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> breakCmd = Commands.literal("break")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.break", 0))
            .executes(context -> executeBreak(context))
        ;
        dispatcher.register(breakCmd);
        dispatcher.register(Commands.literal("ebreak").redirect(breakCmd.build()));


    }

    public static int executeBreak(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
            if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
                net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos();
                player.level().destroyBlock(pos, true);
                context.getSource().sendSystemMessage(Component.literal("Block broken."));
                return 1;
            }
            context.getSource().sendSystemMessage(Component.literal("No block in sight."));
            return 0;
        }

}
