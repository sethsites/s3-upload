import {Upload} from '@vaadin/upload/vaadin-upload.js';

import {fromWebToken } from '@aws-sdk/credential-providers'
import {S3Client, PutObjectCommand} from '@aws-sdk/client-s3';

let s3 = null;

class S3Upload extends Upload {

    static get is() {
        return 's3-upload';
    }

    static get properties() {
        return {
            bucket: {
                type: String,
                value: '',
                reflectToAttribute: true
            },
            region: {
                type: String,
                value: '',
                observer: '_relogClient',
                reflectToAttribute: true
            },
            directory: {
                type: String,
                value: '',
                reflectToAttribute: true
            },
            arn: {
                type: String,
                value: '',
                observer: '_relogClient',
                reflectToAttribute: true
            },
            token: {
                type: String,
                value: '',
                observer: '_relogClient',
                reflectToAttribute: true
            }
        };
    }

    ready() {
        super.ready();
    }

    _relogClient(value) {
        if (this.region !== '' && this.pool !== ''  && this.identity !== '' && this.token !== '') {
            s3 = new S3Client({
                region: this.region,
                credentials: fromWebToken ({
                    roleArn: this.arn,
                    webIdentityToken: this.token
                }),
            });
        }
    }

    async _uploadFile(file) {
        console.log("client = " + s3);
        console.log("directory = " + this.directory);
        console.log("bucket = " + this.bucket);
        console.log("region = " + this.region);
        if (s3 === null) {
            this._relogClient();
            if (s3 === null) {
                return alert("Client is null");
            }
        }
        if (this.directory === '') {
            return alert("Directory is not set.");
        }

        const fileName = file.name;
        const fileKey = this.directory + "/" + fileName;

        const uploadParams = {
            Bucket: this.bucket,
            Key: fileKey,
            Body: file
        };
        try {
            file.fileName = fileName;
            file.status = this.i18n.uploading.status.processing;
            file.uploading = file.indeterminate = true;
            file.complete = file.abort = file.error = file.held = false;

            const data2 = await s3.send(new PutObjectCommand(uploadParams));
            if (data2.$metadata.httpStatusCode === 200) {
                file.status = '';
                file.uploading = file.indeterminate = false;
                file.complete = true;
            }

        } catch (err) {
            console.log("There was an error uploading your file: " + err.message);
            console.log(err);
            file.error = this.i18n.uploading.error.serverUnavailable;
            //file.abort = true;
            file.uploading = file.indeterminate = false;
        }

        this.dispatchEvent(
          new CustomEvent(`s3-upload-${file.error ? 'error' : 'success'}`, {
            detail: { file: this.file, fileName: fileName },
          }),
        );
        this._notifyFileChanges(file);
    }

    /**
     * Fired in case the upload process failed.
     *
     * @event s3-upload-error
     * @param {Object} detail
     * @param {Object} detail.file the file being uploaded
     * @param {Object} detail.fileName the filename being uploaded
     */

    /**
     * Fired in case the upload process failed.
     *
     * @event s3-upload-success
     * @param {Object} detail
     * @param {Object} detail.file the file being uploaded
     * @param {Object} detail.fileName the filename being uploaded
     */
}

customElements.define(S3Upload.is, S3Upload);
