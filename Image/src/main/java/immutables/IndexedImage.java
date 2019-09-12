package immutables;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.nio.file.Path;
import javax.annotation.Nullable;

@Value.Immutable
@JsonSerialize(as = ImmutableIndexedImage.class)
@JsonDeserialize(builder = ImmutableIndexedImage.Builder.class)

public interface IndexedImage {
    @JsonProperty("path") Path getPathToImage();
    @JsonProperty("rgb") Float[] getAverageColor();

    @Nullable
    @JsonProperty("crop-width") Integer getCropWidth();
}
