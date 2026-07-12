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

public class CommandIce {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
                for (String alias : new String[]{"ice", "eice", "efreeze"}) {
            dispatcher.register(Commands.literal(alias)
            .requires(vltno.essentials.EssentialsCommands.require("essentials.ice", 0))
        .executes(context -> executeIce(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException())))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
            .executes(context -> executeIce(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets")))
        )
    );
        }


    }

    public static int executeIce(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeIce(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException())); }

    public static int executeIce(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) {
            for (net.minecraft.world.entity.Entity target : targets) {
                target.setTicksFrozen(target.getTicksRequiredToFreeze() + 200);
                if (target instanceof ServerPlayer p) p.sendSystemMessage(Component.literal("You have been iced."));
            }
            context.getSource().sendSystemMessage(Component.literal("Iced " + targets.size() + " entities."));
            return targets.size();
        }

}
