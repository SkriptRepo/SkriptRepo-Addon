package com.skriptrepo.addon.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.skriptrepo.addon.SkriptRepo;

public class ScriptManager {

	private static List<Script> registeredScripts;
	private Script script; 
	
	public void registerScript(File f) {
		script = new Script(f);
		if(setData(script)) {
			if(registeredScripts.contains(script)) {
				registeredScripts.remove(script);
			}
			registeredScripts.add(script);
		} else {
			if(script.getName() != null) {
				SkriptRepo.instance.getLogger().severe("[SkriptRepo] When loading: " + script.getName() + " an error occoured. Please check the SkriptRepo settings and try again");
			} else {
				SkriptRepo.instance.getLogger().severe("[SkriptRepo] When loading a script an error occoured. Please check the SkriptRepo settings and try again");
			}
		}
	}
	
	public Boolean isCached() {
		return true;
	}
	
	public Boolean setData(Script s) {
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(script.getFile());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        Boolean inMode = false;
        String line = null;
        try {
			while((line = bufferedReader.readLine()) != null) {
				if(line.startsWith("#")) {
					if(line.contains("SkriptRepo data:")) {
						inMode = true;
					} else if (inMode) {
						if(line.contains("version: ")) {
							String[] splitLine = line.split(": ");
							s.setLocalVersion(Double.parseDouble(splitLine[1]));
						} else if (line.contains("version url:")) {
							String[] splitLine = line.split(": ");
							s.setVersionURL(new URL(splitLine[1]));
						} else if (line.contains("update url:")) {
							String[] splitLine = line.split(": ");
							s.setUpdateURL(new URL(splitLine[1]));
						} else if (line.contains("SkriptRepo end")) {
							inMode = false;
							break;
						}
					}
				}
			}
			bufferedReader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
        return checkData(s);
	}
	
	public Boolean checkData(Script s) {
		if(s.isNearlyReady()) {
			if(s.fetchUpdateURL()) {
				if(s.isReady()) {
					return true;
				}
			}
		}
		return false;
	}
	
}
