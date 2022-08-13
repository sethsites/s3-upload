package com.sethsites.vaadin.addons.s3_upload;

public interface S3UploadTokenManager {
    S3UploadToken getTokenForUser(String identity);
}
