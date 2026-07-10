import os

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

old_stub = '''    private static int executeCreatekit(CommandContext<CommandSourceStack> context) {\n        context.getSource().sendSystemMessage(Component.literal("Command createkit is not fully implemented yet!"));\n        return 1;\n    }'''
new_stub = '''    private static int executeCreatekit(CommandContext<CommandSourceStack> context) { context.getSource().sendSystemMessage(Component.literal("Usage: /createkit <name> <delay>")); return 0; }\n    private static int executeCreatekit(CommandContext<CommandSourceStack> context, String name, int delay) throws CommandSyntaxException {\n        ServerPlayer player = context.getSource().getPlayerOrException();\n        KitData kit = new KitData();\n        kit.delay = delay;\n        com.mojang.serialization.DynamicOps<net.minecraft.nbt.Tag> ops = player.registryAccess().createSerializationContext(net.minecraft.nbt.NbtOps.INSTANCE);\n        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {\n            net.minecraft.world.item.ItemStack item = player.getInventory().getItem(i);\n            if (!item.isEmpty()) {\n                try {\n                    net.minecraft.nbt.Tag tag = net.minecraft.world.item.ItemStack.CODEC.encodeStart(ops, item).getOrThrow();\n                    kit.items.add(tag.toString());\n                } catch (Exception e) { e.printStackTrace(); }\n            }\n        }\n        KITS.put(name.toLowerCase(), kit);\n        saveKits();\n        context.getSource().sendSystemMessage(Component.literal("Kit '" + name + "' created with " + kit.items.size() + " items."));\n        return 1;\n    }'''
content = content.replace(old_stub.replace('\n', '\r\n'), new_stub)
content = content.replace(old_stub, new_stub)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
