import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

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

class tree {
	static PrintWriter screen = new PrintWriter(System.out, true);
	static BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
	
	static FileWriter file2;
	static PrintWriter outputFile;
	
	static int triangleNumber = 0; // num of triangles
	static int triangleNumber1 = 0;
	static int vertexNumber; // num of points
	static vertex[] node; // array of points
	static triangle root; 
	static edge e;
	
	// method provide to privide output file storing edges of triangulation
	static void init1() throws IOException 
	{
		file2 = new FileWriter("results1.txt");
		outputFile = new PrintWriter(file2);
	}
	
	// read points and store in array of vertices
	static void init() throws IOException 
	{
		FileReader file = new FileReader("t.txt");
		BufferedReader inputFile = new BufferedReader(file);
		
		// read number of points
		vertexNumber = new Integer(inputFile.readLine()).intValue();
		node = new vertex[vertexNumber];
		for (int i=0; i<vertexNumber; i++) {
			node[i] = new vertex();
			node[i].x = new Integer(inputFile.readLine()).intValue();
			node[i].y = new Integer(inputFile.readLine()).intValue();
		}
	}
}























