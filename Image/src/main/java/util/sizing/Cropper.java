package util.sizing;

import org.openimaj.image.MBFImage;
import org.openimaj.image.pixel.PixelSet;

public class Cropper {
    public static MBFImage cropSquareFromTopLeft(MBFImage image, int dimension) {
        return new PixelSet(0, 0, dimension + 1, dimension + 1)
                .crop(image, true);
    }


}
