package org.nampython.base;


import java.io.IOException;
import java.io.InputStream;

public class MultipartFile {

    private final int fileLength;

    private final String contentType;

    private final String fileName;

    private final String fieldName;

    private final InputStream inputStream;

    public MultipartFile(int fileLength, String contentType, String fileName,
                             String fieldName, InputStream inputStream) {
        this.fileLength = fileLength;
        this.contentType = contentType;
        this.fileName = fileName;
        this.fieldName = fieldName;
        this.inputStream = inputStream;
    }


    public long getFileLength() {
        return this.fileLength;
    }


    public String getContentType() {
        return this.contentType;
    }


    public String getFileName() {
        return this.fileName;
    }


    public String getFieldName() {
        return this.fieldName;
    }


    public InputStream getInputStream() {
        return this.inputStream;
    }


    public byte[] getBytes() {
        try {
            return this.inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
