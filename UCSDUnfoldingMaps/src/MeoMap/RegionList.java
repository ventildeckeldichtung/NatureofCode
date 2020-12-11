package MeoMap;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import de.fhpotsdam.unfolding.geo.Location;

public class RegionList {
	public List<RegionMarker> regionlist = new ArrayList<RegionMarker>();
	public List<RegionNameMarker> regionnames = new ArrayList<RegionNameMarker>();
	public String path;
	public boolean markerhidden = false;
	public boolean nameshidden = false;
	public RegionList(String path) {
		this.path = path;
	}
	public Float convertLongitude(Float lon) {

		if(lon <= 180) {
			return lon;
		}
		else {
			return lon - 360;
		}
	}
	public void hideAll() {
		for(RegionMarker r:regionlist) {
			r.setHidden(true);
			markerhidden = true;
		}
		for(RegionNameMarker r:regionnames) {
			r.setHidden(true);
			nameshidden = true;
		}
	}
	public void showAll() {
		for(RegionMarker r:regionlist) {
			r.setHidden(false);
			markerhidden = false;
		}
		for(RegionNameMarker r:regionnames) {
			r.setHidden(false);
			nameshidden = false;
		}
	}
	public void addRegions() throws Exception{	
		Scanner sc1 = new Scanner(new File(path  + "M001\\M001_zones.csv") );
		boolean first = true;
		while(sc1.hasNextLine()) {
			String data[] = sc1.nextLine().split(",");

			if(first == false) {

				if(!(convertLongitude(Float.parseFloat(data[2])) > convertLongitude(Float.parseFloat(data[4])))) {
					if(data[0].contains("A")){
						RegionMarker sm1 = new RegionMarker(new Location(43,convertLongitude(Float.parseFloat(data[2]))), new Location(43,convertLongitude(Float.parseFloat(data[4]))), new Location(-47,convertLongitude(Float.parseFloat(data[4]))), new Location(-47,convertLongitude(Float.parseFloat(data[2]))),data[0]);
						RegionNameMarker sp1 = new RegionNameMarker();		
						regionlist.add(sm1);
						sp1.setLocation(new Location(-53,convertLongitude(Float.parseFloat(data[3]))));
						sp1.setId(data[0]);
						regionnames.add(sp1);
					}
					else {
						RegionMarker sm1 = new RegionMarker(new Location(47,convertLongitude(Float.parseFloat(data[2]))), new Location(47,convertLongitude(Float.parseFloat(data[4]))), new Location(-43,convertLongitude(Float.parseFloat(data[4]))), new Location(-43,convertLongitude(Float.parseFloat(data[2]))), data[0]);
						RegionNameMarker sp1 = new RegionNameMarker();
						regionlist.add(sm1);
						sp1.setLocation(new Location(49,convertLongitude(Float.parseFloat(data[3]))));
						sp1.setId(data[0]);
						regionnames.add(sp1);
					}

				}
				else {
					if(data[0].contains("A")) {
						RegionMarker sm2 = new RegionMarker(new Location(43,convertLongitude(Float.parseFloat(data[2]))), new Location(43,180), new Location(-47,180), new Location(-47, convertLongitude(Float.parseFloat(data[2]))), data[0]);
						RegionMarker sm3 = new RegionMarker(new Location(43,-179), new Location(43,convertLongitude(Float.parseFloat(data[4]))), new Location(-47,convertLongitude(Float.parseFloat(data[4]))), new Location(-47, -179), data[0]);
						sm2.setStrokeWeight(2);					
						regionlist.add(sm2);
						regionlist.add(sm3);
					}
					else {
						RegionMarker sm2 = new RegionMarker((new Location(47,convertLongitude(Float.parseFloat(data[2])))), new Location(47,180), new Location(-43,180), new Location(-43, convertLongitude(Float.parseFloat(data[2]))), data[0]);
						RegionMarker sm3 = new RegionMarker(new Location(47,-179), new Location(47,convertLongitude(Float.parseFloat(data[4]))), new Location(-43,convertLongitude(Float.parseFloat(data[4]))), new Location(-43, -179), data[0]);
						sm2.setStrokeWeight(2);					
						regionlist.add(sm2);
						regionlist.add(sm3);
					}
				}
			}
			first = false;
		}
		sc1.close();
	}
	
	/*
	 * this function reads the _targets file to find out which gateway (SUN, LUR, NMA, PER, VRN, DUB
	 * SIN or HTL) is serving the region. Then it adds this information to each region
	 */
	public void addRegionGateway() throws Exception {
		Dictionary<String,String> regions = new Hashtable<String, String>(6);
		Scanner sc1 = new Scanner(new File(path  + "M001\\M001_targets.csv") );
		boolean first = true;
		int count = 9;
		while(sc1.hasNextLine()) {
			String data[] = sc1.nextLine().split(",");
			if(first == false) {
					if(data.length > 6) {
						count --;
						if(count >= 0) {
							regions.put(data[2], data[1]);
						}
						else {
							for(RegionNameMarker rn:this.regionnames) {
								if(rn.getId().contains(data[1])) {
									//System.out.println(regions.get(data[2]));
									rn.setGateway(regions.get(data[2]));
								}
							}
							for(RegionMarker rm:this.regionlist) {
								if(rm.getId().contains(data[1])) {
									rm.setGateway(regions.get(data[2]));
								}
							}
							//System.out.println(data[1] + " " + regions.get(data[2]));
						}
					}
				}
			first = false;	
		}
		sc1.close();
	}

}
