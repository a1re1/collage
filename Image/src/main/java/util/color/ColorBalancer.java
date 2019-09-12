package util.color;

import org.apache.log4j.Logger;
import org.openimaj.image.MBFImage;

import java.util.Arrays;
import java.util.Optional;

public class ColorBalancer {
    private final static Logger LOG = Logger.getLogger(ColorBalancer.class);

    public static Float[] getAverageImageColor(MBFImage image, Optional<Integer> crop) throws Exception {
        if (crop.isPresent()) {
            if (crop.get() > image.getWidth() || crop.get() > image.getHeight()) {
                throw new Exception("[crop size: " + crop.get() + "] Image too small. Please use a larger image.");
            }
            return average(image, 0, 0, crop.get(), crop.get());
        }
        return  average(image, 0, 0, image.getWidth(), image.getHeight());
    }

    public static Float[] average(MBFImage image, int xOffset, int yOffset, int widthDivisor, int heightDivisor) {
        Float[] rgb = new Float[]{0f, 0f, 0f};
        int pixels = 0;

        // first pass to get average
        for (int i = 0; i < widthDivisor; i++) {
            for (int j = 0; j < heightDivisor; j++) {
                Float[] tmp = image.getPixel(xOffset + i, yOffset + j);
                pixels++;

                // add
                rgb[0] += tmp[0];
                rgb[1] += tmp[1];
                rgb[2] += tmp[2];
            }
        }

        // average
        rgb[0] /= pixels;
        rgb[1] /= pixels;
        rgb[2] /= pixels;

        // second pass to set all pixels to average color
        LOG.debug(String.format("rgb:%s | pixels: %d", Arrays.toString(rgb), pixels));
        for (int i = 0; i < widthDivisor; i++) {
            for (int j = 0; j < heightDivisor; j++) {
                image.setPixel(xOffset + i, yOffset + j, rgb);
            }
        }

        return rgb;
    }
}
