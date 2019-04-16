# CustomFilePicker Library for Android



A FilePicker library for Android for selecting multiple files.


## Installation
Step 21: Add the dependency

```gradle
    dependencies {
        ...
        implementation 'com.wangsun.android:file-picker:1.0.6'
    }
```

## Usage

Step 1: Declare and Initialize filePicker in Activity or Fragment.

```
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

Step 2: call ```filePicker.submit(data)``` in ```onActivityResult(...)```.

```
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
1. In rare cases u may get invalid files(files which do not exist but contain uri). To ensure to get valid files u can use conditions in ```onFilesChosen```. It is end-developer responsibility, how they want to handle such cases.

```
public void onFilesChosen(List<ChosenFile> files) {
                //filter files
                List<ChosenFile> finalFiles = new ArrayList<>();
                if(!files.isEmpty()){
                    for(int i=0; i<files.size();i++){
                        if(files.get(i).isSuccess() && files.get(i).getSize()!=0){
                            finalFiles.add(files.get(i));
                        }
                    }
                }
                /**
                 * finalFiles contains valid file data:
                 *
                 * finalFiles.get(i).getOriginalPath()
                 * finalFiles.get(i).getSize()
                 * finalFiles.get(i).getDisplayName()
                 * finalFiles.get(i).getQueryUri()
                 */
            }
```

## More examples:

```

    /************************************
     *  Pick particular type of file
     *************************************/
    void pickImage() {
        filePicker.setFilePickerCallback(this)
                .setFileType(MimeUtils.FileType.IMAGE)
                .setMimeTypes(MimeUtils.MimeType.IMAGE)
                .pickFile();
    }

    void pickAudio() {
        filePicker.setFilePickerCallback(this)
                .setFileType(MimeUtils.FileType.AUDIO)
                .setMimeTypes(MimeUtils.MimeType.AUDIO)
                .pickFile();
    }

    void pickVideo() {
        filePicker.setFilePickerCallback(this)
                .setFileType(MimeUtils.FileType.VIDEO)
                .setMimeTypes(MimeUtils.MimeType.VIDEO)
                .pickFile();
    }

    void pickDocs() {
        filePicker.setFilePickerCallback(this)
                .setFileType(MimeUtils.FileType.DOC)
                .setMimeTypes(MimeUtils.MimeType.DOC)
                .pickFile();
    }

    /*****************************************
     *    Pick more specific type of file
     *****************************************/

    void pickPdfOnly() {
        filePicker.setFilePickerCallback(this)
                .setFileType(MimeUtils.FileType.DOC)
                .setMimeTypes(MimeUtils.MimeType.ONLY_PDF)
                .pickFile();
    }
    
    void pickMp4Only() {
        filePicker.setFilePickerCallback(this)
                .setFileType(MimeUtils.FileType.AUDIO)
                .setMimeTypes(MimeUtils.MimeType.ONLY_MP4)
                .pickFile();
    }
    
    void pickJpgOnly() {
        filePicker.setFilePickerCallback(this)
                .setFileType(MimeUtils.FileType.IMAGE)
                .setMimeTypes(MimeUtils.MimeType.ONLY_JPEG)
                .pickFile();
    }
    
```

#### setFileType() filter the applications for picking files while .setMimeTypes() filter the actual files



