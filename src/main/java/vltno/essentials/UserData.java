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
    
    public Map<String, EssentialsCommands.HomePosition> homes = new HashMap<>();
    
    // Constructor
    public UserData() {}
}
