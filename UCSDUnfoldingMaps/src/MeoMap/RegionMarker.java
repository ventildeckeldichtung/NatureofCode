package MeoMap;

import java.util.List;

import de.fhpotsdam.unfolding.geo.Location;

import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import de.fhpotsdam.unfolding.utils.MapPosition;
import processing.core.PConstants;
import processing.core.PGraphics;
public class RegionMarker extends SimplePolygonMarker {
	public float start_lon;
	public float stop_lon;
	public String gateway;
	public RegionMarker(Location l1, Location l2, Location l3, Location l4, String ID) {
		super();
		this.addLocations(l1, l2, l3, l4);
		this.setId(ID);	
		this.start_lon = l1.getLon();
		this.stop_lon = l2.getLon();
		
	}
	public RegionMarker() {
		super();
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
		
	}
	@Override
	public void draw(PGraphics pg, List<MapPosition> mapPositions) {
		if (mapPositions.isEmpty() || isHidden())
			return;
		pg.pushStyle();
		pg.strokeWeight(strokeWeight);
		if (isSelected()) {
			pg.fill(highlightColor);
			pg.stroke(highlightStrokeColor);
		} else {
			this.setStrokeWeight(2);
			this.setColor(500);
			if(this.getId().contains("A")) {
				pg.fill(color);
				pg.stroke(255,0,0);
			}
			else {
				pg.fill(color);
				pg.stroke(0,255,0);
			}
		}

		pg.beginShape();
		for (MapPosition pos : mapPositions) {
			pg.vertex(pos.x, pos.y);
		}
		pg.endShape(PConstants.CLOSE);
		pg.popStyle();
		
		
	}

}
