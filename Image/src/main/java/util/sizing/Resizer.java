package util.sizing;

import org.apache.log4j.Logger;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import static util.color.ColorBalancer.average;

public class Resizer {
    private final static Logger LOG = Logger.getLogger(Resizer.class);

    public static MBFImage scaleDownAndCropToSquare(Path path, int scaleToPx) {
        try {
            MBFImage image = ImageUtilities.readMBF(path.toFile());

            int scaledWidthDivisor = getBestDivisor(image.getWidth(), scaleToPx);
            int scaledHeightDivisor = getBestDivisor(image.getHeight(), scaleToPx);

            // choose if width or height is best
            boolean useWidth = scaledWidthDivisor < scaledHeightDivisor;

            int cropDim = useWidth ? getCropDimension(image.getWidth(), scaleToPx) :
                    getCropDimension(image.getHeight(), scaleToPx);
            int divisor = useWidth ? scaledWidthDivisor : scaledHeightDivisor;

            LOG.info(String.format("scaledWidthDivisor: %d scaledHeightDivisor: %d  " +
                    "useWidth: %s  cropDim: %d divisor: %d", scaledWidthDivisor,
                    scaledHeightDivisor, useWidth, cropDim, divisor));

            MBFImage scaled = new MBFImage(scaleToPx, scaleToPx);
            image = pixelize(image, cropDim, cropDim, Optional.of(cropDim), path.toString());

            for (int i = 0; i < scaleToPx; i++) {
                for (int j = 0; j < scaleToPx; j++) {
                    scaled.setPixel(i, j,
                            image.getPixel(i * divisor, j * divisor));
                }
            }

            //DisplayUtilities.display(scaled);
            return scaled;
        } catch (IOException e) {
            LOG.error(e);
        }
        return null;
    }

    private static int getBestDivisor(int current, int scaleDim) {
        return (current - (current % scaleDim)) / scaleDim;
    }

    private static int getCropDimension(int current, int scaleDim) {
        return(current - (current % scaleDim));
    }

    public static MBFImage pixelize(Path pathToImage, int width, int height, Optional<Integer> boundingBox) {
        try {
            MBFImage image = ImageUtilities.readMBF(pathToImage.toFile());
            return pixelize(image, width, height, boundingBox, pathToImage.toString());
        } catch (IOException e) {
            LOG.error(e);
        }
        return null;
    }

    public static MBFImage pixelize(MBFImage image, int width, int height, Optional<Integer> boundingBox, String imageName) {
        if (boundingBox.isPresent()) {
            if (boundingBox.get() > image.getWidth() || boundingBox.get() > image.getHeight()) {
                LOG.error(String.format("[image:%s]Bounding box size larger than image: %d",
                        imageName,
                        boundingBox.get()));
                return null;
            }
            image = Cropper.cropSquareFromTopLeft(image, boundingBox.get());
        }

        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();

        int widthDivisor = imageWidth / width;
        int heightDivisor = imageHeight / height;

        // todo cleanup to better distribute from int division
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                average(image, i * widthDivisor, j * heightDivisor, widthDivisor, heightDivisor);
            }
        }

        return image;
    }
}
