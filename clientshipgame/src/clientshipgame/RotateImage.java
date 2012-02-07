package clientshipgame;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.MediaTracker;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import javax.swing.JFrame;

/**
 * RotateImage45Degrees.java - 1. scales an image's dimensions by a factor of
 * two 2. rotates it theta degrees around the image center 3. displays the
 * processed image.
 * 
 * This was taken and modified for the purpose of this assignnment from
 * Java Media APIs: Cross-Platform Imaging, Media and Visualization
 * Alejandro Terrazas
 * Sams, Published November 2002, 
 * ISBN 0672320940
 */
public class RotateImage extends JFrame {
  private Image inputImage;

  public BufferedImage sourceBI;

  public BufferedImage destinationBI = null;

  private Insets frameInsets;
  int angle;

  private boolean sizeSet = false;

  /**
   * Rotate the given image by theta amount
   * @param b
   * @param theta
   * @return 
   */
  public BufferedImage RotateImage(BufferedImage b, int theta) {
    addNotify();
    angle = theta;
    frameInsets = getInsets();

    MediaTracker mt = new MediaTracker(this);
    mt.addImage(b, 0);
    try {
      mt.waitForID(0);
    } catch (InterruptedException ie) {
    }


    Graphics2D g = (Graphics2D) b.getGraphics();
    g.drawImage(b, 0, 0, null);

    AffineTransform at = new AffineTransform();

    // rotate theta degrees around image center
    at.rotate(angle * Math.PI / 180.0, b.getWidth() / 2.0, b
        .getHeight() / 2.0);

    /*
     * translate to make sure the rotation doesn't cut off any image data
     */
    AffineTransform translationTransform;
    translationTransform = findTranslation(at, b);
    at.preConcatenate(translationTransform);

    // instantiate and apply affine transformation filter
    BufferedImageOp bio;
    bio = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);

    destinationBI = bio.filter(b, null);
    

     b = destinationBI;
     
    return destinationBI;
     
  }

  /*
   * Find proper translations to keep rotated image correctly displayed
   */
  private AffineTransform findTranslation(AffineTransform at, BufferedImage bi) {
    Point2D p2din, p2dout;

    p2din = new Point2D.Double(0.0, 0.0);
    p2dout = at.transform(p2din, null);
    double ytrans = p2dout.getY();

    p2din = new Point2D.Double(0, bi.getHeight());
    p2dout = at.transform(p2din, null);
    double xtrans = p2dout.getX();

    AffineTransform tat = new AffineTransform();
    tat.translate(-xtrans, -ytrans);
    return tat;
  }

}
