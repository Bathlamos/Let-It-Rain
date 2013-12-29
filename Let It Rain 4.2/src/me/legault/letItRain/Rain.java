package me.legault.letItRain;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Pattern;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

public class Rain implements CommandExecutor{
	
	public static HashMap<Entity, Boolean> thrownedItems = new HashMap<Entity, Boolean>();
	public static HashMap<Integer, Integer> runningTasks = new HashMap<Integer, Integer>();
	
	public boolean onCommand(final CommandSender sender, Command cmd, String label,  String[] args){
		boolean isAmountInit = false;
		boolean isTime = false;
		boolean isOnFire = false;
		int amount = LetItRain.dAmount, radius = LetItRain.dRadius;
		String targetName = null;
		Location targetLocation = null;
		EntityType obj = null;
		PotionType potion = null;
		Material mat = null;
		
		//Permissions
		if (!sender.hasPermission("LetItRain.rain"))
			return true;
		
		//Firerain command
		if (label.equalsIgnoreCase("firerain"))
			isOnFire = true;
		
		//First parameter -animal- (String, int) is required
		if (args == null || args.length == 0){
			displayHelp(label, sender);
			return true;
		}
		
		//Check if the user tries to add/remove a command
		if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete"))
			return addRemoveCoordinates(sender, args);
		
		obj = findEntity(args[0]);
		
		if(LetItRain.rainPotions && (args[0].startsWith("potion:") || args[0].startsWith("potions:"))){
			if(args[0].startsWith("potion:"))
				potion = findPotion(args[0].substring("potion:".length()));
			else
				potion = findPotion(args[0].substring("potions:".length()));
		}
		
		if (potion == null && LetItRain.rainBlocks)
			mat = findMaterial(args[0]);
		
		if (obj == null && mat == null && potion == null){
			Resources.privateMsg(sender, "Please enter a valid entity/material id or name");
			if (!LetItRain.rainBlocks)
				Resources.privateMsg(sender, "Blocks have been disabled ");
			return true;
		}
		
		//Parse remaining arguments
		int recognizedParams = 0;
		for (int i = 1; i < args.length; i++){
			
			//Parse player/target
			if (targetName == null){
				
				//Find programmable target
				Coordinate c = null;
				for(Coordinate f: LetItRain.coordinates)
					if(f.hasName(args[i])){
						c = f;
						break;
					}
				
				if(c != null){
					targetName = args[i];
					World w = LetItRain.server.getWorld(c.world);
					targetLocation = new Location(w, c.x, c.y, c.z);
				}else{
					//Find player
					Player target = Resources.isPlayer(args[i]);
					if(target != null){
						targetName = target.getDisplayName();
						targetLocation = target.getLocation();
					}
				}
				if (targetName != null)
					recognizedParams++;
			}
			
			//Help
			if (args[i].equalsIgnoreCase("help")){
				displayHelp(label, sender);
				return true;
			}
			
			//Parse amount and radius
			try{
				if(args[i].endsWith("s") && !isAmountInit){
					isTime = true;
					args[i] = args[i].substring(0, args[i].length() - 1);
				}
				int holder = Integer.parseInt(args[i]);
				recognizedParams++;
				if (!isAmountInit){
					amount = holder;
					isAmountInit = true;
				}else
					radius = holder;
			}catch(NumberFormatException e){}
		}
		
		//Parameter not recognized
		if (recognizedParams != args.length - 1){
			Resources.privateMsg(sender, "One or more of your parameters were not recognized");
			return false;
		}
		
		//Impossible parameters
		if (radius < 1 || amount < 1){
			Resources.privateMsg(sender, "Send at least one entity with a radius of at least 1");
			return true;
		}else if (amount > LetItRain.maxAmount){
			amount = LetItRain.maxAmount;
			Resources.privateMsg(sender, "The maximum entities allowed is " + LetItRain.maxAmount);
			return true;
		}
		
		//Max radius
		if(isTime && radius > LetItRain.maxRadius){
			Resources.privateMsg(sender, "The maximum radius is " + LetItRain.maxRadius);
			return true;
		}
		
		//Returns false if console forgot name as parameter
		if(targetName == null && isNotPlayer(sender))
			return true;
		
		//Defaults
		if (targetName == null){
			targetName = ((Player) sender).getDisplayName();
			targetLocation = ((Player) sender).getLocation();
		}
		
		//Test whether the animal is blacklisted
		if (obj != null){
			try{
				if (LetItRain.config.getBoolean("LetItRain.rain.blacklist." + obj.getEntityClass().getSimpleName())){
					Resources.privateMsg(sender, "The entity you chose has been blacklisted");
					return true;
				}
			}catch(Exception e){
				Resources.privateMsg(sender, "An unknow exception has occured with your config file. Please try again.");
				return true;
			}
		}
		
		final long initTime = System.currentTimeMillis();
		Random rdm = new Random();
		final int myTaskIdentifier = rdm.nextInt();
		final PotionType fPotion = potion;
		final Location fLocation = targetLocation;
		final Material fMat = mat;
		final int fRadius = radius, fAmount = amount;
		final EntityType fObj = obj;
		final boolean fIsOnFire = isOnFire;
		
		if((!LetItRain.rainLava && mat == Material.LAVA) || (!LetItRain.rainWater && mat == Material.WATER)){
			World w = targetLocation.getWorld();
			if(recognizedParams == 1)
				radius = amount;
			for(int i = -radius; i < radius; i++){
				double boundary = Math.sqrt(Math.pow(radius, 2) - Math.pow(i, 2));
				for(int j = -(int)boundary; j < boundary; j++)
					w.getBlockAt(new Location(targetLocation.getWorld(), targetLocation.getX() + i, targetLocation.getY() + 50, targetLocation.getZ() + j)).setType(mat);
			}
		}else if(isTime){
			@SuppressWarnings("deprecation")
			int id = LetItRain.server.getScheduler().scheduleAsyncRepeatingTask(LetItRain.plugin, new Runnable(){

				@Override
				public void run() {
					if(!spawnEntities(fLocation, fObj, sender, fMat, fPotion, (int)(Math.max(0.125 * Math.pow(fRadius,  2), 100)), fRadius, fIsOnFire) || 
							System.currentTimeMillis() - initTime > Math.max(fAmount * 1000 - 7000, 1000))
						StopScheduler(myTaskIdentifier);
				}
				
			}, 0L,  20); // There are 20 server ticks per second
			runningTasks.put(myTaskIdentifier, id);
		}else{
			boolean res = spawnEntities(targetLocation, obj, sender, mat, potion, amount, radius, fIsOnFire);
			if(!res)
				return true;
		}
		
		String name = "";
		
		if (obj != null)
			name = obj.getEntityClass().getSimpleName();
		else if(potion != null)
			name = potion.name() + " potion";
		else
			name = mat.name();
		
		name = name.replaceAll("_", " ").toLowerCase();
		
		if(amount > 1)
			name = toPlural(name);
		
		displayMsg(targetName, name, isOnFire);
		
		return true;
	}
	
	private static boolean spawnEntities(Location location, EntityType obj, CommandSender sender, Material mat, 
			PotionType potionType, int amount, int radius, boolean isOnFire){
		
		Location newLoc;
		Random rdm = new Random();
		
		try{
			//Spawn entity
			for (int i = 0; i < amount; i++){
				newLoc = location.clone();
				newLoc.setX(location.getX()+(double)rdm.nextInt(radius*2)-(double)radius);
				newLoc.setY(location.getY()+(double)rdm.nextInt(250)+100.0);
				newLoc.setZ(location.getZ()+(double)rdm.nextInt(radius*2)-(double)radius);
				
				if (obj != null){
					Entity creature = location.getWorld().spawn(newLoc, obj.getEntityClass());
					thrownedItems.put(creature, isOnFire);
					
					if (creature instanceof Fireball)
						((Fireball)creature).setDirection(new Vector(0, -1, 0));
					if (creature instanceof ExperienceOrb)
						((ExperienceOrb) creature).setExperience(1000 + (int)rdm.nextFloat()*300);
					if (creature instanceof TNTPrimed)
						((TNTPrimed) creature).setFuseTicks(150);
					if (isOnFire)
						creature.setFireTicks(1000 + (int)rdm.nextFloat()*300);
				}else{
					ItemStack is;
					if(potionType != null)
						is = new Potion(potionType).toItemStack(1);
					else
						is = new ItemStack(mat);
					location.getWorld().dropItem(newLoc, is);
					
				}
			}
		}catch(Exception e){
			Resources.privateMsg(sender, "This entity or world is invalid");
			return false;
		}
		return true;
	}
	
	private EntityType findEntity(String token){
		
		token = toSingular(token);
		
		for(EntityType o: EntityType.values()){
			String name = o.getName() == null ? "": o.getName();
			String simpleName = o.getEntityClass() == null || o.getEntityClass().getSimpleName() == null ? "": o.getEntityClass().getSimpleName();
			
			if(toSingular(simpleName).equalsIgnoreCase(token) ||
					toSingular(name).equalsIgnoreCase(token))
					return o;
		}
		return null;
	}
	
	private Material findMaterial(String token){
		
		try{
			int id = Integer.parseInt(token);
			return Material.getMaterial(id);
		}catch(NumberFormatException e){
			for (Material o: Material.values())
				if (o != Material.AIR && toSingular(o.name()).equalsIgnoreCase(token))
					return o;
		}
		return null;
	}
	
	private PotionType findPotion(String token){
		
		token = token.replaceAll("[(potion|instant)_ ]", "").toLowerCase();
		
		for(PotionType o: PotionType.values())
			if(o.name().replaceAll("[(POTION|INSTANT)_ ]", "").equalsIgnoreCase(token))
				return o;
		return null;
	}
	
	private static void StopScheduler(int id){
		Integer i = runningTasks.remove(id);
		if(i != null)
			LetItRain.server.getScheduler().cancelTask(i);
	}

	/**
	 * Display help
	 */
	private void displayHelp(String label, CommandSender sender){
		Resources.privateMsg(sender, "/" + label + " <entity> <player> <amount> <radius>");
		Resources.privateMsg(sender, "All parameters optional except entity. Order can be changed except for amount and radius");
	}
	
	/**
	 * Substitute [player] and [animal] and display the msg
	 */
	private void displayMsg(String target, String animal, boolean isOnFire){
		String msg;
		if (isOnFire)
			msg = LetItRain.dFirerainMsg;
		else
			msg = LetItRain.dRainMsg;
		
		msg = msg.replaceAll(Pattern.quote("[entity]"), animal.toLowerCase());
		msg = msg.replaceAll(Pattern.quote("[player]"), target);
		
		if(!msg.isEmpty())
			Resources.broadcast(msg);
	}
	
	/**
	 * Grammar: returns the singular lower case version of a word
	 */
	private static String toSingular(String word){

		word = word.toLowerCase();
		
		if(!word.equals("zombies") || !word.equals("slimes")){

			if(word.matches(".*ives$"))
				return word.substring(0, word.length() - 3) + "fe";
			if(word.matches(".*ves$"))
				return word.substring(0, word.length() - 3) + "f";
			if(word.matches(".*men$"))
				return word.substring(0, word.length() - 3) + "man";
			if(word.matches(".*ies$"))
				return word.substring(0, word.length() - 3) + "y";
			if(word.matches(".*es$"))
				return word.substring(0, word.length() - 2);
			
		}
		if(word.matches(".*[^s]s$"))
			return word.substring(0, word.length() - 1);
		return word;
	}
	
	/**
	 * Grammar: returns the plural lower case version of a word
	 */
	private static String toPlural(String word){
		
		word = word.toLowerCase();
		
		if(word.equals("lava") || word.equals("water"))
			return word;

		if(word.matches(".*[sxz(ch)(sh)]$"))
			return word + "es";
		if(word.matches(".*o$"))
			return word + "es";
		if(word.matches(".*f$"))
			return word.substring(0, word.length() - 1) + "ves";
		if(word.matches(".*man$"))
			return word.substring(0, word.length() - 3) + "men";
		if(word.matches(".*ife$"))
			return word.substring(0, word.length() - 3) + "ives";
		if(word.matches(".*y$"))
			return word.substring(0, word.length() - 1) + "ies";
		return word + "s";
	}
	
	private static boolean addRemoveCoordinates(CommandSender sender, String[] args){
		File coordFile = new File("plugins" + File.separator + "LetItRain" + File.separator + "coordinates.yml");
		FileConfiguration coords = YamlConfiguration.loadConfiguration(coordFile);
		
		if(!sender.hasPermission("LetItRain.rain.coordinates")){
			Resources.privateMsg(sender, "You do not have permission to execute this command");
			return true;
		}
		
		if(args.length != 2){
			Resources.privateMsg(sender, "/rain add <location_name>");
			return true;
		}
		if(isNotPlayer(sender))
			return true;
		
		Player p = (Player) sender;
		Location l = p.getLocation();
		
		if(args[0].equals("add")){
			coords.set("LetItRain." + p.getWorld().getName() + "." + args[1], l.getX() + " " + l.getY() + " " + l.getZ());
			if(!LetItRain.coordinates.add(new Coordinate(args[1], p.getWorld().getName(), l.getX(), l.getY(), l.getZ())))
				Resources.privateMsg(sender, "The command has failed. It is likely that a location with the same name already exists");
			else
				Resources.privateMsg(sender, "The coordinate has been added");
		}else{
			if(coords.get("LetItRain." + p.getWorld().getName() + "." + args[1]) != null){
				coords.set("LetItRain." + p.getWorld().getName() + "." + args[1], null);
				Resources.privateMsg(sender, "The coordinate has been removed");
			}else
				Resources.privateMsg(sender, "The coordinate does not exist");
		}
		try {
			coords.save(coordFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	private static boolean isNotPlayer(CommandSender sender){
		if (!(sender instanceof Player)){
			Resources.privateMsg(sender, "You cannot use this command from the console without specifying a player name");
			return true;
		}
		return false;
	}
}
