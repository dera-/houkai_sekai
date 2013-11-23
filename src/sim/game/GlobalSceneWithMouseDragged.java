package sim.game;

public interface GlobalSceneWithMouseDragged extends GlobalScene {
	public void mouseDragged(int x, int y);
	public void mousePressed(int x, int y);
	public void mouseReleased(int x, int y);
}
