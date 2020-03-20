import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import org.opencv.core.Scalar;
import org.opencv.features2d.AKAZE;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

class FeatureDetection {
    public FeatureDetection() {
    	//load opencv
    	System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    	
    	//read images (to be replaced by user input)
    	Mat src1 = Imgcodecs.imread("dh_twice.jpg", Imgcodecs.IMREAD_GRAYSCALE);
    	Mat src2 = Imgcodecs.imread("mina_twice.jpg", Imgcodecs.IMREAD_GRAYSCALE);
    	
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
        System.out.println("# Inliers:                            \t" + listOfInliers1.size());
        System.out.println("# Inlier Ratio:                      \t" + inlierRatio);
        HighGui.imshow("result", res);
        HighGui.waitKey();
        DelaunayTriangulator dt = new DelaunayTriangulator(listOfInliers1);
    }
}
        