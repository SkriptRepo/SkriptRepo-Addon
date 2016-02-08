/*
 * EffLoadFromURL.class - Made by nfell2009
 * Copyright (C) 2016 - SkriptRepo
 * 
*/

package com.skriptrepo.addon.skript;

import java.net.MalformedURLException;
import java.net.URL;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;

import org.bukkit.event.Event;

import com.skriptrepo.addon.api.ScriptManager;
import com.skriptrepo.addon.util.Enums.SRFileTypes;

public class EffLoadFromURL extends Effect {

    private Expression<String> url;
    int matchType;

    @Override
    protected void execute(Event event){
    	String u = url.getSingle(event);
    	URL uu = null;
		try {
			uu = new URL(u);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	if(matchType == 0) {
    		ScriptManager.loadURL(uu, SRFileTypes.DONLINE);
    	} else {
    		ScriptManager.loadURL(uu, SRFileTypes.EONLINE);   		
    	}
    	
    	
    }

    @Override
    public String toString(Event event, boolean b){
        return "Load script from URL";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult){
    	matchType = i;
        url = (Expression<String>) expressions[0];
        return true;
    }
}
