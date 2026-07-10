import os

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

def replace_reg(cmd_name, method_name, new_reg):
    global content
    import re
    reg_pattern = r'dispatcher\.register\(Commands\.literal\("' + cmd_name + r'"\)\s*\.executes\(context -> ' + method_name + r'\(context\)\)\s*\);'
    if cmd_name == 'kit':
        reg_pattern = r'dispatcher\.register\(Commands\.literal\("kit"\)\s*\.executes\(context -> executeKit\(context\)\)\s*\);'
    content = re.sub(reg_pattern, new_reg, content)

def replace_stub(method_name, new_meth):
    global content
    import re
    meth_pattern = r'    private static int ' + method_name + r'\(CommandContext<CommandSourceStack> context[\s\S]*?\{([\s\S]*?return [01];\s*)\}'
    content = re.sub(meth_pattern, new_meth, content)

# --- 1. Kits Cache ---
kit_cache_block = '''
    public static class KitData {
        public int delay;
        public java.util.List<String> items = new java.util.ArrayList<>();
    }
    private static final java.util.Map<String, KitData> KITS = new java.util.HashMap<>();
    private static File getKitsFile() { return new File("essentials_kits.json"); }
    
    public static void loadKits(net.minecraft.server.MinecraftServer server) {
        File file = getKitsFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                java.lang.reflect.Type type = new TypeToken<java.util.Map<String, KitData>>(){}.getType();
                java.util.Map<String, KitData> loaded = GSON.fromJson(reader, type);
                if (loaded != null) {
                    KITS.clear();
                    KITS.putAll(loaded);
                }
                System.out.println("[Essentials] Loaded kits.");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
    
    public static void saveKits(net.minecraft.server.MinecraftServer server) {
        try (FileWriter writer = new FileWriter(getKitsFile())) {
            GSON.toJson(KITS, writer);
            System.out.println("[Essentials] Saved kits.");
        } catch (Exception e) { e.printStackTrace(); }
    }
'''
if 'class KitData' not in content:
    content = content.replace('private static File getDataFile()', kit_cache_block + '\n    private static File getDataFile()')
    content = content.replace('SERVER_STARTED.register(EssentialsCommands::loadData);', 'SERVER_STARTED.register(EssentialsCommands::loadData);\n        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(EssentialsCommands::loadKits);')
    content = content.replace('SERVER_STOPPING.register(EssentialsCommands::saveData);', 'SERVER_STOPPING.register(EssentialsCommands::saveData);\n        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING.register(EssentialsCommands::saveKits);')


# --- 2. /createkit ---
reg_createkit = '''dispatcher.register(Commands.literal("createkit")
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .then(Commands.argument("delay", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
                .executes(context -> executeCreatekit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "delay")))
            )
        )
    );'''
meth_createkit = '''context.getSource().sendSystemMessage(Component.literal("Usage: /createkit <name> <delay>")); return 0; }\n    private static int executeCreatekit(CommandContext<CommandSourceStack> context, String name, int delay) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        KitData kit = new KitData();\n        kit.delay = delay;\n        com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);\n        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {\n            net.minecraft.world.item.ItemStack item = player.getInventory().getItem(i);\n            if (!item.isEmpty()) {\n                try {\n                    net.minecraft.nbt.Tag tag = net.minecraft.world.item.ItemStack.CODEC.encodeStart(ops, item).getOrThrow();\n                    kit.items.add(tag.toString());\n                } catch (Exception e) { e.printStackTrace(); }\n            }\n        }\n        KITS.put(name.toLowerCase(), kit);\n        saveKits(context.getSource().getServer());\n        context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' created with " + kit.items.size() + " items."));\n        return 1;'''
replace_reg('createkit', 'executeCreatekit', reg_createkit)
replace_stub('executeCreatekit', meth_createkit)


# --- 3. /kit ---
reg_kit = '''dispatcher.register(Commands.literal("kit")
        .executes(context -> executeKit(context, ""))
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeKit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        )
    );'''
meth_kit = '''return executeKit(context, ""); }\n    private static int executeKit(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {\n        if (name.isEmpty()) {\n            context.getSource().sendSystemMessage(Component.literal("Available Kits: " + String.join(", ", KITS.keySet())));\n            return 1;\n        }\n        KitData kit = KITS.get(name.toLowerCase());\n        if (kit == null) {\n            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));\n            return 0;\n        }\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);\n        for (String itemStr : kit.items) {\n            try {\n                net.minecraft.nbt.CompoundTag tag = net.minecraft.nbt.TagParser.parseTag(itemStr);\n                net.minecraft.world.item.ItemStack item = net.minecraft.world.item.ItemStack.CODEC.parse(ops, tag).getOrThrow();\n                if (!player.getInventory().add(item)) player.drop(item, false);\n            } catch (Exception e) { e.printStackTrace(); }\n        }\n        context.getSource().sendSystemMessage(Component.literal("You received the kit '" + name + "'."));\n        return 1;'''
replace_reg('kit', 'executeKit', reg_kit)
replace_stub('executeKit', meth_kit)


# --- 4. /delkit ---
reg_delkit = '''dispatcher.register(Commands.literal("delkit")
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeDelkit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        )
    );'''
meth_delkit = '''context.getSource().sendSystemMessage(Component.literal("Usage: /delkit <name>")); return 0; }\n    private static int executeDelkit(CommandContext<CommandSourceStack> context, String name) {\n        if (KITS.remove(name.toLowerCase()) != null) {\n            saveKits(context.getSource().getServer());\n            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' deleted."));\n            return 1;\n        }\n        context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));\n        return 0;'''
replace_reg('delkit', 'executeDelkit', reg_delkit)
replace_stub('executeDelkit', meth_delkit)


# --- 5. /showkit ---
reg_showkit = '''dispatcher.register(Commands.literal("showkit")
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeShowkit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        )
    );'''
meth_showkit = '''context.getSource().sendSystemMessage(Component.literal("Usage: /showkit <name>")); return 0; }\n    private static int executeShowkit(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {\n        KitData kit = KITS.get(name.toLowerCase());\n        if (kit == null) {\n            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));\n            return 0;\n        }\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        net.minecraft.world.SimpleContainer inv = new net.minecraft.world.SimpleContainer(54);\n        com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);\n        for (int i = 0; i < Math.min(54, kit.items.size()); i++) {\n            try {\n                net.minecraft.nbt.CompoundTag tag = net.minecraft.nbt.TagParser.parseTag(kit.items.get(i));\n                net.minecraft.world.item.ItemStack item = net.minecraft.world.item.ItemStack.CODEC.parse(ops, tag).getOrThrow();\n                inv.setItem(i, item);\n            } catch (Exception e) {}\n        }\n        player.openMenu(new net.minecraft.world.SimpleMenuProvider((id, inventory, p) -> {\n            return net.minecraft.world.inventory.ChestMenu.sixRows(id, inventory, inv);\n        }, Component.literal("Kit Preview: " + name)));\n        return 1;'''
replace_reg('showkit', 'executeShowkit', reg_showkit)
replace_stub('executeShowkit', meth_showkit)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Kits safely injected!")