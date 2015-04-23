package me.legault.LetItRain;

public class Coordinate {
	public final double x, y, z;
	public final String name, world;
	
	public Coordinate(String name, String world, double x, double y, double z){
		this.name = name;
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public boolean hasName(String name){
		return this.name.equalsIgnoreCase(name);
	}
	
	public String toString(){
		return world + " :: " + name + " (" + x + ", " + y + ", " + z + ")";
	}
}
