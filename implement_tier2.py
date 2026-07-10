import os

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'

with open(filepath, 'r') as f:
    content = f.read()

# Ping
old = """    private static int executePing(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command ping is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executePing(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Pong!"));
        return 1;
    }"""
content = content.replace(old, new)

# Broadcast
old = """    private static int executeBroadcast(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command broadcast is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeBroadcast(CommandContext<CommandSourceStack> context) {
        context.getSource().getServer().getPlayerList().broadcastSystemMessage(Component.literal("[Broadcast] This is a test broadcast."), false);
        return 1;
    }"""
content = content.replace(old, new)

# Hat
old = """    private static int executeHat(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command hat is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeHat(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.item.ItemStack hand = player.getMainHandItem();
        if (hand.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("You must be holding an item."));
            return 0;
        }
        net.minecraft.world.item.ItemStack head = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
        player.setItemSlot(net.minecraft.world.entity.EquipmentSlot.HEAD, hand.copy());
        player.setItemInHand(net.minecraft.world.InteractionHand.MAIN_HAND, head);
        context.getSource().sendSystemMessage(Component.literal("Enjoy your new hat!"));
        return 1;
    }"""
content = content.replace(old, new)

# Clearinventory
old = """    private static int executeClearinventory(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command clearinventory is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeClearinventory(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.getInventory().clearContent();
        context.getSource().sendSystemMessage(Component.literal("Inventory cleared."));
        return 1;
    }"""
content = content.replace(old, new)

# Getpos
old = """    private static int executeGetpos(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command getpos is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeGetpos(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        net.minecraft.world.phys.Vec3 pos = player.position();
        context.getSource().sendSystemMessage(Component.literal(String.format("Location: X: %.2f Y: %.2f Z: %.2f Pitch: %.1f Yaw: %.1f", pos.x, pos.y, pos.z, player.getXRot(), player.getYRot())));
        return 1;
    }"""
content = content.replace(old, new)

# Depth
old = """    private static int executeDepth(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command depth is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeDepth(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        int depth = player.getBlockY() - player.serverLevel().getMinBuildHeight();
        context.getSource().sendSystemMessage(Component.literal("You are " + depth + " blocks above minimum depth."));
        return 1;
    }"""
content = content.replace(old, new)

# Enderchest
old = """    private static int executeEnderchest(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command enderchest is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeEnderchest(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return net.minecraft.world.inventory.ChestMenu.threeRows(id, inventory, player.getEnderChestInventory());
        }, Component.literal("Ender Chest")));
        return 1;
    }"""
content = content.replace(old, new)

with open(filepath, 'w') as f:
    f.write(content)
print("Tier 2 injected.")
