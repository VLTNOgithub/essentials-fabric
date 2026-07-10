import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Helper to replace method stubs cleanly
def inject_method(method_name, impl_body):
    global content
    pattern = r'    private static int ' + method_name + r'\(CommandContext<CommandSourceStack> context\)[\s\S]*?return 0;\n    \}'
    pattern2 = r'    private static int ' + method_name + r'\(CommandContext<CommandSourceStack> context\)[\s\S]*?return 1;\n    \}'
    if re.search(pattern, content):
        content = re.sub(pattern, impl_body, content)
    else:
        content = re.sub(pattern2, impl_body, content)

# Helper for replacing registration
def replace_reg(cmd_name, method_name, new_reg):
    global content
    reg_pattern = r'dispatcher\.register\(Commands\.literal\("' + cmd_name + r'"\)\s*\.executes\(context -> ' + method_name + r'\(context\)\)\s*\);'
    content = re.sub(reg_pattern, new_reg, content)

# --- 1. /antioch ---
reg_antioch = '''dispatcher.register(Commands.literal("antioch")
        .executes(context -> executeAntioch(context, null))
        .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeAntioch(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
        )
    );'''
meth_antioch = '''    private static int executeAntioch(CommandContext<CommandSourceStack> context, String message) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (message != null && !message.isEmpty()) {
            context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal("...lobbest thou thy Holy Hand Grenade of Antioch towards thy foe,"), false);
            context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal("who being naughty in My sight, shall snuff it."), false);
        }
        net.minecraft.world.phys.HitResult hit = player.pick(100.0D, 0.0F, false);
        net.minecraft.core.BlockPos pos = hit.getType() == net.minecraft.world.phys.HitResult.Type.BLOCK ? ((net.minecraft.world.phys.BlockHitResult) hit).getBlockPos() : player.blockPosition();
        net.minecraft.world.entity.item.PrimedTnt tnt = net.minecraft.world.entity.EntityType.TNT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        if (tnt != null) {
            tnt.setPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
            tnt.setFuse(40);
            player.level().addFreshEntity(tnt);
        }
        return 1;
    }'''
replace_reg('antioch', 'executeAntioch', reg_antioch)
inject_method('executeAntioch', meth_antioch)

# --- 2. /back ---
storage_back = '''
    private static final java.util.Map<java.util.UUID, HomePosition> backPositions = new java.util.HashMap<>();
    public static void saveBackLocation(ServerPlayer player) {
        backPositions.put(player.getUUID(), new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.level().dimension().identifier().toString()));
    }
'''
if 'Map<java.util.UUID, HomePosition> backPositions' not in content:
    content = content.replace('public static class TeleportRequest', storage_back + '\n    public static class TeleportRequest')

# Intercept TP functions to save back location
content = content.replace('player.teleportTo(targetLevel', 'saveBackLocation(player);\n        player.teleportTo(targetLevel')
content = content.replace('player.teleportTo((net.minecraft.server.level.ServerLevel) destination.level()', 'saveBackLocation(player);\n                player.teleportTo((net.minecraft.server.level.ServerLevel) destination.level()')
content = content.replace('target.teleportTo(player.level()', 'saveBackLocation(target);\n        target.teleportTo(player.level()')
content = content.replace('pTarget.teleportTo(player.level()', 'saveBackLocation(pTarget);\n                pTarget.teleportTo(player.level()')
content = content.replace('player.teleportTo(player.level(), x, y + 1.0, z', 'saveBackLocation(player);\n        player.teleportTo(player.level(), x, y + 1.0, z')
content = content.replace('player.teleportTo(player.level(), vec.x, vec.y, vec.z', 'saveBackLocation(player);\n        player.teleportTo(player.level(), vec.x, vec.y, vec.z')

reg_back = '''dispatcher.register(Commands.literal("back")
        .executes(context -> executeBack(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException())))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.players())
            .executes(context -> executeBack(context, net.minecraft.commands.arguments.EntityArgument.getPlayers(context, "targets")))
        )
    );'''
meth_back = '''    private static int executeBack(CommandContext<CommandSourceStack> context, Collection<ServerPlayer> targets) throws CommandSyntaxException {
        int count = 0;
        for (ServerPlayer target : targets) {
            HomePosition back = backPositions.get(target.getUUID());
            if (back != null) {
                net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, net.minecraft.resources.Identifier.parse(back.dimension));
                net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
                if (targetLevel != null) {
                    saveBackLocation(target); // Save current location before going back
                    target.teleportTo(targetLevel, back.x, back.y, back.z, java.util.Collections.emptySet(), back.yaw, back.pitch, false);
                    if (target == context.getSource().getEntity()) {
                        context.getSource().sendSystemMessage(Component.literal("Teleported back to your previous location."));
                    }
                    count++;
                }
            } else if (target == context.getSource().getEntity()) {
                context.getSource().sendSystemMessage(Component.literal("No previous location found."));
            }
        }
        return count;
    }'''
replace_reg('back', 'executeBack', reg_back)
inject_method('executeBack', meth_back)

# --- 3. /broadcast ---
reg_broadcast = '''dispatcher.register(Commands.literal("broadcast")
        .then(Commands.argument("message", com.mojang.brigadier.arguments.StringArgumentType.greedyString())
            .executes(context -> executeBroadcast(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "message")))
        )
    );'''
meth_broadcast = '''    private static int executeBroadcast(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /broadcast <message>")); return 0; }
    private static int executeBroadcast(CommandContext<CommandSourceStack> context, String message) {
        context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal("[Broadcast] " + message).withStyle(net.minecraft.ChatFormatting.LIGHT_PURPLE), false);
        return 1;
    }'''
replace_reg('broadcast', 'executeBroadcast', reg_broadcast)
inject_method('executeBroadcast', meth_broadcast)

# --- 4. /beezooka ---
reg_beezooka = '''dispatcher.register(Commands.literal("beezooka").executes(context -> executeBeezooka(context)));'''
meth_beezooka = '''    private static int executeBeezooka(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.entity.animal.Bee bee = net.minecraft.world.entity.EntityType.BEE.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
        if (bee != null) {
            bee.setPos(player.getX(), player.getEyeY(), player.getZ());
            net.minecraft.world.phys.Vec3 look = player.getLookAngle().scale(2.0);
            bee.setDeltaMovement(look);
            player.level().addFreshEntity(bee);
            
            // Detonate after 20 ticks
            net.minecraft.world.entity.item.PrimedTnt tnt = net.minecraft.world.entity.EntityType.TNT.create(player.level(), net.minecraft.world.entity.EntitySpawnReason.COMMAND);
            tnt.setPos(bee.getX(), bee.getY(), bee.getZ());
            tnt.startRiding(bee);
            tnt.setFuse(20);
            player.level().addFreshEntity(tnt);
            
            context.getSource().sendSystemMessage(Component.literal("Bzzz!"));
        }
        return 1;
    }'''
inject_method('executeBeezooka', meth_beezooka)

# --- 5. /ice ---
reg_ice = '''dispatcher.register(Commands.literal("ice")
        .executes(context -> executeIce(context, java.util.Collections.singletonList(context.getSource().getPlayerOrException())))
        .then(Commands.argument("targets", net.minecraft.commands.arguments.EntityArgument.entities())
            .executes(context -> executeIce(context, net.minecraft.commands.arguments.EntityArgument.getEntities(context, "targets")))
        )
    );'''
meth_ice = '''    private static int executeIce(CommandContext<CommandSourceStack> context, Collection<? extends net.minecraft.world.entity.Entity> targets) {
        for (net.minecraft.world.entity.Entity target : targets) {
            target.setTicksFrozen(target.getTicksRequiredToFreeze() + 200); // Freeze for extra 10 seconds
            if (target instanceof ServerPlayer p) p.sendSystemMessage(Component.literal("You have been iced."));
        }
        context.getSource().sendSystemMessage(Component.literal("Iced " + targets.size() + " entities."));
        return targets.size();
    }'''
replace_reg('ice', 'executeIce', reg_ice)
inject_method('executeIce', meth_ice)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Batch 5 (Antioch, Back, Broadcast, Beezooka, Ice) injected!")