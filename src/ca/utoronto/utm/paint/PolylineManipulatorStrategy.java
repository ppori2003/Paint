package ca.utoronto.utm.paint;

import javafx.scene.input.MouseEvent;

public class PolylineManipulatorStrategy extends ShapeManipulatorStrategy  {

	PolylineManipulatorStrategy(PaintModel paintModel) {
		super(paintModel);
		this.polylineCommand = new PolylineCommand();
		this.addCommand(polylineCommand);
		
		// TODO Auto-generated constructor stub
	}
	private PolylineCommand polylineCommand;
	@Override

	public void mouseClicked(MouseEvent e) {
		this.polylineCommand.add(new Point((int)e.getX(), (int)e.getY()));
		
	}
	public void mouseRighted(MouseEvent e) {
		this.polylineCommand.rightClick();
		this.polylineCommand = new PolylineCommand();
		this.addCommand(polylineCommand);
	
		
	}public void mouseMoved(MouseEvent e){
		this.polylineCommand.setLastPoint(new Point ((int)e.getX(), (int)e.getY()));
	}
}
