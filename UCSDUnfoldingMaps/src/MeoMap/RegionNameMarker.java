package MeoMap;


import de.fhpotsdam.unfolding.geo.Location;
import processing.core.PConstants;
import processing.core.PGraphics;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;


/*
 * This class creates a Marker to name a region
 */
public class RegionNameMarker extends SimplePointMarker{
	public Location l1;
	public String gateway = null;
	RegionNameMarker(){

	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	@Override
	public void draw(PGraphics pg, float x, float y) {
		if (isHidden()) {
			return;
		}
		pg.pushStyle();
		pg.imageMode(PConstants.CORNER);
		if(this.getId().contains("A")) {
			pg.fill(255, 0, 0, 255);
			// The image is drawn in object coordinates, i.e. the marker's origin (0,0) is at its geo-location.
			//System.out.println(this.getId() + " " + this.gateway);
			pg.textSize(16);
			if(this.getId() != null) {
				pg.text(this.getId(), x, y);
			}
			if(this.gateway != null) {
				pg.text(this.gateway, x,y+20);
			}
		}
		else {
			pg.fill(color);
			pg.fill(0, 255, 0, 255);
			// The image is drawn in object coordinates, i.e. the marker's origin (0,0) is at its geo-location.
			pg.textSize(16);
			//System.out.println(this.getId() + " " + this.gateway);
			if(this.getId() != null) {
				pg.text(this.getId(), x, y);
			}
			if(this.gateway != null) {
				pg.text(this.gateway, x,y-20);
			}
		}
		//pg.rect(5, 5, 5, 5);
		pg.popStyle();
	}


}
