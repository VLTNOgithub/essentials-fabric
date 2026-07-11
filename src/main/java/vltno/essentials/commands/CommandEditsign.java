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

public class CommandEditsign {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> signCmd = Commands.literal("editsign")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.editsign", 2))
            .then(Commands.literal("set")
                .then(Commands.argument("line", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 4))
                    .then(Commands.argument("text", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                        .executes(context -> executeEditsignSet(context, com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "line"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "text")))
                    )
                )
            )
            .then(Commands.literal("clear")
                .then(Commands.argument("line", com.mojang.brigadier.arguments.IntegerArgumentType.integer(1, 4))
                    .executes(context -> executeEditsignSet(context, com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "line"), ""))
                )
            );
        dispatcher.register(signCmd);
        dispatcher.register(Commands.literal("sign").redirect(signCmd.build()));
        dispatcher.register(Commands.literal("esign").redirect(signCmd.build()));
        dispatcher.register(Commands.literal("eeditsign").redirect(signCmd.build()));

    }

    public static int executeEditsignSet(CommandContext<CommandSourceStack> context, int line, String text) throws CommandSyntaxException {
        net.minecraft.server.level.ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.HitResult hit = player.pick(10.0D, 0.0F, false);
        if (hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK) {
            net.minecraft.core.BlockPos pos = ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos();
            net.minecraft.world.level.block.entity.BlockEntity be = player.level().getBlockEntity(pos);
            if (be instanceof net.minecraft.world.level.block.entity.SignBlockEntity sign) {
                // Determine which side we are facing. Actually we just edit front text for simplicity if not looking directly at back
                boolean isFront = sign.isFacingFrontText(player);
                sign.updateText(signText -> signText.setMessage(line - 1, Component.literal(text.replace("&", "\u00A7"))), isFront);
                context.getSource().sendSystemMessage(Component.literal("Sign line " + line + " updated."));
                return 1;
            }
        }
        context.getSource().sendSystemMessage(Component.literal("You must be looking at a sign."));
        return 0;
    }

}
