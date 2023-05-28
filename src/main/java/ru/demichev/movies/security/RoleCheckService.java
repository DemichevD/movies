package ru.demichev.movies.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service("RoleCheckService")
public class RoleCheckService {
    public boolean canAdminAndEditorCallGetInformation(Authentication authentication){
        Set<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return authorities.contains("ROLE_ADMIN") || authorities.contains("ROLE_EDITOR");

    }

    public boolean canViewerAndEditorCallGetInformation(Authentication authentication){
        Set<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return authorities.contains("ROLE_VIEWER") || authorities.contains("ROLE_EDITOR");
    }
}
