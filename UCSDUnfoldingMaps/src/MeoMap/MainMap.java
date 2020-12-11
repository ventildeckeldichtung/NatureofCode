package MeoMap;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import controlP5.*;

//Unfolding libraries
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.events.ZoomMapEvent;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractMarker;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
//Processing library
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
/**
 * 
 * @author Martin Hoffmann
 * This is the main class of the MEO ship monitoring tool. The tool is based on an open Source map library
 * called unfolding maps. This library uses the processing language to create interactive maps.
 *
 */


public class MainMap extends PApplet {

	private static final long serialVersionUID = 1L;
	UnfoldingMap map;
	public static String mbTilesString = "data/map1.mbtiles";
	//RegionMarker r1 = new RegionMarker(new Location(88,33), new Location(44,10), new Location(12,45), new Location(66,33), "R01B");
	public PImage satellite;
	public PImage boat;
	
	public String current = " ";
	public String coordinates = " ";
	public String central_config = " ";
	public RegionMarker current_region_a = null;
	public RegionMarker current_region_b = null;
	Satlist sat1 = new Satlist();
	TargetList t1;
	RegionList rl1;
	public List<String> targets_a = new ArrayList<String>();
	public List<String> targets_b = new ArrayList<String>();
	private SatelliteMarker lastclicked = null;
	ControlP5 cp5;
	DropdownList l1;
	private final int SCREENX = 1300;
	private final int SCREENY = 900;
	private boolean demoon = true;
	Timer timer = new Timer();
	
	/*
	 * the setup function is part of the processing library. It runs only once and is used to set up the map.
	 */
		
	
	public void setup() {
		size(displayWidth,displayHeight);
		satellite = loadImage("satellite2.png");
		boat = loadImage("boat2.png");
		frame.setResizable(true);
		
		try {
			Scanner sc1 = new Scanner(new File("data/config.txt"));
			while(sc1.hasNextLine()) {
				String content = sc1.nextLine();
				if(content.contains("current")) {
					current = content.split(" = ")[1];
				}
				if(content.contains("coordinates")) {
					coordinates = content.split(" = ")[1];
				}
				if(content.contains("central_config ")) {
					central_config = content.split(" = ")[1];
				}
			}
			sc1.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
		rl1 = new RegionList(current);
		t1 = new TargetList(coordinates, current, boat);
		try {
			sat1.createSatlist(central_config,satellite);
			t1.createTargets();
			rl1.addRegions();
			rl1.addRegionGateway();
			t1.markShips();
			t1.upDateShips();

		}
		catch(Exception e){
			System.out.println(e);
		}
		map = new UnfoldingMap(this,1,1,displayWidth + 300,displayHeight/2, new MBTilesMapProvider(mbTilesString));
		
		map.setZoomRange(2, 5);

		//map.setTweening(true);
		//map.setPanningRestriction(new Location(0,0),0);
		MapUtils.createDefaultEventDispatcher(this, map);
		map.zoomAndPanTo(2, new Location(30,150));
		for(RegionMarker rm1:rl1.regionlist) {
			map.addMarker(rm1);
		}
		for(RegionNameMarker rn1:rl1.regionnames) {
			map.addMarker(rn1);
		}
		for(TargetMarker r1:t1.targets) {
			map.addMarker(r1);
		}

		for(SatelliteMarker s:sat1.satlist.values()) {
			map.addMarker(s);
		}
		//
		cp5 = new ControlP5(this);
		cp5.addButton("showRegions").setPosition(50, 100).setSize(100,48).setValue(0).setColorValue(22).setLabel("Show/Hide Regions");
		cp5.addButton("forceReload").setPosition(50,300).setSize(100,48).setValue(0).setColorValue(22).setLabel("Force Reload");
		//cp5.addButton("hideRegions").setPosition(10, 620).setSize(100,50).setValue(0).setColorValue(22);
		cp5.addButton("clearMap").setPosition(50, 150).setSize(100,48).setValue(0).setColorValue(22);
		cp5.addButton("WorldView").setPosition(50, 200).setSize(100,48).setValue(0).setColorValue(22);
		cp5.addButton("demoModeOn").setPosition(50, 250).setSize(100,48).setValue(0).setColorValue(22).setLabel("Demo Mode");
		PFont font = createFont("arial", 20);
		cp5.addTextfield("findTarget").setPosition(50, 350).setSize(100,48).setAutoClear(false).setFont(font);
		l1 = cp5.addDropdownList("SelectSatellite").setSize(100, 400).setPosition(50,50).close();
		l1.setBarVisible(true);
		l1.setItemHeight(20);
		l1.setBarHeight(20);
		
		
		for(int i=0; i<sat1.satlist.size(); i++) {
			String satname;
			if(i<9) {
				satname = "M00" + (i+1);
			}
			else {
				satname = "M0" + (i+1);
			}
			
			l1.addItem(satname, i);
		}

		timer.schedule(new TimerTask() {
			//Iterator it = sat1.satlist.values().iterator();
			
			@Override
			public void run() {
				if(demoon) {
					Random rand = new Random(); 
			        SatelliteMarker s = sat1.satlist_ordered.get(rand.nextInt(20));
					lastclicked = s;
					current_region_a = s.region_a;
					current_region_b = s.region_b;
					zoomToTargets(s);	
				}

			}
		}, 1,6000);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if(lastclicked != null) {
					updateBeamPlan();

				}
			}
		},1,90000);
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					t1.upDateShips();

				}
				catch(Exception e) {
				}
			}
		},1,90000);
		
	}
	/*
	 * the controlEvent function of the Processing library. It handles all the interaction with the user
	 */
	public void controlEvent(ControlEvent event) {
		String text = new String(); 
		if(event.isController()) {
			if(event.getController().getName() == "findTarget") {
				text = event.getStringValue().toUpperCase();
				for(TargetMarker tm:t1.targets) {
					if(tm.shortname.contains(text)) {	
						SatelliteMarker s = sat1.satlist.get(findTargetBySatellite(tm));
						//System.out.println(tm.shortname + findTargetBySatellite(tm));
						if(s != null) {
							System.out.println(s.getId() + " " + tm.shortname);
							lastclicked = s;
							current_region_a = s.region_a;
							current_region_b = s.region_b;
							//System.out.println(current_region_a.getId() + " " + current_region_b.getId());
							zoomToTargets(s);
						}
						break;
					}
				}
			}
			
			if(event.getController().getName() == "SelectSatellite") {	
				SatelliteMarker s = sat1.satlist_ordered.get((int)event.getController().getValue());
				lastclicked = s;
				current_region_a = s.region_a;
				current_region_b = s.region_b;
				zoomToTargets(s);
				System.out.println(sat1.satlist_ordered.get((int)event.getController().getValue()));
				
			}
		}
	}
	/*
	 * The draw of the Processing library runs continously to draw the map. 
	 */
	public void draw() {
		frameRate(10);
		
		map.draw();
		fill(255,255,255);
		rect(1,1,230,SCREENY);
		//map.mapDisplay.resize(1024, 768);
		
		

		if(lastclicked != null) {

			drawBeamplan();
		}

		sat1.updateSatPosition();
		drawOffPointed();
		updateSatRegions();
		
	}


    static public void main(String args[]) {
       PApplet.main(new String[] { "MeoMap.MainMap" });
    }

	public void demoModeOn() {	
		demoon = !demoon;
		
	}
	/*
	 * This function draws a list of all the off-pointed ships to the map 
	 */
	public void drawOffPointed() {
		int count = 0;
		for(TargetMarker t:t1.offpointed_ships) {
			ClickButton cb = new ClickButton(new Location(-60 - count,-150), t);
			cb.info = t.shortname + ": old coordinates: " + t.old_coordinates + t.getLocation().getLon() + " new coordinates " + t.getLocation().getLat();
			map.addMarker(cb);
			count += 4.5;
		}
	}
	/*
	 * This function takes a satellite object as an input. It zooms the map to the targets tracked by this satellite
	 */
	public void zoomToTargets(SatelliteMarker s) {
		if(s.isspare) {
			map.zoomAndPanTo(3, s.getLocation());
			try {
				addInfo(s);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
		else {
			//map.zoomAndPanTo(3, s.getLocation());
			map.zoomAndPanToFit(showLocationsBySatellite(s));
			try {
				addInfo(s);
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	/*
	 * This function hides all the targets on the map
	 */
	public void clearMap() {
		lastclicked = null;
		hideTargets();
		map.zoomAndPanTo(1, new Location(50,-30));
	}
	
	public void forceReload() throws Exception {
		t1.upDateShips();
		updateBeamPlan();

	}


	public void WorldView() {
		lastclicked = null;
		for(TargetMarker t:t1.targets) {
			t.setHidden(false);
		}
		rl1.showAll();
		map.zoomAndPanTo(1, new Location(50,-30));
	}

	/*
	 * zoom to all the targets currently tracked by a given satellite. All other targets and regions will be hidden
	 */

	public void drawBeamplan() {
		int height = 130;
		int base_x1 = 2;
		int base_x2 = 2;
		int base_y2 = SCREENY - height - 100;
		int base_y1 = SCREENY - height*2 - 130;
		//fill(255,255,255);
		//rect(base_x1, base_y1,width,height);
		int a = 0;
		fill(0,0,0);
		textSize(14);
		text(lastclicked.getId() + " LON: " + lastclicked.getLocation().getLon(), base_x1 + 10, base_y1 - 35);

		lastclicked.updateTimeLeft();
		textSize(12);
		text(current_region_a.getId() + " GW: " + lastclicked.region_a.gateway + " Time Left: "+lastclicked.time_left_a,  base_x1 + 10, base_y1 - 10 );

		for(String s:targets_a) {
			a = a + 19;
			textSize(11);
			fill(200,0,0);
			text(s,base_x1 + 12,base_y1 + a);
		}

		//fill(255,255,255);
		a=0;
		textSize(12);
		fill(0,0,0);
		text(current_region_b.getId() + " GW: " + lastclicked.region_b.gateway + " Time Left: "+lastclicked.time_left_b,  base_x1 + 10, base_y2 - 10 );

		for(String s:targets_b) {
			a = a + 20;
			textSize(11);
			fill(0,255,0);
			text(s,base_x2 + 12,base_y2 + a);
		}

	}
	/*
	This function takes a Satellite object as input. It uses this information to look up the current 
	payload configuration in the corresponding beamplan.csv file and prints the inforamtion to the map.
	 */
	public void addInfo(SatelliteMarker satellite) throws Exception{
		targets_a.clear();
		targets_b.clear();
		//System.out.println(satellite.region_a.getId() + " " + satellite.region_b.getId());
		//System.out.println("new region info");
		Scanner sc1 = new Scanner(new File(current + satellite.getId() + "\\" + satellite.getId() + "_beamplan.csv"));	
		Scanner sc2 = new Scanner(new File(current + satellite.getId() + "\\" + satellite.getId() + "_PMT.csv"));
		String antennas_a[] = new String[5];
		String antennas_b[] = new String[5];
		int n=0;
		int k=0;
		
		if(!satellite.isspare) {
			while(sc2.hasNextLine()) {
				String data[] = sc2.nextLine().split(",");
				if(data.length > 6) {
					if(data[5].contains("00026SHL") && data[6].contains(satellite.region_a.getId()) && data[6].contains("Ch") && data[6].contains(" pointing")) {

						antennas_a[n] = "ID" + data[8] + " " + data[7];
						n++;
					}
					else if(data[5].contains("00026SHL") && data[6].contains(satellite.region_b.getId()) && data[6].contains("Ch") && data[6].contains(" pointing")) {

						antennas_b[k] = "ID" + data[8] + " " +  data[7];
						k++;
					}
				}

			}
		}
		sc2.close();
		n = 0;
		k = 0;
		if(!satellite.isspare) {
			while(sc1.hasNextLine()) {
				String data[] = sc1.nextLine().split(",");
				if(data[2].equals(satellite.region_a.getId())) {

					if(data.length > 6) {
						targets_a.add(data[3] + " " + data[6] + " " + data[9] +  " " + antennas_a[n]);
						n++;
					}
					else {
						targets_a.add(data[3]);

					}

				}
				else if(data[2].equals(satellite.region_b.getId())) {
					if(data.length > 6) {
						targets_b.add(data[3] + " " + data[6] + " " + data[9] + " " + antennas_b[k]);
						k++;
					}
					else {
						targets_b.add(data[3]);
					}
				}
			}
		}
		sc1.close();

	}

	/*
	 * This function updates the beamplan of the currently selected satellite if it has entered a new region. 
	 * 
	 */
	public void updateBeamPlan() {
		if(lastclicked != null && current_region_a != null && current_region_b != null) {
			updateSatRegions();
			if(!lastclicked.region_a.getId().contains(current_region_a.getId())) {
				try {
					addInfo(lastclicked);
					current_region_a = lastclicked.region_a;
				} catch (Exception e) {
					System.out.println(e);
				}
			}

			if(!lastclicked.region_b.getId().contains(current_region_b.getId())) {
				try {
					addInfo(lastclicked);
					current_region_b = lastclicked.region_b;
				} catch (Exception e) {

					System.out.println(e);
				}
			}
		}
	}
	/*
	 * This function  updates the current region for all the satellites
	 */
	public void updateSatRegions() {
		for(SatelliteMarker sat:sat1.satlist.values()) {
			for(RegionMarker r:rl1.regionlist) {
				if(r.contains(sat.getLocation())) {
					if(r.getId().contains("A")) {
						//System.out.println(sat.getId() + r.getId());
						sat.region_a = r;						
					}
					else {
						sat.region_b = r;

					}
				}
			}
		}
	}
	public void mouseMoved() {
		for(TargetMarker t:t1.targets) {
			if(t.isInside(map, mouseX, mouseY)) {
				t.setSelected(true);
			}
			else {
				t.setSelected(false);
			}
		}

	}
	public void mousePressed() {
		if(mouseButton == RIGHT) {
			map.zoomAndPanTo(1, new Location(0,0));
		}

	}
	public void mouseReleased() {

	}


	public void mouseClicked() {

	}


	//this method takes a marker from the current map (i.e. a SatelliteMarker or a TargetMarker) as an argument.
	//It returns a list of regions in which the marker is present.
	public List<RegionMarker> getRegion(AbstractMarker m) {
		List<RegionMarker> regions = new ArrayList<RegionMarker>();
		for(RegionMarker r:rl1.regionlist) {
			if(r.contains(m.getLocation())) {
				regions.add(r);

			}
		}
		return regions;
	}

	public void hideTargets() {
		for(TargetMarker t:t1.targets) {

			t.setHidden(true);
		}
		lastclicked = null;
	}

	public void showRegions() {
		if(rl1.markerhidden) {
			rl1.showAll();
		}
		else {
			rl1.hideAll();
		}
	}
	/*
	 * Takes a satellite object as input. It reads the _beamplan file to check which targets the satellite track
	 * in this region.  
	 */
	public List<Location> showLocationsBySatellite(SatelliteMarker sat){
		List<Location> targets = new ArrayList<Location>();
		List<String> beamplan = new ArrayList<String>();
		try {
			Scanner sc1 = new Scanner(new File(current + sat.getId() + "//" + sat.getId() + "_beamplan.csv"));
			while(sc1.hasNextLine()) {
				String data[] = sc1.nextLine().split(",");
				if(data.length >= 7) {
					if(data[2].contains(sat.region_a.getId()) || data[2].contains(sat.region_b.getId())) {
						beamplan.add(data[6]);
					}		
				}
			}
			sc1.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
		for(TargetMarker t:t1.targets) {
			if(beamplan.contains(t.name)) {
				targets.add(t.getLocation());
				t.setHidden(false);
			}
			else {
				t.setHidden(true);
			}
		}
		return targets;
	}
	public List<TargetMarker> showTargetsBySatellite(SatelliteMarker sat){
		List<TargetMarker> targets = new ArrayList<TargetMarker>();
		List<String> beamplan = new ArrayList<String>();
		try {
			Scanner sc1 = new Scanner(new File(current + sat.getId() + "//" + sat.getId() + "_beamplan.csv"));
			while(sc1.hasNextLine()) {
				String data[] = sc1.nextLine().split(",");
				if(data.length >= 7) {
					if(data[2].contains(sat.region_a.getId()) || data[2].contains(sat.region_b.getId())) {
						beamplan.add(data[6]);
					}		
				}
			}
			sc1.close();
		}
		catch(Exception e) {
			System.out.println(e);
		}
		for(TargetMarker t:t1.targets) {
			if(beamplan.contains(t.name)) {
				targets.add(t);		
			}
			else {
			}
		}
		return targets;
	}
	/*
	 * This function returns the satellite which is currently tracking a given target
	 */
	public String findTargetBySatellite(TargetMarker t) {
		SatelliteMarker satellite_a = null;
		SatelliteMarker satellite_b = null;
		//First get the current regions of the target
		for(RegionMarker r:rl1.regionlist) {
			if(r.contains(t.getLocation())) {
				if(r.getId().contains("A")) {
					t.region_a = r;						
				}
				else {
					t.region_b = r;
				}
			}
		}
		//now check which satellites are in this regions
		//System.out.println(sat1.satlist.get(t.region_a.getId()));
		for(SatelliteMarker s:sat1.satlist.values()) {
			if(t.region_a != null) {
				if(s.region_a.getId().equals(t.region_a.getId())) {
					satellite_a = s;

				}
			}
			if(t.region_b != null) {
				if(s.region_b.getId().equals(t.region_b.getId())) {
					satellite_b = s;
				}
			}
		}
		//System.out.println(satellite_a.getId() + showTargetsBySatellite(satellite_a));
		if(satellite_a != null) {
			if(showTargetsBySatellite(satellite_a).contains(t)) {
				return satellite_a.getId();
			}
		}
		if(satellite_b != null) {
			if(showTargetsBySatellite(satellite_b).contains(t)){
				return satellite_b.getId();
			}
		}
		if(satellite_a != null) {
			return satellite_a.getId();
		}
		if(satellite_b != null) {
			return satellite_b.getId();
		}
		return null;
	}
	/*
	 * Check the beamplan file of the given Satellite. Return which targets are tracked in the current region the satellite 
	 * is in
	 */
	public List<TargetMarker> targetsBySatellite(SatelliteMarker s){

		return null;
	}

	/*
	 * this function returns all the targets that are currently inside a given region
	 */
	public List<Location> showTargets(List<RegionMarker> regions) {
		List<Location> current = new ArrayList<Location>();
		for(TargetMarker t:t1.targets) {
			if(!t.isgateway) {
				for(RegionMarker r:regions) {
					if(r.contains(t.getLocation())) {
						current.add(t.getLocation());
						t.setHidden(false);
					}
					else {
						t.setHidden(true);
					}
				}
			}
		}
		//System.out.println(current);
		return current;
	}
	/*
	 * 
	 */

}
