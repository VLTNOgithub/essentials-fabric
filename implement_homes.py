import os
import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Inject Home Storage
if 'class HomePosition' not in content:
    storage_block = '''
    public static class HomePosition {
        public final double x, y, z;
        public final float yaw, pitch;
        public final String dimension;
        public HomePosition(double x, double y, double z, float yaw, float pitch, String dimension) {
            this.x = x; this.y = y; this.z = z; this.yaw = yaw; this.pitch = pitch; this.dimension = dimension;
        }
    }
    private static final java.util.Map<java.util.UUID, java.util.Map<String, HomePosition>> playerHomes = new java.util.HashMap<>();
'''
    content = content.replace('public class EssentialsCommands {\n', 'public class EssentialsCommands {\n' + storage_block)

def replace_home_cmd(cmd_name, method_name, method_impl):
    global content
    # registration
    reg_pattern = r'dispatcher\.register\(Commands\.literal\("' + cmd_name + r'"\)\s*\.executes\(context -> ' + method_name + r'\(context\)\)\s*\);'
    new_reg = f'''dispatcher.register(Commands.literal("{cmd_name}")\n        .executes(context -> {method_name}(context))\n        .then(Commands.argument("name", com.mojang.brigadier.arguments.StringArgumentType.word())\n            .executes(context -> {method_name}(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "name")))\n        )\n    );'''
    content = re.sub(reg_pattern, new_reg, content)
    # method
    meth_pattern = r'    private static int ' + method_name + r'\(CommandContext<CommandSourceStack> context\) \{[\s\S]*?return 1;\n    \}'
    content = re.sub(meth_pattern, method_impl, content)

sethome_impl = '''    private static int executeSethome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeSethome(context, "home"); }
    private static int executeSethome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String dim = player.level().dimension().location().toString();
        HomePosition home = new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), dim);
        playerHomes.computeIfAbsent(player.getUUID(), k -> new java.util.HashMap<>()).put(name.toLowerCase(), home);
        context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' set."));
        return 1;
    }'''

home_impl = '''    private static int executeHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeHome(context, "home"); }
    private static int executeHome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        java.util.Map<String, HomePosition> homes = playerHomes.get(player.getUUID());
        if (homes == null || !homes.containsKey(name.toLowerCase())) {
            context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' does not exist."));
            return 0;
        }
        HomePosition home = homes.get(name.toLowerCase());
        net.minecraft.resources.ResourceLocation dimLoc = net.minecraft.resources.ResourceLocation.parse(home.dimension);
        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimLoc);
        net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
        if (targetLevel == null) {
            context.getSource().sendSystemMessage(Component.literal("Invalid dimension for home."));
            return 0;
        }
        player.teleportTo(targetLevel, home.x, home.y, home.z, java.util.Collections.emptySet(), home.yaw, home.pitch, false);
        context.getSource().sendSystemMessage(Component.literal("Teleported to home '" + name + "'."));
        return 1;
    }'''

delhome_impl = '''    private static int executeDelhome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeDelhome(context, "home"); }
    private static int executeDelhome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        java.util.Map<String, HomePosition> homes = playerHomes.get(player.getUUID());
        if (homes != null && homes.remove(name.toLowerCase()) != null) {
            context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' deleted."));
            return 1;
        }
        context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' does not exist."));
        return 0;
    }'''

replace_home_cmd('sethome', 'executeSethome', sethome_impl)
replace_home_cmd('home', 'executeHome', home_impl)
replace_home_cmd('delhome', 'executeDelhome', delhome_impl)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Homes injected.")
