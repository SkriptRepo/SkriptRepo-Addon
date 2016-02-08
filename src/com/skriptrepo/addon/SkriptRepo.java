package com.skriptrepo.addon;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.plugin.java.JavaPlugin;

import com.skriptrepo.addon.api.ScriptManager;
import com.skriptrepo.addon.skript.EffLoadFromURL;
import com.skriptrepo.addon.skript.EffUpdateScript;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;

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
		
		Skript.registerEffect(EffLoadFromURL.class, new String[] {"load script from url %string%", "load encrypted script from url %string%" });
		Skript.registerEffect(EffUpdateScript.class, new String[] {"update [this] script", "update [script] %string%" });
		File scriptsFolder = new File(Skript.getInstance().getDataFolder(), Skript.SCRIPTSFOLDER + File.separator);
		List<File> allFiles = getFiles(scriptsFolder);
		for(File f : allFiles) {
			if(!f.getName().startsWith("-")) {
				String ext = FilenameUtils.getExtension(f.getName());
				if(ext.equalsIgnoreCase("sk")) {
					ScriptManager sm = new ScriptManager();
					sm.registerScript(f);
				}
				
			}
		}
		
		
	}
		
	@SuppressWarnings("null")
	public List<File> getFiles(File folder) {
		List<File> allFiles = null;
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
	        	for(File f : getFiles(file)) {
	        		allFiles.add(f);
	        	}
	        } else {
	            allFiles.add(file);
	        }
	    }
		return allFiles;
	}

}
