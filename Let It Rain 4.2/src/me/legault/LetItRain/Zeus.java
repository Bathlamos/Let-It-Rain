package me.legault.letItRain;

import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Zeus implements CommandExecutor{
	
	public Zeus(LetItRain plugin){}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label,  String[] args){
		
		Player player = null;
		if (!(sender instanceof Player))
			Resources.privateMsg(sender, "Only a player can execute this command");
		else{
			//Permissions
			if (!sender.hasPermission("LetItRain.zeus"))
				return true;
			
			
			player = (Player)sender;
			PlayerInventory inventory = player.getInventory();
			
			inventory.addItem(new ItemStack(LetItRain.itemZeus));
			
			String outputMsg = LetItRain.dZeusMsg;
			outputMsg = outputMsg.replaceAll(Pattern.quote("[player]"), player.getName());
			if(!outputMsg.isEmpty())
				Resources.broadcast(outputMsg);
		}
		
		return true;
	}
}
