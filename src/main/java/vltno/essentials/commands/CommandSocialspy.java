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

public class CommandSocialspy {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("socialspy")
            .executes(context -> executeSocialspy(context))
        );
        dispatcher.register(Commands.literal("esocialspy")
            .executes(context -> executeSocialspy(context))
        );

    }

    public static int executeSocialspy(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        net.minecraft.server.level.ServerPlayer player = context.getSource().getPlayerOrException();
        vltno.essentials.UserData data = vltno.essentials.UserCache.getUser(player);
        data.socialSpy = !data.socialSpy;
        vltno.essentials.UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("SocialSpy toggled to: " + data.socialSpy));
        return 1;
    }

}
