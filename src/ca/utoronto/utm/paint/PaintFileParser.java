package ca.utoronto.utm.paint;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;
/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about
 * its effort to parse a file. After a successful parse, an instance
 * will have an ArrayList of PaintCommand suitable for rendering.
 * If there is an error in the parse, the instance stores information
 * about the error. For more on the format of Version 1.0 of the paint 
 * save file format, see the associated documentation.
 * 
 * @author 
 *
 */
public class PaintFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage =""; // error encountered during parse
	private PaintModel paintModel; 
	private Color current_color;
	private boolean isFilled;
	private Point centerPoint;
	private int radius;
	private Point RectP1;
	private Point RectP2;
	ArrayList<Point> SquigglePoint = new ArrayList<Point>();
	ArrayList<Point> PolyPoint = new ArrayList<Point>();
	/**
	 * Below are Patterns used in parsing 
	 */
	private Pattern pFileStart=Pattern.compile("^PaintSaveFileVersion1.0$");
	private Pattern pFileEnd=Pattern.compile("^EndPaintSaveFile$");
	private Pattern pEmptySpace = Pattern.compile("[ ]*");
	private Pattern pCircleStart=Pattern.compile("^Circle$");
	private Pattern pCircleEnd=Pattern.compile("^EndCircle$");
	
	private Pattern pRectStart = Pattern.compile("^Rectangle$");
	private Pattern pRectEnd = Pattern.compile("^EndRectangle$");
	
	private Pattern pSquiggleStart = Pattern.compile("^Squiggle$");
	private Pattern pSquiggleEnd = Pattern.compile("^EndSquiggle$");
	
	private Pattern pPolyStart = Pattern.compile("^Polyline$");
	private Pattern pPolyEnd = Pattern.compile("^EndPolyline$");
	
	private Pattern pColor = Pattern.compile("^color:(([0-9],?|[0-9][0-9],?|1[0-9][0-9],?|2[0-4][0-9],?|25[0-5],?){3})");
	private Pattern pFilled = Pattern.compile("^filled:(true|false{1})");
	private Pattern pCenter = Pattern.compile("^center:\\((-?0*[0-9],?|-?0*[1-9][0-9],?|-?0*[1-9][0-9][0-9],?){2}\\)");
	private Pattern pRadius  = Pattern.compile("^radius:([0-9]|[1-9][0-9]|[1-9][0-9][0-9]{1})");
	
	private Pattern pRectP1 = Pattern.compile("^p1:\\((-?0*[0-9],?|-?0*[1-9][0-9],?|-?0*[1-9][0-9][0-9],?){2}\\)");
	private Pattern pRectP2 = Pattern.compile("^p2:\\((-?0*[0-9],?|-?0*[1-9][0-9],?|-?0*[1-9][0-9][0-9],?){2}\\)");
	
	private Pattern pPoint = Pattern.compile("^points$");
	private Pattern pPoints = Pattern.compile("^point:\\((-?0*[0-9],?|-?0*[1-9][0-9],?|-?0*[1-9][0-9][0-9],?){2}\\)");
	private Pattern pEndPoint = Pattern.compile("^endpoints$");
	
	// ADD MORE!!
	
	/**
	 * Store an appropriate error message in this, including 
	 * lineNumber where the error occurred.
	 * @param mesg
	 */
	private void error(String mesg){
		this.errorMessage = "Error in line "+lineNumber+" "+mesg;
	}
	
	/**
	 * 
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage(){
		return this.errorMessage;
	}
	
	/**
	 * Parse the inputStream as a Paint Save File Format file.
	 * The result of the parse is stored as an ArrayList of Paint command.
	 * If the parse was not successful, this.errorMessage is appropriately
	 * set, with a useful error message.
	 * 
	 * @param inputStream the open file to parse
	 * @param paintModel the paint model to add the commands to
	 * @return whether the complete file was successfully parsed
	 */
	public boolean parse(BufferedReader inputStream, PaintModel paintModel) {
		this.paintModel = paintModel;
		this.errorMessage="";
		this.current_color = Color.rgb(0, 0, 0);
		this.isFilled = false;
		this.centerPoint = new Point(0,0);
		this.radius = 0;
		this.RectP1 = new Point(0,0);
		this.RectP2 = new Point(0,0);
		this.SquigglePoint = new ArrayList<Point>();
		this.PolyPoint = new ArrayList<Point>();
		// During the parse, we will be building one of the 
		// following commands. As we parse the file, we modify 
		// the appropriate command.
		
		CircleCommand circleCommand = null; 
		RectangleCommand rectangleCommand = null;
		SquiggleCommand squiggleCommand = null;
		PolylineCommand polylineCommand = null;

		try {	
			int state=0; Matcher m; String l;
			
			this.lineNumber=0;
			while ((l = inputStream.readLine()) != null) {
				l = l.replaceAll("\\s+",""); // strip all white spaces
				this.lineNumber++;
				System.out.println(lineNumber+" "+l+" "+state);
				switch(state){
					case 0:
						m=pFileStart.matcher(l);
						if(m.matches()){
							state=1;
							break;
						}
						error("Expected Start of Paint Save File");
						throw new Exception();
						
					case 1: // Looking for the start of a new object or end of the save file
						m=pCircleStart.matcher(l);
						if(m.matches()){
							// ADD CODE!!!
							state=2;
							break;
						}
						m=pRectStart.matcher(l);
						if(m.matches()) {
							state =7;
							break;
						}
						m = pSquiggleStart.matcher(l);
						if(m.matches()) {
							state = 12;
							break;
						}
						m = pPolyStart.matcher(l);
						if(m.matches()) {
							state = 18;
							break;
						}
						m = pFileEnd.matcher(l);
						if(m.matches()) {
							break;
						}
						m = pEmptySpace.matcher(l);
						if(m.matches()) {
							return true;
						}
						error("End Paint Save File Expected");
						throw new Exception();
						
					case 2:
						// Color for circle
						m = pColor.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("color:","");
							ArrayList<Integer> ArrColor = new ArrayList<Integer>();
							String[] colorPt = l.split(",");
							for (int i =0; i < 3; i++) {
								int c = Integer.parseInt(colorPt[i]);
								ArrColor.add(c);
							}
							Color color = Color.rgb(ArrColor.get(0),ArrColor.get(1),ArrColor.get(2));
							this.current_color = color; // Store color 
							state = 3;
							break;
						}
						error("Expected Color & Correct Format");
						throw new Exception();
						
					case 3:
						// Filled for circle
						m = pFilled.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("filled:", "");
							String [] bool = l.split(",");
							boolean b = Boolean.parseBoolean(bool[0]);
							this.isFilled = b;
							state = 4;
							break;
						}
						error("Wrong Boolean Type");
						throw new Exception();
						
					case 4:
						// Checking center
						m = pCenter.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("center:", "");
							l = l.replace("(", "");
							l = l.replace(")", "");
							String[] centerPt = l.split(",");
							this.centerPoint.x  = Integer.parseInt(centerPt[0]);
							this.centerPoint.y = Integer.parseInt(centerPt[1]);	
							state = 5;
							break;
						}
						error("Wrong Center Input");
						throw new Exception();
					case 5:
						// checking radius
						m = pRadius.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("radius:", "");
							String[] radiusPt = l.split(",");
							this.radius = Integer.parseInt(radiusPt[0]);
							state = 6;
							break;
						}
						error("Wrong Radius Value");
						throw new Exception();
					case 6:	
						m = pCircleEnd.matcher(l);
						if(m.matches()) {
							circleCommand = new CircleCommand(this.centerPoint,this.radius);
							circleCommand.setColor(this.current_color);
							circleCommand.setFill(this.isFilled);
							this.paintModel.addCommand(circleCommand);
							this.isFilled = false;
							this.centerPoint = new Point(0,0);
							this.radius = 0;
							state = 1;
							break;
						}
						error("Expected End Circle");
						throw new Exception();
					case 7:
						// color for rectangle
						m = pColor.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("color:","");
							ArrayList<Integer> ArrColor = new ArrayList<Integer>();
							String[] colorPt = l.split(",");
							for (int i =0; i < 3; i++) {
								int c = Integer.parseInt(colorPt[i]);
								ArrColor.add(c);
							}
							Color color = Color.rgb(ArrColor.get(0),ArrColor.get(1),ArrColor.get(2));
							this.current_color = color; // Store color 
							state = 8;
							break;
						
						}
						error("Wrong format of Color");
						throw new Exception();
					case 8:
						m = pFilled.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("filled:", "");
							String [] bool = l.split(",");
							boolean b = Boolean.parseBoolean(bool[0]);
							this.isFilled = b;
							state = 9;
							break;
						}
						error("Wrong Boolean Type");
						throw new Exception();
					case 9:
						m = pRectP1.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("p1:", "");
							l = l.replace("(", "");
							l = l.replace(")", "");
							String[] arrRectP1 = l.split(",");
							this.RectP1.x = Integer.parseInt(arrRectP1[0]);
							this.RectP1.y = Integer.parseInt(arrRectP1[1]);
							state = 10;
							break;
						}

						error("Expected point P1");
						throw new Exception();
					case 10:
						m = pRectP2.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("p2:", "");
							l = l.replace("(", "");
							l = l.replace(")", "");
							String[] arrRectP2 = l.split(",");
							this.RectP2.x = Integer.parseInt(arrRectP2[0]);
							this.RectP2.y = Integer.parseInt(arrRectP2[1]);
							state = 11;
							break;
						}
						error("Wrong Point P2 for Rectangle");
						throw new Exception();
					case 11:
						m = pRectEnd.matcher(l);
						if(m.matches()) {
							rectangleCommand = new RectangleCommand(this.RectP1,this.RectP2);
							rectangleCommand.setColor(this.current_color);
							rectangleCommand.setFill(this.isFilled);
							this.paintModel.addCommand(rectangleCommand);
							this.isFilled = false;
							this.RectP1 = new Point(0,0);
							this.RectP2 = new Point(0,0);
							state = 1;
							break;
						}
						error("End Rectangle Expected");
						throw new Exception();
					case 12:
						// color for squiggle
						m = pColor.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("color:","");
							ArrayList<Integer> ArrColor = new ArrayList<Integer>();
							String[] colorPt = l.split(",");
							for (int i =0; i < 3; i++) {
								int c = Integer.parseInt(colorPt[i]);
								ArrColor.add(c);
							}
							Color color = Color.rgb(ArrColor.get(0),ArrColor.get(1),ArrColor.get(2));
							this.current_color = color; // Store color 
							state = 13;
							break;
						}
						error("Expected Color & Correct Format");
						throw new Exception();
					case 13:
						m = pFilled.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("filled:", "");
							String [] bool = l.split(",");
							boolean b = Boolean.parseBoolean(bool[0]);
							this.isFilled = b;
							state = 14;
							break;
						}
						error("Wrong Boolean Type");
						throw new Exception();
					case 14:
						m = pPoint.matcher(l);
						if(m.matches()) {
							state =15;
							break;
						}
						error("points Expected");
						throw new Exception();
					
					case 15:
						m = pPoints.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("point:", "");
							l = l.replace("(", "");
							l = l.replace(")", "");
							String[] Spoints = l.split(",");
							int x  = Integer.parseInt(Spoints[0]);
							int y = Integer.parseInt(Spoints[1]);
							Point squiggle_points = new Point(x,y);
							this.SquigglePoint.add(squiggle_points);
							break;
						}else{
							state = 16;
							break;
						}
					case 16:
						m = pEndPoint.matcher(l);
						if(m.matches()) {
							state = 17;
							break;
						}
					
					case 17:
						m = pSquiggleEnd.matcher(l);
						if(m.matches()) {
							squiggleCommand = new SquiggleCommand();
							squiggleCommand.setColor(this.current_color);
							squiggleCommand.setFill(this.isFilled);
							for(int i = 0 ; i < SquigglePoint.size(); i++ ) { // add all the points 
								squiggleCommand.add(SquigglePoint.get(i));
							}
							this.paintModel.addCommand(squiggleCommand);
							this.isFilled = false;
							this.SquigglePoint = new ArrayList<Point>();
							squiggleCommand = new SquiggleCommand();
							state =1;
							break;
						}
						error("End Squiggle Expected");
						throw new Exception();
					case 18:
						m = pColor.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("color:","");
							ArrayList<Integer> ArrColor = new ArrayList<Integer>();
							String[] colorPt = l.split(",");
							for (int i =0; i < 3; i++) {
								int c = Integer.parseInt(colorPt[i]);
								ArrColor.add(c);
							}
							Color color = Color.rgb(ArrColor.get(0),ArrColor.get(1),ArrColor.get(2));
							this.current_color = color; // Store color 
							state = 19;
							break;
						}
						error("Expected Color & Correct Format");
						throw new Exception();
					case 19:
						m = pFilled.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("filled:", "");
							String [] bool = l.split(",");
							boolean b = Boolean.parseBoolean(bool[0]);
							this.isFilled = b;
							state = 20;
							break;
						}
						error("Wrong Boolean Type");
						throw new Exception();
					case 20:
						m = pPoint.matcher(l);
						if(m.matches()) {
							state =21;
							break;
						}
						error("point Expected");
						throw new Exception();
						
					case 21:
						m = pPoints.matcher(l);
						if(m.matches()) {
							l = l.replaceAll("point:", "");
							l = l.replace("(", "");
							l = l.replace(")", "");
							String[] Polypoints = l.split(",");
							int x  = Integer.parseInt(Polypoints[0]);
							int y = Integer.parseInt(Polypoints[1]);
							Point poly_points = new Point(x,y);
							this.PolyPoint.add(poly_points);
							break;
						}else{
							state = 22;
							break;
						}
					case 22:
						m = pEndPoint.matcher(l);
						if(m.matches()) {
							state = 23;
							break;
						}
						
					case 23:
						m = pPolyEnd.matcher(l);
						if(m.matches()) {
							polylineCommand = new PolylineCommand();
							polylineCommand.setColor(this.current_color);
							polylineCommand.setFill(this.isFilled);
							for(int i = 0 ; i < PolyPoint.size(); i++ ) { // add all the points 
								polylineCommand.add(this.PolyPoint.get(i));
							}
							polylineCommand.rightClick();
							this.paintModel.addCommand(polylineCommand);
							this.isFilled = false;
							this.PolyPoint = new ArrayList<Point>();
							polylineCommand = new PolylineCommand();
							state =1;
							break;
						}
						error("End Polyline Expected");
						throw new Exception();
				}
			}
		}  catch (Exception e){
			System.out.println(this.getErrorMessage());
			return false;
		}
		return true;
	}
}
