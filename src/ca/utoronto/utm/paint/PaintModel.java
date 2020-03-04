package ca.utoronto.utm.paint;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PaintModel extends Observable implements Observer {
	public void save(PrintWriter writer) {
		writer.print("Paint Save File Version 1.0" + "\n");
		for(PaintCommand c: this.commands){ 
			if(c.getClass().getName().contains("CircleCommand")){
				CircleCommand i = (CircleCommand)c;
				Point center = i.getCentre();
				int radius = i.getRadius();
				writer.print("Circle" + "\n");
				writer.print(c.rgbColor());
				writer.print("\tcenter:" + "("+center.x + "," + center.y +")"+ "\n");
				writer.print("\tradius:" + radius + "\n");
				writer.print("End Circle" + "\n");
			}else if(c.getClass().getName().contains("RectangleCommand")) {
				RectangleCommand i = (RectangleCommand)c;
				Point p1 = i.getP1();
				Point p2 = i.getP2();
				writer.print("Rectangle" + "\n");
				writer.print(c.rgbColor());
				writer.print("\tp1:" + "("+p1.x + "," + p1.y +")"+ "\n");
				writer.print("\tp2:" + "("+p2.x + "," + p2.y +")"+ "\n");
				writer.print("End Rectangle" + "\n");
			}else if(c.getClass().getName().contains("SquiggleCommand")) {
				SquiggleCommand i = (SquiggleCommand)c;
				ArrayList<Point> points = i.getPoints();
				writer.print("Squiggle"+ "\n");
				writer.print(c.rgbColor());
				writer.print("points" + "\n");
				for(int j = 0; j<points.size(); j++) {
					writer.print("\tpoint:" + "(" + points.get(j).x + "," + points.get(j).y + ")" + "\n");
				}
				writer.print("end points" + "\n");
				writer.print("End Squiggle" + "\n");
			}else if(c.getClass().getName().contains("PolylineCommand")) {
				PolylineCommand i = (PolylineCommand)c;
				ArrayList<Point> points = i.getPoints();
				if(points.size()>0) {
					writer.print("Polyline" + "\n");
					writer.print(c.rgbColor());
					writer.print("points" + "\n");
					for(int j = 0; j<points.size(); j++) {
						writer.print("\tpoint:" + "(" + points.get(j).x + "," + points.get(j).y + ")" + "\n");
					}
					writer.print("end points" + "\n");
					writer.print("End Polyline" + "\n");
				}
				
			}

		}
		writer.print("End Paint Save File");
	}
	public void reset(){
		for(PaintCommand c: this.commands){
			c.deleteObserver(this);
		}
		this.commands.clear();
		this.setChanged();
		this.notifyObservers();
	}
	
	public void addCommand(PaintCommand command){
		this.commands.add(command);
		command.addObserver(this);
		this.setChanged();
		this.notifyObservers();
	}
	
	private ArrayList<PaintCommand> commands = new ArrayList<PaintCommand>();
	public void executeAll(GraphicsContext g) {
		for(PaintCommand c: this.commands){
			c.execute(g);
		}
	}
	
	/**
	 * We Observe our model components, the PaintCommands
	 */
	@Override
	public void update(Observable o, Object arg) {
		this.setChanged();
		this.notifyObservers();
	}	
}
