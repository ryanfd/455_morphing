import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

import org.opencv.core.KeyPoint;
import org.opencv.core.MatOfKeyPoint;

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
	
	// method to provide output file storing edges of triangulation
	static void init1() throws IOException 
	{
		file2 = new FileWriter("results1.txt");
		outputFile = new PrintWriter(file2);
	}
	
	// read points and store in array of vertices
	static void init(List<KeyPoint> points) throws IOException 
	{
		// write coords of points
		FileWriter fw = new FileWriter("src/t.txt");
		BufferedWriter bw = new BufferedWriter(fw);
		
		// write size
		bw.write(new Integer(points.size()).toString());
		bw.newLine();
		
		for (int i=0; i<points.size(); i++) {
			bw.write(new Integer((int) points.get(i).pt.x).toString());
			bw.newLine();
			bw.write(new Integer((int) points.get(i).pt.y).toString());
			if (i != points.size()-1) bw.newLine();
			
		}
		bw.close();
		FileReader file = new FileReader("src/t.txt"); 		   		   
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
	
	// create initial triangle: includes points
	static void getStart()
	{
		int maxX, maxY, m;
		int i,j;
		maxX = Math.abs(node[0].x);
		maxY = Math.abs(node[0].y);
		
		// find max absolute value of coordinates of points
		for (int index=1; index<vertexNumber; index++) {
			if (maxX < Math.abs(node[index].x)) maxX = Math.abs(node[index].x);
			if (maxY < Math.abs(node[index].y)) maxY = Math.abs(node[index].y);
		}
		
		m = maxX;
		if (maxY > maxX) m = maxY;
		
		vertex[] bound;
		bound = new vertex[3];
		bound[0] = new vertex(0, 3*m); // set coords of 3 triangle top points
		bound[1] = new vertex(3*m, 0);
		bound[2] = new vertex(-3*m, -3*m);
		
		edge e1 = new edge();
		edge e2 = new edge();
		edge e3 = new edge();
		
		e1.origin = bound[0];
		e1.destin = bound[1];
		e2.origin = bound[1];
		e2.destin = bound[2];
		e3.origin = bound[2];
		e3.destin = bound[0];
		
		// give each edge next, previous and twin
		e1.next = e2;
		e1.prev = e3;
		e1.twin = null;
		
		e2.next = e3;
		e2.prev = e1;
		e2.twin = null;
		
		e3.next = e1;
		e3.prev = e2;
		e3.twin = null;
		
		triangle temp = new triangle();
		temp.triEdge1 = e1;
		temp.triEdge2 = e2;
		temp.triEdge3 = e3;
		
		// children are null before triangulation
		temp.child1 = null;
		temp.child2 = null;
		temp.child3 = null;
		root = temp;
	}
	
	// print leaves of tree
	// NOTE: leaves are triangles
	static void printLeaf(triangle myTriangle) throws IOException
	{
		triangle temp = new triangle();
		myTriangle.flag = 1; // triangle has been visited
		temp = myTriangle;
		
		if (temp.child1 == null) {
			triangleNumber++;
			screen.println(temp.triEdge1.origin.x + "," + temp.triEdge1.origin.y + "->" + 
			temp.triEdge1.destin.x + "," + temp.triEdge1.destin.y + "->" + temp.triEdge1.next.destin.x + "," + temp.triEdge1.next.destin.y);
			
			// put coords in output file
			outputFile.println(temp.triEdge1.origin.x);
			outputFile.println(temp.triEdge1.origin.y);
			outputFile.println(temp.triEdge1.destin.x);
			outputFile.println(temp.triEdge1.destin.y);
			outputFile.println(temp.triEdge1.next.destin.x);
			outputFile.println(temp.triEdge1.next.destin.y);
			
			return;
		}
		
		if (temp.child1 != null && temp.child1.flag == 0) printLeaf(temp.child1);
		if (temp.child2 != null && temp.child2.flag == 0) printLeaf(temp.child2);
		if (temp.child3 != null && temp.child3.flag == 0) printLeaf(temp.child3);
	}
	
	// count created triangles and search from graph
	static void getNumberOfTriangles(triangle myTriangle)
	{
		triangle temp = new triangle();
		myTriangle.flag1 = 1;
		temp = myTriangle;
		
		if (temp.child1 == null) {
			triangleNumber1++;
			return;
		}
		if (temp.child1 != null && temp.child1.flag1 == 0) getNumberOfTriangles(temp.child1);
		if (temp.child2 != null && temp.child2.flag1 == 0) getNumberOfTriangles(temp.child2);
		if (temp.child3 != null && temp.child3.flag1 == 0) getNumberOfTriangles(temp.child3);
	}
	
	// splitting and flipping
	// This monstrosity of a method is brought to you by the textbook pdf that I followed along with.
	// I would just like to point out that I hated every second of writing this method because it is painful to look at.
	static void splitAndFlip(vertex myNode, triangle myTriangle)
	{
		int i,j;
		int d1,d2,d3;
		
		triangle temp = new triangle();
		triangle temp1 = new triangle();
		triangle temp2 = new triangle();
		triangle temp3 = new triangle();
		triangle temp4 = new triangle();
		
		// verify if point lies on edge
		d1 = criter(myTriangle.triEdge1.origin.x,
				myTriangle.triEdge1.origin.y,
				myTriangle.triEdge1.destin.x,
				myTriangle.triEdge1.destin.y,
				myNode.x, myNode.y);
		
		d2 = criter(myTriangle.triEdge2.origin.x,
				myTriangle.triEdge2.origin.y,
				myTriangle.triEdge2.destin.x,
				myTriangle.triEdge2.destin.y,
				myNode.x, myNode.y);
		
		d3 = criter(myTriangle.triEdge3.origin.x,
				myTriangle.triEdge3.origin.y,
				myTriangle.triEdge3.destin.x,
				myTriangle.triEdge3.destin.y,
				myNode.x, myNode.y);
		
		// if a point lies within a triangle, must split into three triangles
		if (d1 != 0 && d2 != 0 && d3 != 0) {
			// get info and split
			edge e1 = new edge();
			edge twin1 = new edge();
			edge e2 = new edge();
			edge twin2 = new edge();
			edge e3 = new edge();
			edge twin3 = new edge();
			
			e1.origin = myNode;
			e1.destin = myTriangle.triEdge1.origin;
			twin1.origin = e1.destin;
			twin1.destin = e1.origin;
			e1.twin = twin1;
			twin1.twin = e1;
			
			e2.origin = myNode;
			e2.destin = myTriangle.triEdge1.destin;
			twin2.origin = e2.destin;
			twin2.destin = e2.origin;
			e2.twin = twin2;
			twin2.twin = e2;
			
			e3.origin = myNode;
			e3.destin = myTriangle.triEdge2.destin;
			twin3.origin = e3.destin;
			twin3.destin = e3.origin;
			e3.twin = twin3;
			twin3.twin = e3;
			
			e1.next = myTriangle.triEdge1;
			e1.prev = twin2;
			myTriangle.triEdge1.next = twin2;
			myTriangle.triEdge1.prev = e1;
			twin2.next = e1;
			twin2.prev = myTriangle.triEdge1;
			
			temp1.triEdge1 = e1;
			temp1.triEdge2 = myTriangle.triEdge1;
			temp1.triEdge3 = twin2;
			updateEdge(temp1);
			
			e2.next = myTriangle.triEdge2;
			e2.prev = twin3;
			myTriangle.triEdge2.next = twin3;
			myTriangle.triEdge2.prev = e2;
			twin3.next = e2;
			twin3.prev = myTriangle.triEdge2;
			
			temp2.triEdge1 = e2;
			temp2.triEdge2 = myTriangle.triEdge2;
			temp2.triEdge3 = twin3;
			updateEdge(temp2);
			
			e3.next = myTriangle.triEdge3;
			e3.prev = twin1;
			myTriangle.triEdge3.next = twin1;
			myTriangle.triEdge3.prev = e3;
			twin1.next = e3;
			twin1.prev = myTriangle.triEdge3;
			
			temp3.triEdge1 = e3;
			temp3.triEdge2 = myTriangle.triEdge3;
			temp3.triEdge3 = twin1;
			updateEdge(temp3);
			
			temp1.child1 = temp1.child2 = temp1.child3 = null;
			temp2.child1 = temp2.child2 = temp2.child3 = null;
			temp3.child1 = temp3.child2 = temp3.child3 = null;
			
			myTriangle.child1 = temp1;
			myTriangle.child2 = temp2;
			myTriangle.child3 = temp3;
			
			// flip each edge of triangle until legal triangle is found
			legalizeEdge(myNode, myTriangle.triEdge1);
			legalizeEdge(myNode, myTriangle.triEdge2);
			legalizeEdge(myNode, myTriangle.triEdge3);
		} else {
			// if point is on edge of triangle, it splits into 4
			edge tempEdge = new edge();
			
			edge e1 = new edge();
			edge twin1 = new edge();
			edge e2 = new edge();
			edge twin2 = new edge();
			edge e3 = new edge();
			edge twin3 = new edge();
			edge e4 = new edge();
			edge twin4 = new edge();
			
			// look for edge containing the point
			if (d1 == 0) tempEdge = myTriangle.triEdge1; 
			if (d2 == 0) tempEdge = myTriangle.triEdge2; 
			if (d3 == 0) tempEdge = myTriangle.triEdge3;
			
			// find corresponding twin's edge
			temp = findTriangle(root, tempEdge.twin);
			
			// create 4 new triangles and set them as child of current
			e1.origin = tempEdge.origin;
			e1.destin = myNode;
			twin1.origin = e1.destin;
			twin1.destin = e1.origin;
			e1.twin = twin1;
			twin1.twin = e1;
			
			e2.origin = myNode;
			e2.destin = tempEdge.destin;
			twin2.origin = e2.destin;
			twin2.destin = e2.origin;
			e2.twin = twin2;
			twin2.twin = e2;
			
			e3.origin = myNode;
			e3.destin = tempEdge.next.destin;
			twin3.origin = e3.destin;
			twin3.destin = e3.origin;
			e3.twin = twin3;
			twin3.twin = e3;
			
			e4.origin = myNode;
			e4.destin = tempEdge.twin.next.destin;
			twin4.origin = e4.destin;
			twin4.destin = e4.origin;
			e4.twin = twin4;
			twin4.twin = e4;
			
			e1.next = e3;
			e1.prev = tempEdge.prev;
			e3.next = tempEdge.prev;
			e3.prev = e1;
			tempEdge.prev.next = e1;
			tempEdge.prev.prev = e3;
			
			temp1.triEdge1 = e1;
			temp1.triEdge2 = e3;
			temp1.triEdge3 = tempEdge.prev;
			updateEdge(temp1); 
			
			e2.next = tempEdge.next;
			e2.prev = twin3;
			tempEdge.next.next = twin3;
			tempEdge.next.prev = e2;
			twin3.next = e2;
			twin3.prev = tempEdge.next;
			
			temp2.triEdge1 = e2;
			temp2.triEdge2 = tempEdge.next;
			temp2.triEdge3 = twin3;
			updateEdge(temp2);
			
			twin1.next = tempEdge.twin.next;
			twin1.prev = twin4;
			tempEdge.twin.next.next = twin4;
			tempEdge.twin.next.prev = twin1;
			twin4.next = twin1;
			twin4.prev = tempEdge.twin.next;
			
			temp3.triEdge1 = twin1;
			temp3.triEdge2 = tempEdge.twin.next;
			temp3.triEdge3 = twin4;
			updateEdge(temp3);
			
			twin2.next = e4;
			twin2.prev = tempEdge.twin.prev;
			e4.next = tempEdge.twin.prev;
			e4.prev = twin2;
			tempEdge.twin.prev.next = twin2;
			tempEdge.twin.prev.prev = e4;
			
			temp4.triEdge1 = twin2;
			temp4.triEdge2 = e4;
			temp4.triEdge3 = tempEdge.twin.prev;
			updateEdge(temp4);
			
			temp1.child1 = temp1.child2 = temp1.child3 = null;
			temp2.child1 = temp2.child2 = temp2.child3 = null;
			
			myTriangle.child1 = temp1;
			myTriangle.child2 = temp2;
			myTriangle.child3 = null;
			
			temp1.child1 = temp1.child2 = temp1.child3 = null;
			temp2.child1 = temp2.child2 = temp2.child3 = null;
			temp3.child1 = temp3.child2 = temp3.child3 = null;
			temp4.child1 = temp4.child2 = temp4.child3 = null;
			
			temp.child1 = temp3;
			temp.child2 = temp4;
			temp.child3 = null;
			
			// flip edges that bounds these 4 triangles
			legalizeEdge(myNode, tempEdge.prev);
			legalizeEdge(myNode, tempEdge.next);
			legalizeEdge(myNode, tempEdge.twin.next);
			legalizeEdge(myNode, tempEdge.twin.prev);
		}
	}
	
	// give point and find triangle
	static triangle query(vertex myNode)
	{
		int d1,d2,d3;
		
		triangle temp = new triangle();
		triangle temp4 = new triangle();
		
		// search tree until leaves found, find triangle from leaves
		temp = root;
		while (temp.child1 != null) {
			temp4 = temp;
			for (int i=1; i<=3; i++) {
				if (i == 1 && temp4.child1 != null) temp = temp4.child1;
				if (i == 2 && temp4.child2 != null) temp = temp4.child2;
				if (i == 3 && temp4.child3 != null) temp = temp4.child3;
				
				// if point lies on left edge of triangle, triangle must contain point
				d1 = criter(temp.triEdge1.origin.x, temp.triEdge1.origin.y, 
						temp.triEdge1.destin.x, temp.triEdge1.destin.y, myNode.x, myNode.y);
				
				d2 = criter(temp.triEdge2.origin.x, temp.triEdge2.origin.y, 
						temp.triEdge2.destin.x, temp.triEdge2.destin.y, myNode.x, myNode.y);
				
				d3 = criter(temp.triEdge3.origin.x, temp.triEdge3.origin.y, 
						temp.triEdge3.destin.x, temp.triEdge3.destin.y, myNode.x, myNode.y);
				
				if (d1 <= 0 && d2 <= 0 && d3 <= 0) break;
			}
		}
		
		return temp;
	}
	
	// recursively flip edge until you get legal edge
	static void legalizeEdge(vertex p, edge e)
	{
		System.out.println("legalizeEdge()");
		int cir;
		
		// reaches initial triangle's edge
		if (e.twin == null) {
			return;
		} else {
			// verify if edge is legal
			cir = verifyCircle(e.origin, e.destin, p, e.twin.next.destin);
			
			screen.println(cir);
			if (cir > 0) {
				return; // edge is legal, recursion finished
			} else {
				triangle triangle1 = new triangle();
				triangle triangle2 = new triangle();
				triangle temp1 = new triangle();
				triangle temp2 = new triangle();
				
				edge newEdge = new edge();
				edge newTwin = new edge();
				edge tempEdge1 = new edge();
				edge tempEdge2 = new edge();
				
				temp1 = findTriangle(root, e); // find triangle incident to edge e
											   // error
				temp2 = findTriangle(root, e.twin); // same as above but for e.twin
				
				// update triangles created due to flipping
				newEdge.origin = p;
				newEdge.destin = e.twin.next.destin;
				newTwin.origin = e.twin.next.destin;
				newTwin.destin = p;
				
				newEdge.next = e.twin.prev;
				newEdge.prev = e.next;
				e.twin.prev.next = e.next;
				e.twin.prev.prev = newEdge;
				e.next.next = newEdge;
				e.next.prev = e.twin.prev;
				
				newTwin.next = e.prev;
				newTwin.prev = e.twin.next;
				e.prev.next = e.twin.next;
				e.prev.prev = newTwin;
				e.twin.next.next = newTwin;
				e.twin.next.prev = e.prev;
				
				newEdge.twin = newTwin;
				newTwin.twin = newEdge;
				
				triangle1.triEdge1 = e.prev;
				triangle1.triEdge2 = e.twin.next;
				triangle1.triEdge3 = newTwin;
				
				tempEdge1 = triangle1.triEdge2;
				
				triangle2.triEdge1 = newEdge;
				triangle2.triEdge2 = e.twin.prev;
				triangle2.triEdge3 = e.next;
				
				tempEdge2 = triangle2.triEdge2;
				/*
				 * triangle1.triEdge3.twin = triangle2.triEdge1;
				 * triangle2.triEdge1.twin = triangl1.triEdge3;
				 */
				
				// set new triangles as children of flipped triangles
				triangle1.child1 = triangle1.child2 = triangle1.child3 = null;
				triangle2.child1 = triangle2.child2 = triangle2.child3 = null;
				
				temp1.child1 = triangle1;
				temp1.child2 = triangle2;
				temp1.child3 = null;
				
				temp2.child1 = triangle1;
				temp2.child2 = triangle2;
				temp2.child3 = null;
				
				legalizeEdge(p, tempEdge1);
				legalizeEdge(p, tempEdge2);
			}
		}
	}
	
	// decide if point lies on left egde
	static int criter(int x1, int y1, int x2, int y2, int x3, int y3)
	{
		return x2*y3 + x1*y2 + x3*y1 - x2*y1 - x3*y2 - x1*y3;
	}
	
	// update relationship between edges of triangle
	static void updateEdge(triangle myTriangle)
	{
		System.out.println("updateEdge()");
		myTriangle.triEdge1.next = myTriangle.triEdge2;
		myTriangle.triEdge1.prev = myTriangle.triEdge3;
		myTriangle.triEdge2.next = myTriangle.triEdge3;
		myTriangle.triEdge2.prev = myTriangle.triEdge1;
		// PROBLEM AREA: null pointer
		myTriangle.triEdge3.next = myTriangle.triEdge1;
		myTriangle.triEdge3.prev = myTriangle.triEdge2;
	}
	
	// this next method hurts my soul
	// calculate verification value and decide if edge is illegal
	static int verifyCircle(vertex x, vertex y, vertex z, vertex t)
	{
		int x1,x2,y1,y2,z1,z2,t1,t2,cir;
		
		x1 = x.x; x2 = x.y;
		y1 = y.x; y2 = y.y;
		z1 = z.x; z2 = z.y;
		t1 = t.x; t2 = t.y;
		
		cir = (x1*(y2*(z1*z1 + z2*z2)+t2*(y1*y1 + y2*y2)+z2*(t1*t1 + t2*t2)
				-t2*(z1*z2 + z2*z2)-y2*(t1*t1 + t2*t2)-z2*(y1*y1 + y2*y2))) 
				-
				(x2*(y1*(z1*z1 + z2*z2)+t1*(y1*y1 + y2*y2)+z1*(t1*t1 + t2*t2)
				-t1*(z1*z1 + z2*z2)-y1*(t1*t1 + t2*t2)-z1*(y1*y1 + y2*y2)))
				+
				((x1*x1 + x2*x2)*(y1*z2 + y2*t1 + t2*z1 - t1*z2 - t2*y1 - y2*z1))
				-
				(y1*z2*(t1*t1 + t2*t2) + y2*t1*(z1*z1 + z2*z2) + z1*t2*(y1*y1 + y2*y2)
				- (y1*y1 + y2*y2)*z2*t1 - (z1*z1 + z2*z2)*t2*y1 - (t1*t1 + t2*t2)*y2*z1);
		
		return cir;
	}
	
	// look for triangle with specified edge from triangulation
	static triangle findTriangle(triangle myTriangle, edge myEdge)
	{
		System.out.println("findTriangle()");
		triangle temp = new triangle();
		triangle temp1 = new triangle();
		temp = myTriangle;
		
		if (temp.triEdge1 == myEdge || temp.triEdge2 == myEdge || // error: null ptr
			temp.triEdge3 == myEdge && (temp.child1 == null)) {
			return temp;
		} else {
			temp1 = findTriangle(temp.child1, myEdge); // error: null ptr
			if (temp1 == null) {
				temp1 = findTriangle(temp.child2, myEdge);
				if (temp1 == null) {
					temp1 = findTriangle(temp.child3, myEdge);
				}
			}
			return temp1;
		}
	}
	
	// Quicksort
	static void quicksort(float[] a, int[] b, int m, int n)
	{
		if (m < n) {
			int p = partition(a, b, m, n);
			quicksort(a, b, m, p-1);
			quicksort(a, b, p+1, n);
		}
	}
	
	static int partition(float[] a, int[] b, int i, int j)
	{
		float pivot, temp;
		int middle, p, ch;
		
		middle = (i+j)/2;
		pivot = a[middle];
		a[middle] = a[i];
		a[i] = pivot;
		p = i;
		
		for (int k=i+1; k<=j; k++) {
			if (a[k] < pivot) {
				temp = a[++p];
				a[p] = a[k];
				a[k] = temp;
				ch = b[p];
				b[p] = b[k]; // Boston Pizza = Burger King
				b[k] = ch;
			}
		}
		temp = a[i];
		a[i] = a[p];
		a[p] = temp;
		
		return p;
	}
}

public class DelaunayTriangulator extends tree {
	
	public DelaunayTriangulator(List<KeyPoint> listOfKeypoints1) {
		int k, indexAfterRandom;
		triangle temp = new triangle();
		int pointNumber;
		int maxNumberOfPoint = 100;
		
		try {
			init1(); // open output file and store vertices
		} catch (IOException e) {
			System.err.print("Something went wrong!");
		}
		
		// init not working
		try {
			init(listOfKeypoints1); // read coords of all points
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		getStart(); // get intial triangle
		
		pointNumber = listOfKeypoints1.size();
		
		float[] randomNumber = new float[maxNumberOfPoint];
		int[] randomIndex = new int[maxNumberOfPoint];
		
		Random value = new Random();
		
		for (int i=0; i<pointNumber; i++) {
			randomNumber[i] = value.nextFloat();
			randomIndex[i] = i;
		}
		
		quicksort(randomNumber, randomIndex, 0, pointNumber-1);
		
		for (int i=0; i<pointNumber; i++) {
			indexAfterRandom = randomIndex[i];
			
			temp = query(node[indexAfterRandom]); // location of point
			
			splitAndFlip(node[indexAfterRandom], temp); 
		}
		
		getNumberOfTriangles(root);
		
		outputFile.println(triangleNumber1);
		
		try {
			printLeaf(root);
		} catch (Exception e) {
			System.err.print("Something went wrong!");
		}
		
		outputFile.close();
		
		for (int i=0; i<pointNumber; i++) {
			screen.println(randomIndex[i]);
		}
		System.out.println("Finished");
	}
}























