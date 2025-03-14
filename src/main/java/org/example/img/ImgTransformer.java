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
    private String outputDir;

    public ImgTransformer(List<String> imgUrls, String dir) {
        outputDir = dir;
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
            Imgcodecs.imwrite(String.format(outputDir + i + ".jpg"), foreground);
        }
    }

    private static Mat loadImageFromUrl(String imageUrl) throws IOException {
        URL url = new URL(imageUrl);
        BufferedImage image = ImageIO.read(url);
        if (image == null) return null;

        byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);

        Mat matRGB = new Mat();
        Imgproc.cvtColor(mat, matRGB, Imgproc.COLOR_BGR2RGB);

        return matRGB;
    }

    private Mat removeBackground(Mat image) {
        // 1. Преобразование в HSV (или другое цветовое пространство, например Lab)
        Mat hsvImage = new Mat();
        Imgproc.cvtColor(image, hsvImage, Imgproc.COLOR_RGB2HSV);

        // 2. Определение диапазона цветов фона (настройте эти значения под свои изображения)
        //   Нижний предел
        Scalar lowerBound = new Scalar(0, 0, 50);
        //   Верхний предел
        Scalar upperBound = new Scalar(360, 50, 255);

        // 3. Создание маски на основе диапазона цветов
        Mat mask = new Mat();
        Core.inRange(hsvImage, lowerBound, upperBound, mask);

        // 4. Инвертирование маски (чтобы фон был черным, а передний план белым)
        Core.bitwise_not(mask, mask);

        // 5. Морфологические операции (улучшение маски)
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);

        // 6. Извлечение переднего плана
        Mat foreground = new Mat();
        image.copyTo(foreground, mask);

        return foreground;
    }
}
