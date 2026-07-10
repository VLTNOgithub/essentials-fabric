import os
import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Add TeleportRequest storage at the top of the class
if 'class TeleportRequest' not in content:
    storage_block = '''
    public static class TeleportRequest {
        public final java.util.UUID sender;
        public final boolean isTpaHere;
        public final long timestamp;
        public TeleportRequest(java.util.UUID sender, boolean isTpaHere) {
            this.sender = sender;
            this.isTpaHere = isTpaHere;
            this.timestamp = System.currentTimeMillis();
        }
    }
    private static final java.util.Map<java.util.UUID, TeleportRequest> pendingRequests = new java.util.HashMap<>();
    private static final java.util.Set<java.util.UUID> tpTogglePlayers = new java.util.HashSet<>();
    private static final java.util.Set<java.util.UUID> tpAutoPlayers = new java.util.HashSet<>();
'''
    content = content.replace('public class EssentialsCommands {\n', 'public class EssentialsCommands {\n' + storage_block)

# Helper to replace registration and method body cleanly
def replace_command(cmd_name, method_name, args_regex, new_reg, new_method):
    global content
    # Replace registration
    reg_pattern = r'dispatcher\.register\(Commands\.literal\("' + cmd_name + r'"\)\s*\.executes\(context -> ' + method_name + r'\(context\)\)\s*\);'
    content = re.sub(reg_pattern, new_reg, content)
    # Replace method
    meth_pattern = r'    private static int ' + method_name + r'\(CommandContext<CommandSourceStack> context\) \{[\s\S]*?return 1;\n    \}'
    content = re.sub(meth_pattern, new_method, content)


# 1. /tpa
reg_tpa = '''dispatcher.register(Commands.literal("tpa")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpa(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );'''
meth_tpa = '''    private static int executeTpa(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        if (tpTogglePlayers.contains(target.getUUID())) {
            context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + " has teleportation disabled."));
            return 0;
        }
        if (tpAutoPlayers.contains(target.getUUID())) {
            sender.teleportTo(sender.level(), target.getX(), target.getY(), target.getZ(), java.util.Collections.emptySet(), sender.getYRot(), sender.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal("Teleported to " + target.getName().getString() + " (Auto-Accepted)."));
            return 1;
        }
        pendingRequests.put(target.getUUID(), new TeleportRequest(sender.getUUID(), false));
        context.getSource().sendSystemMessage(Component.literal("Teleport request sent to " + target.getName().getString() + "."));
        target.sendSystemMessage(Component.literal(sender.getName().getString() + " has requested to teleport to you. Type /tpaccept to accept or /tpdeny to deny."));
        return 1;
    }'''
replace_command('tpa', 'executeTpa', None, reg_tpa, meth_tpa)

# 2. /tpahere
reg_tpahere = '''dispatcher.register(Commands.literal("tpahere")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpahere(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );'''
meth_tpahere = '''    private static int executeTpahere(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        if (tpTogglePlayers.contains(target.getUUID())) {
            context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + " has teleportation disabled."));
            return 0;
        }
        if (tpAutoPlayers.contains(target.getUUID())) {
            target.teleportTo(sender.level(), sender.getX(), sender.getY(), sender.getZ(), java.util.Collections.emptySet(), target.getYRot(), target.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal(target.getName().getString() + " was teleported to you (Auto-Accepted)."));
            return 1;
        }
        pendingRequests.put(target.getUUID(), new TeleportRequest(sender.getUUID(), true));
        context.getSource().sendSystemMessage(Component.literal("Teleport here request sent to " + target.getName().getString() + "."));
        target.sendSystemMessage(Component.literal(sender.getName().getString() + " has requested that you teleport to them. Type /tpaccept to accept or /tpdeny to deny."));
        return 1;
    }'''
replace_command('tpahere', 'executeTpahere', None, reg_tpahere, meth_tpahere)

# 3. /tpaccept
reg_tpaccept = '''dispatcher.register(Commands.literal("tpaccept")
        .executes(context -> executeTpaccept(context))
    );'''
meth_tpaccept = '''    private static int executeTpaccept(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TeleportRequest req = pendingRequests.remove(player.getUUID());
        if (req == null || System.currentTimeMillis() - req.timestamp > 120000) {
            context.getSource().sendSystemMessage(Component.literal("You do not have any pending teleport requests."));
            return 0;
        }
        ServerPlayer sender = context.getSource().getServer().getPlayerList().getPlayer(req.sender);
        if (sender == null) {
            context.getSource().sendSystemMessage(Component.literal("The player who sent the request is no longer online."));
            return 0;
        }
        if (req.isTpaHere) {
            player.teleportTo(sender.level(), sender.getX(), sender.getY(), sender.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal("Teleported to " + sender.getName().getString() + "."));
            sender.sendSystemMessage(Component.literal(player.getName().getString() + " accepted your teleport request."));
        } else {
            sender.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), java.util.Collections.emptySet(), sender.getYRot(), sender.getXRot(), false);
            context.getSource().sendSystemMessage(Component.literal(sender.getName().getString() + " has been teleported to you."));
            sender.sendSystemMessage(Component.literal("Teleport request accepted."));
        }
        return 1;
    }'''
replace_command('tpaccept', 'executeTpaccept', None, reg_tpaccept, meth_tpaccept)

# 4. /tpdeny
reg_tpdeny = '''dispatcher.register(Commands.literal("tpdeny")
        .executes(context -> executeTpdeny(context))
    );'''
meth_tpdeny = '''    private static int executeTpdeny(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        TeleportRequest req = pendingRequests.remove(player.getUUID());
        if (req == null) {
            context.getSource().sendSystemMessage(Component.literal("You do not have any pending teleport requests."));
            return 0;
        }
        ServerPlayer sender = context.getSource().getServer().getPlayerList().getPlayer(req.sender);
        if (sender != null) {
            sender.sendSystemMessage(Component.literal(player.getName().getString() + " denied your teleport request."));
        }
        context.getSource().sendSystemMessage(Component.literal("Teleport request denied."));
        return 1;
    }'''
replace_command('tpdeny', 'executeTpdeny', None, reg_tpdeny, meth_tpdeny)

# 5. /tptoggle
reg_tptoggle = '''dispatcher.register(Commands.literal("tptoggle")
        .executes(context -> executeTptoggle(context))
    );'''
meth_tptoggle = '''    private static int executeTptoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (tpTogglePlayers.contains(player.getUUID())) {
            tpTogglePlayers.remove(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Teleportation requests enabled."));
        } else {
            tpTogglePlayers.add(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Teleportation requests disabled."));
        }
        return 1;
    }'''
replace_command('tptoggle', 'executeTptoggle', None, reg_tptoggle, meth_tptoggle)

# 6. /tpauto
reg_tpauto = '''dispatcher.register(Commands.literal("tpauto")
        .executes(context -> executeTpauto(context))
    );'''
meth_tpauto = '''    private static int executeTpauto(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (tpAutoPlayers.contains(player.getUUID())) {
            tpAutoPlayers.remove(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Auto-accept teleport requests disabled."));
        } else {
            tpAutoPlayers.add(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Auto-accept teleport requests enabled."));
        }
        return 1;
    }'''
replace_command('tpauto', 'executeTpauto', None, reg_tpauto, meth_tpauto)

# 7. /tpo (Teleport Override)
reg_tpo = '''dispatcher.register(Commands.literal("tpo")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpo(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );'''
meth_tpo = '''    private static int executeTpo(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.teleportTo(target.level(), target.getX(), target.getY(), target.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal("Teleported to " + target.getName().getString() + " (Override)."));
        return 1;
    }'''
replace_command('tpo', 'executeTpo', None, reg_tpo, meth_tpo)

# 8. /tpohere (Teleport Here Override)
reg_tpohere = '''dispatcher.register(Commands.literal("tpohere")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpohere(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );'''
meth_tpohere = '''    private static int executeTpohere(CommandContext<CommandSourceStack> context, ServerPlayer target) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        target.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), java.util.Collections.emptySet(), target.getYRot(), target.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal("Teleported " + target.getName().getString() + " to you (Override)."));
        return 1;
    }'''
replace_command('tpohere', 'executeTpohere', None, reg_tpohere, meth_tpohere)

# 9. /tpoffline (Teleport to offline player last known location)
# Note: This would normally require saving coordinates to a database. We'll add a placeholder message indicating database required.
reg_tpoffline = '''dispatcher.register(Commands.literal("tpoffline")
        .then(Commands.argument("target_uuid", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeTpoffline(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "target_uuid")))
        )
    );'''
meth_tpoffline = '''    private static int executeTpoffline(CommandContext<CommandSourceStack> context, String uuid) {
        context.getSource().sendSystemMessage(Component.literal("Offline player location resolution requires database persistence. Not fully implemented."));
        return 1;
    }'''
replace_command('tpoffline', 'executeTpoffline', None, reg_tpoffline, meth_tpoffline)

# 10. /tpr (Random Teleport)
reg_tpr = '''dispatcher.register(Commands.literal("tpr")
        .executes(context -> executeTpr(context))
    );'''
meth_tpr = '''    private static int executeTpr(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.level.border.WorldBorder border = player.level().getWorldBorder();
        double minX = Math.max(border.getMinX(), -5000);
        double maxX = Math.min(border.getMaxX(), 5000);
        double minZ = Math.max(border.getMinZ(), -5000);
        double maxZ = Math.min(border.getMaxZ(), 5000);
        double x = minX + (player.getRandom().nextDouble() * (maxX - minX));
        double z = minZ + (player.getRandom().nextDouble() * (maxZ - minZ));
        int y = player.level().getMaxY() - 1;
        // Basic top-down scan to find surface (will just teleport to top block for simplicity)
        net.minecraft.core.BlockPos pos = new net.minecraft.core.BlockPos((int)x, y, (int)z);
        while(y > player.level().getMinY() && player.level().getBlockState(pos).isAir()) {
            y--;
            pos = new net.minecraft.core.BlockPos((int)x, y, (int)z);
        }
        player.teleportTo(player.level(), x, y + 1.0, z, java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal(String.format("Randomly teleported to X: %.1f Z: %.1f", x, z)));
        return 1;
    }'''
replace_command('tpr', 'executeTpr', None, reg_tpr, meth_tpr)

# 11. /tpacancel
reg_tpacancel = '''dispatcher.register(Commands.literal("tpacancel")
        .executes(context -> executeTpacancel(context))
    );'''
meth_tpacancel = '''    private static int executeTpacancel(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        boolean canceled = false;
        java.util.Iterator<java.util.Map.Entry<java.util.UUID, TeleportRequest>> it = pendingRequests.entrySet().iterator();
        while (it.hasNext()) {
            java.util.Map.Entry<java.util.UUID, TeleportRequest> entry = it.next();
            if (entry.getValue().sender.equals(player.getUUID())) {
                it.remove();
                canceled = true;
                ServerPlayer target = context.getSource().getServer().getPlayerList().getPlayer(entry.getKey());
                if (target != null) {
                   target.sendSystemMessage(Component.literal(player.getName().getString() + " canceled their teleport request."));
                }
            }
        }
        if (canceled) {
            context.getSource().sendSystemMessage(Component.literal("Teleport request canceled."));
        } else {
            context.getSource().sendSystemMessage(Component.literal("You have no pending outgoing teleport requests."));
        }
        return 1;
    }'''
replace_command('tpacancel', 'executeTpacancel', None, reg_tpacancel, meth_tpacancel)

# 12. /tpall (Teleport everyone to player)
reg_tpall = '''dispatcher.register(Commands.literal("tpall")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .executes(context -> executeTpall(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target")))
        )
    );'''
meth_tpall = '''    private static int executeTpall(CommandContext<CommandSourceStack> context, ServerPlayer target) {
        int count = 0;
        for (ServerPlayer player : context.getSource().getServer().getPlayerList().getPlayers()) {
            if (player != target) {
                player.teleportTo(target.level(), target.getX(), target.getY(), target.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
                count++;
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Teleported " + count + " players to " + target.getName().getString() + "."));
        return count;
    }'''
replace_command('tpall', 'executeTpall', None, reg_tpall, meth_tpall)

# 13. /tpaall (Request everyone to teleport to player)
reg_tpaall = '''dispatcher.register(Commands.literal("tpaall")
        .executes(context -> executeTpaall(context))
    );'''
meth_tpaall = '''    private static int executeTpaall(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer sender = context.getSource().getPlayerOrException();
        int count = 0;
        for (ServerPlayer target : context.getSource().getServer().getPlayerList().getPlayers()) {
            if (target != sender && !tpTogglePlayers.contains(target.getUUID())) {
                pendingRequests.put(target.getUUID(), new TeleportRequest(sender.getUUID(), true));
                target.sendSystemMessage(Component.literal(sender.getName().getString() + " has requested that you teleport to them. Type /tpaccept to accept or /tpdeny to deny."));
                count++;
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Teleport here requests sent to " + count + " players."));
        return count;
    }'''
replace_command('tpaall', 'executeTpaall', None, reg_tpaall, meth_tpaall)


with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("TPA suite injected.")