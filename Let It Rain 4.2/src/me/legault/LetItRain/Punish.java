package me.legault.LetItRain;

import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Punish implements CommandExecutor{
	
	public Punish(LetItRain plugin){}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label,  String[] args){
		LetItRain.server = sender.getServer();
		
		World world;
		Player target;
		String outputMsg = LetItRain.dPunishMsg;
		
		if (args != null && args.length > 0)
			target = Resources.isPlayer(args[0]);
		else{
			Resources.privateMsg(sender, "Please enter the name of the player you want to strike");
			return true;
		}
		
		if (target == null){
			Resources.privateMsg(sender, "Please enter a valid player name");
			return true;
		}
		
		//Permissions
		if (!sender.hasPermission("LetItRain.strike"))
			return true;

		
		Location location = target.getLocation(); 
		    
		world = target.getWorld();
			
		world.strikeLightning(location);
		target.setHealth(0);
		
		outputMsg = outputMsg.replaceAll(Pattern.quote("[player]"), target.getName());
		
		if(!outputMsg.isEmpty())
			Resources.broadcast(outputMsg);
		
		return true;
	}
}
