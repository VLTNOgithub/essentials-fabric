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

public class CommandDelkit {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"delkit", "edelkit", "remkit", "eremkit", "rmkit", "ermkit", "deletekit", "edeletekit"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.delkit", 2))
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeDelkit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        ));
        }

    }

    public static int executeDelkit(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /delkit <name>")); return 0; }

    public static int executeDelkit(CommandContext<CommandSourceStack> context, String name) {
            if (KITS.remove(name.toLowerCase()) != null) {
                saveKits(); saveJailsWarps();
                context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' deleted."));
                return 1;
            }
            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));
            return 0;
        }

}
