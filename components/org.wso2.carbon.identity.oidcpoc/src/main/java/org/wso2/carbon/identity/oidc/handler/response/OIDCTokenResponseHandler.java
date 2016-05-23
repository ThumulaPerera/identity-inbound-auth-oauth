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

package org.wso2.carbon.identity.oidc.handler.response;

import org.apache.oltu.oauth2.common.OAuth;
import org.wso2.carbon.identity.application.authentication.framework.context.AuthenticationContext;
import org.wso2.carbon.identity.core.bean.context.MessageContext;
import org.wso2.carbon.identity.oauth2poc.bean.message.response.authz.AuthzResponse;
import org.wso2.carbon.identity.oauth2poc.handler.response.TokenResponseHandler;
import org.wso2.carbon.identity.oauth2poc.util.OAuth2Util;
import org.wso2.carbon.identity.oidc.IDTokenBuilder;
import org.wso2.carbon.identity.oidc.OIDC;
import org.wso2.carbon.identity.oidc.bean.message.request.authz.OIDCAuthzRequest;
import org.wso2.carbon.identity.oidc.handler.OIDCHandlerManager;

import java.util.Set;

public class OIDCTokenResponseHandler extends TokenResponseHandler {

    public String getName() {
        return "OIDCTokenResponseHandler";
    }

    public boolean canHandle(MessageContext messageContext) {
        if(super.canHandle(messageContext)) {
            String scope = ((AuthenticationContext) messageContext).getInitialAuthenticationRequest()
                    .getParameter(OAuth.OAUTH_SCOPE);
            Set<String> scopes = OAuth2Util.buildScopeSet(scope);
            if (scopes.contains(OIDC.OPENID_SCOPE)) {
                return true;
            }
        }
        return false;
    }

    protected AuthzResponse.AuthzResponseBuilder buildAuthzResponse(AuthenticationContext messageContext) {

        AuthzResponse.AuthzResponseBuilder builder = super.buildAuthzResponse(messageContext);
        OIDCAuthzRequest authzRequest = (OIDCAuthzRequest)messageContext.getInitialAuthenticationRequest();
//        AuthenticationResult authenticationResult = (AuthenticationResult)messageContext.getParameter(
//                InboundConstants.RequestProcessor.AUTHENTICATION_RESULT);
        if(authzRequest.getResponseType().contains("id_token")) {
            addIDToken(builder, messageContext);
        }
        return builder;
    }

    protected void addIDToken(AuthzResponse.AuthzResponseBuilder builder, AuthenticationContext messageContext) {

        IDTokenBuilder idTokenBuilder = OIDCHandlerManager.getInstance().buildIDToken(messageContext);
        String idToken = idTokenBuilder.build();
        builder.getBuilder().setParam("id_token", idToken);
    }

}
