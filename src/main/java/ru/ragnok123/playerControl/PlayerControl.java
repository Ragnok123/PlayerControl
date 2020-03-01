package ru.ragnok123.playerControl;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;

import java.util.*;

public class PlayerControl extends PluginBase{
	
	public ArrayList<Control> controlsEnabled = new ArrayList<Control>();
	public static PlayerControl instance;
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		instance = this;
		if(getServer().getPluginManager().getPlugin("MenuAPI") == null ) {
			getLogger().info("This plugin requires MenuAPI by Ragnok123. Download it on https://nukkitx.com");
			this.setEnabled(false);
		}
		getServer().getPluginManager().registerEvents(new Listener(),this);
		getServer().getCommandMap().register("pc", new PlayerControlCommand("pc"));
		getServer().getScheduler().scheduleRepeatingTask(new Runnable() {
			public void run() {
				for(Control c : controlsEnabled) {
					c.run();
				}
			}
		},20);
	}
	
	public static PlayerControl getInstance() {
		return instance;
	}
	
	public Control getControlByController(Player p) {
		for(Control c : controlsEnabled) {
			if(c.getController().getUniqueId().equals(p.getUniqueId())) {
				return c;
			}
		}
		return null;
	}
	
	public Control getControlByVictim(Player p) {
		for(Control c : controlsEnabled) {
			if(c.getVictim().getUniqueId().equals(p.getUniqueId())) {
				return c;
			}
		}
		return null;
	}
	
	public boolean isControlling(Player p) {
		return getControlByController(p) != null ? true : false;
	}
	
	public boolean isUnderControll(Player p) {
		return getControlByVictim(p) != null ? true : false;
	}
	
}
