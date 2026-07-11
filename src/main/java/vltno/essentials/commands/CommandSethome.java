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

public class CommandSethome {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> sethomeCmd = Commands.literal("sethome")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.sethome", 0))
            .executes(context -> executeSethome(context))
            .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeSethome(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
            );
        dispatcher.register(sethomeCmd);
        dispatcher.register(Commands.literal("esethome").redirect(sethomeCmd.build()));
        dispatcher.register(Commands.literal("createhome").redirect(sethomeCmd.build()));
        dispatcher.register(Commands.literal("ecreatehome").redirect(sethomeCmd.build()));

    }

    public static int executeSethome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeSethome(context, "home"); }

    public static int executeSethome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            UserData data = UserCache.getUser(player);
            String dim = player.level().dimension().identifier().toString();
            HomePosition home = new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), dim);
            data.homes.put(name.toLowerCase(), home);
            UserCache.saveUser(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' set."));
            return 1;
        }

}
