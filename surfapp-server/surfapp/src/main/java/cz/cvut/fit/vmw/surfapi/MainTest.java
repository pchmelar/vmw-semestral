/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.cvut.fit.vmw.surfapi;

import com.stromberglabs.jopensurf.SURFInterestPoint;
import com.stromberglabs.jopensurf.Surf;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author jan
 */
public class MainTest {

    /**
     * @param args the command line arguments
     */
    String imagePath = "/Users/jan/Downloads/graffiti.png";
    String imagePath2 = "/Users/jan/Downloads/graffiti2.png";
    public static void main(String[] args) {
        MainTest main = new MainTest();

        File img = new File(main.imagePath);
        File img2 = new File(main.imagePath2);
        BufferedImage image = null;
        BufferedImage image2 = null;
//        InputStream in = new ByteArrayInputStream();

        try {
            image = ImageIO.read(img);
            image2 = ImageIO.read(img2);
        } catch (IOException ex) {
            Logger.getLogger(MainTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Surf surf = new Surf(image);
        Surf surf1 = new Surf(image2);
        
        List<SURFInterestPoint> listInterests = surf.getUprightInterestPoints();
        List<SURFInterestPoint> listInterests1 = surf1.getUprightInterestPoints();
        Map<SURFInterestPoint,SURFInterestPoint> map = surf.getMatchingPoints(surf1, false);
       
        System.out.println(map.size());
        
        
        System.out.println("IP - " + listInterests.size());
        System.out.println("IP1 - " + listInterests1.size());
        // TODO code application logic here

    }

}
