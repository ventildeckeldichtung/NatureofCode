package MeoMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractMarker;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;


public class TargetMarker extends AbstractMarker{
	public String targetid; //a unique target number
	public String name; //The long name of the target
	public String type; //ship, TT&C or fixed customer
	public long age = 0; //how old are the coordinates? only applicable for ships
	public boolean isoffpointed = false;
	public String old_coordinates = " ";
	public boolean hastestflag = false; 
	public boolean isgateway = false;
	public boolean isship = false;
	public boolean isshared = false;
	public String shortname = "";
	public PImage img;
	public RegionMarker region_a = null;
	public RegionMarker region_b = null;
	public TargetMarker(String targetid, String name, Location location, boolean isgateway, PImage img) {
		this.targetid = targetid;
		this.name = name;
		this.shortname = name;
		this.setId(targetid);
		this.setLocation(location);
		this.isgateway = isgateway;
		this.img = img;
		if(this.isgateway) {
			this.setHidden(false);
		}
		else {
			this.setHidden(true);
		}
	}
	public void updateLocation(Location location) {
		this.setLocation(location);
	}
	//set the age of the coordinates
	public void setAge(long age) {
		this.age = age;
	}
	public void setRegions(RegionMarker region_a, RegionMarker region_b) {
		this.region_a = region_a;
		this.region_b = region_b;
	}
	@Override
	public void draw(PGraphics pg, float x, float y) {
		// TODO Auto-generated method stub
		if(!this.isHidden()) {
			pg.pushStyle();
			pg.imageMode(PConstants.CORNER);
			if(this.isSelected()) {
				this.showTitle(pg, x, y);
			}
			if(this.isship) {
				
				pg.textSize(12);
				if(this.age <= 10) {
					pg.tint(0,255,0);
				}
				else if(this.age > 10 && this.age <= 30) {
					pg.tint(100,23,55);
				}
				else {
					pg.tint(180,0,0);
				}
				pg.fill(99,0,0);
				if(this.hastestflag) {
					pg.text(this.shortname + " T",x,y);
				}
				if(this.isoffpointed) {
					pg.text(this.shortname + " Off Pointed " ,x,y);
				}
				else {
					pg.text(this.shortname,x,y);
				}
				
				pg.image(img,x,y);
			}
			else if (this.isgateway) {
				pg.fill(150, 30, 30);
				pg.ellipse(x, y, 8, 8);
				pg.textSize(12);
				pg.text(this.name,x ,y - 8);
			}
			else {
				
				pg.fill(150, 30, 30);
				pg.ellipse(x, y, 8, 8);
				pg.textSize(12);
				pg.text(this.name,x ,y - 8);
			}
			pg.popStyle();
		}
	}
	
	public void showTitle(PGraphics pg, float x, float y)
	{
		pg.pushStyle();
		pg.textSize(12);
		pg.fill(255,255,255);
		pg.rect(x-5,y-5,280,50,5);
		pg.fill(0,0,0);
		if(this.isship) {
			if(this.isoffpointed) {
				pg.text("LON: " + this.getLocation().getLon() + " LAT: " + this.getLocation().getLat(),x +19,y +20);
				pg.text("Age: " + this.age,x + 19,y + 10);
				pg.text("Old : " + this.old_coordinates, x + 19, y + 30);
			}
			else {	
				pg.text("LON: " + this.getLocation().getLon() + " LAT: " + this.getLocation().getLat(),x +19,y +10);
				pg.text("Age: " + this.age,x + 15,y + 25);
			}
		}
		else if(!this.isgateway){
			pg.text("LON: " + this.getLocation().getLon() + " LAT: " + this.getLocation().getLat(),x +19,y + 15);
		}
		pg.popStyle();
	}
	@Override
	protected boolean isInside(float checkX, float checkY, float x, float y) {	
		return checkX > x && checkX < x + 10 && checkY > y && checkY < y + 10;
	}
	@Override
	public String toString() {
		if(this.isgateway) {
			return "Gateway: " + this.name + this.getId();
		}
		else {
			if(this.isship) {
				return "Ship " + this.shortname + this.getId();
			}
			else {
				return "Target " + this.name + this.getId() + " " + this.age;
			}
		}
	}
}
