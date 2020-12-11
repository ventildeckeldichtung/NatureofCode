package MeoMap;

import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractMarker;
import processing.core.PConstants;
import processing.core.PGraphics;

public class ClickButton extends AbstractMarker{
	private int sizey = 40;
	private int sizex = 680;
	public boolean clicked = false;
	public boolean pressed = false;
	public String info = " ";
	public TargetMarker target;
	public ClickButton(Location l1, TargetMarker target) {
		this.setLocation(l1);
		this.setId(target.getId());
		this.target = target;
	}

	@Override
	public void draw(PGraphics pg, float x, float y) {
		pg.pushStyle();
		pg.imageMode(PConstants.CORNER);
		
		if(this.pressed) {
			pg.fill(0,230,0);
		}
		else {
			pg.fill(0,150,0);
		}
		pg.rect(x, y, sizex, sizey/2,7);
		pg.fill(0,0,250);
		pg.textSize(13);
		pg.text(info,x + 7, y+sizey/3);
		pg.popStyle();		
	}
	
	@Override
	protected boolean isInside(float checkX, float checkY, float x, float y) {
		return checkX > x && checkX < x + sizex && checkY > y && checkY < y + sizey;
		/*
		if((checkX >= (x - size/2) && checkX <= (x + size/2))) {
			if((checkY >= (y - size/2) && checkY <= (y + size/2))) {
				return true;
			}
			else {
				return false;
			}
						
		}
		else {
			return false;
		}
	*/
	}
	
}
