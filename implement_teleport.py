import os
import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'

with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. TP Registration
content = re.sub(
    r'dispatcher\.register\(Commands\.literal\("([^"]+)"\)\s*\.executes\(context -> executeTp\(context\)\)\s*\);',
    r'''dispatcher.register(Commands.literal("\1")
        .then(Commands.argument("destination", net.minecraft.commands.arguments.EntityArgument.entity())
            .executes(context -> executeTp(context, Collections.singletonList(context.getSource().getPlayerOrException()), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
                .executes(context -> executeTp(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets"), net.minecraft.commands.arguments.EntityArgument.getEntity(context, "destination")))
            )
        )
    );''',
    content
)

# TP Method
tp_stub = r'    private static int executeTp\(CommandContext<CommandSourceStack> context\) \{[\s\S]*?return 1;\n    \}'
tp_impl = '''    private static int executeTp(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets, net.minecraft.world.entity.Entity destination) throws CommandSyntaxException {
        for (net.minecraft.world.entity.Entity target : targets) {
            if (target instanceof ServerPlayer player) {
                player.teleportTo((net.minecraft.server.level.ServerLevel) destination.level(), destination.getX(), destination.getY(), destination.getZ(), destination.getYRot(), destination.getXRot());
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

# 2. Tphere Registration
content = re.sub(
    r'dispatcher\.register\(Commands\.literal\("([^"]+)"\)\s*\.executes\(context -> executeTphere\(context\)\)\s*\);',
    r'''dispatcher.register(Commands.literal("\1")
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
            .executes(context -> executeTphere(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets")))
        )
    );''',
    content
)

# Tphere Method
tphere_stub = r'    private static int executeTphere\(CommandContext<CommandSourceStack> context\) \{[\s\S]*?return 1;\n    \}'
tphere_impl = '''    private static int executeTphere(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        for (net.minecraft.world.entity.Entity target : targets) {
            if (target instanceof ServerPlayer pTarget) {
                pTarget.teleportTo(player.serverLevel(), player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot());
            } else {
                target.teleportTo(player.getX(), player.getY(), player.getZ());
            }
        }
        context.getSource().sendSystemMessage(Component.literal("Teleported " + targets.size() + " entities to you."));
        return targets.size();
    }'''
content = re.sub(tphere_stub, tphere_impl, content)

# 3. Tppos Registration
content = re.sub(
    r'dispatcher\.register\(Commands\.literal\("([^"]+)"\)\s*\.executes\(context -> executeTppos\(context\)\)\s*\);',
    r'''dispatcher.register(Commands.literal("\1")
        .then(Commands.argument("pos", net.minecraft.commands.arguments.coordinates.Vec3Argument.vec3())
            .executes(context -> executeTppos(context, net.minecraft.commands.arguments.coordinates.Vec3Argument.getCoordinates(context, "pos")))
        )
    );''',
    content
)

# Tppos Method
tppos_stub = r'    private static int executeTppos\(CommandContext<CommandSourceStack> context\) \{[\s\S]*?return 1;\n    \}'
tppos_impl = '''    private static int executeTppos(CommandContext<CommandSourceStack> context, net.minecraft.commands.arguments.coordinates.Coordinates pos) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.Vec3 vec = pos.getPosition(context.getSource());
        player.teleportTo((net.minecraft.server.level.ServerLevel) player.level(), vec.x, vec.y, vec.z, player.getYRot(), player.getXRot());
        context.getSource().sendSystemMessage(Component.literal(String.format("Teleported to %.1f, %.1f, %.1f", vec.x, vec.y, vec.z)));
        return 1;
    }'''
content = re.sub(tppos_stub, tppos_impl, content)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Teleportation injected.")