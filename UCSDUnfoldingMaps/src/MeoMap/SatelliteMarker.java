package MeoMap;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractMarker;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

public class SatelliteMarker extends AbstractMarker{
	public boolean isspare;
	public boolean ispaired;
	public PImage img;
	public RegionMarker region_a;
	public RegionMarker region_b;
	public String time_left_a;
	public String time_left_b;
	public int pos;
	public List<List<String>> beamplan = new ArrayList<List<String>>();
	//private LocalTime time = LocalTime.now();
	private Location reference_location;
	private LocalTime reference = LocalTime.parse("10:15:24");
	public SatelliteMarker(Location location, boolean isspare, boolean ispaired, String id, PImage img, int pos) {
		this.setLocation(location);
		this.reference_location = location;
		this.isspare = isspare;
		this.ispaired = ispaired;
		this.setId(id);
		this.img = img;
		this.pos = pos;
		this.region_a = new RegionMarker(new Location(0,0), new Location(0,0), new Location(0,0), new Location(0,0), " ");
		this.region_b = new RegionMarker(new Location(0,0), new Location(0,0), new Location(0,0), new Location(0,0), " ");
	}
	//Take the current time to update the satellite position. The reference is always at 10:15:24
	//Get the number of minutes away from 10:15 to calculate the current position
	public Float convertLongitude(Float lon) {
		if(lon <= 180) {
			return lon;
		}
		else {
			return lon - 360;
		}
	}
	/*
	 * Update the satellite position according to the current time, the initial position and the 
	 * reference time.
	
	 */

	public void updatePosition() {
		LocalTime time = LocalTime.now();
		float timeminutes = time.getHour() * 60 + time.getMinute() + time.getSecond()/(float)60.0;
		float referenceminutes = reference.getHour() * 60 + reference.getMinute()+ reference.getSecond()/(float)60.0;	
		float tzero = referenceminutes - reference_location.getLon();
		float location_new;
		if(timeminutes < tzero) {
			location_new = 360 - ((tzero - timeminutes)%360);
		}
		else {
			location_new = (timeminutes - tzero)%360;
		}
		this.setLocation(0, convertLongitude(location_new));
	}
	//This updates the time the satellite has left in a given region
	public void updateTimeLeft() {
		
		Float time_a = Math.abs(this.getLocation().getLon() - this.region_a.stop_lon);
		Float time_b = Math.abs(this.getLocation().getLon() - this.region_b.stop_lon);
		
		int minutes_a = time_a.intValue();
		int minutes_b = time_b.intValue();
		int seconds_a = (int)((time_a - time_a.intValue())*60);
		int seconds_b = (int)((time_b - time_b.intValue())*60);
		
		this.time_left_a = minutes_a + ":" + seconds_a;
		this.time_left_b = minutes_b + ":" + seconds_b;
	}
	@Override
	public void draw(PGraphics pg, float x, float y) {
		pg.pushStyle();
		pg.imageMode(PConstants.CENTER);
		
		
		if(this.isspare) {
			pg.tint(100,23,55);
			pg.fill(130, 23, 55);
			pg.textSize(16);
			pg.text(id + "(spare)", x, y + 38);
			pg.image(img, x , y + 5);
		}
		else if(this.ispaired) {
			pg.tint(0,233,0);
			pg.fill(0, 0, 0);
			pg.textSize(16);
			pg.text(id + ("active paired"), x, y - 38);
			pg.image(img, x , y + pos * 2);
			
		}
		else {
			if(pos == 0) {
				pg.tint(0,233,0);
				pg.fill(0, 0, 0);
				pg.textSize(16);
				pg.text(id + ("active"), x, y - 15);
			}
			else if(pos == 1) {
				pg.tint(0,233,0);
				pg.fill(0, 0, 0);
				pg.textSize(16);
				pg.text(id + ("active"), x, y + 15);
			}
			else if(pos == 2) {
				pg.tint(0,233,0);
				pg.fill(0, 0, 0);
				pg.textSize(16);
				pg.text(id + ("active"), x, y - 15);
			}
			pg.image(img, x -5 , y + 5);
		}
		if(this.isSelected()) {
			pg.text(this.getLocation().getLon(),x,y + 10);
		}
		
		pg.popStyle();
	}
	@Override
	protected boolean isInside(float checkX, float checkY, float x, float y) {
		if((checkX >= (x - img.width/2) && checkX <= (x + img.width/2))) {
			if((checkY >= (y - img.height/2) && checkY <= (y + img.height/2))) {
				
				return true;
			}
			else {
				return false;
			}
						
		}
		else {
			return false;
		}
	}
	@Override
	public String toString() {
		return (id + " " + location + " " + isspare);
	}


}
