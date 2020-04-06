import java.util.ArrayList;
import java.util.HashSet;

public class DPolygon {

	public ArrayList<DPoint> polygon;

	public DPolygon(ArrayList<DPoint> set){
		polygon=ConvexHull.getPolygon(set);
	}
	public String toString(){
		return polygon.toString();
	}
}