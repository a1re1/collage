package processing;

import com.google.inject.Guice;
import com.google.inject.Injector;
import injection.ImageModule;
import org.apache.log4j.Logger;
import org.openimaj.image.ImageUtilities;
import org.openimaj.image.MBFImage;
import util.sizing.Resizer;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static util.Constants.SCALE_SIZE;

public class ImageScaler {
    private final static Logger LOG = Logger.getLogger(ImageIndexer.class);

    private final Resizer resizer;

    @Inject
    public ImageScaler(Resizer resizer) {
        this.resizer = resizer;
    }

    public static void main(String[] args) {
        if (!(args.length == 1)) {
            LOG.error("Usage: [directory of images]");
            return;
        }

        Injector injector = Guice.createInjector(new ImageModule());
        injector.getInstance(ImageScaler.class).resizeFolder(args[0], SCALE_SIZE);
    }

    private void resizeFolder(String folderString, int resizeDim) {
        Path folder = Paths.get(folderString);
        try {
            Files.walk(folder).filter(Files::isRegularFile)
                    .forEach(path -> {
                        LOG.info(String.format("Scaling: %s", path.getFileName()));
                        Optional<MBFImage> imageMaybe = Optional.ofNullable(Resizer.scaleDownAndCropToSquare(path, resizeDim));
                        if (imageMaybe.isPresent()) {
                            try {
                                LOG.info(String.format("resources/scaled-images/%dpx-%s",
                                        resizeDim,
                                        path.getFileName().toString()));
                                ImageUtilities.write(imageMaybe.get(), Paths.get(String.format("resources/scaled-images/%dpx-%s",
                                        resizeDim,
                                        path.getFileName().toString())).toFile());
                            } catch (IOException e) {
                                LOG.error(String.format("Could not scale %s because of: %s",
                                        path.getFileName().toString(), e));
                            }
                        } else {
                            LOG.error(String.format("Scaled image not present: %s", path.getFileName().toString()));
                        }

                    });
        } catch (IOException io) {
            LOG.error("Io exception walking through file: {}", io);
        }
    }
}
