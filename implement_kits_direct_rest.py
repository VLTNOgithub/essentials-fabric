import os
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
    meth_pattern = r'    private static int ' + method_name + r'\(CommandContext<CommandSourceStack> context\)[\s\S]*?return 1;\s*\}'
    content = re.sub(meth_pattern, new_meth, content)

# --- 2. /kit ---
reg_kit = '''dispatcher.register(Commands.literal("kit")
        .executes(context -> executeKit(context, ""))
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeKit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        )
    );'''
meth_kit = '''    private static int executeKit(CommandContext<CommandSourceStack> context) throws CommandSyntaxException { return executeKit(context, ""); }\n    private static int executeKit(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {\n        if (name.isEmpty()) {\n            context.getSource().sendSystemMessage(Component.literal("Available Kits: " + String.join(", ", KITS.keySet())));\n            return 1;\n        }\n        KitData kit = KITS.get(name.toLowerCase());\n        if (kit == null) {\n            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));\n            return 0;\n        }\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);\n        for (String itemStr : kit.items) {\n            try {\n                net.minecraft.nbt.CompoundTag tag = net.minecraft.nbt.TagParser.parseTag(itemStr);\n                net.minecraft.world.item.ItemStack item = net.minecraft.world.item.ItemStack.CODEC.parse(ops, tag).getOrThrow();\n                if (!player.getInventory().add(item)) player.drop(item, false);\n            } catch (Exception e) { e.printStackTrace(); }\n        }\n        context.getSource().sendSystemMessage(Component.literal("You received the kit '" + name + "'."));\n        return 1;\n    }'''
content = re.sub(r'dispatcher\.register\(Commands\.literal\("kit"\)[\s\S]*?\)\s*\);', reg_kit, content)
replace_stub('executeKit', meth_kit)

# --- 3. /delkit ---
reg_delkit = '''dispatcher.register(Commands.literal("delkit")
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeDelkit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        )
    );'''
meth_delkit = '''    private static int executeDelkit(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /delkit <name>")); return 0; }\n    private static int executeDelkit(CommandContext<CommandSourceStack> context, String name) {\n        if (KITS.remove(name.toLowerCase()) != null) {\n            saveKits();\n            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' deleted."));\n            return 1;\n        }\n        context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));\n        return 0;\n    }'''
replace_reg('delkit', 'executeDelkit', reg_delkit)
replace_stub('executeDelkit', meth_delkit)

# --- 4. /showkit ---
reg_showkit = '''dispatcher.register(Commands.literal("showkit")
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeShowkit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        )
    );'''
meth_showkit = '''    private static int executeShowkit(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /showkit <name>")); return 0; }\n    private static int executeShowkit(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {\n        KitData kit = KITS.get(name.toLowerCase());\n        if (kit == null) {\n            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));\n            return 0;\n        }\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        net.minecraft.world.SimpleContainer inv = new net.minecraft.world.SimpleContainer(54);\n        com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);\n        for (int i = 0; i < Math.min(54, kit.items.size()); i++) {\n            try {\n                net.minecraft.nbt.CompoundTag tag = net.minecraft.nbt.TagParser.parseTag(kit.items.get(i));\n                net.minecraft.world.item.ItemStack item = net.minecraft.world.item.ItemStack.CODEC.parse(ops, tag).getOrThrow();\n                inv.setItem(i, item);\n            } catch (Exception e) {}\n        }\n        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {\n            return net.minecraft.world.inventory.ChestMenu.sixRows(id, inventory, inv);\n        }, Component.literal("Kit Preview: " + name)));\n        return 1;\n    }'''
replace_reg('showkit', 'executeShowkit', reg_showkit)
replace_stub('executeShowkit', meth_showkit)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
