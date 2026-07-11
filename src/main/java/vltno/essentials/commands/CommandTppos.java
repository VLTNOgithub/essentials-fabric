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

public class CommandTppos {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> tpposCmd = Commands.literal("tppos")
        .then(Commands.argument("pos", net.minecraft.commands.arguments.coordinates.Vec3Argument.vec3())
            .executes(context -> executeTppos(context, net.minecraft.commands.arguments.coordinates.Vec3Argument.getCoordinates(context, "pos")))
        )
    ;
        dispatcher.register(tpposCmd);
        dispatcher.register(Commands.literal("etppos").redirect(tpposCmd.build()));


    }

    public static int executeTppos(CommandContext<CommandSourceStack> context, net.minecraft.commands.arguments.coordinates.Coordinates pos) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.phys.Vec3 vec = pos.getPosition(context.getSource());
            saveBackLocation(player);
            player.teleportTo(player.level(), vec.x, vec.y, vec.z, java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal(String.format("Teleported to %.1f, %.1f, %.1f", vec.x, vec.y, vec.z)));
            return 1;
        }

}
