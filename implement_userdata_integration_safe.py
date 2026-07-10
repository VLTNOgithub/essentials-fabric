import os

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# --- 1. Refactor executeHome ---
old_home = '''    private static int executeHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeHome(context, "home"); }
    private static int executeHome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        java.util.Map<String, HomePosition> homes = playerHomes.get(player.getUUID());
        if (homes == null || homes.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You have no homes set."));
            return 0;
        }
        if (homes.size() == 1) {
            // Teleport to the only home
            return executeHome(context, homes.keySet().iterator().next());
        }
        // List homes
        context.getSource().sendSystemMessage(Component.literal("Homes: " + String.join(", ", homes.keySet())));
        return 1;
    }'''

new_home = '''    private static int executeHome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeHome(context, "home"); }
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
content = content.replace(old_home, new_home)

# --- 2. Refactor executeSethome ---
old_sethome = '''    private static int executeSethome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeSethome(context, "home"); }
    private static int executeSethome(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String dim = player.level().dimension().identifier().toString();
        HomePosition home = new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), dim);
        playerHomes.computeIfAbsent(player.getUUID(), k -> new java.util.HashMap<>()).put(name.toLowerCase(), home);
        context.getSource().sendSystemMessage(Component.literal("Home '" + name + "' set."));
        return 1;
    }'''

new_sethome = '''    private static int executeSethome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeSethome(context, "home"); }
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
content = content.replace(old_sethome, new_sethome)

# --- 3. Refactor executeDelhome ---
old_delhome = '''    private static int executeDelhome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeDelhome(context, "home"); }
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

new_delhome = '''    private static int executeDelhome(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeDelhome(context, "home"); }
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
content = content.replace(old_delhome, new_delhome)

# --- 4. Implement /compass ---
old_compass = '''    private static int executeCompass(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command compass is not fully implemented yet!"));
        return 1;
    }'''

new_compass = '''    private static int executeCompass(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
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
content = content.replace(old_compass, new_compass)

# --- 5. Implement /clearinventoryconfirmtoggle ---
old_clear_toggle = '''    private static int executeClearinventoryconfirmtoggle(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command clearinventoryconfirmtoggle is not fully implemented yet!"));
        return 1;
    }'''

new_clear_toggle = '''    private static int executeClearinventoryconfirmtoggle(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        UserData data = UserCache.getUser(player);
        data.clearInventoryConfirmToggle = !data.clearInventoryConfirmToggle;
        UserCache.saveUser(player.getUUID());
        context.getSource().sendSystemMessage(Component.literal("Clear inventory confirmation toggle set to: " + data.clearInventoryConfirmToggle));
        return 1;
    }'''
content = content.replace(old_clear_toggle, new_clear_toggle)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("UserData Integration injected safely.")