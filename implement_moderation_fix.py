import os
import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix execution signatures in literal nodes
content = content.replace('Collection<com.mojang.authlib.GameProfile> targets', 'Collection<net.minecraft.server.players.NameAndId> targets')

# Fix Ban Method body
ban_stub = r'    private static int executeBan\(CommandContext<CommandSourceStack> context, Collection<net.minecraft.server.players.NameAndId> targets, String reason\) \{[\s\S]*?return targets\.size\(\);\n    \}'
ban_impl = '''    private static int executeBan(CommandContext<CommandSourceStack> context, Collection<net.minecraft.server.players.NameAndId> targets, String reason) {
        if (targets.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Please specify a player to ban."));
            return 0;
        }
        net.minecraft.server.players.UserBanList banList = context.getSource().getServer().getPlayerList().getBans();
        for (net.minecraft.server.players.NameAndId profile : targets) {
            net.minecraft.server.players.UserBanListEntry entry = new net.minecraft.server.players.UserBanListEntry(profile, null, context.getSource().getTextName(), null, reason != null ? reason : "Banned by an operator.");
            banList.add(entry);
            ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayer(profile.id());
            if (player != null) {
                player.connection.disconnect(Component.literal(reason != null ? reason : "Banned by an operator."));
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Banned " + targets.size() + " players."));
        return targets.size();
    }'''
content = re.sub(ban_stub, ban_impl, content)

# Fix Unban Method body
unban_stub = r'    private static int executeUnban\(CommandContext<CommandSourceStack> context, Collection<net.minecraft.server.players.NameAndId> targets\) \{[\s\S]*?return targets\.size\(\);\n    \}'
unban_impl = '''    private static int executeUnban(CommandContext<CommandSourceStack> context, Collection<net.minecraft.server.players.NameAndId> targets) {
        if (targets.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Please specify a player to unban."));
            return 0;
        }
        net.minecraft.server.players.UserBanList banList = context.getSource().getServer().getPlayerList().getBans();
        for (net.minecraft.server.players.NameAndId profile : targets) {
            banList.remove(profile);
        }
        context.getSource().sendSystemMessage(Component.literal("Unbanned " + targets.size() + " players."));
        return targets.size();
    }'''
content = re.sub(unban_stub, unban_impl, content)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Moderation fixed.")