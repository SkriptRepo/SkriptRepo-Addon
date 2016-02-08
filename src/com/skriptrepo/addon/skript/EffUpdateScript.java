/*
 * EffLoadFromURL.class - Made by nfell2009
 * Copyright (C) 2016 - SkriptRepo
 * 
*/

package com.skriptrepo.addon.skript;


import java.io.File;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;

import org.bukkit.event.Event;

import com.skriptrepo.addon.api.Script;
import com.skriptrepo.addon.api.ScriptManager;

public class EffUpdateScript extends Effect {

    private Expression<String> url;
    private int matchType;

    @Override
    protected void execute(Event event){
    	File sf = null;
    	if(matchType == 0) {
    		sf = ch.njol.skript.ScriptLoader.currentScript.getFile();
    	} else {
    		String u = url.getSingle(event);
    		sf = new File(u);
    	}
    	Script s = new Script(sf);
		ScriptManager.updateScript(s);
    }

    @Override
    public String toString(Event event, boolean b){
        return "Update script";
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult){
    	matchType = i;
        url = (Expression<String>) expressions[0];
        return true;
    }
}
