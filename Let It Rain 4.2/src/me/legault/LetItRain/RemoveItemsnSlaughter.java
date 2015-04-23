package me.legault.LetItRain;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class RemoveItemsnSlaughter implements CommandExecutor{

	
	public RemoveItemsnSlaughter(LetItRain plugin){}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,  String[] args) {
		
		if (!(sender instanceof Player)){
			Resources.privateMsg(sender, "You must be a player to specify this command");
			return true;
		}
		
		Player player = (Player) sender;
		int radius = 100;
		
		if (args != null  && args.length > 0)
			try{
				radius = Integer.parseInt(args[0]);
			}catch (NumberFormatException e){
				Resources.privateMsg(sender, "The radius you specified is invalid");
				return true;
			}
		
		
		if (radius < 1){
			Resources.privateMsg(sender, "You must specify a radius greater than 0");
			return true;
		}
		
		Location origin = player.getLocation();
		int num = 0;
		
		if (cmd.getName().equalsIgnoreCase("removeItems")){
			
			//Permissions
			if (!sender.hasPermission("LetItRain.removeItems"))
				return false;
			
			List<Entity> p = player.getWorld().getEntities();
			for (Entity ent: p){
				if (ent.getLocation().distance(origin) <= radius){
					if (ent instanceof Item || ent instanceof Arrow || (ent instanceof ExperienceOrb && ent.getType() == EntityType.EXPERIENCE_ORB)){
						ent.remove();
						num++;
					}
						
				}
			}
			
			switch(num){
				case 0: Resources.privateMsg(player, "No items have been removed");	break;
				case 1: Resources.privateMsg(player, num + " item have been removed"); break;
				default: Resources.privateMsg(player, num + " items have been removed"); break;
			}
			
		}else{
			
			//Permissions
			if (!sender.hasPermission("LetItRain.slaughter"))
				return false;
			
			List<LivingEntity> p = player.getWorld().getLivingEntities();
			for (LivingEntity ent: p){
				if (ent.getLocation().distance(origin) <= radius && !(ent instanceof Player)){
					ent.setHealth(0);
					num++;
				}
			}
			switch(num){
				case 0: Resources.privateMsg(player, "No creature has been slaughtered");	break;
				case 1: Resources.privateMsg(player, num + " creature has been slaughtered"); break;
				default: Resources.privateMsg(player, num + " creatures have been slaughtered"); break;
		}
			
		}
		
		return true;
		
	}

}
