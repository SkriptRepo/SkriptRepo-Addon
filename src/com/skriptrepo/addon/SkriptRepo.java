package com.skriptrepo.addon;

import org.bukkit.plugin.java.JavaPlugin;

import ch.njol.skript.Skript;
import ch.njol.skript.events.bukkit.ScriptEvent;
import ch.njol.skript.lang.util.SimpleEvent;

public class SkriptRepo extends JavaPlugin {
	
	public static JavaPlugin instance;
	
	public void onEnable() {
		if(!getDataFolder().exists()) {
			if(!getDataFolder().mkdirs()) {
				getLogger().warning("[SkriptRepo] Failed to create directories required!");
				getLogger().warning("[SkriptRepo] Attempted creation of: ");
				getLogger().warning("[SkriptRepo] " + getDataFolder());
			}
		}
		instance = this;
		Skript.registerEvent("SkriptRepo settings", SimpleEvent.class, ScriptEvent.class, "skriptrepo settings");
	}

}
