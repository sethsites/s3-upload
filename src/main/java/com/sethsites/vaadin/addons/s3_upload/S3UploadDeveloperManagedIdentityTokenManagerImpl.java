package com.sethsites.vaadin.addons.s3_upload;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentity.CognitoIdentityClient;
import software.amazon.awssdk.services.cognitoidentity.model.*;
import software.amazon.awssdk.services.cognitoidentityprovider.model.CognitoIdentityProviderException;

import java.util.HashMap;

public class S3UploadDeveloperManagedIdentityTokenManagerImpl implements S3UploadTokenManager {
    private final Logger log = LoggerFactory.getLogger(S3UploadDeveloperManagedIdentityTokenManagerImpl.class);
    private final CognitoIdentityClient cognitoClient;
    private final String identityPoolId;

    private final String identityPoolDeveloperId;

    public S3UploadDeveloperManagedIdentityTokenManagerImpl(String identityPoolId, String identityPoolDeveloperId) {
        this.identityPoolId = identityPoolId;
        this.identityPoolDeveloperId = identityPoolDeveloperId;
        cognitoClient = CognitoIdentityClient.builder()
            .region(Region.US_EAST_1)
            .build();
    }

    @Override
    public S3UploadToken getTokenForUser(String identity) {
        S3UploadToken token = null;
        try {
            HashMap<String, String> logins = new HashMap<>();
            logins.put(identityPoolDeveloperId, identity);
            GetOpenIdTokenForDeveloperIdentityRequest identityRequest =
                    GetOpenIdTokenForDeveloperIdentityRequest.builder().identityPoolId(identityPoolId).logins(logins).build();

            GetOpenIdTokenForDeveloperIdentityResponse response =
                    cognitoClient.getOpenIdTokenForDeveloperIdentity(identityRequest);
            log.debug("Identity ID {}, Access key ID {}", response.identityId(), response.token());

            token = new S3UploadToken(response.identityId(), response.token());
        } catch (CognitoIdentityProviderException e) {
            log.error("Exception getting token for user {}",e.awsErrorDetails().errorMessage());
        }
        return token;
    }
}
