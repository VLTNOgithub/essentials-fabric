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

public class CommandRepair {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> repairCmd = Commands.literal("repair")
            .executes(context -> executeRepair(context))
        ;
        dispatcher.register(repairCmd);
        dispatcher.register(Commands.literal("fix").redirect(repairCmd.build()));
        dispatcher.register(Commands.literal("efix").redirect(repairCmd.build()));
        dispatcher.register(Commands.literal("erepair").redirect(repairCmd.build()));


    }

    public static int executeRepair(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
            if (hand.isEmpty() || !hand.isDamageableItem()) {
                context.getSource().sendSystemMessage(Component.literal("You are not holding a repairable item."));
                return 0;
            }
            hand.setDamageValue(0);
            context.getSource().sendSystemMessage(Component.literal("Item repaired successfully."));
            return 1;
        }

}
