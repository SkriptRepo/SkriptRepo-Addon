package com.skriptrepo.addon.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import ch.njol.skript.ScriptLoader;

import com.skriptrepo.addon.SkriptRepo;
import com.skriptrepo.addon.util.Encryption;
import com.skriptrepo.addon.util.Enums.SRFileTypes;

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
	
	public void encrypt(Script s) {
		Encryption en = new Encryption();
		String encrypted = null;
		File writeTo = new File(SkriptRepo.instance.getDataFolder(), s.getFile() + ".sre");
		try {
			FileReader fileReader = new FileReader(s.getFile());
			BufferedReader bufferedReader = new BufferedReader(fileReader);
	        String line = null;
	        Writer writer = null;
			while((line = bufferedReader.readLine()) != null) {
				encrypted = Encryption.bytesToHex(en.encrypt(line));
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeTo), "utf-8"));
				writer.write(encrypted);
			}
		    writer.close();
		    bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(encrypted == null) {
			return;
		}
	}
	
	public static File decrypt(File f) {
		Encryption en = new Encryption();
		String decrypted = null;
		File writeTo = new File(SkriptRepo.instance.getDataFolder(), f.getName() + ".sr");
		try {
			FileReader fileReader = new FileReader(f);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
	        String line = null;
	        Writer writer = null;
			while((line = bufferedReader.readLine()) != null) {
				decrypted = new String(en.decrypt(line));
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writeTo), "utf-8"));
				writer.write(decrypted);
			}
		    writer.close();
		    bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return writeTo;
		
	}
	
	public static void loadURL(URL url, SRFileTypes fileType) {
        File f = new File(SkriptRepo.instance.getDataFolder(), "tmp.sr");     
        File toLoad = null;
        try {
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream(f);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            if(fileType == SRFileTypes.EONLINE) {
            	toLoad = decrypt(f);
            } else {
            	toLoad = f;
            }
            loadScript(toLoad);
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (f.exists()) f.delete();
        while (toLoad.exists()) toLoad.delete();
    }
	
	public static void loadScript(File f) {
        try {
            Class<?> cs = ScriptLoader.class;
            Method method = cs.getDeclaredMethod("loadScript", File.class);
            method.setAccessible(true);
            method.invoke(null, f);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public static void unloadScript(File f) {
        try {
            Class<?> cs = ScriptLoader.class;
            Method method = cs.getDeclaredMethod("unloadScript", File.class);
            method.setAccessible(true);
            method.invoke(null, f);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
    public static void updateScript(Script s) {
    	if(s.isReady()) {
    		if(s.getOnlineVersion() > s.getLocalVersion()) {
    			String fName = s.getFile().getName();
    			unloadScript(s.getFile());
    			s.getFile().delete();
    			File writeTo = new File("/plugins/Skript/scripts/" + fName);
    			try {
    				ReadableByteChannel rbc = Channels.newChannel(s.getUpdateURL().openStream());
    				FileOutputStream fos = new FileOutputStream(writeTo);
    				fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    				fos.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			if(writeTo != null) {
    				loadScript(writeTo);
    			}
    		}
    	}
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
