package vltno.essentials;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.server.level.ServerPlayer;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserCache {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<UUID, UserData> USERS = new HashMap<>();
    private static File dataFolder;

    public static void init(File folder) {
        dataFolder = new File(folder, "userdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public static UserData getUser(UUID uuid) {
        return USERS.computeIfAbsent(uuid, k -> {
            File file = new File(dataFolder, uuid.toString() + ".json");
            if (file.exists()) {
                try (FileReader reader = new FileReader(file)) {
                    return GSON.fromJson(reader, UserData.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return new UserData();
        });
    }

    public static UserData getUser(ServerPlayer player) {
        return getUser(player.getUUID());
    }

    public static Map<UUID, UserData> getLoadedUsers() {
        return USERS;
    }

    public static void saveUser(UUID uuid) {
        UserData data = USERS.get(uuid);
        if (data != null && dataFolder != null) {
            File file = new File(dataFolder, uuid.toString() + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                GSON.toJson(data, writer);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveAll() {
        for (UUID uuid : USERS.keySet()) {
            saveUser(uuid);
        }
    }
}
