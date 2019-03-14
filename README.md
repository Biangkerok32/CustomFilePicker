# CustomFilePicker Library for Android

[![](https://jitpack.io/v/wangsun6/CustomFilePicker.svg)](https://jitpack.io/#wangsun6/CustomFilePicker)

A FilePicker library for Android for selecting multiple files.

[Sample Apk](https://github.com/jaiselrahman/FilePicker/releases/download/1.2.0/app-release.apk)


## Usage

Step 1: Add it in your root build.gradle at the end of repositories

```gradle
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```

Step 2: Add the dependency

```gradle
    dependencies {
        ...
        implementation 'com.github.wangsun6:CustomFilePicker:1.0.4'
    }
```

Step 3: Declare and Initialize filepicker in Activity or Fragment.

```java
    private FilePicker filePicker = new FilePicker(this);
    filePicker.setFilePickerCallback(new FilePickerCallback() {
            @Override
            public void onFilesChosen(List<ChosenFile> files) {

            }

            @Override
            public void onError(String message) {

            }})
                .setFileType(MimeUtils.FileType.IMAGE)
                .setMimeTypes(MimeUtils.MimeType.IMAGE)
                .allowMultipleFiles(true) 
                .pickFile();
```

Step 3: call ```filePicker.submit(data)``` in ```onActivityResult(...)```.

```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == Picker.PICK_FILE){
            filePicker.submit(data);
        }
        else
            super.onActivityResult(requestCode, resultCode, data);
    }
```

## Note:
In rare cases u may get invalid files(files which do not exist but contain uri). To ensure to get valid files u can use conditions in ```onFilesChosen```. It is end-developer responsibility, how they want to handle such cases.

```java
public void onFilesChosen(List<ChosenFile> files) {
                List<ChosenFile> finalFiles = new ArrayList<>();
                if(!files.isEmpty()){
                    for(int i=0; i<files.size();i++){
                        if(files.get(i).isSuccess() && files.get(i).getSize()!=0){
                            finalFiles.add(files.get(i));
                        }
                    }
                }
                /**
                 * finalFiles contains valid files:
                 *
                 * finalFiles.get(i).getOriginalPath()
                 * finalFiles.get(i).getSize()
                 * finalFiles.get(i).getDisplayName()
                 * finalFiles.get(i).getQueryUri()
                 */
            }
```
