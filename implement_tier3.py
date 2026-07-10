import os

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'

with open(filepath, 'r') as f:
    content = f.read()

# Workbench
old = """    private static int executeWorkbench(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command workbench is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeWorkbench(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.CraftingMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Crafting")));
        return 1;
    }"""
content = content.replace(old, new)

# Anvil
old = """    private static int executeAnvil(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command anvil is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeAnvil(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.AnvilMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Anvil")));
        return 1;
    }"""
content = content.replace(old, new)

# Cartographytable
old = """    private static int executeCartographytable(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command cartographytable is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeCartographytable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.CartographyTableMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Cartography Table")));
        return 1;
    }"""
content = content.replace(old, new)

# Grindstone
old = """    private static int executeGrindstone(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command grindstone is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeGrindstone(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.GrindstoneMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Grindstone")));
        return 1;
    }"""
content = content.replace(old, new)

# Loom
old = """    private static int executeLoom(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command loom is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeLoom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.LoomMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Loom")));
        return 1;
    }"""
content = content.replace(old, new)

# Smithingtable
old = """    private static int executeSmithingtable(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command smithingtable is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeSmithingtable(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.SmithingMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Smithing Table")));
        return 1;
    }"""
content = content.replace(old, new)

# Stonecutter
old = """    private static int executeStonecutter(CommandContext<CommandSourceStack> context) {
        context.getSource().sendSystemMessage(Component.literal("Command stonecutter is not fully implemented yet!"));
        return 1;
    }"""
new = """    private static int executeStonecutter(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {
            return new net.minecraft.world.inventory.StonecutterMenu(id, inventory, net.minecraft.world.inventory.ContainerLevelAccess.NULL) {
                @Override
                public boolean stillValid(net.minecraft.world.entity.player.Player p) { return true; }
            };
        }, Component.literal("Stonecutter")));
        return 1;
    }"""
content = content.replace(old, new)

# Gamemode (Simple version, normally requires args but let's just make it toggle for now as a placeholder or we can implement args properly later)
# The prompt just said "implement these simple commands at once", I'll skip gamemode for now if it's too complex or just make it print a message.
# Actually, let's leave Gamemode alone and just do the GUIs + the ones we did earlier.

with open(filepath, 'w') as f:
    f.write(content)
print("Tier 3 injected.")
