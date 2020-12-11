package MeoMap;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PImage;

/*
 * This class reads the _targets.csv file, the shipcon.csv file and the ship coordinate files
 * to create a list of targets.
 */
public class TargetList {
	public String coordinates; //originally in iodata/coordinates
	public String current; //originally in 
	public List<TargetMarker> targets = new ArrayList<TargetMarker>();
	public PImage img;
	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	public List<String> beamplan = new ArrayList<String>();
	public List<TargetMarker> offpointed_ships = new ArrayList<TargetMarker>();
	public TargetList(String coordinates, String current, PImage img) {
		this.coordinates = coordinates;
		this.current = current;
		this.img = img;
	}
	private Float convertLongitude(Float lon) {
		if(lon < 180) {
			return lon;
		}
		else {
			return lon - 360;
		}
	}
	public void showTargetsBySatellite(String sat) throws Exception{
		Scanner sc1 = new Scanner(new File(current + "M001" + "//" + "M001" + "_beamplan.csv"));
		while(sc1.hasNextLine()) {
			String data[] = sc1.nextLine().split(",");
			if(data.length >= 7) {
				String target = data[6];
				beamplan.add(target);
			}
		}
		sc1.close();
	}
	//read the _targets.csv file, and create a list of targets
	public void createTargets() throws Exception {
		Scanner sc1 = new Scanner(new File(current + "//M001//M001_targets.csv"));
		Scanner sc2 = new Scanner(new File(current + "M001" + "//" + "M001" + "_beamplan.csv"));
		while(sc2.hasNextLine()) {
			String data[] = sc2.nextLine().split(",");
			if(data.length >= 7) {
				String target = data[6];
				beamplan.add(target);
			}
		}
		sc2.close();
		boolean first = true;
		//Start with the second line of the text. Create TargetMarker objects and add them to the list.
		while(sc1.hasNextLine()) {
			String data[] = sc1.nextLine().split(",");
			if(first == false) {
				//The Target is either a ship or a fixed customer
				
				if(data.length <= 6) { 
					//ID, Long_name, Lon, Lat, 
					
					if(beamplan.contains(data[1])) {
						targets.add(new TargetMarker(data[0], data[1], new Location(Float.parseFloat(data[2]), convertLongitude(Float.parseFloat(data[3]))), false, img));
					}

					}
				//The target is a TT&C gateway. i.e. It comes with AOS and LOS and EL data. 
				else if(data.length >= 7) {
					if(data[5].equals("T")) {
						targets.add(new TargetMarker(data[0], data[1], new Location(Float.parseFloat(data[2]), convertLongitude(Float.parseFloat(data[3]))), true, img));
					}

				}


			}
			first = false;
		}
		sc1.close();
	}
	//go through the shipConf.txt file, and mark all the targets that are ships
	public void markShips() throws Exception{
		Scanner sc1 = new Scanner(new File(coordinates + "\\conf\\shipConf.txt"));
		while(sc1.hasNextLine()) {
			String line = sc1.nextLine();
			String data[] = line.split(",");
			
			//If the current ship ID is in the targetList, mark this target as a ship.
			for(TargetMarker t1: targets) {
				if(t1.targetid.equals(data[1])) {
					t1.isship = true;
					t1.isgateway = false;
					t1.shortname = data[0]; 
					if(line.contains("T")) {
						t1.hastestflag = true;
					}
					else {
						t1.hastestflag = false;
					}
					t1.setHidden(false);
					
				}
	
			}
		}
		sc1.close();
	}
	//read the ships coordinate.csv data to set the current ship position
	public void upDateShips() throws Exception {
		
		LocalDateTime now = LocalDateTime.now();
		offpointed_ships.clear();
		for(TargetMarker t:targets) {
			if(t.isship) {
				Scanner sc1 = new Scanner(new File(coordinates + "\\" + t.shortname + ".csv"));
				//System.out.println(coordinates + "\\" + t.shortname  + "_ORIG.csv");
				if(new File(coordinates + "\\" + t.shortname  + "_ORIG.csv").exists()) {
					offpointed_ships.add(t);
					Scanner sc2 = new Scanner(new File(coordinates + "\\" + t.shortname  + "_ORIG.csv"));
					while(sc2.hasNextLine()) {
						String old_coordinates[] = sc2.nextLine().split(",");
						t.old_coordinates = "LAT: " + old_coordinates[1] + " LON: " + old_coordinates[2];
						
					}
					sc2.close();
					t.isoffpointed = true;
				}
				if(t.shortname.contains("BEAM")) {
					//System.out.println("This is a shared capacity service! ");
					t.isshared = true;
				}
				while(sc1.hasNextLine()) {
					String data[] = sc1.nextLine().split(",");
					t.setLocation(new Location(Float.parseFloat(data[1]), Float.parseFloat(data[2])));
					LocalDateTime lastupdate = LocalDateTime.parse(data[0], formatter);
					Duration age = Duration.between(lastupdate, now);
					t.setAge(age.toMinutes());
					
				}
				sc1.close();
				
			}
		}	
		
	}
	public void addRegions(RegionMarker region_a, RegionMarker region_b) {
		for(TargetMarker t:targets) {
			t.setRegions(region_a, region_b);
		}
	}
}
