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

public class CommandRemove {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> removeCmd = Commands.literal("remove")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.remove", 2))
            .executes(context -> executeRemove(context))
        ;
        dispatcher.register(removeCmd);
        dispatcher.register(Commands.literal("eremove").redirect(removeCmd.build()));
        dispatcher.register(Commands.literal("butcher").redirect(removeCmd.build()));
        dispatcher.register(Commands.literal("ebutcher").redirect(removeCmd.build()));
        dispatcher.register(Commands.literal("killall").redirect(removeCmd.build()));
        dispatcher.register(Commands.literal("ekillall").redirect(removeCmd.build()));
        dispatcher.register(Commands.literal("mobkill").redirect(removeCmd.build()));
        dispatcher.register(Commands.literal("emobkill").redirect(removeCmd.build()));


    }

    public static int executeRemove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            int count = 0;
            for (net.minecraft.world.entity.Entity entity : player.level().getEntitiesOfClass(net.minecraft.world.entity.Entity.class, player.getBoundingBox().inflate(100.0))) {
                if (entity instanceof net.minecraft.world.entity.item.ItemEntity) {
                    entity.discard();
                    count++;
                }
            }
            context.getSource().sendSystemMessage(Component.literal("Removed " + count + " dropped items."));
            return 1;
        }

}
