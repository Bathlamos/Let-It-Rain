package me.legault.LetItRain;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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
		
		if (args.length <= 1 && !(sender instanceof Player)){
			Resources.privateMsg(sender, "This is the command to use from console:");
			Resources.privateMsg(sender, "/slaughter(or /removeitems) <radius> <x> <y> <z> <World>");
			return true;
		}
		
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
				
		Location origin;
		
		//Slaughter location: /slaughter <radius> <x> <y> <z> <world>
		if (args.length == 5){
			int x = 0;
			int y = 0;
			int z = 0;
			World w = Bukkit.getWorld(args[4]);
			if (w == null){
				Resources.privateMsg(sender, "This world doesn't exist or is not loaded");
				return true;
			}
			try{
				x = Integer.parseInt(args[1]);
				y = Integer.parseInt(args[2]);
				z = Integer.parseInt(args[3]);
				origin = new Location(w, x, y, z);
			}catch (NumberFormatException e){
				Resources.privateMsg(sender, "The coordinates need to be x, y and z integer numbers");
				Resources.privateMsg(sender, "Usage: /slaughter(or /removeitems) <radius> <x> <y> <z> <World>");
				return true;
			}
		} else {
			origin = ((Player)sender).getLocation();
		}	
		
		int num = 0;
		
		//Removeitens: /removeitens <radius> <x> <y> <z> <world>
		if (cmd.getName().equalsIgnoreCase("removeItems")){
			
			//Permissions
			if (!sender.hasPermission("LetItRain.removeItems"))
				return false;
			
			List<Entity> p = null;  
			if (args.length == 5){
				p = Bukkit.getWorld(args[4]).getEntities();
			} else {
				p = ((Player)sender).getWorld().getEntities();
			}
			for (Entity ent: p){
				if (ent.getLocation().distance(origin) <= radius){
					if (ent instanceof Item || ent instanceof Arrow || (ent instanceof ExperienceOrb && ent.getType().equals(EntityType.EXPERIENCE_ORB))){
						ent.remove();
						num++;
					}
						
				}
			}
			
			switch(num){
				case 0: Resources.privateMsg(sender, "No items have been removed");	break;
				case 1: Resources.privateMsg(sender, num + " item have been removed"); break;
				default: Resources.privateMsg(sender, num + " items have been removed"); break;
			}
			
		}else{
			
			//Permissions
			if (!sender.hasPermission("LetItRain.slaughter"))
				return false;
			
			List<LivingEntity> p = null;  
			if (args.length == 5){
				p = Bukkit.getWorld(args[4]).getLivingEntities();
			} else {
				p = ((Player)sender).getWorld().getLivingEntities();
			}
			for (LivingEntity ent: p){
				if (ent.getLocation().distance(origin) <= radius && !(ent instanceof Player)){
					ent.setHealth(0);
					num++;
				}
			}
			switch(num){
				case 0: Resources.privateMsg(sender, "No creature has been slaughtered");	break;
				case 1: Resources.privateMsg(sender, num + " creature has been slaughtered"); break;
				default: Resources.privateMsg(sender, num + " creatures have been slaughtered"); break;
		}
			
		}
		
		return true;
		
	}

}
