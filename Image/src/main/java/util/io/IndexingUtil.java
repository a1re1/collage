package util.io;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.inject.Inject;
import immutables.ImmutableIndexedImage;
import org.apache.log4j.Logger;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static util.color.ColorBalancer.getAverageImageColor;

public class IndexingUtil {
    private final static Logger LOG = Logger.getLogger(IndexingUtil.class);

    private final Multimap<String, ImmutableIndexedImage> index;
    private final ObjectMapper mapper;

    @Inject
    public IndexingUtil(ObjectMapper mapper) {
        this.mapper = mapper;
        index = MultimapBuilder.hashKeys().arrayListValues().build();
    }

    public void indexImage(Path pathToImage, Optional<Integer> cropSize, int tolerance) {
        try {
            MBFImage mbfImage = ImageUtilities.readMBF(pathToImage.toFile());
            Float[] averageColor = getAverageImageColor(mbfImage, cropSize);

            ImmutableIndexedImage.Builder builder = ImmutableIndexedImage.builder()
                    .averageColor(averageColor)
                    .pathToImage(pathToImage);
            cropSize.ifPresent(builder::cropWidth);

            index.put(Arrays.toString(applyTolerance(averageColor, tolerance)), builder.build());
        } catch (Exception e) {
            LOG.error(String.format("Could not index image: %s due to: %s",
                    pathToImage.getFileName(),
                    e.getLocalizedMessage()));
        }
    }

    public static Integer[] applyTolerance(Float[] rgb, int tolerance) {
        Integer[] newRgb = new Integer[3];
        newRgb[0] = getClostNDivisibleByM(getRgbFromFloat(rgb[0]), tolerance);
        newRgb[1] = getClostNDivisibleByM(getRgbFromFloat(rgb[1]), tolerance);
        newRgb[2] = getClostNDivisibleByM(getRgbFromFloat(rgb[2]), tolerance);
        return newRgb;
    }

    // todo for now, making it simple and alternating up and down. could
    // todo do a diff calculator at some point
    public static Integer[] getNthClosestShade(Float[] rgb, int tolerance, int n) {
        Integer[] closest = applyTolerance(rgb, tolerance);

        // n should be a possible boundary, probably should check this in caller, but this validation is just in case
        n %= 256 % tolerance;

        // check if we go up or down
        boolean odd = n % 2 == 1;

        if (odd) {
            applyNthOddTolerance(closest, rgb, tolerance, n/2);
            if (closest[0] > 255 || closest[1] > 255 || closest[2] > 255) {
                applyNthEvenTolerance(closest, rgb, tolerance, n/2);
            }
        } else {
            applyNthEvenTolerance(closest, rgb, tolerance, n/2);
            if (closest[0] < 0 || closest[1] < 0 || closest[2] < 0) {
                applyNthEvenTolerance(closest, rgb, tolerance, n/2);
            }
        }

        return closest;
    }

    private static void applyNthOddTolerance(Integer[] closest, Float[] rgb, int tolerance, int n) {
        closest[0] = (int) (rgb[0] + (n * tolerance));
        closest[1] = (int) (rgb[1] + (n * tolerance));
        closest[2] = (int) (rgb[2] + (n * tolerance));
    }

    private static void applyNthEvenTolerance(Integer[] closest, Float[] rgb, int tolerance, int n) {
        closest[0] = (int) (rgb[0] - (n * tolerance));
        closest[1] = (int) (rgb[1] - (n * tolerance));
        closest[2] = (int) (rgb[2] - (n * tolerance));
    }

    private static int getRgbFromFloat(float f) {
        return (int) (f * 255);
    }

    private static int getClostNDivisibleByM(int n, int m) {
        int q = n / m; // quotient
        int n1 = m * q; // 1st possible closest number
        int n2 = (n * m) > 0 ? (m * (q + 1)) : (m * (q - 1)); // 2nd possible closest number

        // if true, then n1 is the required closest number
        if (Math.abs(n - n1) < Math.abs(n - n2))
            return n1;

        // else n2 is the required closest number
        return n2;
    }

    public void writeIndexFile(BufferedWriter indexFile) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(indexFile, index.asMap());
        } catch (IOException e) {
            LOG.error(String.format("Error writing index: %s",
                    e.getLocalizedMessage()));
        }
    }

    public Map<String, List<ImmutableIndexedImage>> readIndexFile(File file) {
        try {
            return mapper.reader()
                    .forType(new TypeReference<Map<String, ArrayList<ImmutableIndexedImage>>>() {})
                    .readValue(file);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Could not read index file: %s", e));
        }
    }
}
