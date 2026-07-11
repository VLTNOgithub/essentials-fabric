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

public class CommandNuke {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> nukeCmd = Commands.literal("nuke")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.nuke", 2))
            .executes(context -> executeNuke(context))
        ;
        dispatcher.register(nukeCmd);
        dispatcher.register(Commands.literal("enuke").executes(nukeCmd.getCommand()).redirect(nukeCmd.build()));


    }

    public static int executeNuke(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            context.getSource().sendSystemMessage(Component.literal("May death rain upon them."));
            for (ServerPlayer target : context.getSource().getServer().getPlayerList().getPlayers()) {
                net.minecraft.world.entity.item.PrimedTnt tnt = net.minecraft.world.entity.EntityType.TNT.create(target.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
                if (tnt != null) {
                    tnt.setPos(target.getX(), target.getY() + 10, target.getZ());
                    tnt.setFuse(40);
                    target.level().addFreshEntity(tnt);
                }
            }
            return 1;
        }

}
