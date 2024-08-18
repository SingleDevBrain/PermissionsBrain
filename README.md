# Runtime Permissions Helper
How to use:

### Step 1:
Add it in your root build.gradle at the end of repositories:

For Groovy DSL
```kotlin 

    dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
 
```

For Kotlin DSL
```kotlin 

    dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven {
				url = uri("https://jitpack.io")
			}
		}
	}
 
```

### Step 2:
Add the dependency

version: [![](https://jitpack.io/v/zeeshan00084/PermissionsBrain.svg)](https://jitpack.io/#zeeshan00084/PermissionsBrain)


```kotlin 

    dependencies {
	        implementation("com.github.SingleDevBrain:PermissionsBrain:version")
	}
 
```

### Step 3: 
Add necessary permissions in AndroidManifest file

```kotlin 

    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
    <uses-permission android:name="android.permission.READ_MEDIA_VISUAL_USER_SELECTED" />
    <uses-permission android:name="android.permission.READ_MEDIA_VIDEO" />
    <uses-permission android:name="android.permission.READ_MEDIA_AUDIO" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        android:maxSdkVersion="32" />
 
```

### Step 4(Optional):
Rational dialog description

```kotlin 

    val rationale = "Please provide access to work properly"
 
```

### Step 5(Optinal):
Add permission Options

```kotlin 

    val permissionOption: PermissionHelper.PermissionOptions = PermissionHelper.PermissionOptions()
                .setRationaleDialogTitle("Permissions Required...!")
                .setSettingsDialogTitle("Urgent")
 
```

### Step 6:
To grant single permission

```kotlin 

    PermissionHelper.checkPermission(
                this,
                Manifest.permission.CAMERA,
                rationale/*null*/",
                object : PermissionCallback() {
                    override fun onPermissionsGranted() {
                        Toast.makeText(this@MainActivity, "Camera permission granted", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionsDenied(
                        context: Context,
                        deniedPermissions: List<String>
                    ) {
                        super.onPermissionsDenied(context, deniedPermissions)
                        Toast.makeText( this@MainActivity,"Camera permission denied",Toast.LENGTH_SHORT).show()
                    }

                })
 
```

### Step 7:
To grant multiple permissions 

```kotlin 

    PermissionHelper.checkPermissions(
                this,
                permissions,
                rationale/*null*/,l̥
                permissionOption/*null*/,
                object : PermissionCallback() {
                    override fun onPermissionsGranted() {
                        Toast.makeText(this@MainActivity, "All permissions granted", Toast.LENGTH_SHORT).show()

                    }

                    override fun onPermissionsDenied(
                        context: Context,
                        deniedPermissions: List<String>
                    ) {
                        super.onPermissionsDenied(context, deniedPermissions)
                        Toast.makeText(this@MainActivity, "All permissions denied", Toast.LENGTH_SHORT).show()
                    }
                })
 
```





