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

public class CommandStonecutter {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> stonecutterCmd = Commands.literal("stonecutter")
            .executes(context -> executeStonecutter(context))
        ;
        dispatcher.register(stonecutterCmd);
        dispatcher.register(Commands.literal("estonecutter").redirect(stonecutterCmd.build()));


    }

    public static int executeStonecutter(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
                return new net.minecraft.world.inventory.StonecutterMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                    @Override
                    public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
                };
            }, Component.literal("Stonecutter")));
            return 1;
        }

}
