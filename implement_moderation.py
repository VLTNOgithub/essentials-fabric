import os
import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Add missing imports if needed
if 'import java.util.Collection;' not in content:
    content = content.replace('import com.mojang.brigadier.exceptions.CommandSyntaxException;',
                              'import com.mojang.brigadier.exceptions.CommandSyntaxException;\nimport java.util.Collection;\nimport java.util.Collections;')

# 1. Kick Registration
content = re.sub(
    r'dispatcher\.register\(Commands\.literal\("([^"]+)"\)\s*\.executes\(context -> executeKick\(context\)\)\s*\);',
    r'''dispatcher.register(Commands.literal("\1")
        .executes(context -> executeKick(context, Collections.emptyList(), null))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
            .executes(context -> executeKick(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), null))
            .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeKick(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
            )
        )
    );''',
    content
)

# Kick Method
kick_stub = r'    private static int executeKick\(CommandContext<CommandSourceStack> context\) \{[\s\S]*?return 1;\n    \}'
kick_impl = '''    private static int executeKick(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets, String reason) {
        if (targets.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Please specify a player to kick."));
            return 0;
        }
        Component reasonComp = Component.literal(reason != null ? reason : "Kicked by an operator.");
        for (ServerPlayer target : targets) {
            target.connection.disconnect(reasonComp);
        }
        context.getSource().sendSystemMessage(Component.literal("Kicked " + targets.size() + " players."));
        return targets.size();
    }'''
content = re.sub(kick_stub, kick_impl, content)

# 2. Ban Registration
content = re.sub(
    r'dispatcher\.register\(Commands\.literal\("([^"]+)"\)\s*\.executes\(context -> executeBan\(context\)\)\s*\);',
    r'''dispatcher.register(Commands.literal("\1")
        .executes(context -> executeBan(context, Collections.emptyList(), null))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.GameProfileArgument.gameProfile())
            .executes(context -> executeBan(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets"), null))
            .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
                .executes(context -> executeBan(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
            )
        )
    );''',
    content
)

# Ban Method
ban_stub = r'    private static int executeBan\(CommandContext<CommandSourceStack> context\) \{[\s\S]*?return 1;\n    \}'
ban_impl = '''    private static int executeBan(CommandContext<CommandSourceStack> context, Collection<com.mojang.authlib.GameProfile> targets, String reason) {
        if (targets.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Please specify a player to ban."));
            return 0;
        }
        net.minecraft.server.players.UserBanList banList = context.getSource().getServer().getPlayerList().getBans();
        for (com.mojang.authlib.GameProfile profile : targets) {
            net.minecraft.server.players.UserBanListEntry entry = new net.minecraft.server.players.UserBanListEntry(profile, null, context.getSource().getTextName(), null, reason != null ? reason : "Banned by an operator.");
            banList.add(entry);
            ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayer(profile.getId());
            if (player != null) {
                player.connection.disconnect(Component.literal(reason != null ? reason : "Banned by an operator."));
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Banned " + targets.size() + " players."));
        return targets.size();
    }'''
content = re.sub(ban_stub, ban_impl, content)

# 3. Unban Registration
content = re.sub(
    r'dispatcher\.register\(Commands\.literal\("([^"]+)"\)\s*\.executes\(context -> executeUnban\(context\)\)\s*\);',
    r'''dispatcher.register(Commands.literal("\1")
        .executes(context -> executeUnban(context, Collections.emptyList()))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.GameProfileArgument.gameProfile())
            .executes(context -> executeUnban(context, net.minecraft.commands.arguments.GameProfileArgument.getGameProfiles(context, "targets")))
        )
    );''',
    content
)

# Unban Method
unban_stub = r'    private static int executeUnban\(CommandContext<CommandSourceStack> context\) \{[\s\S]*?return 1;\n    \}'
unban_impl = '''    private static int executeUnban(CommandContext<CommandSourceStack> context, Collection<com.mojang.authlib.GameProfile> targets) {
        if (targets.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Please specify a player to unban."));
            return 0;
        }
        net.minecraft.server.players.UserBanList banList = context.getSource().getServer().getPlayerList().getBans();
        for (com.mojang.authlib.GameProfile profile : targets) {
            banList.remove(profile);
        }
        context.getSource().sendSystemMessage(Component.literal("Unbanned " + targets.size() + " players."));
        return targets.size();
    }'''
content = re.sub(unban_stub, unban_impl, content)

# 4. Kickall Registration
content = re.sub(
    r'dispatcher\.register\(Commands\.literal\("([^"]+)"\)\s*\.executes\(context -> executeKickall\(context\)\)\s*\);',
    r'''dispatcher.register(Commands.literal("\1")
        .executes(context -> executeKickall(context, null))
        .then(Commands.argument("reason", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeKickall(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "reason")))
        )
    );''',
    content
)

# Kickall Method
kickall_stub = r'    private static int executeKickall\(CommandContext<CommandSourceStack> context\) \{[\s\S]*?return 1;\n    \}'
kickall_impl = '''    private static int executeKickall(CommandContext<CommandSourceStack> context, String reason) {
        Component reasonComp = Component.literal(reason != null ? reason : "Kicked by an operator.");
        int count = 0;
        for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
            if (context.getSource().getEntity() != player) {
                player.connection.disconnect(reasonComp);
                count++;
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Kicked " + count + " players."));
        return count;
    }'''
content = re.sub(kickall_stub, kickall_impl, content)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Moderation commands injected.")