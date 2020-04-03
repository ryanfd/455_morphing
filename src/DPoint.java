// points to triangulate
public class DPoint {

	public double x;
	public double y;
	public double value;
	public DPoint(double xs,double ys){
		x=xs;
		y=ys;
	}
	public DPoint(double xs,double ys,double vl){
		x=xs;
		y=ys;
		value=vl;
	}
	
	public String toString(){
		return "("+x+","+y+") ";
	}
	
	public DPoint plus(DPoint dest) {
		DPoint result = new DPoint(x + dest.x, y + dest.y);
		return result;
	}
	
	public DPoint getMean(DPoint dest) {
		DPoint result = new DPoint((x + dest.x)/2, (y + dest.y)/2);
		return result;
	}
}
