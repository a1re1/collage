package processing;

import com.google.inject.Guice;
import com.google.inject.Injector;
import immutables.ImmutableIndexedImage;
import injection.ImageModule;
import org.apache.log4j.Logger;
import org.openimaj.image.DisplayUtilities;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import util.io.IndexingUtil;
import util.sizing.Resizer;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static util.Constants.CROP_SIZE;
import static util.Constants.INDEX;
import static util.Constants.TOLERANCE;

public class CollageGenerator {
    private final static Logger LOG = Logger.getLogger(CollageGenerator.class);

    private final IndexingUtil indexingUtil;
    private final Map<String, AtomicInteger> counter = new HashMap<>();
    private final Random random = new Random();

    @Inject
    public CollageGenerator(IndexingUtil indexingUtil) {
        this.indexingUtil = indexingUtil;

        counter.computeIfAbsent("Hits", (k) -> new AtomicInteger());
        counter.computeIfAbsent("Misses", (k) -> new AtomicInteger());
    }

    public static void main(String[] args) {
        Path khaleesi  = Paths.get("resources/images/edward.jpg");
        int width = 125;
        int height = width;
        int bounding = 500; // TODO make this a constant or something configurable; right now best results this is multiple of width

        Optional<MBFImage> imageMaybe = Optional.ofNullable(
                Resizer.pixelize(khaleesi, width, height, Optional.of(bounding)));

        imageMaybe.ifPresent(image -> {
            Injector injector = Guice.createInjector(new ImageModule());
            CollageGenerator generator = injector.getInstance(CollageGenerator.class);
            generator.buildCollage(image, width, height, bounding / width);
        });

    }

    private void buildCollage(MBFImage image, int width, int height, int pxWidth) {
        MBFImage collage = new MBFImage(CROP_SIZE * width, CROP_SIZE * height);
        Map<String, List<ImmutableIndexedImage>> index = indexingUtil.readIndexFile(Paths.get(INDEX).toFile());

        DisplayUtilities.display(image);

        // TODO parallelize this operation since these do not depend on each other
        for (int i = 0; i < width; i++) {
            LOG.info(String.format("Fetching row: %d", i));
            for (int j = 0; j < height; j++) {
                Float[] color = image.getPixel(i * pxWidth, j * pxWidth);
                String key = Arrays.toString(IndexingUtil.applyTolerance(color, TOLERANCE));

                if (index.containsKey(key)) {
                    // We have an image that exists on this image. Use it.
                    fetchAndRenderChunk(index, key, collage, i, j, color);
                } else {
                    for (int k = 1; k < 256 % TOLERANCE; k++) {
                        key = Arrays.toString(IndexingUtil.getNthClosestShade(color, TOLERANCE, k));
                        if (index.containsKey(key)) {
                            LOG.debug("Found a different shade to use.");
                            fetchAndRenderChunk(index, key, collage, i, j, color);
                            counter.get("Hits").incrementAndGet();
                            break;
                        }
                    }
                    renderColor(collage, i * CROP_SIZE, j * CROP_SIZE, color);
                }
            }
        }

        //DisplayUtilities.display(collage);
        try {
            ImageUtilities.write(collage, Paths.get("resources/collage.png").toFile());
        } catch (IOException e) {
            LOG.error(String.format("Failed to write file due to: ", e));
        }

        LOG.info(String.format("Hits: %d; Misses: %d",
                counter.get("Hits").get(),
                counter.get("Misses").get()));
    }

    private void fetchAndRenderChunk(Map<String, List<ImmutableIndexedImage>> index,
                                     String key,
                                     MBFImage collage,
                                     int i,
                                     int j,
                                     Float[] color) {
        try {
            List<ImmutableIndexedImage> images = index.get(key);
            MBFImage chunk = ImageUtilities.readMBF(images.get(random.nextInt(images.size())).getPathToImage().toFile());
            renderChunk(collage, i * CROP_SIZE, j * CROP_SIZE, Optional.of(chunk), Optional.empty());
            counter.get("Hits").incrementAndGet();
        } catch(IOException e){
            LOG.error(String.format("Could not read image: %s. Defaulting to color",
                    index.get(key).iterator().next()));

            renderColor(collage, i * CROP_SIZE, j * CROP_SIZE, color);
        }
    }

    private static void renderChunk(MBFImage collage, int startX, int startY, Optional<MBFImage> chunkMaybe, Optional<Float[]> colorMaybe) {
        for (int i = startX; i < startX + CROP_SIZE; i++) {
            for (int j = startY; j < startY + CROP_SIZE; j++) {
                if (chunkMaybe.isPresent()) {
                    LOG.trace(String.format("%d %d", i - startX, j - startY));
                    collage.setPixel(i, j,
                            chunkMaybe.get().getPixel(i - startX, j - startY));
                } else if (colorMaybe.isPresent()) {
                    collage.setPixel(i, j, colorMaybe.get());
                } else {
                    LOG.warn("Rendered chunk without a chunk or a color. Please check that this is without error.");
                }
            }
        }
    }

    private void renderColor(MBFImage collage, int i, int j, Float[] color) {
        renderChunk(collage, i, j, Optional.empty(), Optional.of(color));
        counter.computeIfAbsent("Misses", (k) -> new AtomicInteger());
        counter.get("Misses").incrementAndGet();
        LOG.debug(Arrays.toString(color));
    }
}
