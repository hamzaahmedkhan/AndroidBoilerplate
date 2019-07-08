package com.android.structure.managers.retrofit.entities;


import com.android.structure.enums.FileType;

import java.io.File;

public class MultiFileModel {

    private File file;
    private FileType fileType;
    private String keyName;

    public MultiFileModel(File file, FileType fileType, String keyName) {
        this.file = file;
        this.fileType = fileType;
        this.keyName = keyName;
    }


    public File getFile() {
        return file;
    }

    public FileType getFileType() {
        return fileType;
    }

    public String getKeyName() {
        return keyName;
    }
}
