package immutables;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import org.immutables.value.Generated;

/**
 * Immutable implementation of {@link IndexedImage}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableIndexedImage.builder()}.
 */
@Generated(from = "IndexedImage", generator = "Immutables")
@SuppressWarnings({"all"})
@ParametersAreNonnullByDefault
@Immutable
public final class ImmutableIndexedImage implements IndexedImage {
  private final Path pathToImage;
  private final Float[] averageColor;
  private final @Nullable Integer cropWidth;

  private ImmutableIndexedImage(
      Path pathToImage,
      Float[] averageColor,
      @Nullable Integer cropWidth) {
    this.pathToImage = pathToImage;
    this.averageColor = averageColor;
    this.cropWidth = cropWidth;
  }

  /**
   * @return The value of the {@code pathToImage} attribute
   */
  @JsonProperty("path")
  @Override
  public Path getPathToImage() {
    return pathToImage;
  }

  /**
   * @return A cloned {@code averageColor} array
   */
  @JsonProperty("rgb")
  @Override
  public Float[] getAverageColor() {
    return averageColor.clone();
  }

  /**
   * @return The value of the {@code cropWidth} attribute
   */
  @JsonProperty("crop-width")
  @Override
  public @Nullable Integer getCropWidth() {
    return cropWidth;
  }

  /**
   * Copy the current immutable object by setting a value for the {@link IndexedImage#getPathToImage() pathToImage} attribute.
   * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for pathToImage
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableIndexedImage withPathToImage(Path value) {
    if (this.pathToImage == value) return this;
    Path newValue = Objects.requireNonNull(value, "pathToImage");
    return new ImmutableIndexedImage(newValue, this.averageColor, this.cropWidth);
  }

  /**
   * Copy the current immutable object with elements that replace the content of {@link IndexedImage#getAverageColor() averageColor}.
   * The array is cloned before being saved as attribute values.
   * @param elements The non-null elements for averageColor
   * @return A modified copy of {@code this} object
   */
  public final ImmutableIndexedImage withAverageColor(Float... elements) {
    Float[] newValue = elements.clone();
    return new ImmutableIndexedImage(this.pathToImage, newValue, this.cropWidth);
  }

  /**
   * Copy the current immutable object by setting a value for the {@link IndexedImage#getCropWidth() cropWidth} attribute.
   * An equals check used to prevent copying of the same value by returning {@code this}.
   * @param value A new value for cropWidth (can be {@code null})
   * @return A modified copy of the {@code this} object
   */
  public final ImmutableIndexedImage withCropWidth(@Nullable Integer value) {
    if (Objects.equals(this.cropWidth, value)) return this;
    return new ImmutableIndexedImage(this.pathToImage, this.averageColor, value);
  }

  /**
   * This instance is equal to all instances of {@code ImmutableIndexedImage} that have equal attribute values.
   * @return {@code true} if {@code this} is equal to {@code another} instance
   */
  @Override
  public boolean equals(@Nullable Object another) {
    if (this == another) return true;
    return another instanceof ImmutableIndexedImage
        && equalTo((ImmutableIndexedImage) another);
  }

  private boolean equalTo(ImmutableIndexedImage another) {
    return pathToImage.equals(another.pathToImage)
        && Arrays.equals(averageColor, another.averageColor)
        && Objects.equals(cropWidth, another.cropWidth);
  }

  /**
   * Computes a hash code from attributes: {@code pathToImage}, {@code averageColor}, {@code cropWidth}.
   * @return hashCode value
   */
  @Override
  public int hashCode() {
    int h = 5381;
    h += (h << 5) + pathToImage.hashCode();
    h += (h << 5) + Arrays.hashCode(averageColor);
    h += (h << 5) + Objects.hashCode(cropWidth);
    return h;
  }

  /**
   * Prints the immutable value {@code IndexedImage} with attribute values.
   * @return A string representation of the value
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper("IndexedImage")
        .omitNullValues()
        .add("pathToImage", pathToImage)
        .add("averageColor", Arrays.toString(averageColor))
        .add("cropWidth", cropWidth)
        .toString();
  }

  /**
   * Creates an immutable copy of a {@link IndexedImage} value.
   * Uses accessors to get values to initialize the new immutable instance.
   * If an instance is already immutable, it is returned as is.
   * @param instance The instance to copy
   * @return A copied immutable IndexedImage instance
   */
  public static ImmutableIndexedImage copyOf(IndexedImage instance) {
    if (instance instanceof ImmutableIndexedImage) {
      return (ImmutableIndexedImage) instance;
    }
    return ImmutableIndexedImage.builder()
        .from(instance)
        .build();
  }

  /**
   * Creates a builder for {@link ImmutableIndexedImage ImmutableIndexedImage}.
   * <pre>
   * ImmutableIndexedImage.builder()
   *    .pathToImage(java.nio.file.Path) // required {@link IndexedImage#getPathToImage() pathToImage}
   *    .averageColor(Float) // required {@link IndexedImage#getAverageColor() averageColor}
   *    .cropWidth(Integer | null) // nullable {@link IndexedImage#getCropWidth() cropWidth}
   *    .build();
   * </pre>
   * @return A new ImmutableIndexedImage builder
   */
  public static ImmutableIndexedImage.Builder builder() {
    return new ImmutableIndexedImage.Builder();
  }

  /**
   * Builds instances of type {@link ImmutableIndexedImage ImmutableIndexedImage}.
   * Initialize attributes and then invoke the {@link #build()} method to create an
   * immutable instance.
   * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
   * but instead used immediately to create instances.</em>
   */
  @Generated(from = "IndexedImage", generator = "Immutables")
  @NotThreadSafe
  public static final class Builder {
    private static final long INIT_BIT_PATH_TO_IMAGE = 0x1L;
    private static final long INIT_BIT_AVERAGE_COLOR = 0x2L;
    private long initBits = 0x3L;

    private @Nullable Path pathToImage;
    private @Nullable Float[] averageColor;
    private @Nullable Integer cropWidth;

    private Builder() {
    }

    /**
     * Fill a builder with attribute values from the provided {@code IndexedImage} instance.
     * Regular attribute values will be replaced with those from the given instance.
     * Absent optional values will not replace present values.
     * @param instance The instance from which to copy values
     * @return {@code this} builder for use in a chained invocation
     */
    public final Builder from(IndexedImage instance) {
      Objects.requireNonNull(instance, "instance");
      pathToImage(instance.getPathToImage());
      averageColor(instance.getAverageColor());
      @Nullable Integer cropWidthValue = instance.getCropWidth();
      if (cropWidthValue != null) {
        cropWidth(cropWidthValue);
      }
      return this;
    }

    /**
     * Initializes the value for the {@link IndexedImage#getPathToImage() pathToImage} attribute.
     * @param pathToImage The value for pathToImage 
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("path")
    public final Builder pathToImage(Path pathToImage) {
      this.pathToImage = Objects.requireNonNull(pathToImage, "pathToImage");
      initBits &= ~INIT_BIT_PATH_TO_IMAGE;
      return this;
    }

    /**
     * Initializes the value for the {@link IndexedImage#getAverageColor() averageColor} attribute.
     * @param averageColor The elements for averageColor
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("rgb")
    public final Builder averageColor(Float... averageColor) {
      this.averageColor = averageColor.clone();
      initBits &= ~INIT_BIT_AVERAGE_COLOR;
      return this;
    }

    /**
     * Initializes the value for the {@link IndexedImage#getCropWidth() cropWidth} attribute.
     * @param cropWidth The value for cropWidth (can be {@code null})
     * @return {@code this} builder for use in a chained invocation
     */
    @JsonProperty("crop-width")
    public final Builder cropWidth(@Nullable Integer cropWidth) {
      this.cropWidth = cropWidth;
      return this;
    }

    /**
     * Builds a new {@link ImmutableIndexedImage ImmutableIndexedImage}.
     * @return An immutable instance of IndexedImage
     * @throws java.lang.IllegalStateException if any required attributes are missing
     */
    public ImmutableIndexedImage build() {
      if (initBits != 0) {
        throw new IllegalStateException(formatRequiredAttributesMessage());
      }
      return new ImmutableIndexedImage(pathToImage, averageColor, cropWidth);
    }

    private String formatRequiredAttributesMessage() {
      List<String> attributes = new ArrayList<>();
      if ((initBits & INIT_BIT_PATH_TO_IMAGE) != 0) attributes.add("pathToImage");
      if ((initBits & INIT_BIT_AVERAGE_COLOR) != 0) attributes.add("averageColor");
      return "Cannot build IndexedImage, some of required attributes are not set " + attributes;
    }
  }
}
