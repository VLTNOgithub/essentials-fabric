import os
import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Fix tp
tp_stub = r'    private static int executeTp\(CommandContext<CommandSourceStack> context, Collection<\? extends net\.minecraft\.world\.entity\.Entity> targets, net\.minecraft\.world\.entity\.Entity destination\) throws CommandSyntaxException \{[\s\S]*?return targets\.size\(\);\n    \}'
tp_impl = '''    private static int executeTp(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets, net.minecraft.world.entity.Entity destination) throws CommandSyntaxException {
        for (net.minecraft.world.entity.Entity target : targets) {
            if (target instanceof ServerPlayer player) {
                player.teleportTo((net.minecraft.server.level.ServerLevel) destination.level(), destination.getX(), destination.getY(), destination.getZ(), java.util.Collections.emptySet(), destination.getYRot(), destination.getXRot(), false);
            }
        }
        if (targets.size() == 1) {
            context.getSource().sendSystemMessage(Component.literal("Teleported to " + destination.getName().getString() + "."));
        } else {
            context.getSource().sendSystemMessage(Component.literal("Teleported " + targets.size() + " entities to " + destination.getName().getString() + "."));
        }
        return targets.size();
    }'''
content = re.sub(tp_stub, tp_impl, content)

# Fix tphere
tphere_stub = r'    private static int executeTphere\(CommandContext<CommandSourceStack> context, Collection<\? extends net\.minecraft\.world\.entity\.Entity> targets\) throws CommandSyntaxException \{[\s\S]*?return targets\.size\(\);\n    \}'
tphere_impl = '''    private static int executeTphere(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        for (net.minecraft.world.entity.Entity target : targets) {
            if (target instanceof ServerPlayer pTarget) {
                pTarget.teleportTo(player.level(), player.getX(), player.getY(), player.getZ(), java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
            } else {
                target.teleportTo(player.getX(), player.getY(), player.getZ());
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Teleported " + targets.size() + " entities to you."));
        return targets.size();
    }'''
content = re.sub(tphere_stub, tphere_impl, content)

# Fix tppos
tppos_stub = r'    private static int executeTppos\(CommandContext<CommandSourceStack> context, net\.minecraft\.commands\.arguments\.coordinates\.Coordinates pos\) throws CommandSyntaxException \{[\s\S]*?return 1;\n    \}'
tppos_impl = '''    private static int executeTppos(CommandContext<CommandSourceStack> context, net.minecraft.commands.arguments.coordinates.Coordinates pos) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.Vec3 vec = pos.getPosition(context.getSource());
        player.teleportTo(player.level(), vec.x, vec.y, vec.z, java.util.Collections.emptySet(), player.getYRot(), player.getXRot(), false);
        context.getSource().sendSystemMessage(Component.literal(String.format("Teleported to %.1f, %.1f, %.1f", vec.x, vec.y, vec.z)));
        return 1;
    }'''
content = re.sub(tppos_stub, tppos_impl, content)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Teleport fixed.")