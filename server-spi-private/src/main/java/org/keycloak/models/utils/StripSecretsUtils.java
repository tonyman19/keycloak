/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
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
 */

package org.keycloak.models.utils;

import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.representations.idm.ComponentRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class StripSecretsUtils {

    public static ComponentRepresentation strip(KeycloakSession session, ComponentRepresentation rep) {
        Map<String, ProviderConfigProperty> configProperties = ComponentUtil.getComponentConfigProperties(session, rep);
        if (rep.getConfig() == null) {
            return rep;
        }

        Iterator<Map.Entry<String, List<String>>> itr = rep.getConfig().entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, List<String>> next = itr.next();
            ProviderConfigProperty configProperty = configProperties.get(next.getKey());
            if (configProperty != null) {
                if (configProperty.isSecret()) {
                    next.setValue(Collections.singletonList(ComponentRepresentation.SECRET_VALUE));
                }
            } else {
                itr.remove();
            }
        }
        return rep;
    }

    public static RealmRepresentation strip(RealmRepresentation rep) {
        if (rep.getSmtpServer() != null && rep.getSmtpServer().containsKey("password")) {
            rep.getSmtpServer().put("password", ComponentRepresentation.SECRET_VALUE);
        }
        return rep;
    }

    public static IdentityProviderRepresentation strip(IdentityProviderRepresentation rep) {
        if (rep.getConfig() != null && rep.getConfig().containsKey("clientSecret")) {
            rep.getConfig().put("clientSecret", ComponentRepresentation.SECRET_VALUE);
        }
        return rep;
    }

}