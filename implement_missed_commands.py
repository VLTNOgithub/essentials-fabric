import os
import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# 1. Add Offline Position Tracking
storage_block = '''
    private static final java.util.Map<String, HomePosition> offlinePositions = new java.util.HashMap<>();
    
    public static void onPlayerDisconnect(net.minecraft.server.network.ServerGamePacketListenerImpl handler, net.minecraft.server.MinecraftServer server) {
        ServerPlayer player = handler.player;
        String dim = player.level().dimension().identifier().toString();
        offlinePositions.put(player.getName().getString().toLowerCase(), new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), dim));
    }
'''
if 'offlinePositions =' not in content:
    content = content.replace('private static final java.util.Map<java.util.UUID, java.util.Map<String, HomePosition>> playerHomes = new java.util.HashMap<>();',
                              'private static final java.util.Map<java.util.UUID, java.util.Map<String, HomePosition>> playerHomes = new java.util.HashMap<>();' + storage_block)

# Add the disconnect event registration in the register() method
if 'ServerPlayConnectionEvents.DISCONNECT.register' not in content:
    content = content.replace('CommandRegistrationCallback.EVENT.register(EssentialsCommands::registerCommands);',
                              'CommandRegistrationCallback.EVENT.register(EssentialsCommands::registerCommands);\n        net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register(EssentialsCommands::onPlayerDisconnect);')


# 2. Implement /tpoffline
reg_tpoffline = '''dispatcher.register(Commands.literal("tpoffline")\n        .then(Commands.argument("target", com.mojang.brigadier.arguments.StringArgumentType.word())\n            .executes(context -> executeTpoffline(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "target")))\n        )\n    );'''

meth_tpoffline = '''    private static int executeTpoffline(CommandContext<CommandSourceStack> context, String targetName) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        HomePosition pos = offlinePositions.get(targetName.toLowerCase());\n        if (pos == null) {\n            context.getSource().sendSystemMessage(Component.literal("No offline location recorded for " + targetName + " since the server started."));\n            return 0;\n        }\n        net.minecraft.resources.Identifier dimLoc = net.minecraft.resources.Identifier.parse(pos.dimension);\n        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimLoc);\n        net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);\n        if (targetLevel != null) {\n            player.teleportTo(targetLevel, pos.x, pos.y, pos.z, java.util.Collections.emptySet(), pos.yaw, pos.pitch, false);\n            context.getSource().sendSystemMessage(Component.literal("Teleported to " + targetName + "'s last known offline location."));\n            return 1;\n        }\n        return 0;\n    }'''

content = re.sub(r'dispatcher\.register\(Commands\.literal\("tpoffline"\)[\s\S]*?\)\s*\);', reg_tpoffline, content)
content = re.sub(r'    private static int executeTpoffline\(CommandContext<CommandSourceStack> context, String uuid\) \{[\s\S]*?return 1;\n    \}', meth_tpoffline, content)


# 3. Implement /renamehome
reg_renamehome = '''dispatcher.register(Commands.literal("renamehome")\n        .then(Commands.argument("oldName", com.mojang.brigadier.arguments.StringArgumentType.word())\n            .then(Commands.argument("newName", com.mojang.brigadier.arguments.StringArgumentType.word())\n                .executes(context -> executeRenamehome(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "oldName"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "newName")))\n            )\n        )\n    );'''

meth_renamehome = '''    private static int executeRenamehome(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /renamehome <old> <new>")); return 0; }\n    private static int executeRenamehome(CommandContext<CommandSourceStack> context, String oldName, String newName) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        java.util.Map<String, HomePosition> homes = playerHomes.get(player.getUUID());\n        if (homes == null || !homes.containsKey(oldName.toLowerCase())) {\n            context.getSource().sendSystemMessage(Component.literal("Home '" + oldName + "' does not exist."));\n            return 0;\n        }\n        if (homes.containsKey(newName.toLowerCase())) {\n            context.getSource().sendSystemMessage(Component.literal("A home named '" + newName + "' already exists."));\n            return 0;\n        }\n        HomePosition home = homes.remove(oldName.toLowerCase());\n        homes.put(newName.toLowerCase(), home);\n        context.getSource().sendSystemMessage(Component.literal("Successfully renamed home '" + oldName + "' to '" + newName + "'."));\n        return 1;\n    }'''

content = re.sub(r'dispatcher\.register\(Commands\.literal\("renamehome"\)[\s\S]*?\.executes\(context -> executeRenamehome\(context\)\)\s*\);', reg_renamehome, content)
content = re.sub(r'    private static int executeRenamehome\(CommandContext<CommandSourceStack> context\) \{[\s\S]*?return 1;\n    \}', meth_renamehome, content)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Missed commands injected.")
