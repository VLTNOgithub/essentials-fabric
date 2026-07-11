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

public class CommandTogglejail {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registryAccess) {
        com.mojang.brigadier.builder.LiteralArgumentBuilder<CommandSourceStack> togglejailCmd = Commands.literal("togglejail")
            .requires(vltno.essentials.EssentialsCommands.require("essentials.togglejail", 2))
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .then(Commands.argument("jailname", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeTogglejail(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "jailname")))
            )
        );
        dispatcher.register(togglejailCmd);
        dispatcher.register(Commands.literal("jail").executes(togglejailCmd.getCommand()).redirect(togglejailCmd.build()));
        dispatcher.register(Commands.literal("ejail").executes(togglejailCmd.getCommand()).redirect(togglejailCmd.build()));
        dispatcher.register(Commands.literal("tjail").executes(togglejailCmd.getCommand()).redirect(togglejailCmd.build()));
        dispatcher.register(Commands.literal("etjail").executes(togglejailCmd.getCommand()).redirect(togglejailCmd.build()));
        dispatcher.register(Commands.literal("etogglejail").executes(togglejailCmd.getCommand()).redirect(togglejailCmd.build()));
        dispatcher.register(Commands.literal("unjail").executes(togglejailCmd.getCommand()).redirect(togglejailCmd.build()));
        dispatcher.register(Commands.literal("eunjail").executes(togglejailCmd.getCommand()).redirect(togglejailCmd.build()));

    }

    public static int executeTogglejail(CommandContext<CommandSourceStack> context, ServerPlayer target, String jailname) throws CommandSyntaxException {
            UserData data = UserCache.getUser(target);
            if (data.jail != null) {
                data.jail = null;
                UserCache.saveUser(target.getUUID());
                context.getSource().sendSystemMessage(Component.literal("Unjailed " + target.getName().getString()));
                target.sendSystemMessage(Component.literal("You have been released from jail."));
                return 1;
            }
            HomePosition jailPos = JAILS.get(jailname.toLowerCase());
            if (jailPos == null) {
                context.getSource().sendSystemMessage(Component.literal("Jail '" + jailname + "' not found."));
                return 0;
            }
            data.jail = jailname.toLowerCase();
            UserCache.saveUser(target.getUUID());
            net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, net.minecraft.resources.Identifier.parse(jailPos.dimension));
            net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
            if (targetLevel != null) {
                target.teleportTo(targetLevel, jailPos.x, jailPos.y, jailPos.z, java.util.Collections.emptySet(), jailPos.yaw, jailPos.pitch, false);
            }
            context.getSource().sendSystemMessage(Component.literal("Jailed " + target.getName().getString() + " in " + jailname));
            target.sendSystemMessage(Component.literal("You have been jailed.").withStyle(net.minecraft.ChatFormatting.RED));
            return 1;
        }

}
