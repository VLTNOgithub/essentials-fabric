import os

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

def replace_reg(cmd_name, method_name, new_reg):
    global content
    import re
    reg_pattern = r'dispatcher\.register\(Commands\.literal\("' + cmd_name + r'"\)\s*\.executes\(context -> ' + method_name + r'\(context\)\)\s*\);'
    content = re.sub(reg_pattern, new_reg, content)

def replace_stub(cmd_name, new_body):
    global content
    stub_string = f'context.getSource().sendSystemMessage(Component.literal("Command {cmd_name} is not fully implemented yet!"));\n        return 1;'
    if stub_string in content:
        content = content.replace(stub_string, new_body)
    else:
        print(f"Warning: Could not find stub for {cmd_name}")


# --- KIT CACHE STORAGE ---
kit_cache_block = '''
    public static class KitData {
        public int delay;
        public java.util.List<String> items = new java.util.ArrayList<>();
    }
    private static final java.util.Map<String, KitData> KITS = new java.util.HashMap<>();
    private static File getKitsFile() { return new File("essentials_kits.json"); }
    
    public static void loadKits() {
        File file = getKitsFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                java.lang.reflect.Type type = new TypeToken<java.util.Map<String, KitData>>(){}.getType();
                java.util.Map<String, KitData> loaded = GSON.fromJson(reader, type);
                if (loaded != null) {
                    KITS.clear();
                    KITS.putAll(loaded);
                }
            } catch (Exception e) { e.printStackTrace(); }
        }
    }
    
    public static void saveKits() {
        try (FileWriter writer = new FileWriter(getKitsFile())) {
            GSON.toJson(KITS, writer);
        } catch (Exception e) { e.printStackTrace(); }
    }
'''
if 'class KitData' not in content:
    content = content.replace('private static File getDataFile()', kit_cache_block + '\n    private static File getDataFile()')
    # Inject into lifecycle hooks
    content = content.replace('loadData(server);', 'loadData(server); loadKits();')
    content = content.replace('saveData(server);', 'saveData(server); saveKits();')
    content = content.replace('loadData);', 'loadData); net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(s -> loadKits());')
    content = content.replace('saveData);', 'saveData); net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING.register(s -> saveKits());')


# --- 1. /createkit ---
reg_createkit = '''dispatcher.register(Commands.literal("createkit")
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .then(Commands.argument("delay", com.mojang.brigadier.arguments.IntegerArgumentType.integer(0))
                .executes(context -> executeCreatekit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname"), com.mojang.brigadier.arguments.IntegerArgumentType.getInteger(context, "delay")))
            )
        )
    );'''
meth_createkit = '''context.getSource().sendSystemMessage(Component.literal("Usage: /createkit <name> <delay>")); return 0; }
    private static int executeCreatekit(CommandContext<CommandSourceStack> context, String name, int delay) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        KitData kit = new KitData();
        kit.delay = delay;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            net.minecraft.world.item.ItemStack item = player.getInventory().getItem(i);
            if (!item.isEmpty()) {
                net.minecraft.resources.Identifier id = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(item.getItem());
                kit.items.add(id.toString() + " " + item.getCount());
            }
        }
        KITS.put(name.toLowerCase(), kit);
        saveKits();
        context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' created with " + kit.items.size() + " items."));
        return 1;'''
replace_reg('createkit', 'executeCreatekit', reg_createkit)
replace_stub('createkit', meth_createkit)


# --- 2. /kit ---
reg_kit = '''dispatcher.register(Commands.literal("kit")
        .executes(context -> executeKit(context, ""))
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeKit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        )
    );'''
meth_kit = '''return executeKit(context, ""); }
    private static int executeKit(CommandContext<CommandSourceStack> context, String name) throws CommandSyntaxException {
        if (name.isEmpty()) {
            context.getSource().sendSystemMessage(Component.literal("Available Kits: " + String.join(", ", KITS.keySet())));
            return 1;
        }
        KitData kit = KITS.get(name.toLowerCase());
        if (kit == null) {
            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));
            return 0;
        }
        ServerPlayer player = context.getSource().getPlayerOrException();
        // In a real implementation we would check UserData kit cooldowns here
        for (String itemStr : kit.items) {
            String[] parts = itemStr.split(" ");
            net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.get(net.minecraft.resources.Identifier.parse(parts[0]));
            int count = parts.length > 1 ? Integer.parseInt(parts[1]) : 1;
            player.getInventory().add(new net.minecraft.world.item.ItemStack(item, count));
        }
        context.getSource().sendSystemMessage(Component.literal("You received the kit '" + name + "'."));
        return 1;'''
replace_reg('kit', 'executeKit', reg_kit)
replace_stub('kit', meth_kit)


# --- 3. /delkit ---
reg_delkit = '''dispatcher.register(Commands.literal("delkit")
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeDelkit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        )
    );'''
meth_delkit = '''context.getSource().sendSystemMessage(Component.literal("Usage: /delkit <name>")); return 0; }
    private static int executeDelkit(CommandContext<CommandSourceStack> context, String name) {
        if (KITS.remove(name.toLowerCase()) != null) {
            saveKits();
            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' deleted."));
            return 1;
        }
        context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));
        return 0;'''
replace_reg('delkit', 'executeDelkit', reg_delkit)
replace_stub('delkit', meth_delkit)

# --- 4. /showkit ---
reg_showkit = '''dispatcher.register(Commands.literal("showkit")
        .then(Commands.argument("kitname", com.mojang.brigadier.arguments.StringArgumentType.word())
            .executes(context -> executeShowkit(context, com.mojang.brigadier.arguments.StringArgumentType.getString(context, "kitname")))
        )
    );'''
meth_showkit = '''context.getSource().sendSystemMessage(Component.literal("Usage: /showkit <name>")); return 0; }
    private static int executeShowkit(CommandContext<CommandSourceStack> context, String name) {
        KitData kit = KITS.get(name.toLowerCase());
        if (kit == null) {
            context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' does not exist."));
            return 0;
        }
        context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' contains: " + String.join(", ", kit.items)));
        return 1;'''
replace_reg('showkit', 'executeShowkit', reg_showkit)
replace_stub('showkit', meth_showkit)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Kits safely injected!")