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
implementation 'com.github.mutkuensert:BitmapCompression:2.0'
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
    private val file: File,
    val sizeLimitBytes: Int,
    val compressPriority: CompressPriority = CompressPriority.STARTBYCOMPRESS,
    val lowerWidthLimit: Int? = null,
    val lowerHeightLimit: Int? = null,
    @IntRange(from = 1, to = 90)
    val compressionQualityDownTo: Int = 10,
    @FloatRange(from = 0.1, to = 0.9)
    val scaleDownFactor: Float = 0.5f
)
```

## About the class
The BitmapCompression class provides a utility to reduce the size of the image files 
under a specified size limit while maintaining control over compression parameters.


## Usage
This example reduces the file size under 1048576 value and prevents scaling the image width down below 1080 pixels.
```kotlin
BitmapCompression(
    file = tempFile,
    sizeLimitBytes = 1048576,
    compressPriority = BitmapCompression.CompressPriority.STARTBYSCALEDOWN,
    lowerWidthLimit = 1080
).compressAndScaleDown()
```

### Static functions
Scale down a bitmap or a file preserving the aspect ratio.
```kotlin
val scaledDownToFullHdBitmap = BitmapCompression.scaleDownToWidth(bitmap, 1080)

BitmapCompression.scaleDownToWidth(tempFile, 1080)
```

```kotlin
val scaledDownToFullHdBitmap = BitmapCompression.scaleDownToHeight(bitmap, 1920)

BitmapCompression.scaleDownToHeight(tempFile, 1920)
```

 ## License
```xml
Copyright 2024 Mustafa Utku Ensert

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
