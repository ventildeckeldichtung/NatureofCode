package MeoMap;

import java.util.TimerTask;

public class DemoTask extends TimerTask{
	public boolean running;
	public String s = "M001";
	int i=0;
	public String[] satellites = {"M001", "M002", "M003", "M004", "M005", "M006", "M007", "M008", "M009", "M010", "M011", "M012", "M013", "M014", "M015", "M016", "M017", "M018", "M019", "M020"};
	public DemoTask() {
		running = true;
	}
	public void toggle() {
		this.running = !this.running;
	}
	@Override
	public void run() {	
		if(this.running) {
			i=(i = i + 1)%20;
			s = satellites[i];
		}
	}
}
