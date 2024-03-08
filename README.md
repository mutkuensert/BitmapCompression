## Adding the dependency
Add jitpack into the repositories

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency in build.gradle file.
```gradle
implementation 'com.github.mutkuensert:BitmapCompression:1.0'
```

## Class Information
```kotlin
/**
 * @property sizeLimitBytes Max size the file can be after compression.
 * @property compressPriority Start reducing file size by scaling down or compressing.
 * @property lowerWidthLimit Stop scaling down before dropping down below this value.
 * @property lowerHeightLimit Stop scaling down before dropping down below this value.
 * @property compressionQualityDownTo Lower value means lower quality and smaller size.
 * @property scaleDownFactor Scale factor to divide width and height of image in every loop.
 */
class BitmapCompression(
    val sizeLimitBytes: Int,
    val compressPriority: CompressPriority = CompressPriority.STARTBYCOMPRESS,
    val lowerWidthLimit: Int? = null,
    val lowerHeightLimit: Int? = null,
    @IntRange(from = 1, to = 90)
    val compressionQualityDownTo: Int = 10,
    @FloatRange(from = 0.1, to = 0.9)
    val scaleDownFactor: Float = 0.5f
) {
    fun compress(file: File) {
      ...
```

## About the class
The BitmapCompression class provides a utility to reduce the size of image files under a specified size limit while maintaining control over compression parameters.
This library is particularly useful for reducing image file sizes in Android applications.

## Basic Usage
```kotlin
BitmapCompression(
    sizeLimitBytes = MAX_BYTES_SERVER_ACCEPTS,
    lowerWidthLimit = 480
).compress(tempFile)
```
This example compresses the file under MAX_BYTES_SERVER_ACCEPTS value and prevents scaling the image width down below 480 pixels.
