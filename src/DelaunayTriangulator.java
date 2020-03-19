// coordinates on plane
class vertex {
	public int x, y;
	public vertex() {;}
	public vertex(int x, int y)
	{
		this.x = x;
		this.y = x;
	}
}

// edge information
class edge {
	public vertex origin; // start point of edge
	public vertex destin; // end point
	public edge next; // next edge
	public edge prev; // previous
	public edge twin; // twin of current edge
	public edge() {;}
}

class triangle {
	public int flag; //status
	public int flag1; 
	public triangle()
	{
		flag = 0;
		flag1 = 0;
	}
	
	// edges of triangle
	public edge triEdge1;
	public edge triEdge2;
	public edge triEdge3;
	
	// children created by current triangle
	public triangle child1;
	public triangle child2;
	public triangle child3;
}

