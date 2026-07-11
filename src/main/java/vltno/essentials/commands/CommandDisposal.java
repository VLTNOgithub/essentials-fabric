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

public class CommandDisposal {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("disposal")
            .executes(context -> executeDisposal(context))
        );
        dispatcher.register(Commands.literal("edisposal")
            .executes(context -> executeDisposal(context))
        );
        dispatcher.register(Commands.literal("trash")
            .executes(context -> executeDisposal(context))
        );
        dispatcher.register(Commands.literal("etrash")
            .executes(context -> executeDisposal(context))
        );

    }

    public static int executeDisposal(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inv, p) -> net.minecraft.world.inventory.ChestMenu.threeRows(id, inv, new net.minecraft.world.SimpleContainer(27)), Component.literal("Disposal")));
            return 1;
        }

}
