/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.vmw.surfapi;

import java.util.Calendar;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 *
 * @author jan
 */
public class OpenCVTest {

    /**
     * @param args the command line arguments
     */
    public static String mousePath = "/Users/jan/Downloads/photos/mouse.jpg";
    public static String compoPath = "/Users/jan/Downloads/photos/compo.jpg";
    public static String photoPath = "/Users/jan/Downloads/photos/";

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Mat image01 = Highgui.imread(mousePath);
        Mat image02 = Highgui.imread(compoPath);
        if (image01 == null || image02 == null) {
            System.out.println("ﾇﾙﾎﾟ('A`)");
            System.exit(0);
        }

        Mat grayImage01 = new Mat(image01.rows(), image01.cols(), image01.type());
        Imgproc.cvtColor(image01, grayImage01, Imgproc.COLOR_BGRA2GRAY);
        Core.normalize(grayImage01, grayImage01, 0, 255, Core.NORM_MINMAX);

        Mat grayImage02 = new Mat(image02.rows(), image02.cols(), image02.type());
        Imgproc.cvtColor(image02, grayImage02, Imgproc.COLOR_BGRA2GRAY);
        Core.normalize(grayImage02, grayImage02, 0, 255, Core.NORM_MINMAX);

        FeatureDetector siftDetector = FeatureDetector.create(FeatureDetector.SIFT);
        DescriptorExtractor siftExtractor = DescriptorExtractor.create(DescriptorExtractor.SIFT);

        MatOfKeyPoint keyPoint01 = new MatOfKeyPoint();
        siftDetector.detect(grayImage01, keyPoint01);

        MatOfKeyPoint keyPoint02 = new MatOfKeyPoint();
        siftDetector.detect(grayImage02, keyPoint02);

        Mat descripters01 = new Mat(image01.rows(), image01.cols(), image01.type());
        siftExtractor.compute(grayImage01, keyPoint01, descripters01);

        Mat descripters02 = new Mat(image02.rows(), image02.cols(), image02.type());
        siftExtractor.compute(grayImage02, keyPoint02, descripters02);

        MatOfDMatch matchs = new MatOfDMatch();
        DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
        matcher.match(descripters01, descripters02, matchs);

        int N = 50;
        DMatch[] tmp01 = matchs.toArray();
        DMatch[] tmp02 = new DMatch[N];
        for (int i = 0; i < tmp02.length; i++) {
            tmp02[i] = tmp01[i];
        }
        matchs.fromArray(tmp02);

        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        int second = Calendar.getInstance().get(Calendar.SECOND);
        String now = year + "" + month + "" + day + "" + hour + "" + minute + "" + second;

        Mat matchedImage = new Mat(image01.rows(), image01.cols() * 2, image01.type());
        Features2d.drawMatches(image01, keyPoint01, image02, keyPoint02, matchs, matchedImage);

        // 出力画像 at SIFT
        Highgui.imwrite(photoPath + "descriptedImageBySIFT-" + now + ".jpg", matchedImage);

        FeatureDetector surfDetector = FeatureDetector.create(FeatureDetector.SURF);
        DescriptorExtractor surfExtractor = DescriptorExtractor.create(DescriptorExtractor.SURF);

        keyPoint01 = new MatOfKeyPoint();
        surfDetector.detect(grayImage01, keyPoint01);

        keyPoint02 = new MatOfKeyPoint();
        surfDetector.detect(grayImage02, keyPoint02);

        descripters01 = new Mat(image01.rows(), image01.cols(), image01.type());
        surfExtractor.compute(grayImage01, keyPoint01, descripters01);

        descripters02 = new Mat(image02.rows(), image02.cols(), image02.type());
        surfExtractor.compute(grayImage02, keyPoint02, descripters02);

        matchs = new MatOfDMatch();
        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE);
        matcher.match(descripters01, descripters02, matchs);

        DMatch[] tmp03 = matchs.toArray();
        DMatch[] tmp04 = new DMatch[N];
        for (int i = 0; i < tmp04.length; i++) {
            tmp04[i] = tmp03[i];
        }
        matchs.fromArray(tmp02);

        matchedImage = new Mat(image01.rows(), image01.cols() * 2, image01.type());
        Features2d.drawMatches(image01, keyPoint01, image02, keyPoint02, matchs, matchedImage);

        // 出力画像 at SURF
        Highgui.imwrite(photoPath + "descriptedImageBySURF-" + now + ".jpg", matchedImage);
    }

}
