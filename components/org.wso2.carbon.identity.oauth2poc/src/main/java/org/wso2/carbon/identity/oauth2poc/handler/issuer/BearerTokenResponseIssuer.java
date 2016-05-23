/*
 *  Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.oauth2poc.handler.issuer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.oltu.oauth2.as.issuer.MD5Generator;
import org.apache.oltu.oauth2.as.issuer.OAuthIssuerImpl;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.application.authentication.framework.model.AuthenticatedUser;
import org.wso2.carbon.identity.core.bean.context.MessageContext;
import org.wso2.carbon.identity.oauth2poc.OAuth2;
import org.wso2.carbon.identity.oauth2poc.bean.context.OAuth2MessageContext;
import org.wso2.carbon.identity.oauth2poc.exception.OAuth2RuntimeException;
import org.wso2.carbon.identity.oauth2poc.model.AccessToken;
import org.wso2.carbon.identity.oauth2poc.model.OAuth2ServerConfig;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Set;

public class BearerTokenResponseIssuer extends AccessTokenResponseIssuer {

    private static Log log = LogFactory.getLog(BearerTokenResponseIssuer.class);

    private OAuthIssuerImpl oltuIssuer = new OAuthIssuerImpl(new MD5Generator());

    @Override
    public String getName() {
        return "BearerTokenResponseIssuer";
    }

    @Override
    public boolean canHandle(MessageContext messageContext) {
        return true;
    }



    protected AccessToken issueNewAccessToken(String clientId, AuthenticatedUser authzUser, Set<String> scopes,
                                              boolean isRefreshTokenValid, boolean markAccessTokenExpired,
                                              AccessToken prevAccessToken, long accessTokenCallbackValidity,
                                              long refreshTokenCallbackValidity, String grantOrResponseType,
                                              AuthenticationContext messageContext) {

        Timestamp timestamp = new Timestamp(new Date().getTime());

        Timestamp accessTokenIssuedTime = timestamp;

        long accessTokenValidity = OAuth2ServerConfig.getInstance().getUserAccessTokenValidity();

        // if a VALID validity period is set through the callback, then use it
        if (accessTokenCallbackValidity != OAuth2.UNASSIGNED_VALIDITY_PERIOD) {
            accessTokenValidity = accessTokenCallbackValidity;
        }

        accessTokenValidity = accessTokenValidity * 1000;

        String bearerToken;
        try {
            bearerToken = oltuIssuer.accessToken();
        } catch (OAuthSystemException e) {
            throw OAuth2RuntimeException.error(e.getMessage(), e);
        }

        AccessToken newAccessToken = new AccessToken(bearerToken, clientId, authzUser.toString(),
                                                     null, null, accessTokenIssuedTime,
                                                     accessTokenValidity);

        newAccessToken.setAuthzUser(authzUser);
        newAccessToken.setScopes(scopes);

        return newAccessToken;
    }

    protected void storeNewAccessToken(AccessToken accessToken, AuthenticationContext messageContext) {

        //AuthzCode authzCode = (AuthzCode)messageContext.getParameter(OAuth2.AUTHZ_CODE);
        //boolean markAccessTokenExpired = (Boolean)messageContext.getParameter(MARK_ACCESS_TOKEN_EXPIRED);

        // if authzCode != null, invalidate it
        // if markAccessTokenExpired == true, mark it expired
        // if persist new access token
        // All the above should go as a single transaction

    }


}
