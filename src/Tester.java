//package delaunay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javax.swing.JFrame;

//import mst.Kruskal;
//import structure.DEdge;
//import structure.DPoint;
//import structure.DTriangle;
//import utils.Lines;
//import utils.Polygons;

public class Tester {

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		double height=500;
//		double width=500;
//		ArrayList<DPoint> points=new ArrayList<DPoint>();
////		points.add(new DPoint(20,20));
////		points.add(new DPoint(20,40));
////		points.add(new DPoint(60,53));
////		points.add(new DPoint(80,70));
//
//		Random r = new Random();
//		for(int i=0; i<50; i++) 
//		{
//			points.add(new DPoint( width*r.nextDouble(), height*r.nextDouble(),10000*r.nextDouble())); 
//		}
//		
//		BowyerWatson bw=new BowyerWatson(width,height,points);
//		DTriangle x=new DTriangle(new DPoint(0,0),new DPoint(100,0),new DPoint(10,10));
//		//System.out.println(bw.toString());
//	    JFrame window = new JFrame();
//	    window.setBounds(0, 0, 510, 525);
//	    //window.getContentPane().add(new Polygons(bw.getPolygons()));
//	    HashSet<DEdge> full_edges=bw.getPrunEdges();
//	    Kruskal k=new Kruskal(points,full_edges);
//	    window.getContentPane().add(new Lines(full_edges,k.getMST()));
//	    window.setVisible(true);
//	}

}