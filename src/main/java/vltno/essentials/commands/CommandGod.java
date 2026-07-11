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

public class CommandGod {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> godCmd = Commands.literal("god")
            .executes(context -> executeGod(context))
        ;
        dispatcher.register(godCmd);
        dispatcher.register(Commands.literal("egod").redirect(godCmd.build()));
        dispatcher.register(Commands.literal("godmode").redirect(godCmd.build()));
        dispatcher.register(Commands.literal("egodmode").redirect(godCmd.build()));
        dispatcher.register(Commands.literal("tgm").redirect(godCmd.build()));
        dispatcher.register(Commands.literal("etgm").redirect(godCmd.build()));


    }

    public static int executeGod(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            boolean isGod = player.isInvulnerable();
            player.setInvulnerable(!isGod);
            context.getSource().sendSystemMessage(Component.literal("God mode " + (!isGod ? "enabled" : "disabled") + "."));
            return 1;
        }

}
