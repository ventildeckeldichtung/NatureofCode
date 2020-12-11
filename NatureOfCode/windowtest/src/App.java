
import de.fhpotsdam.unfolding.*;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;

import processing.core.PApplet;

public class App extends PApplet {
    UnfoldingMap map;
    public void settings(){
        size(800, 600);
    }
    public void setup() {
        surface.setResizable(true);
        map = new UnfoldingMap(this);
        MapUtils.createDefaultEventDispatcher(this, map);
        map.addMarker(new SimplePointMarker(new Location(49.0,0)));
    }
    static public void main(String args[]) {
        PApplet.main(new String[] { "App" });
     }
    public void draw() {
        map.draw();
    }
 
}
