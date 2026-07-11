package vltno.essentials;

import java.util.HashMap;
import java.util.Map;

public class UserData {
    public double money = 0.0;
    public boolean godMode = false;
    public boolean tptoggle = false;
    public boolean tpauto = false;
    public boolean msgtoggle = false;
    public boolean clearInventoryConfirmToggle = true;
    public boolean payConfirmToggle = true;
    public boolean payToggle = true;
    public long lastMute = 0;
    public long muteTimeout = 0;
    public String nickname = null;
    public String jail = null;

    public java.util.List<java.util.UUID> ignoredPlayers = new java.util.ArrayList<>();
    public boolean isMuted = false;
    public java.util.List<String> mail = new java.util.ArrayList<>();
    public Map<String, EssentialsCommands.HomePosition> homes = new HashMap<>();
    public Map<String, Long> kitCooldowns = new HashMap<>();
    public Map<String, String> powertools = new HashMap<>();
    public boolean powertoolEnabled = true;
    public boolean socialSpy = false;
    public java.util.List<String> unlimitedItems = new java.util.ArrayList<>();

    // Constructor
    public UserData() {}
}
