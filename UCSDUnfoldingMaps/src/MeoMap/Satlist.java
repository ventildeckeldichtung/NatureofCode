package MeoMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.HashMap;

import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PImage;

/*
 * This Class takes the o3b central_config file to create a list of SatelliteMarker objects.
 */
public class Satlist{

	public HashMap<String, SatelliteMarker> satlist = new HashMap<String, SatelliteMarker>();
	public HashMap<Integer,SatelliteMarker> satlist_ordered = new HashMap<Integer, SatelliteMarker>();
	public Satlist() {

	}
	public Float convertLongitude(Float lon) {
		
		if(lon <= 180) {
			return lon;
		}
		else {
			return lon - 360;
		}
	}
	
	public void createSatlist(String path, PImage img) throws Exception {
		Scanner sc1 = new Scanner(new File(path)); 
		String satname = "";
		Float position = null;
		boolean isspare = false;
		boolean ispaired = false;
		int pos = 0;
		while(sc1.hasNextLine()) {
			String tmp = sc1.nextLine();
			if(!tmp.contains("#")) {
				
				if(tmp.contains("[M0")) {
					
					satname = tmp;
					satname = satname.replace("[", "");
					satname = satname.replace("]", "");
				}
				if(tmp.contains("POSITION ")) {
					position = Float.parseFloat(tmp.split(" = ")[1]);
					
				}
				if(tmp.contains("PAYLOAD_STATUS ")) {
					if(tmp.contains("spare")) {
						isspare = true;
					}
					else if(tmp.contains("active")) {
						isspare = false;
					}
				}
				if(tmp.contains("PAIRED_WITH")) {
					if(tmp.contains("NA")) {
						ispaired = false;
					}
					else {
						ispaired = true;
					}
					pos = (pos+1)%3;
					SatelliteMarker s = new SatelliteMarker(new Location(0, convertLongitude(position)), isspare, ispaired, satname, img, pos);
					satlist.put(satname, s);
					satlist_ordered.put(Integer.parseInt(satname.replaceAll("M", ""))-1,s);
				}
			}
		}
		sc1.close();
	}
	
	public void updateSatPosition() {
		for(SatelliteMarker s:satlist.values()) {
			s.updatePosition();
		}
	}


}
