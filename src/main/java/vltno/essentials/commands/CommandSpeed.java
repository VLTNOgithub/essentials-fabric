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

public class CommandSpeed {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> speedCmd = Commands.literal("speed")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.speed", 2))
            .executes(context -> executeSpeed(context, -1, "both"))
            .then(Commands.argument("speed", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0, 10))
                .executes(context -> executeSpeed(context, com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "speed"), "both"))
            )
            .then(Commands.literal("fly")
                .then(Commands.argument("speed", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0, 10))
                    .executes(context -> executeSpeed(context, com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "speed"), "fly"))
                )
            )
            .then(Commands.literal("walk")
                .then(Commands.argument("speed", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0, 10))
                    .executes(context -> executeSpeed(context, com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "speed"), "walk"))
                )
            );
        dispatcher.register(speedCmd);
        dispatcher.register(Commands.literal("espeed").redirect(speedCmd.build()));

        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> flyspeedCmd = Commands.literal("flyspeed")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.flyspeed", 0))
            .then(Commands.argument("speed", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0, 10))
                .executes(context -> executeSpeed(context, com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "speed"), "fly"))
            );
        dispatcher.register(flyspeedCmd);
        dispatcher.register(Commands.literal("eflyspeed").redirect(flyspeedCmd.build()));
        dispatcher.register(Commands.literal("fspeed").redirect(flyspeedCmd.build()));
        dispatcher.register(Commands.literal("efspeed").redirect(flyspeedCmd.build()));

        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> walkspeedCmd = Commands.literal("walkspeed")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.walkspeed", 0))
            .then(Commands.argument("speed", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0, 10))
                .executes(context -> executeSpeed(context, com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "speed"), "walk"))
            );
        dispatcher.register(walkspeedCmd);
        dispatcher.register(Commands.literal("ewalkspeed").redirect(walkspeedCmd.build()));
        dispatcher.register(Commands.literal("wspeed").redirect(walkspeedCmd.build()));
        dispatcher.register(Commands.literal("ewspeed").redirect(walkspeedCmd.build()));

    }

    public static int executeSpeed(CommandContext<CommandSourceStack> context, float speed, String type) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (speed == -1) {
            player.getAbilities().setFlyingSpeed(0.05F);
            player.getAbilities().setWalkingSpeed(0.1F);
            player.onUpdateAbilities();
            context.getSource().sendSystemMessage(Component.literal("Speed reset to defaults."));
            return 1;
        }
        float realSpeed = speed / 10.0F; // Essentials normalizes 1-10 to 0.1-1.0
        if (type.equals("fly") || (type.equals("both") && player.getAbilities().flying)) {
            player.getAbilities().setFlyingSpeed(realSpeed / 2.0F); // Default fly is 0.05
            context.getSource().sendSystemMessage(Component.literal("Fly speed set to " + speed));
        }
        if (type.equals("walk") || (type.equals("both") && !player.getAbilities().flying)) {
            player.getAbilities().setWalkingSpeed(realSpeed); // Default walk is 0.1
            context.getSource().sendSystemMessage(Component.literal("Walk speed set to " + speed));
        }
        player.onUpdateAbilities();
        return 1;
    }

}
