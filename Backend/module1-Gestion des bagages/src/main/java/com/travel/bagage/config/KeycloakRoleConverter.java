package com.travel.bagage.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Convertisseur des rôles Keycloak extraits du token JWT
 * vers les GrantedAuthority de Spring Security (préfixe ROLE_).
 * Prend en charge realm_access.roles et resource_access.{client}.roles.
 */
public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    @Override
    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = defaultGrantedAuthoritiesConverter.convert(jwt);
        Set<GrantedAuthority> grantedAuthorities = authorities != null ? new HashSet<>(authorities) : new HashSet<>();

        // 1. Extraction des rôles du Realm (realm_access.roles)
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {
            Collection<String> roles = (Collection<String>) realmAccess.get("roles");
            for (String role : roles) {
                // Ajout des variantes ROLE_user, ROLE_admin, ROLE_USER, ROLE_ADMIN et nom brut
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.toLowerCase()));
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                grantedAuthorities.add(new SimpleGrantedAuthority(role.toLowerCase()));
                grantedAuthorities.add(new SimpleGrantedAuthority(role.toUpperCase()));
            }
        }

        // 2. Extraction des rôles de ressources client (resource_access.{client}.roles)
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            for (Map.Entry<String, Object> entry : resourceAccess.entrySet()) {
                if (entry.getValue() instanceof Map<?, ?> clientMap && clientMap.containsKey("roles")) {
                    Collection<String> clientRoles = (Collection<String>) clientMap.get("roles");
                    for (String role : clientRoles) {
                        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.toLowerCase()));
                        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));
                    }
                }
            }
        }

        return new ArrayList<>(grantedAuthorities);
    }
}
