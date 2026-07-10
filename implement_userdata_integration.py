import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

def replace_meth(method_name, new_meth):
    global content
    meth_pattern = r'    private static int ' + method_name + r'\(CommandContext<CommandSourceStack> context[\s\S]*?\{([\s\S]*?return [01];\s*)\}'
    content = re.sub(meth_pattern, new_meth, content)

def replace_reg(cmd_name, method_name, new_reg):
    global content
    reg_pattern = r'dispatcher\.register\(Commands\.literal\("' + cmd_name + r'"\)\s*\.executes\(context -> ' + method_name + r'\(context\)\)\s*\);'
    content = re.sub(reg_pattern, new_reg, content)


# 1. Refactor Homes
home_impl = '''    private static int executeHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeHome(context, "home"); }
    private static int executeHome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        java.util.Map<String, HomePosition> homes = data.homes;
        if (homes == null || homes.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You have no homes set."));
            return 0;
        }
        if (name.equals("home") && homes.size() > 1 && !homes.containsKey("home")) {
            context.getSource().sendSystemMessage(Component.literal("Homes: " + String.join(", ", homes.keySet())));
            return 1;
        }
        if (!homes.containsKey(name.toLowerCase())) {
            context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' does not exist."));
            return 0;
        }
        HomePosition home = homes.get(name.toLowerCase());
        net.minecraft.resources.Identifier dimLoc = net.minecraft.resources.Identifier.parse(home.dimension);
        net.minecraft.resources.ResourceKey<net.minecraft.world.level.Level> dimKey = net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, dimLoc);
        net.minecraft.server.level.ServerLevel targetLevel = context.getSource().getServer().getLevel(dimKey);
        if (targetLevel != null) {
            saveBackLocation(player);
            player.teleportTo(targetLevel, home.x, home.y, home.z, java.util.Collections.emptySet(), home.yaw, home.pitch, false);
            context.getSource().sendSystemMessage(Component.literal("Teleported to home '" + name + "'."));
            return 1;
        }
        return 0;
    }'''
replace_meth('executeHome', home_impl)

sethome_impl = '''    private static int executeSethome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeSethome(context, "home"); }
    private static int executeSethome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        String dim = player.level().dimension().identifier().toString();
        HomePosition home = new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), dim);
        data.homes.put(name.toLowerCase(), home);
        UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' set."));
        return 1;
    }'''
replace_meth('executeSethome', sethome_impl)

delhome_impl = '''    private static int executeDelhome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeDelhome(context, "home"); }
    private static int executeDelhome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        if (data.homes.remove(name.toLowerCase()) != null) {
            UserCache.saveUser(player.getUUID());
            context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' deleted."));
            return 1;
        }
        context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' does not exist."));
        return 0;
    }'''
replace_meth('executeDelhome', delhome_impl)

# 2. Implement /compass
compass_impl = '''    private static int executeCompass(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int bearing = (int) (player.getYRot() + 180 + 360) % 360;
        String dir;
        if (bearing < 23) dir = "North";
        else if (bearing < 68) dir = "North-East";
        else if (bearing < 113) dir = "East";
        else if (bearing < 158) dir = "South-East";
        else if (bearing < 203) dir = "South";
        else if (bearing < 248) dir = "South-West";
        else if (bearing < 293) dir = "West";
        else if (bearing < 338) dir = "North-West";
        else dir = "North";
        context.getSource().sendSystemMessage(Component.literal("Bearing: " + dir + " (" + bearing + " degrees)."));
        return 1;
    }'''
replace_meth('executeCompass', compass_impl)

# 3. Implement /clearinventoryconfirmtoggle
clear_toggle_impl = '''    private static int executeClearinventoryconfirmtoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        data.clearInventoryConfirmToggle = !data.clearInventoryConfirmToggle;
        UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Clear inventory confirmation toggle set to: " + data.clearInventoryConfirmToggle));
        return 1;
    }'''
replace_meth('executeClearinventoryconfirmtoggle', clear_toggle_impl)


with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("UserData Integration injected.")