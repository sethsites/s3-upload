package com.sethsites.vaadin.addons.s3_upload;

import com.vaadin.flow.component.ComponentEvent;

public class S3FileFinishedEvent extends ComponentEvent<S3Upload> {
    private final String filename;

    public S3FileFinishedEvent(S3Upload source, boolean fromClient, String filename) {
        super(source, fromClient);
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}
