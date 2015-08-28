package me.legault.LetItRain;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class LetItRainHelp implements CommandExecutor{
	
	public LetItRainHelp(LetItRain plugin){}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label,  String[] args) {
		Resources.privateMsg(sender, "________________ " + LetItRain.plugin.getDescription().getFullName() + " ________________");
		Resources.privateMsg(sender, "/rain <creature/item> <amount/duration> <radius> <player/coordinate name>");
		Resources.privateMsg(sender, "/firerain <creature> <amount/duration> <radius> <player/coordinate name>");
		Resources.privateMsg(sender, "/rain add <coordinate name>");
		Resources.privateMsg(sender, "/rain remove <coordinate name>");
		Resources.privateMsg(sender, "/zeus");
		Resources.privateMsg(sender, "/strike <player>");
		Resources.privateMsg(sender, "/launcher");
		Resources.privateMsg(sender, "/slaughter <radius> <x> <y> <z> <world>");
		Resources.privateMsg(sender, "/removeItems <radius> <x> <y> <z> <world>");				
		return true;
	}
	
}
