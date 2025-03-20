package org.example.img;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImgTransformer {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    private List<Mat> images = new ArrayList<>();
    private List<String> imagesUrls;
    private String outputDir;

    public ImgTransformer(List<String> imgUrls, String dir) {
        outputDir = dir;
        imagesUrls = imgUrls;
        for (String imgUrl : imgUrls) {
            try {
                images.add(loadImageFromUrl(imgUrl));
            } catch (IOException e) {
                System.err.println("Ошибка при обработке изображения из: " + imgUrl + ": " + e.getMessage());
            }
        }
    }

    public void removeImagesBg() {
        for (int i = 0; i < images.size(); i++) {
            Mat foreground = removeBackground(images.get(i));
            Mat bgrImage = new Mat();
            Imgproc.cvtColor(foreground, bgrImage, Imgproc.COLOR_RGB2BGR);
            
            String imgUrl = imagesUrls.get(i);
            String filename = imgUrl.substring(imgUrl.lastIndexOf('/') + 1);
            Imgcodecs.imwrite(String.format(outputDir + filename), bgrImage);
        }
    }

    private static Mat loadImageFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        BufferedImage image = ImageIO.read(url);
        if (image == null) return null;

        int width = image.getWidth();
        int height = image.getHeight();
        int type = image.getType();

        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);

        BufferedImage convertedImage = image;
        if (type != BufferedImage.TYPE_3BYTE_BGR) {
            convertedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            convertedImage.getGraphics().drawImage(image, 0, 0, null);
        }

        byte[] pixels = ((DataBufferByte) convertedImage.getRaster().getDataBuffer()).getData();

        int expectedSize = width * height * 3;
        if (pixels.length != expectedSize) {
            System.err.println("Ошибка: Неправильный размер массива пикселей.  Ожидалось: " + expectedSize + ", Фактически: " + pixels.length);
            return null;
        }

        mat.put(0, 0, pixels);

        Mat matRGB = new Mat();
        Imgproc.cvtColor(mat, matRGB, Imgproc.COLOR_BGR2RGB);

        // Размытие для уменьшения шума
        Imgproc.GaussianBlur(matRGB, matRGB, new Size(3,3), 0);

        return matRGB;
    }

    private Mat removeBackground(Mat image) {
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_RGB2HSV);

        Scalar lowerBound = new Scalar(0, 0, 200);
        Scalar upperBound = new Scalar(180, 30, 255);

        Mat mask = new Mat();
        Core.inRange(hsvImage, lowerBound, upperBound, mask);

        Core.bitwise_not(mask, mask);

        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(7, 7));
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);

        Mat foreground = new Mat();
        image.copyTo(foreground, mask);

        Mat edges = new Mat();
        Imgproc.Canny(foreground, edges, 100, 200);
        Core.bitwise_or(mask, edges, mask);

        return foreground;
    }
}
