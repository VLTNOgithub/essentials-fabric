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

public class CommandSkull {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> skullCmd = Commands.literal("skull")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.skull", 2))
            .executes(context -> executeSkull(context))
        ;
        dispatcher.register(skullCmd);
        dispatcher.register(Commands.literal("eskull").redirect(skullCmd.build()));
        dispatcher.register(Commands.literal("playerskull").redirect(skullCmd.build()));
        dispatcher.register(Commands.literal("eplayerskull").redirect(skullCmd.build()));
        dispatcher.register(Commands.literal("head").redirect(skullCmd.build()));
        dispatcher.register(Commands.literal("ehead").redirect(skullCmd.build()));


    }

    public static int executeSkull(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            net.minecraft.world.item.ItemStack skull = new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.PLAYER_HEAD, 1);
            // Adding profile component normally requires NBT handling, we just give the item here.
            if (!player.getInventory().add(skull)) player.drop(skull, false);
            context.getSource().sendSystemMessage(Component.literal("You received a player skull."));
            return 1;
        }

}
