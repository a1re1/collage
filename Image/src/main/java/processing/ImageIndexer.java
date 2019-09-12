package processing;

import com.google.inject.Guice;
import com.google.inject.Injector;
import injection.ImageModule;
import org.apache.log4j.Logger;
import util.io.IndexingUtil;

import javax.inject.Inject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static util.Constants.CROP_SIZE;
import static util.Constants.INDEX;
import static util.Constants.TOLERANCE;

public class ImageIndexer {
    private final static Logger LOG = Logger.getLogger(ImageIndexer.class);

    private final IndexingUtil indexingUtil;

    @Inject
    public ImageIndexer(IndexingUtil indexingUtil) {
        this.indexingUtil = indexingUtil;
    }

    public static void main(String[] args) {
        if (!(args.length == 1)) {
            LOG.error("Usage: [directory of images]");
            return;
        }

        Injector injector = Guice.createInjector(new ImageModule());
        injector.getInstance(ImageIndexer.class).indexFolder(args[0]);
    }

    private void indexFolder(String folderString) {
        Path folder = Paths.get(folderString);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(INDEX));

            Files.walk(folder).filter(Files::isRegularFile)
                    .forEach(image -> {
                        LOG.info(image.getFileName());
                        indexingUtil.indexImage(image, Optional.of(CROP_SIZE), TOLERANCE);
                    });

            indexingUtil.writeIndexFile(writer);
            writer.close();
            LOG.info(INDEX + " " + folderString);
        } catch (IOException io) {
            LOG.error("Files IO exception: {}", io);
        }
    }
}
