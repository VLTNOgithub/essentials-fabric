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

public class CommandFirework {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> fireworkCmd = Commands.literal("firework")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.firework", 2))
            .executes(context -> executeFirework(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> fireworkCmdNode = dispatcher.register(fireworkCmd);
        dispatcher.register(Commands.literal("efirework").requires(fireworkCmdNode.getRequirement()).redirect(fireworkCmdNode));


    }

    public static int executeFirework(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.entity.projectile.FireworkRocketEntity rocket = new net.minecraft.world.entity.projectile.FireworkRocketEntity(player.level(), player.getX(), player.getY(), player.getZ(), net.minecraft.world.item.ItemStack.EMPTY);
            player.level().addFreshEntity(rocket);
            context.getSource().sendSystemMessage(Component.literal("Firework spawned."));
            return 1;
        }

}
