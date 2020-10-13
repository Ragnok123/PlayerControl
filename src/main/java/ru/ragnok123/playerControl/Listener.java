package ru.ragnok123.playerControl;

import java.lang.reflect.Field;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.player.PlayerAnimationEvent;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerInteractEvent.Action;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.permission.PermissionAttachment;
import ru.ragnok123.menuAPI.inventory.InventoryCategory;
import ru.ragnok123.menuAPI.inventory.InventoryMenu;
import ru.ragnok123.menuAPI.inventory.item.ItemClick;
import ru.ragnok123.menuAPI.inventory.item.ItemData;

public class Listener implements cn.nukkit.event.Listener{
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(PlayerControl.getInstance().isControlling(p)) {
			PlayerControl.getInstance().getControlByController(p).move();
		} else if(PlayerControl.getInstance().isUnderControll(p)) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(PlayerControl.getInstance().isUnderControll(p)) {
		} else if(PlayerControl.getInstance().isControlling(p)) {
			e.setCancelled();
			//p.getServer().getPluginManager().callEvent(new PlayerInteractEvent(PlayerControl.getInstance().getControlByController(p).getVictim(),e.getItem(),e.getTouchVector(),e.getFace(),e.getAction()));
		}
	}
	
	@EventHandler
	public void onCmd(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if(PlayerControl.getInstance().isUnderControll(p)) {
			Control control = PlayerControl.getInstance().getControlByVictim(p);
			if(!p.hasPermission("pc.cmd")) {
				e.setCancelled();
			} else {
				p.removeAttachment(control.permV);
			}
		} else if(PlayerControl.getInstance().isControlling(p)) {
			Control co = PlayerControl.getInstance().getControlByController(p);
			if(!p.hasPermission("pc.cmd")) {
				e.setCancelled();
				Player v = co.getVictim();
				
				final InventoryMenu menu = new InventoryMenu();
				
				InventoryCategory c = new InventoryCategory();
				c.addElement(2, new ItemData(Item.IRON_SWORD,0,1,"§6Run as victim"), new ItemClick() {
					@Override
					public void onClick(Player player, Item item) {
						co.permV = v.addAttachment(PlayerControl.getInstance(), "pc.cmd");
						v.getServer().dispatchCommand(v, e.getMessage().substring(1));
						//v.chat(e.getMessage());
					}
				});
				c.addElement(6, new ItemData(Item.IRON_PICKAXE,0,1,"§aRun as yourself"), new ItemClick() {
					@Override
					public void onClick(Player player, Item item) {
						co.permC = p.addAttachment(PlayerControl.getInstance(), "pc.cmd");
						p.getServer().dispatchCommand(p,e.getMessage().substring(1));
						//p.chat(e.getMessage());
					}
				});
				

				menu.setName("§aCommand: §b"+e.getMessage());
				menu.setMainCategory(c);
				menu.setOnlyRead(true);
				menu.show(p);
			} else {
				p.removeAttachment(co.permC);
			}
		}
	}
	
	@EventHandler
	public void getPacket(DataPacketReceiveEvent e) {
		Player p = e.getPlayer();
		DataPacket pk = e.getPacket();
		if(pk instanceof InventoryTransactionPacket) {
			InventoryTransactionPacket packet = (InventoryTransactionPacket)pk;
			if(PlayerControl.getInstance().isControlling(p)) {
				Player victim = PlayerControl.getInstance().getControlByController(p).getVictim();
				switch(packet.transactionType) {
				case InventoryTransactionPacket.TYPE_USE_ITEM:
						UseItemData data = (UseItemData)packet.transactionData;
						BlockVector3 vec = data.blockPos;
						switch(data.actionType) {
						case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_AIR:
							PlayerInteractEvent ev = new PlayerInteractEvent(victim,p.getInventory().getItemInHand(),p.getDirectionVector(),data.face,Action.RIGHT_CLICK_AIR);
							p.getServer().getPluginManager().callEvent(ev);
							break;
						}
					break;
				}
			}
		}
	
	}
	
	@EventHandler
	public void onMotion(PlayerAnimationEvent e) {
		Player p = e.getPlayer();
		if(PlayerControl.getInstance().isUnderControll(p)) {
			e.setCancelled(true);
		} else if(PlayerControl.getInstance().isControlling(p)) {
			e.setCancelled();
			AnimatePacket pk = new AnimatePacket();
			pk.eid = PlayerControl.getInstance().getControlByController(p).getVictim().getId();
			pk.action = e.getAnimationType();
			p.getServer();
			Server.broadcastPacket(PlayerControl.getInstance().getControlByController(p).getVictim().getViewers().values(),pk);
		}
	}
	
	@EventHandler
	public void breakB(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if(PlayerControl.getInstance().isUnderControll(p)) {
			e.setCancelled(true);
		} 
	}
	
	@EventHandler
	public void placeB(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(PlayerControl.getInstance().isUnderControll(p)) {
			e.setCancelled(true);
		} 
	}
	
	@EventHandler
	public void q(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(PlayerControl.getInstance().isUnderControll(p)) {
			PlayerControl.getInstance().getControlByVictim(p).end();
		} else if(PlayerControl.getInstance().isControlling(p)) {
			PlayerControl.getInstance().getControlByController(p).end();
		}
	}
	
	@EventHandler
	public void c(PlayerChatEvent e) {
		Player p = e.getPlayer();
		if(PlayerControl.getInstance().isUnderControll(p)) {
			e.setCancelled(true);
		} else if(PlayerControl.getInstance().isControlling(p)) {
			PlayerControl.getInstance().getControlByController(p).chat(e);
			e.setCancelled(true);
		}
	}

}
