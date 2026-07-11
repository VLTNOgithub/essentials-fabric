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

public class CommandHome {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        dispatcher.register(Commands.literal("home")
        .executes(context -> executeHome(context))
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeHome(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    );
        dispatcher.register(Commands.literal("ehome")
            .executes(context -> executeHome(context))
        );
        dispatcher.register(Commands.literal("homes")
            .executes(context -> executeHome(context))
        );
        dispatcher.register(Commands.literal("ehomes")
            .executes(context -> executeHome(context))
        );

    }

    public static int executeHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            java.util.Map<String, HomePosition> homes = playerHomes.get(player.getUUID());
            if (homes == null || homes.isEmpty()) {
                context.getSource().sendSystemMessage(Component.literal("You have no homes set."));
                return 0;
            }
            if (homes.size() == 1) {
                // Teleport to the only home
                return executeHome(context, homes.keySet().iterator().next());
            }
            // List homes
            context.getSource().sendSystemMessage(Component.literal("Homes: " + String.join(", ", homes.keySet())));
            return 1;
        }

    public static int executeHome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
            ServerPlayer player = context.getSource().getPlayerOrException();
            java.util.Map<String, HomePosition> homes = playerHomes.get(player.getUUID());
            if (homes == null || !homes.containsKey(name.toLowerCase())) {
                context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' does not exist."));
                return 0;
            }
            HomePosition home = homes.get(name.toLowerCase());
            net.minecraft.resources.Identifier dimLoc = net.minecraft.resources.Identifier.parse(home.dimension);
            net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimLoc);
            net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
            if (targetLevel == null) {
                context.getSource().sendSystemMessage(Component.literal("Invalid dimension for home."));
                return 0;
            }
            saveBackLocation(player);
            player.teleportTo(targetLevel, home.x, home.y, home.z, java.util.Collections.emptySet(), home.yaw, home.pitch, false);
            context.getSource().sendSystemMessage(Component.literal("Teleported to home '" + name + "'."));
            return 1;
        }

}
