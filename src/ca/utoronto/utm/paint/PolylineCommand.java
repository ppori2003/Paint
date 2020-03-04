package ca.utoronto.utm.paint;
import javafx.scene.canvas.GraphicsContext;


import java.util.ArrayList;
public class PolylineCommand extends PaintCommand {
	private ArrayList<Point> points=new ArrayList<Point>();
	private Point lastPoint;
	private Point firstPoint;
	private boolean Rclick = false;
	private Point final_pt;
	public void add(Point p){ 
		this.points.add(p); 
		this.setFirstPoint(p);
		this.setChanged();
		this.notifyObservers();
	}
	public void rightClick() {
		this.Rclick = true;
	}
	public Point getFinalPoint() {
		return this.final_pt;
	}
	public ArrayList<Point> getPoints(){ return this.points; }
	
	public void setLastPoint(Point lastpt) {
		this.lastPoint = lastpt;
		this.setChanged();
		this.notifyObservers();
}
	public void setFirstPoint(Point firstpt) {
		this.firstPoint = firstpt;
}
	@Override
	public void execute(GraphicsContext g) {
		ArrayList<Point> points = this.getPoints();
		g.setStroke(this.getColor());
		if(this.firstPoint != null && this.Rclick == false) {
			g.strokeLine(this.firstPoint.x, this.firstPoint.y, this.lastPoint.x, this.lastPoint.y);
		}
		for(int i=0;i<points.size()-1;i++){
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
			g.strokeLine(p1.x, p1.y, p2.x, p2.y);
		}
	}
}


