/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.vmw.surfapi;

import boofcv.abst.feature.detdesc.DetectDescribePoint;
import boofcv.abst.feature.detect.interest.ConfigFastHessian;
import boofcv.alg.descriptor.DescriptorDistance;
import boofcv.factory.feature.detdesc.FactoryDetectDescribe;
import boofcv.io.image.ConvertBufferedImage;
import boofcv.struct.feature.SurfFeature;
import boofcv.struct.image.ImageFloat32;
import com.stromberglabs.jopensurf.SURFInterestPoint;
import com.stromberglabs.jopensurf.Surf;
import cz.cvut.fit.vmw.model.PhotoFile;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author jan
 */
public class SURFApi {

    public static List<PhotoFile> findMatches(PhotoFile origPhoto, List<PhotoFile> allPhotos) {
        List<PhotoFile> res = new ArrayList<>();
        BufferedImage origImg = getBufferedImage(origPhoto);
        if (origImg == null) {
            return res;
        }
        Surf origSurf = new Surf(origImg);
        for (PhotoFile img : allPhotos) {
            BufferedImage buffImg = getBufferedImage(img);
            Surf dbImgSurf = new Surf(buffImg);

            Map<SURFInterestPoint, SURFInterestPoint> map = origSurf.getMatchingPoints(dbImgSurf, true);
            img.setSimilarity((double) map.size());
            res.add(img);
            System.out.println("matches: " + map.size());
        }
        Collections.sort(res);
        System.out.println("DONE!!!");
        return res;
    }

    private static BufferedImage getBufferedImage(PhotoFile img) {
        InputStream in = new ByteArrayInputStream(img.getData());
        try {
            return ImageIO.read(in);
        } catch (IOException ex) {
            Logger.getLogger(SURFApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static List<PhotoFile> findMatches2(PhotoFile origPhoto, List<PhotoFile> allPhotoFiles) {
        List<PhotoFile> res = new ArrayList<>();
        BufferedImage origImageBuff = getBufferedImage(origPhoto);
        ImageFloat32 origImage = new ImageFloat32(origImageBuff.getWidth(),origImageBuff.getHeight());
        origImage = ConvertBufferedImage.convertFrom(origImageBuff, origImage);
        DetectDescribePoint<ImageFloat32, SurfFeature> surf = FactoryDetectDescribe.
                surfStable(new ConfigFastHessian(0, 2, 200, 2, 9, 4, 4), null, null, ImageFloat32.class);
        surf.detect(origImage);
        List<SurfFeature> origImageList = getSurfFeatureList(surf);
        for (PhotoFile testImage : allPhotoFiles) {
            surf = FactoryDetectDescribe.
                    surfStable(new ConfigFastHessian(0, 2, 200, 2, 9, 4, 4), null, null, ImageFloat32.class);
            BufferedImage testImageBuff = getBufferedImage(testImage);
            ImageFloat32 testImageFloat = new ImageFloat32(testImageBuff.getWidth(),testImageBuff.getHeight());
            testImageFloat = ConvertBufferedImage.convertFrom(testImageBuff, testImageFloat);
            surf.detect(testImageFloat);
            List<SurfFeature> compare = getSurfFeatureList(surf);
            List<SurfFeature> matches = new ArrayList<>();
            
            for (SurfFeature sf : origImageList) {
                List<Double> distances = new ArrayList<>();
                for (SurfFeature cmp : compare) {
                    Double dist = DescriptorDistance.euclidean(sf, cmp);
                    distances.add(dist);
                }
                Collections.sort(distances);
//                System.out.println("distance0 = " + distances.get(0));
//                System.out.println("distance1 = " + distances.get(1));
//                System.out.println("ration: " + (distances.get(0) / distances.get(1)));
//                System.out.println("---------");
                if (distances.get(0) == 0) {
                    if ((distances.get(1) / distances.get(2)) < 0.7 && (distances.get(1) / distances.get(2)) > 0.3) {
                        matches.add(sf);
                    }
                } else if ((distances.get(0) / distances.get(1)) < 0.7 && (distances.get(0) / distances.get(1)) > 0.3) {
                    matches.add(sf);
                }
            }
            System.out.println("Matches: " + matches.size());
            testImage.setSimilarity((double)matches.size());
            res.add(testImage);

        }
        Collections.sort(res);
        return res;
    }

    private static List<SurfFeature> getSurfFeatureList(DetectDescribePoint<ImageFloat32, SurfFeature> surf) {
        List<SurfFeature> list = new ArrayList<>();
        for (int i = 0; i < surf.getNumberOfFeatures(); i++) {
            list.add(surf.getDescription(i));
        }
        return list;
    }

}
