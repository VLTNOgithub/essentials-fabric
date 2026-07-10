import os
import re

filepath = 'src/main/java/vltno/essentials/EssentialsCommands.java'
with open(filepath, 'r', encoding='utf-8') as f:
    content = f.read()

# Add Gson imports
if 'import com.google.gson.Gson;' not in content:
    content = content.replace('import java.util.Collections;',
                              'import java.util.Collections;\nimport com.google.gson.Gson;\nimport com.google.gson.GsonBuilder;\nimport com.google.gson.reflect.TypeToken;\nimport java.io.File;\nimport java.io.FileReader;\nimport java.io.FileWriter;')

# Add load and save methods
persistence_block = '''
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File getDataFile() {
        return new File("essentials_offline_data.json");
    }

    public static void loadData(net.minecraft.server.MinecraftServer server) {
        File file = getDataFile();
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                java.lang.reflect.Type type = new TypeToken<java.util.Map<String, HomePosition>>(){}.getType();
                java.util.Map<String, HomePosition> loaded = GSON.fromJson(reader, type);
                if (loaded != null) {
                    offlinePositions.clear();
                    offlinePositions.putAll(loaded);
                }
                System.out.println("[Essentials] Loaded offline positions.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveData(net.minecraft.server.MinecraftServer server) {
        try (FileWriter writer = new FileWriter(getDataFile())) {
            GSON.toJson(offlinePositions, writer);
            System.out.println("[Essentials] Saved offline positions.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
'''

if 'private static final Gson GSON' not in content:
    content = content.replace('public static void onPlayerDisconnect', persistence_block + '\n    public static void onPlayerDisconnect')

# Also save whenever a player disconnects to ensure real-time safety
content = content.replace(
    'offlinePositions.put(player.getName().getString().toLowerCase(), new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), dim));',
    'offlinePositions.put(player.getName().getString().toLowerCase(), new HomePosition(player.getX(), player.getY(), player.getZ(), player.getYRot(), player.getXRot(), dim));\n        saveData(server);'
)

# Hook into Server Lifecycle events
lifecycle_hooks = '''
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STARTED.register(EssentialsCommands::loadData);
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents.SERVER_STOPPING.register(EssentialsCommands::saveData);
'''
if 'SERVER_STARTED.register' not in content:
    content = content.replace('net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register(EssentialsCommands::onPlayerDisconnect);',
                              'net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents.DISCONNECT.register(EssentialsCommands::onPlayerDisconnect);' + lifecycle_hooks)

with open(filepath, 'w', encoding='utf-8') as f:
    f.write(content)
print("Persistence injected.")
