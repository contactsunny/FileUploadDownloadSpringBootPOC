package com.contactsunny.poc.FileUploadDownloadApiSpringBootPOC.dtos;

public class UploadFileResponse {

    private String fileUrl;
    private long fileSize;

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
