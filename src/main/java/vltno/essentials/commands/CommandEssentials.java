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

public class CommandEssentials {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> essentialsCmd = Commands.literal("essentials")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.essentials", 2))
            .executes(context -> executeEssentials(context))
        ;
        com.mojang.brigadier.tree.LiteralCommandNode<CommandSourceStack> essentialsCmdNode = dispatcher.register(essentialsCmd);
        dispatcher.register(Commands.literal("eessentials").requires(essentialsCmdNode.getRequirement()).redirect(essentialsCmdNode));
        dispatcher.register(Commands.literal("ess").requires(essentialsCmdNode.getRequirement()).redirect(essentialsCmdNode));
        dispatcher.register(Commands.literal("eess").requires(essentialsCmdNode.getRequirement()).redirect(essentialsCmdNode));
        dispatcher.register(Commands.literal("essversion").requires(essentialsCmdNode.getRequirement()).redirect(essentialsCmdNode));


    }

    public static int executeEssentials(CommandContext<CommandSourceStack> context) {
            context.getSource().sendSystemMessage(Component.literal("Essentials Fabric Port v1.0"));
            return 1;
        }

}
