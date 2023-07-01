package com.dh.msbills.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.stereotype.Component;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final JwtAuthConverterProperties properties;

    public JwtAuthConverter(JwtAuthConverterProperties properties) {
        this.properties = properties;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                customAuthorities(jwt).stream()).collect(Collectors.toSet());

        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }


    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        if (properties.getPrincipalAttribute() != null) {
            claimName = properties.getPrincipalAttribute();
        }
        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> customAuthorities (Jwt jwt){

        Collection<GrantedAuthority> allAuthorities = new HashSet<>();
        allAuthorities.addAll(extractResourceRoles(jwt));
        allAuthorities.addAll(extractRealmRoles(jwt));
        allAuthorities.addAll(extractAccountRoles(jwt));
        allAuthorities.addAll(extractScope(jwt));
        allAuthorities.addAll(extractGroups(jwt));

        return allAuthorities;
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {

        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        Map<String, Object> resource;
        Collection<String> resourceRoles;

        if (resourceAccess == null
                || (resource = (Map<String, Object>) resourceAccess.get(properties.getResourceId())) == null
                || (resourceRoles = (Collection<String>) resource.get("roles")) == null) {
            return Set.of();
        }
        return resourceRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }


    private Collection<? extends GrantedAuthority> extractRealmRoles(Jwt jwt) {

        Map<String, Object> claims = jwt.getClaims();
        for (Map.Entry<String, Object> entry: claims.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
        }

        Map<String, Object> realmAccess = (Map<String, Object>) claims.get("realm_access");
        ArrayList<String > realmAccessRoles = (ArrayList<String>) realmAccess.get("roles");

        return realmAccessRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    private Collection<? extends GrantedAuthority> extractAccountRoles(Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        Map<String, Object> account = (Map<String, Object>) resourceAccess.get("account");
        ArrayList<String > accountRoles = (ArrayList<String>) account.get("roles");

        return accountRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }

    private Collection<? extends GrantedAuthority> extractGroups(Jwt jwt) {

        Map<String, Object> claims = jwt.getClaims();
        for (Map.Entry<String, Object> entry: claims.entrySet()){
            String key = entry.getKey();
            Object value = entry.getValue();
        }

        ArrayList<String> groups = (ArrayList<String>) claims.get("user_groups");

        if (groups == null) return Set.of();

        return groups.stream()
                .map(role -> new SimpleGrantedAuthority("GROUP_" + role))
                .collect(Collectors.toSet());
    }

    private Collection<? extends GrantedAuthority> extractScope(Jwt jwt) {

        String scopesValues = jwt.getClaim("scope");
        String[] allScopes = scopesValues.split(" ");
        ArrayList<String> scopeList = new ArrayList<>();
        for (String item: allScopes ) {
            scopeList.add(item);
        }

        return scopeList.stream()
                .map(role -> new SimpleGrantedAuthority("SCOPE_" + role))
                .collect(Collectors.toSet());

    }
}

