/*
 * 2007-2016 [PagSeguro Internet Ltda.]
 *
 * NOTICE OF LICENSE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright: 2007-2016 PagSeguro Internet Ltda.
 * Licence: http://www.apache.org/licenses/LICENSE-2.0
 */
package br.com.uol.pagseguro.api.application.authorization;

import br.com.uol.pagseguro.api.Endpoints;
import br.com.uol.pagseguro.api.PagSeguro;
import br.com.uol.pagseguro.api.application.authorization.search.AuthorizationSearchResource;
import br.com.uol.pagseguro.api.exception.PagSeguroLibException;
import br.com.uol.pagseguro.api.http.HttpClient;
import br.com.uol.pagseguro.api.http.HttpMethod;
import br.com.uol.pagseguro.api.http.HttpResponse;
import br.com.uol.pagseguro.api.http.HttpXMLRequestBody;
import br.com.uol.pagseguro.api.utils.Builder;
import br.com.uol.pagseguro.api.utils.CharSet;
import br.com.uol.pagseguro.api.utils.RequestMap;
import br.com.uol.pagseguro.api.utils.RequestXML;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory to authorization registration and authorizations search
 *
 * @author PagSeguro Internet Ltda.
 */
public class AuthorizationsResource {

    private static final AuthorizationRegistrationV2MapConverter AUTHORIZATION_REGISTRATION_MC =
        new AuthorizationRegistrationV2MapConverter();

    private static final AuthorizationRegistrationV2XMLConverter AUTHORIZATION_REGISTRATION_XMLC =
        new AuthorizationRegistrationV2XMLConverter();

    private final PagSeguro pagSeguro;
    private final HttpClient httpClient;

    public AuthorizationsResource(PagSeguro pagSeguro, HttpClient httpClient) {
        this.pagSeguro = pagSeguro;
        this.httpClient = httpClient;
    }

    /**
     * Authorization registration
     *
     * @param authorizationRegistrationBuilder Builder of interface with attributes for authorization
     *                                         registration
     * @return Response of authorization registration
     * @see AuthorizationRegistration
     * @see RegisteredAuthorization
     */
    public RegisteredAuthorization register(
        Builder<AuthorizationRegistration> authorizationRegistrationBuilder) {
        return register(authorizationRegistrationBuilder.build());
    }

    /**
     * Authorization registration
     *
     * @param authorizationRegistration Interface with attributes for authorization registration
     * @return Response of authorization registration
     * @see AuthorizationRegistration
     * @see RegisteredAuthorization
     */
    public RegisteredAuthorization register(AuthorizationRegistration authorizationRegistration) {
        final RequestMap map = AUTHORIZATION_REGISTRATION_MC.convert(authorizationRegistration);
        final HttpResponse response;
        try {
            response =
                httpClient.execute(HttpMethod.POST,
                    String.format(Endpoints.AUTHORIZATION_REQUEST, pagSeguro.getHost()),
                    null, map.toHttpRequestBody(CharSet.ENCODING_ISO));

            RegisteredAuthorizationResponseXML responseXML = response.parseXMLContent(pagSeguro,
                RegisteredAuthorizationResponseXML.class);

            return responseXML;
        } catch (IOException e) {
            throw new PagSeguroLibException(e);
        }

    }

    /**
     * Authorization registration
     *
     * @param authorizationRegistration Interface with attributes for authorization registration
     * @return Response of authorization registration
     * @see AuthorizationRegistration
     * @see RegisteredAuthorization
     */
    public RegisteredAuthorization registerWithSuggestion(AuthorizationRegistration authorizationRegistration) {
        try {
            final RequestXML xmlBody = AUTHORIZATION_REGISTRATION_XMLC.convert(authorizationRegistration);
            final HttpResponse response;

            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/xml");

            response =
                httpClient.executeXML(HttpMethod.POST,
                    String.format(Endpoints.AUTHORIZATION_REQUEST, pagSeguro.getHost()),
                    headers, xmlBody.toHttpXMLRequestBody(CharSet.ENCODING_ISO));

            RegisteredAuthorizationResponseXML responseXML = response.parseXMLContent(pagSeguro,
                RegisteredAuthorizationResponseXML.class);

            return responseXML;
        } catch (IOException e) {
            throw new PagSeguroLibException(e);
        } catch (JAXBException e) {
            throw new PagSeguroLibException(e);
        }
    }

    /**
     * Factory to Search Authorizations
     *
     * @return Factory to Search Authorizations
     * @see AuthorizationSearchResource
     */
    public AuthorizationSearchResource search() {
        return new AuthorizationSearchResource(pagSeguro, httpClient);
    }

}
