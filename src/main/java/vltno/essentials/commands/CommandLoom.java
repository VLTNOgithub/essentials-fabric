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

public class CommandLoom {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> loomCmd = Commands.literal("loom")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.loom", 0))
            .executes(context -> executeLoom(context))
        ;
        dispatcher.register(loomCmd);
        dispatcher.register(Commands.literal("eloom").redirect(loomCmd.build()));


    }

    public static int executeLoom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
                return new net.minecraft.world.inventory.LoomMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                    @Override
                    public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
                };
            }, Component.literal("Loom")));
            return 1;
        }

}
