package me.legault.LetItRain;

import java.util.regex.Pattern;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Zeus implements CommandExecutor{
	
	public Zeus(LetItRain plugin){}
	
	@SuppressWarnings("deprecation")
	public boolean onCommand(CommandSender sender, Command cmd, String label,  String[] args){
		
		Player player = null;
		if (!(sender instanceof Player))
			Resources.privateMsg(sender, "Only a player can execute this command");
		else{
			//Permissions
			if (!sender.hasPermission("LetItRain.zeus"))
				return true;
			
			
			player = (Player)sender;
			PlayerInventory inv = player.getInventory();
			Material mat = Material.getMaterial(LetItRain.itemZeus);
			ItemStack item = new ItemStack(mat);
			if (!inv.contains(mat) && inv.firstEmpty() != -1){                	
				inv.addItem(item);
				String outputMsg = LetItRain.dZeusMsg;
				outputMsg = outputMsg.replaceAll(Pattern.quote("[player]"), player.getName());
				if(!outputMsg.isEmpty()){
					Resources.broadcast(outputMsg);
				}
			}
		}
		return true;
	}
}
