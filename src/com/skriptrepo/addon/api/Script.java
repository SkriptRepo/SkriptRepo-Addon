package com.skriptrepo.addon.api;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Script {

	private File file;
	private Double localVersion, onlineVersion;
	private URL updateURL, versionURL;
	private String name;
	// Construct
	public Script(File f) {
		this.file = f;
	}
	
	// Setters
	public void setLocalVersion(Double v) {
		this.localVersion = v;
	}
	
	public void setOnlineVersion(Double v) {
		this.onlineVersion = v;
	}
	
	public void setUpdateURL(URL u) {
		this.updateURL = u;
	}
		
	public void setVersionURL(URL u) {
		this.versionURL = u;
	}
	
	public void setName(String n) {
		this.name = n;
	}
	
	// Getters
	public File getFile() {
		return this.file;
	}
	
	public Double getLocalVersion() {
		return this.localVersion;
	}
	
	public Double getOnlineVersion() {
		return this.onlineVersion;
	}
	
	public URL getUpdateURL() {
		return this.updateURL;
	}
	
	public URL getVersionURL() {
		return this.versionURL;
	}
	
	public String getName() {
		return this.name;
	}
	
	// Extras
	public Boolean isNearlyReady() {
		if(getFile() != null && getUpdateURL() != null && getLocalVersion() != null && getVersionURL() != null && getName() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean isReady() {
		if(getFile() != null && getUpdateURL() != null && getLocalVersion() != null && getVersionURL() != null && getUpdateURL() != null && getName() != null) {
			return true;
		} else {
			return false;
		}
	}
	
	public Boolean fetchUpdateURL() {
		if(!isNearlyReady()) {
			return false;
		}
		try { 
			URLConnection yc = getUpdateURL().openConnection(); 
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream())); 
			String inputLine; 
			while ((inputLine = in.readLine()) != null) { 
				this.updateURL = new URL(inputLine);
			}
			in.close();
			return true;
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
		return false;
	}
	
	public Boolean fetchOnlineVersion() {
		try { 
			URLConnection yc = getVersionURL().openConnection(); 
			BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream())); 
			String inputLine; 
			while ((inputLine = in.readLine()) != null) { 
				this.onlineVersion = Double.parseDouble(inputLine);
			}
			in.close();
			return true;
		} catch (Exception e) { 
			e.printStackTrace(); 
		}
		return false;
	}
	
}
