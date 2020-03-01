package ru.ragnok123.playerControl;

import java.util.ArrayList;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

public class PlayerControlCommand extends Command {

	public PlayerControlCommand(String name) {
		super(name);
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		Player p = (Player) sender;
		if (p.hasPermission("pc.command")) {
			if (args.length == 0) {
				p.sendMessage("§l§eUsage: §c/pc <player> <take/stop>");
			} else {
				if (!args[0].isEmpty()) {
					Player t = p.getServer().getPlayerExact(args[0]);
					if (t == null) {
						p.sendMessage("§e[PlayerControl] §cPlayer §b" + args[0] + " §cis offline");
					} else {
						if (t.getName().equals(p.getName())) {
							p.sendMessage("§e[PlayerControl] §cCannot control yourself");
						} else {
							if (!args[1].isEmpty()) {
								ArrayList<Control> l = PlayerControl.getInstance().controlsEnabled;
								switch (args[1]) {
								case "take":
									if (PlayerControl.getInstance().isControlling(p)) {
										p.sendMessage("§e[PlayerControl] §cYou already controlling someone");
									} else {
										Control c = new Control(p, t);
										if (!PlayerControl.getInstance().controlsEnabled.contains(c)) {
											c.start();
											PlayerControl.getInstance().controlsEnabled.add(c);
										}
										p.sendMessage("§e[PlayerControl] §aYou took control over §b" + t.getName());
									}
									break;
								case "stop":
									Control c1 = PlayerControl.getInstance().getControlByController(p);
									if (c1 == null) {
										return false;
									} else {
										c1.end();
										p.sendMessage("§e[PlayerControl] §aYou ended control journey with §b"+t.getName());
									}
									break;
								}
							}
						}
					}
				}
			}
		} else {
			p.sendMessage("§e[PlayerControl] §cYou don't have permissions");
		}
		return false;
	}

}
