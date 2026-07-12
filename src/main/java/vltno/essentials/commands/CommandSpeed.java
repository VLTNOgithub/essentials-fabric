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
                for (String alias : new String[]{"speed", "espeed"}) {
            dispatcher.register(Commands.literal(alias)
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
            ));
        }

        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> flyspeedCmd = Commands.literal("flyspeed")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.flyspeed", 0))
            .then(Commands.argument("speed", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0, 10))
                .executes(context -> executeSpeed(context, com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "speed"), "fly"))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> flyspeedCmdNode = dispatcher.register(flyspeedCmd);
        dispatcher.register(Commands.literal("eflyspeed").requires(flyspeedCmdNode.getRequirement()).redirect(flyspeedCmdNode));
        dispatcher.register(Commands.literal("fspeed").requires(flyspeedCmdNode.getRequirement()).redirect(flyspeedCmdNode));
        dispatcher.register(Commands.literal("efspeed").requires(flyspeedCmdNode.getRequirement()).redirect(flyspeedCmdNode));

        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> walkspeedCmd = Commands.literal("walkspeed")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.walkspeed", 0))
            .then(Commands.argument("speed", com.mojang.brigadier.arguments.FloatArgumentType.floatArg(0, 10))
                .executes(context -> executeSpeed(context, com.mojang.brigadier.arguments.FloatArgumentType.getFloat(context, "speed"), "walk"))
            );
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> walkspeedCmdNode = dispatcher.register(walkspeedCmd);
        dispatcher.register(Commands.literal("ewalkspeed").requires(walkspeedCmdNode.getRequirement()).redirect(walkspeedCmdNode));
        dispatcher.register(Commands.literal("wspeed").requires(walkspeedCmdNode.getRequirement()).redirect(walkspeedCmdNode));
        dispatcher.register(Commands.literal("ewspeed").requires(walkspeedCmdNode.getRequirement()).redirect(walkspeedCmdNode));

    }

    public static int executeSpeed(CommandContext<CommandSourceStack> context, float speed, String type) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        vltno.essentials.UserData data = vltno.essentials.UserCache.getUser(player);
        if (speed == -1) {
            player.getAbilities().setFlyingSpeed(0.05F);
            net.minecraft.world.entity.ai.attributes.AttributeInstance walkAttr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
            if (walkAttr != null) walkAttr.setBaseValue(0.10000000149011612D);
            player.getAbilities().setWalkingSpeed(0.1F);
            player.onUpdateAbilities();
            data.flySpeed = 0.05F;
            data.walkSpeed = 0.1F;
            vltno.essentials.UserCache.saveUser(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Speed reset to defaults."));
            return 1;
        }
        float realSpeed = speed / 10.0F; // Essentials normalizes 1-10 to 0.1-1.0
        if (type.equals("fly") || (type.equals("both") && player.getAbilities().flying)) {
            player.getAbilities().setFlyingSpeed(realSpeed / 2.0F); // Default fly is 0.05
            data.flySpeed = realSpeed / 2.0F;
            context.getSource().sendSystemMessage(Component.literal("Fly speed set to " + speed));
        }
        if (type.equals("walk") || (type.equals("both") && !player.getAbilities().flying)) {
            net.minecraft.world.entity.ai.attributes.AttributeInstance walkAttr = player.getAttribute(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED);
            if (walkAttr != null) walkAttr.setBaseValue(realSpeed); // Default walk is 0.1, so setting to realSpeed aligns perfectly (1 = 0.1, 10 = 1.0)
            player.getAbilities().setWalkingSpeed(realSpeed); // Still send to client for standard sync
            data.walkSpeed = realSpeed;
            context.getSource().sendSystemMessage(Component.literal("Walk speed set to " + speed));
        }
        player.onUpdateAbilities();
        vltno.essentials.UserCache.saveUser(player.getUUID());
        return 1;
    }

}
