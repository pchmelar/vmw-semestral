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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author jan
 */
public class SURFApi {

    private static final java.util.logging.Logger LOG = java.util.logging.Logger.getLogger(SURFApi.class.getName());
    private static final Double MAX_THRESHOLD = 0.8;
    private static final Double MIN_THRESHOLD = 0.2;

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
        ImageFloat32 origImage = new ImageFloat32(origImageBuff.getWidth(), origImageBuff.getHeight());
        origImage = ConvertBufferedImage.convertFrom(origImageBuff, origImage);
        DetectDescribePoint<ImageFloat32, SurfFeature> surf = FactoryDetectDescribe.
                surfStable(new ConfigFastHessian(0, 2, 200, 2, 9, 4, 4), null, null, ImageFloat32.class);
        surf.detect(origImage);
        List<SurfFeature> origImageList = getSurfFeatureList(surf);
        for (PhotoFile testImage : allPhotoFiles) {
            long start = System.currentTimeMillis();
            surf = FactoryDetectDescribe.
                    surfStable(new ConfigFastHessian(0, 2, 200, 2, 9, 4, 4), null, null, ImageFloat32.class);
            BufferedImage testImageBuff = getBufferedImage(testImage);
            ImageFloat32 testImageFloat = new ImageFloat32(testImageBuff.getWidth(), testImageBuff.getHeight());
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
                    if ((distances.get(1) / distances.get(2)) < MAX_THRESHOLD && (distances.get(1) / distances.get(2)) > MIN_THRESHOLD) {
                        matches.add(sf);
                    }
                } else if ((distances.get(0) / distances.get(1)) < MAX_THRESHOLD && (distances.get(0) / distances.get(1)) > MIN_THRESHOLD) {
                    matches.add(sf);
                }
//                matches.add(sf);
            }
            long elapsedTimeMillis = System.currentTimeMillis() - start;
            float elapsedTimeSec = elapsedTimeMillis / 1000F;
            LOG.info("elapsedTimeSec: " + elapsedTimeSec);
            System.out.println("Matches: " + matches.size());
            testImage.setSimilarity((double) matches.size());
            res.add(testImage);

        }
        Collections.sort(res);
        return res;
    }

    public static List<PhotoFile> findParallel(PhotoFile origPhoto, List<PhotoFile> allPhotoFiles) {
        List<PhotoFile> res = new ArrayList<>();
        try {
            res = processInputs(allPhotoFiles, origPhoto);
        } catch (InterruptedException ex) {
            Logger.getLogger(SURFApi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(SURFApi.class.getName()).log(Level.SEVERE, null, ex);
        }
        Collections.sort(res);
        return res;
    }

    private static List<PhotoFile> processInputs(List<PhotoFile> inputs, PhotoFile origPhoto)
            throws InterruptedException, ExecutionException {

        int threads = Runtime.getRuntime().availableProcessors();
        System.out.println(threads);

        ExecutorService service = Executors.newFixedThreadPool(threads);

        BufferedImage origImageBuff = getBufferedImage(origPhoto);
        ImageFloat32 origImage = new ImageFloat32(origImageBuff.getWidth(), origImageBuff.getHeight());
        origImage = ConvertBufferedImage.convertFrom(origImageBuff, origImage);
        DetectDescribePoint<ImageFloat32, SurfFeature> surf = FactoryDetectDescribe.
                surfStable(new ConfigFastHessian(0, 2, 200, 2, 9, 4, 4), null, null, ImageFloat32.class);
        surf.detect(origImage);
        final List<SurfFeature> origImageList = getSurfFeatureList(surf);

        List<Future<PhotoFile>> futures = new ArrayList<Future<PhotoFile>>();
        for (final PhotoFile input : inputs) {
            Callable<PhotoFile> callable = new Callable<PhotoFile>() {
                @Override
                public PhotoFile call() throws Exception {
                    PhotoFile output = new PhotoFile();

                    long start = System.currentTimeMillis();
                    DetectDescribePoint<ImageFloat32, SurfFeature> surf = FactoryDetectDescribe.
                            surfStable(new ConfigFastHessian(0, 2, 200, 2, 9, 4, 4), null, null, ImageFloat32.class);
                    BufferedImage testImageBuff = getBufferedImage(input);
                    ImageFloat32 testImageFloat = new ImageFloat32(testImageBuff.getWidth(), testImageBuff.getHeight());
                    testImageFloat = ConvertBufferedImage.convertFrom(testImageBuff, testImageFloat);
                    surf.detect(testImageFloat);
//                    List<SurfFeature> compare = getSurfFeatureList(surf);
                    List<SurfFeature> matches = new ArrayList<>();

                    for (SurfFeature sf : origImageList) {
                        List<Double> distances = new ArrayList<>();
//                        for (SurfFeature cmp : compare) {
//                            Double dist = DescriptorDistance.euclideanSq(sf, cmp);
//                            distances.add(dist);
//                        }
                        
                        for (int i = 0 ; i< surf.getNumberOfFeatures() ; i++) {
                            distances.add(DescriptorDistance.euclideanSq(sf, surf.getDescription(i)));
                        }
                        
                        Collections.sort(distances);

//                        if (distances.get(0) == 0) {
//                            if ((distances.get(1) / distances.get(2)) < MAX_THRESHOLD && (distances.get(1) / distances.get(2)) > MIN_THRESHOLD) {
//                                matches.add(sf);
//                            }
//                        } else if ((distances.get(0) / distances.get(1)) < MAX_THRESHOLD && (distances.get(0) / distances.get(1)) > MIN_THRESHOLD) {
//                            matches.add(sf);
//                        }
                        if ((distances.get(0) / distances.get(1)) < MAX_THRESHOLD && (distances.get(0) / distances.get(1)) > MIN_THRESHOLD) {
                            matches.add(sf);
                        }

                    }
                    long elapsedTimeMillis = System.currentTimeMillis() - start;
                    float elapsedTimeSec = elapsedTimeMillis / 1000F;
                    LOG.info("elapsedTimeSec: " + elapsedTimeSec);
                    System.out.println("Matches: " + matches.size());
                    output.setCreateDate(input.getCreateDate());
                    output.setId(input.getId());
                    output.setSimilarity((double) matches.size());
                    return output;
                }
            };
            futures.add(service.submit(callable));
        }

        service.shutdown();

        List<PhotoFile> outputs = new ArrayList<PhotoFile>();
        for (Future<PhotoFile> future : futures) {
            outputs.add(future.get());
        }
        return outputs;
    }

    private static List<SurfFeature> getSurfFeatureList(DetectDescribePoint<ImageFloat32, SurfFeature> surf) {
        List<SurfFeature> list = new ArrayList<>();
        for (int i = 0; i < surf.getNumberOfFeatures(); i++) {
            list.add(surf.getDescription(i));
        }
        return list;
    }

}
