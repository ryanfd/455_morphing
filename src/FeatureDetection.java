import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.AKAZE;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

class FeatureDetection {
    public FeatureDetection() {
    	//load opencv
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    	
    	ArrayList<DPolygon> polys=new ArrayList<DPolygon>();
    	ArrayList<DPolygon> polys2=new ArrayList<DPolygon>();
    	ArrayList<DPolygon> destPolys=new ArrayList<DPolygon>();
    	
    	//read images (to be replaced by user input)
    	Mat srcIn1 = Imgcodecs.imread("smallDahyun.jpg", Imgcodecs.IMREAD_ANYCOLOR);
    	Mat srcIn2 = Imgcodecs.imread("jy_head.jpg", Imgcodecs.IMREAD_ANYCOLOR);
    	Mat src1 = new Mat(1000, 1000, srcIn1.type());
    	Mat src2 = new Mat(1000, 1000, srcIn2.type());
    	srcIn1.copyTo(src1);
    	srcIn2.copyTo(src2);
    	Mat result = new Mat(srcIn1.height(), srcIn1.width(), srcIn1.type());
    	
    	if (src1.empty() || src2.empty()) {
    		System.err.print("Can't read image");
    		System.exit(0);
    	} else {
    		System.out.print("I hope this works");
    	}
    	
    	//get convolution homography matrix and set up factory pattern
    	File file = new File("H1to3p.xml");
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db;
    	Document d;
    	
    	Mat homography = new Mat(3, 3, CvType.CV_64F);
    	double[] homographyData = new double[(int) (homography.total()*homography.channels())];
    	
    	try {
    		db = dbf.newDocumentBuilder();
    		d = db.parse(file);
    		String homographyStr = d.getElementsByTagName("data").item(0).getTextContent();
    		String[] splitted = homographyStr.split("\\s+");
    		int i = 0;
    		for (String s : splitted) {
    			if (!s.isEmpty()) {
    				homographyData[i] = Double.parseDouble(s);
    				i++;
    			}
    		}
    	} catch (ParserConfigurationException e) {
    		e.printStackTrace();
    		System.exit(0);
    	} catch (SAXException e) {
    		e.printStackTrace();
    		System.exit(0);
    	} catch (IOException e) {
    		e.printStackTrace();
    		System.exit(0);
    	}
    	
    	//store homography
    	homography.put(0,  0, homographyData);
    	
    	/*detecting keypoints and compute descriptors with AKAZE*/
    	AKAZE akaze = AKAZE.create();
    	MatOfKeyPoint keyPoints1 = new MatOfKeyPoint();
    	MatOfKeyPoint keyPoints2 = new MatOfKeyPoint();
    	Mat descriptor1 = new Mat();
    	Mat descriptor2 = new Mat();
    	akaze.detectAndCompute(src1, new Mat(), keyPoints1, descriptor1);
    	akaze.detectAndCompute(src2, new Mat(), keyPoints2, descriptor2);

    	/* using the brute-force matcher to find matches between images*/
    	DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
    	List<MatOfDMatch> knnMatches = new ArrayList<>();
    	matcher.knnMatch(descriptor1, descriptor2,  knnMatches, 2);
    	
    	/*use matches and ratio criterion to find correct keypoint matches*/
    	float ratioThreshold = 0.8f;
    	List<KeyPoint> listOfMatched1 = new ArrayList<>();
    	List<KeyPoint> listOfMatched2 = new ArrayList<>();
    	List<KeyPoint> listOfKeypoints1 = keyPoints1.toList();
    	List<KeyPoint> listOfKeypoints2 = keyPoints2.toList();
    	
    	for (int i = 0; i < knnMatches.size(); i++) {
    		DMatch[] matches = knnMatches.get(i).toArray();
    		float dist1 = matches[0].distance;
    		float dist2 = matches[1].distance;
    		
    		if (dist1 < ratioThreshold * dist2) {
    			listOfMatched1.add(listOfKeypoints1.get(matches[0].queryIdx));
    			listOfMatched2.add(listOfKeypoints2.get(matches[0].trainIdx));
    		}
    	}
    	
    	/* check if our matches fit in homography model*/
    	double inlierThreshold = 150; 
    	List<KeyPoint> listOfInliers1 = new ArrayList<>();
    	List<KeyPoint> listOfInliers2 = new ArrayList<>();
    	List<DMatch> listOfGoodMatches = new ArrayList<>();
    	
    	for (int i = 0; i < listOfMatched1.size(); i++) {
    		Mat col = new Mat(3, 1, CvType.CV_64F);
    		double[] colData = new double[ (int) (col.total() * col.channels())];
    		colData[0] = listOfMatched1.get(i).pt.x;
    		colData[1] = listOfMatched1.get(i).pt.y;
    		colData[2] = 1.0;
    		col.put(0, 0, colData);
    		
    		Mat colRes = new Mat();
    		Core.gemm(homography, col, 1.0, new Mat(), 0.0, colRes);
    		colRes.get(0, 0, colData);
    		Core.multiply(colRes, new Scalar(1.0 / colData[2]), col);
    		col.get(0, 0, colData);
    		
    		double dist = Math.sqrt(Math.pow(colData[0] - listOfMatched2.get(i).pt.x, 2) +
                    Math.pow(colData[1] - listOfMatched2.get(i).pt.y, 2));
    		
//    		System.out.println(dist);
    		System.out.println("dist: " + dist);
    		System.out.println("inlier: " + inlierThreshold);
    		if (dist < inlierThreshold) {
    			listOfGoodMatches.add(new DMatch(listOfInliers1.size(), listOfInliers2.size(), 0));
    			listOfInliers1.add(listOfMatched1.get(i));
    			listOfInliers2.add(listOfMatched2.get(i));
    		}
    	}
    	
    	/*outputting test results*/
    	Mat res = new Mat();
        MatOfKeyPoint inliers1 = new MatOfKeyPoint(listOfInliers1.toArray(new KeyPoint[listOfInliers1.size()]));
        MatOfKeyPoint inliers2 = new MatOfKeyPoint(listOfInliers2.toArray(new KeyPoint[listOfInliers2.size()]));
        MatOfDMatch goodMatches = new MatOfDMatch(listOfGoodMatches.toArray(new DMatch[listOfGoodMatches.size()]));
        Features2d.drawMatches(src1, inliers1, src2, inliers2, goodMatches, res);
        Imgcodecs.imwrite("akaze_result.png", res);
        double inlierRatio = listOfInliers1.size() / (double) listOfMatched1.size();
        System.out.println("A-KAZE Matching Results");
        System.out.println("*******************************");
        System.out.println("# Keypoints 1:                        \t" + listOfKeypoints1.size());
        System.out.println("# Keypoints 2:                        \t" + listOfKeypoints2.size());
        System.out.println("# Matches:                            \t" + listOfMatched1.size());
        System.out.println("# Inliers 1:                            \t" + listOfInliers1.size());
        System.out.println("# Inliers 2:                            \t" + listOfInliers2.size());
        System.out.println("# Inlier Ratio:                      \t" + inlierRatio);
        HighGui.imshow("result", res);
        HighGui.waitKey();
        
        // Delaunay Triangulation
        double height=src1.height();
		double width=src1.width();
		ArrayList<DPoint> points=new ArrayList<DPoint>();

		for(int i=0; i<listOfInliers1.size(); i++) 
		{
			points.add(new DPoint( listOfInliers1.get(i).pt.x, listOfInliers1.get(i).pt.y
//					,10000*r.nextDouble()
					)); 
		}
		
		ArrayList<DPoint> points2=new ArrayList<DPoint>();

		for(int i=0; i<listOfInliers2.size(); i++) 
		{
			points2.add(new DPoint( listOfInliers2.get(i).pt.x, listOfInliers2.get(i).pt.y
//					,10000*r.nextDouble()
					)); 
		}
		
		BowyerWatson bw=new BowyerWatson(width,height,points);
		BowyerWatson bw2=new BowyerWatson(src2.width(),src2.height(),points2);
		
		//set from before
		polys = bw.getPolygons();
		polys2 = bw2.getPolygons();
//		System.out.println(bw.triangles.size());
		
		//get in between target points for both images
		int destI = 0;
		DPolygon toAdd = null;
		for (DPolygon poly:polys) {
			ArrayList<DPoint> pts = new ArrayList<DPoint>();
			DPoint a, b, c;
			
			a = poly.polygon.get(0).getMean(polys2.get(destI).polygon.get(0));
			b = poly.polygon.get(1).getMean(polys2.get(destI).polygon.get(1));
			c = poly.polygon.get(2).getMean(polys2.get(destI).polygon.get(2));
			
			pts.add(a);
			pts.add(b);
			pts.add(c);
			
			toAdd = new DPolygon(pts);
			
			destPolys.add(toAdd);
			++destI;
		}
		//now we have the midpoint by finding average of triangles
		
		
		//for each triangle in first source\
		destI = 0;
		System.out.println("POLYS: " + polys.size());
		for (DPolygon poly:polys) {
			System.out.println(destI);
			//pick corresponding triangle in destination morph
			ArrayList<Point> srcTri = new ArrayList<Point>();
	        srcTri.add(new Point( poly.polygon.get(0).x, poly.polygon.get(0).y ));
	        srcTri.add(new Point( poly.polygon.get(1).x, poly.polygon.get(1).y ));
	        srcTri.add(new Point( poly.polygon.get(2).x, poly.polygon.get(2).y ));
	        
	        ArrayList<Point> src2Tri = new ArrayList<Point>();
	        src2Tri.add(new Point( polys2.get(destI).polygon.get(0).x, polys2.get(destI).polygon.get(0).y ));
	        src2Tri.add(new Point( polys2.get(destI).polygon.get(1).x, polys2.get(destI).polygon.get(1).y ));
	        src2Tri.add(new Point( polys2.get(destI).polygon.get(2).x, polys2.get(destI).polygon.get(2).y ));
	        
	        ArrayList<Point> dstTri = new ArrayList<Point>();
	        dstTri.add(new Point( destPolys.get(destI).polygon.get(0).x, destPolys.get(destI).polygon.get(0).y ));
	        dstTri.add(new Point( destPolys.get(destI).polygon.get(1).x, destPolys.get(destI).polygon.get(1).y ));
	        dstTri.add(new Point( destPolys.get(destI).polygon.get(2).x, destPolys.get(destI).polygon.get(2).y ));

	        Mat pm1 = new Mat();
	        
	         pm1 = Converters.vector_Point_to_Mat(srcTri);
	        
	        Mat pm2 = Converters.vector_Point_to_Mat(src2Tri);
	        System.out.println(pm1);
	        Mat pmDest = Converters.vector_Point_to_Mat(dstTri);
	        
	        result = morphTriangle(src1, src2, result, pm1, pm2, pmDest, poly, polys2.get(destI), destPolys.get(destI), 0.5);
//	        System.out.println(destI);	        
//	        		
//	        Point center = new Point(centerX, centerY);
//	        double angle = 0;
//	        double scale = 1;
//	        Mat rotMat = Imgproc.getRotationMatrix2D( center, angle, scale );
//	        Mat warpRotateDst = new Mat();
//	        Imgproc.warpAffine( warpDst, warpRotateDst, rotMat, warpDst.size() );
	        HighGui.imshow( "Source image", src1 );
//	        HighGui.waitKey(0);

			++destI;
			System.out.println("MorphLoop: " + destI);
		}
		
		
//		DTriangle x=new DTriangle(new DPoint(0,0),new DPoint(100,0),new DPoint(10,10));
		//System.out.println(bw.toString());
	    JFrame window = new JFrame();
	    window.setBounds(0, 0, 1000, 1000);
	    //window.getContentPane().add(new Polygons(bw.getPolygons()));
	    HashSet<DEdge> full_edges=bw.getPrunEdges();
	    Kruskal k=new Kruskal(points,full_edges);
	    window.getContentPane().add(new Lines(full_edges,k.getMST()));
	    window.setVisible(true);
	    System.out.println("Morph Complete");
    }
    
 // Apply affine transform calculated using srcTri and dstTri to src
    void applyAffineTransform(Mat warpDst, Mat src, Point[] srcTri, Point[] dstTri)
    {
        
        // Given a pair of triangles, find the affine transform.
        Mat warpMat = Imgproc.getAffineTransform( new MatOfPoint2f(srcTri), new MatOfPoint2f(dstTri) );
        
        // Apply the Affine Transform just found to the src image
        Imgproc.warpAffine( src, warpDst, warpMat, warpDst.size(), Imgproc.INTER_LINEAR);
    }
    
 // Warps and alpha blends triangular regions from img1 and img2 to result
    public Mat morphTriangle(Mat img1, Mat img2, Mat result, Mat t1, Mat t2, Mat tDest, DPolygon p1, DPolygon p2, DPolygon pDest, double alpha)
    {
        
        // Find bounding rectangle for each triangle
        Rect r = Imgproc.boundingRect(tDest);
        Rect r1 = Imgproc.boundingRect(t1);
        Rect r2 = Imgproc.boundingRect(t2);
        
        
        // Offset points by left top corner of the respective rectangles
        Point[] t1Rect = new Point[3], t2Rect = new Point[3], tRect = new Point[3];
        ArrayList<Point> tRectInt = new ArrayList<Point>();
        for(int i = 0; i < 3; i++)
        {
            tRect[i] = new Point( pDest.polygon.get(i).x - r.x, pDest.polygon.get(i).y -  r.y);
            tRectInt.add(new Point(pDest.polygon.get(i).x - r.x, pDest.polygon.get(i).y - r.y)) ; // for fillConvexPoly
            
            t1Rect[i] = new Point( p1.polygon.get(i).x - r1.x, p1.polygon.get(i).y -  r1.y);
            t2Rect[i] = new Point( p2.polygon.get(i).x - r2.x, p2.polygon.get(i).y  - r2.y);
        }
        
        // Get mask by filling triangle
//        Mat mask = new Mat();
        Mat mask = new Mat(new Size(img1.height(), img1.width()), img1.type());
//        fillConvexPoly(mask, tRectInt, Scalar(1.0, 1.0, 1.0), 16, 0);
        MatOfPoint moo = new MatOfPoint();
        moo.fromList(tRectInt);
        Imgproc.fillConvexPoly(mask, moo, new Scalar(1.0, 1.0, 1.0), 16, 0);
//        HighGui.imshow("test", mask);
        
        // Apply warpImage to small rectangular patches        
        Mat img1Rect = new Mat();
        Mat img2Rect = new Mat();
        System.out.println(r2);
        System.out.println(img2);
        r2.height = r2.height - 10;
        r2.width = r2.width - 10;
        r1.height = r1.height - 10;
        r1.width = r1.width - 10;
        System.out.println(r1.x + " " + r1.y + " " + r2.height + r2.width);
        
        Mat sub1= img1.submat(r1);
        Mat sub2= img2.submat(r2);
        sub1.copyTo(img1Rect);
        sub2.copyTo(img2Rect);
        
//        img1ROI = new Mat(img1Rect, r1);
//        img2ROI = new Mat(img2Rect, r2);
//        resultROI = new Mat(result, r);
//        img1.submat(r1).copyTo(img1Rect);
//        img2.submat(r2).copyTo(img2Rect);
        
        
        
        Mat warpImage1 = new Mat(img1.height(), img1.width(), img1.type());
        Mat warpImage2 = new Mat(img2.height(), img2.width(), img1.type());
        
        
        applyAffineTransform(warpImage1, img1Rect, t1Rect, tRect);
        applyAffineTransform(warpImage2, img2Rect, t2Rect, tRect);
//        applyAffineTransform(Mat warpDst, Mat src, Point[] srcTri, Point[] dstTri)
//        applyAffineTransform(Mat warpDst, Mat src, Point[] srcTri, Point[] dstTri)
        
        // Alpha blend rectangular patches
//        Mat imgRect = (1.0 - alpha) * warpImage1 + alpha * warpImage2;
        Mat imgRect = new Mat(img1.height(), img1.width(), img1.type());
        double beta = ( 1.0 - alpha );
        
        // mats must be same size for Core operations
        Mat resized_warpImage2 = new Mat();
        Size size = new Size(warpImage1.width(), warpImage1.height());
        Imgproc.resize(warpImage2, resized_warpImage2, size);
        Core.addWeighted((Mat)warpImage1, alpha, (Mat)resized_warpImage2, beta, 0.0, imgRect);
        
        Mat resized_mask = new Mat();
        Size maskSize = new Size(imgRect.width(), imgRect.height());
        Imgproc.resize(mask, mask, maskSize);
        
        // convert mask to 3 channel (same as imgRect)
        Imgproc.cvtColor(imgRect, imgRect, Imgproc.COLOR_RGB2GRAY);
        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_RGB2GRAY);
//        System.out.println("IMGRECT CHANNELS: " + imgRect.channels());
//        System.out.println("MASK CHANNELS: " + mask.channels());
//        System.out.println("-------------------");
        
        
        //1
        Core.multiply(imgRect, mask, imgRect);
//        resultROI.copyTo(result);
        
        //2
        mask.convertTo(mask, CvType.CV_32FC3);
        Mat temp = new Mat(result.height(), result.width(), CvType.CV_32FC3);
        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2RGB);
        Core.subtract(Mat.ones(mask.size(),CvType.CV_32FC3),mask,temp);
        
//        System.out.println("RESULT DIMENSIONS: " + result.size());
//        System.out.println("RESULT CHANNELS: " + result.channels());
//        System.out.println("TEMP DIMENSIONS: " + temp.size());
//        System.out.println("TEMP CHANNELS: " + temp.channels());
//        System.out.println("MASK DIMENSIONS: " + mask.size());
//        System.out.println("MASK CHANNELS: " + mask.channels());
//        System.out.println("-------------------");
        
        System.out.println("R DIMENSIONS: " + r.size());
        r.height = result.height();
        r.width = result.width();
        System.out.println("RESULT DIMENSIONS: " + result.size());
        System.out.println("RESULT TYPE: " + result.type());
        temp.convertTo(temp, result.type());
        System.out.println("TEMP DIMENSIONS: " + temp.size());
        System.out.println("TEMP TYPE: " + temp.type());
        System.out.println("R DIMENSIONS: " + r.size());
        System.out.println("-------------------");
        Core.multiply(result.submat(r), temp, result.submat(r));
//        result.submat(r).copyTo(result);
        
        //3
//        img(r) = img(r) + imgRect
//        Core.add(img1Rect, img2Rect, result);
//        System.out.println("RESULT DIMENSIONS: " + result.size());
//        System.out.println("RESULT TYPE: " + result.type());
//        System.out.println("RESULT CHANNELS: " + result.channels());
        Imgproc.cvtColor(imgRect,imgRect,Imgproc.COLOR_GRAY2BGR);
//        System.out.println("IMGRECT DIMENSIONS: " + imgRect.size());
//        System.out.println("IMGRECT TYPE: " + imgRect.type());
//        System.out.println("IMGRECT CHANNELS: " + imgRect.channels());
        Core.add(result.submat(r), imgRect, result.submat(r));
//        HighGui.imshow("why", temp);
//        HighGui.imshow("how", result);
//        try {
//			TimeUnit.SECONDS.sleep(1);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
        return result;
        
    }
}
        