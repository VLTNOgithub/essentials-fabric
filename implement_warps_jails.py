import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

def replace_reg(cmd_name, method_name, new_reg):
    global content
    reg_pattern = r'dispatcher\.register\(Commands\.literal\("' + cmd_name + r'"\)\s*\.executes\(context -> ' + method_name + r'\(context\)\)\s*\);'
    content = re.sub(reg_pattern, new_reg, content)

def replace_stub(method_name, new_meth):
    global content
    meth_pattern = r'    private static int ' + method_name + r'\(CommandContext<CommandSourceStack> context[\s\S]*?\{([\s\S]*?return [01];\s*)\}'
    content = re.sub(meth_pattern, new_meth, content)

# --- 1. Jails Cache & Events ---
jails_cache_block = '''
    private static final java.util.Map<String, HomePosition> JAILS = new java.util.HashMap<>();
    private static final java.util.Map<String, HomePosition> WARPS = new java.util.HashMap<>();
    private static File getJailsFile() { return new File("essentials_jails.json"); }
    private static File getWarpsFile() { return new File("essentials_warps.json"); }

    public static void loadJailsWarps() {
        if (getJailsFile().exists()) {
            try (FileReader reader = new FileReader(getJailsFile())) {
                java.util.Map<String, HomePosition> loaded = GSON.fromJson(reader, new TypeToken<java.util.Map<String, HomePosition>>(){}.getType());
                if (loaded != null) { JAILS.clear(); JAILS.putAll(loaded); }
            } catch (Exception e) { e.printStackTrace(); }
        }
        if (getWarpsFile().exists()) {
            try (FileReader reader = new FileReader(getWarpsFile())) {
                java.util.Map<String, HomePosition> loaded = GSON.fromJson(reader, new TypeToken<java.util.Map<String, HomePosition>>(){}.getType());
                if (loaded != null) { WARPS.clear(); WARPS.putAll(loaded); }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    public static void saveJailsWarps() {
        try (FileWriter writer = new FileWriter(getJailsFile())) { GSON.toJson(JAILS, writer); }
        catch (Exception e) { e.printStackTrace(); }
        try (FileWriter writer = new FileWriter(getWarpsFile())) { GSON.toJson(WARPS, writer); }
        catch (Exception e) { e.printStackTrace(); }
    }
    
    public static void registerJailEvents() {
        net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            UserData data = UserCache.getUser(player.getUUID());
            if (data.jail != null) {
                player.sendSystemMessage(Component.literal("You cannot break blocks while jailed.").withStyle(net.minecraft.ChatFormatting.RED));
                return false;
            }
            return true;
        });
        net.fabricmc.fabric.api.event.player.UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            UserData data = UserCache.getUser(player.getUUID());
            if (data.jail != null) {
                player.sendSystemMessage(Component.literal("You cannot interact while jailed.").withStyle(net.minecraft.ChatFormatting.RED));
                return net.minecraft.world.InteractionResult.FAIL;
            }
            return net.minecraft.world.InteractionResult.PASS;
        });
    }
'''
if 'Map<String, HomePosition> JAILS' not in content:
    content = content.replace('private static File getDataFile()', jails_cache_block + '\n    private static File getDataFile()')
    content = content.replace('loadKits();', 'loadKits(); loadJailsWarps();')
    content = content.replace('saveKits();', 'saveKits(); saveJailsWarps();')
    content = content.replace('CommandRegistrationCallback.EVENT.register(EssentialsCommands::registerCommands);', 'CommandRegistrationCallback.EVENT.register(EssentialsCommands::registerCommands);\n        registerJailEvents();')

# Extend UserData string inject to support jails
# We will do this via a multi_edit on UserData.java directly after this script.

# --- 2. Jails Commands ---
reg_setjail = '''dispatcher.register(Commands.literal("setjail")
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeSetjail(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    );'''
meth_setjail = '''    private static int executeSetjail(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /setjail <name>")); return 0; }\n    private static int executeSetjail(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        HomePosition pos = new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.level().dimension().identifier().toString());\n        JAILS.put(name.toLowerCase(), pos);\n        saveJailsWarps();\n        context.getSource().sendSystemMessage(Component.literal("Jail '" + name + "' set."));\n        return 1;\n    }'''
replace_reg('setjail', 'executeSetjail', reg_setjail)
replace_stub('executeSetjail', meth_setjail)

reg_deljail = '''dispatcher.register(Commands.literal("deljail")
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeDeljail(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    );'''
meth_deljail = '''    private static int executeDeljail(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /deljail <name>")); return 0; }\n    private static int executeDeljail(CommandContext<CommandSourceStack> context, String name) {\n        if (JAILS.remove(name.toLowerCase()) != null) {\n            saveJailsWarps();\n            context.getSource().sendSystemMessage(Component.literal("Jail '" + name + "' deleted."));\n            return 1;\n        }\n        context.getSource().sendSystemMessage(Component.literal("Jail '" + name + "' not found."));\n        return 0;\n    }'''
replace_reg('deljail', 'executeDeljail', reg_deljail)
replace_stub('executeDeljail', meth_deljail)

reg_jails = '''dispatcher.register(Commands.literal("jails").executes(context -> executeJails(context)));'''
meth_jails = '''    private static int executeJails(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Jails: " + String.join(", ", JAILS.keySet())));\n        return 1;\n    }'''
replace_reg('jails', 'executeJails', reg_jails)
replace_stub('executeJails', meth_jails)

reg_togglejail = '''dispatcher.register(Commands.literal("togglejail")
        .then(Commands.argument("target", net.minecraft.commands.arguments.EntityArgument.player())
            .then(Commands.argument("jailname", com.mojang.brigadier.arguments.StringArgumentType.word())
                .executes(context -> executeTogglejail(context, net.minecraft.commands.arguments.EntityArgument.getPlayer(context, "target"), com.mojang.brigadier.arguments.StringArgumentType.getString(context, "jailname")))
            )
        )
    );'''
meth_togglejail = '''    private static int executeTogglejail(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /togglejail <player> <jailname>")); return 0; }\n    private static int executeTogglejail(CommandContext<CommandSourceStack> context, ServerPlayer target, String jailname) throws CommandSyntaxException {\n        UserData data = UserCache.getUser(target);\n        if (data.jail != null) {\n            data.jail = null;\n            UserCache.saveUser(target.getUUID());\n            context.getSource().sendSystemMessage(Component.literal("Unjailed " + target.getName().getString()));\n            target.sendSystemMessage(Component.literal("You have been released from jail."));\n            return 1;\n        }\n        HomePosition jailPos = JAILS.get(jailname.toLowerCase());\n        if (jailPos == null) {\n            context.getSource().sendSystemMessage(Component.literal("Jail '" + jailname + "' not found."));\n            return 0;\n        }\n        data.jail = jailname.toLowerCase();\n        UserCache.saveUser(target.getUUID());\n        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, net.minecraft.resources.Identifier.parse(jailPos.dimension));\n        net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);\n        if (targetLevel != null) {\n            target.teleportTo(targetLevel, jailPos.x, jailPos.y, jailPos.z, java.util.Collections.emptySet(), jailPos.yaw, jailPos.pitch, false);\n        }\n        context.getSource().sendSystemMessage(Component.literal("Jailed " + target.getName().getString() + " in " + jailname));\n        target.sendSystemMessage(Component.literal("You have been jailed.").withStyle(net.minecraft.ChatFormatting.RED));\n        return 1;\n    }'''
replace_reg('togglejail', 'executeTogglejail', reg_togglejail)
replace_stub('executeTogglejail', meth_togglejail)


# --- 3. Warps Commands ---
reg_setwarp = '''dispatcher.register(Commands.literal("setwarp")
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeSetwarp(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    );'''
meth_setwarp = '''    private static int executeSetwarp(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /setwarp <name>")); return 0; }\n    private static int executeSetwarp(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        HomePosition pos = new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), player.level().dimension().identifier().toString());\n        WARPS.put(name.toLowerCase(), pos);\n        saveJailsWarps();\n        context.getSource().sendSystemMessage(Component.literal("Warp '" + name + "' set."));\n        return 1;\n    }'''
replace_reg('setwarp', 'executeSetwarp', reg_setwarp)
replace_stub('executeSetwarp', meth_setwarp)

reg_delwarp = '''dispatcher.register(Commands.literal("delwarp")
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeDelwarp(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    );'''
meth_delwarp = '''    private static int executeDelwarp(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /delwarp <name>")); return 0; }\n    private static int executeDelwarp(CommandContext<CommandSourceStack> context, String name) {\n        if (WARPS.remove(name.toLowerCase()) != null) {\n            saveJailsWarps();\n            context.getSource().sendSystemMessage(Component.literal("Warp '" + name + "' deleted."));\n            return 1;\n        }\n        context.getSource().sendSystemMessage(Component.literal("Warp '" + name + "' not found."));\n        return 0;\n    }'''
replace_reg('delwarp', 'executeDelwarp', reg_delwarp)
replace_stub('executeDelwarp', meth_delwarp)

reg_warp = '''dispatcher.register(Commands.literal("warp")
        .executes(context -> executeWarp(context, ""))
        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeWarp(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))
        )
    );'''
meth_warp = '''    private static int executeWarp(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeWarp(context, ""); }\n    private static int executeWarp(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {\n        if (name.isEmpty()) {\n            context.getSource().sendSystemMessage(Component.literal("Warps: " + String.join(", ", WARPS.keySet())));\n            return 1;\n        }\n        HomePosition warpPos = WARPS.get(name.toLowerCase());\n        if (warpPos == null) {\n            context.getSource().sendSystemMessage(Component.literal("Warp '" + name + "' not found."));\n            return 0;\n        }\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, net.minecraft.resources.Identifier.parse(warpPos.dimension));\n        net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);\n        if (targetLevel != null) {\n            saveBackLocation(player);\n            player.teleportTo(targetLevel, warpPos.x, warpPos.y, warpPos.z, java.util.Collections.emptySet(), warpPos.yaw, warpPos.pitch, false);\n        }\n        context.getSource().sendSystemMessage(Component.literal("Warped to " + name));\n        return 1;\n    }'''
replace_reg('warp', 'executeWarp', reg_warp)
replace_stub('executeWarp', meth_warp)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Warps and Jails injected!")