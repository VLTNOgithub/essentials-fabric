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

public class CommandWarpinfo {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> warpinfoCmd = Commands.literal("warpinfo")
            .then(Commands.argument("warp", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeWarpinfo(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "warp")))
            );
        dispatcher.register(warpinfoCmd);
        dispatcher.register(Commands.literal("ewarpinfo").redirect(warpinfoCmd.build()));

    }

    public static int executeWarpinfo(CommandContext<CommandSourceStack> context, String warp) {
        vltno.essentials.EssentialsCommands.HomePosition pos = vltno.essentials.EssentialsCommands.WARPS.get(warp.toLowerCase());
        if (pos == null) {
            context.getSource().sendSystemMessage(net.minecraft.network.chat.Component.literal("Warp '" + warp + "' not found."));
            return 0;
        }
        context.getSource().sendSystemMessage(net.minecraft.network.chat.Component.literal("Warp '" + warp + "' Info:\nDimension: " + pos.dimension + "\nLocation: X: " + String.format("%.1f", pos.x) + " Y: " + String.format("%.1f", pos.y) + " Z: " + String.format("%.1f", pos.z)));
        return 1;
    }

}
