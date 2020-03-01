package ru.ragnok123.playerControl;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.permission.PermissionAttachment;

import java.util.*;
import lombok.Getter;
import lombok.Setter;
public class Control {
	
	@Getter
	public Player controller = null;
	@Getter
	public Player victim = null;
	
	public HashMap<UUID, Map<Integer,Item>> backup = new HashMap<UUID,Map<Integer,Item>>();
	public HashMap<UUID, Item[]> armor = new HashMap<UUID,Item[]>();
	public HashMap<UUID,Skin> skin = new HashMap<UUID,Skin>();
	public HashMap<UUID,Position> pos = new HashMap<UUID,Position>();
	
	public PermissionAttachment permV = null;
	public PermissionAttachment permC = null;
	
	@Getter
	@Setter
	public boolean control = false;
	
	public Control(Player controller, Player victim) {
		this.controller = controller;
		this.victim = victim;
		this.backup.put(controller.getUniqueId(),controller.getInventory().getContents());
		this.armor.put(controller.getUniqueId(),controller.getInventory().getArmorContents());
		this.skin.put(controller.getUniqueId(),controller.getSkin());
		this.pos.put(controller.getUniqueId(),new Position(controller.getX(),controller.getY(),controller.getZ(), controller.getLevel()));
	}
	
	public void run() {
		if(isControl()) {
			if(!victim.getInventory().getContents().equals(controller.getInventory().getContents())) {
				victim.getInventory().setContents(controller.getInventory().getContents());
			}
		}
	}
	
	public void move() {
		if(isControl()) {
			victim.teleport(new Location(controller.getX(),controller.getY(),controller.getZ(),controller.getYaw(),controller.getPitch()));
		}
	}
	
	public void chat(PlayerChatEvent e) {
		if(isControl()) {
			Server.getInstance().broadcastMessage(Server.getInstance().getLanguage().translateString(e.getFormat(),new String[] {victim.getDisplayName(),e.getMessage()}),e.getRecipients());
		}
	}
	
	public void start() {
		controller.getInventory().setContents(victim.getInventory().getContents());
		controller.getInventory().setArmorContents(victim.getInventory().getArmorContents());
		controller.setSkin(victim.getSkin());
		controller.hidePlayer(victim);
		controller.teleport(victim);
		for(Player p : controller.getServer().getOnlinePlayers().values()) {
			p.hidePlayer(controller);
		}
		setControl(true);
	}
	
	public void end() {
		setControl(false);
		PlayerControl.getInstance().controlsEnabled.remove(this);
		UUID u = controller.getUniqueId();
		controller.getInventory().setContents(backup.get(u));
		controller.getInventory().setArmorContents(armor.get(u));
		controller.setSkin(skin.get(u));
		controller.teleport(pos.get(u));
		controller.showPlayer(victim);
		controller.getServer().getScheduler().scheduleDelayedTask(new Runnable() {
			public void run() {
				for(Player p : controller.getServer().getOnlinePlayers().values()) {
					p.showPlayer(controller);
				}
			}
		}, 20);
	}
	
}
