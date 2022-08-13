package com.sethsites.vaadin.addons.s3_upload;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.upload.GeneratedVaadinUpload;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.shared.Registration;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag("s3-upload")
@NpmPackage.Container({@NpmPackage(
    value = "@aws-sdk/client-s3",
    version = "3.145.0"
), @NpmPackage(
    value = "@aws-sdk/credential-providers",
    version = "3.145.0"
)})
@JsModule("./s3-upload/s3-upload.js")
public class S3Upload extends Upload {
    private final Logger log = LoggerFactory.getLogger(S3Upload.class);

    public S3Upload(S3UploadTokenManager tokenManager, String identity, String region, String bucketName, String directory, String arn) {
        super();
        S3UploadToken token = tokenManager.getTokenForUser(identity);

        setToken(token.token());

        setRegion(region);
        setArn(arn);
        setDirectory(directory);
        setBucket(bucketName);

    }

    private void setRegion(String region) {
        this.getElement().setAttribute("region", region);
    }

    private void setArn(String arn) {
        this.getElement().setAttribute("arn", arn);
    }

    private void setBucket(String bucket) {
        this.getElement().setAttribute("bucket", bucket);
    }

    private void setDirectory(String directory) {
        this.getElement().setAttribute("directory", directory);
    }

    private void setToken(String token) {
        this.getElement().setAttribute("token", token);
    }

    public JsonArray getFiles() {
        return getFilesJsonArray();
    }

    public Registration addS3AllFinishedListener(ComponentEventListener<S3FileFinishedEvent> listener) {
        return this.addListener(S3FileFinishedEvent.class, listener);
    }

    public Registration addS3UploadSuccessListener(ComponentEventListener<S3UploadSuccessEvent> listener) {
        return this.addListener(S3UploadSuccessEvent.class, listener);
    }

    @DomEvent("s3-upload-success")
    public static class S3UploadSuccessEvent extends ComponentEvent<S3Upload> {
        private final JsonObject detail;
        private final JsonObject detailFile;

        public S3UploadSuccessEvent(S3Upload source, boolean fromClient, @EventData("event.detail") JsonObject detail, @EventData("event.detail.file") JsonObject detailFile) {
            super(source, fromClient);
            this.detail = detail;
            this.detailFile = detailFile;
        }

        public JsonObject getDetail() {
            return this.detail;
        }

        public JsonObject getDetailFile() {
            return this.detailFile;
        }
    }

    @DomEvent("s3-upload-error")
    public static class S3UploadErrorEvent<R extends GeneratedVaadinUpload<R>> extends ComponentEvent<R> {
        private final JsonObject detail;
        private final JsonObject detailFile;

        public S3UploadErrorEvent(R source, boolean fromClient, @EventData("event.detail") JsonObject detail, @EventData("event.detail.file") JsonObject detailFile) {
            super(source, fromClient);
            this.detail = detail;
            this.detailFile = detailFile;
        }

        public JsonObject getDetail() {
            return this.detail;
        }

        public JsonObject getDetailFile() {
            return this.detailFile;
        }
    }

}
