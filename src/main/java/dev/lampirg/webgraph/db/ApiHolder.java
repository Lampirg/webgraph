package dev.lampirg.webgraph.db;

import dev.lampirg.webgraph.db.convert.AuthorityConverter;
import lombok.*;
import org.springframework.data.convert.ValueConverter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Document
@Getter
@Setter
@EqualsAndHashCode
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ApiHolder implements UserDetails {

    @MongoId
    private String username;
    private String apiKey;
    @ValueConverter(AuthorityConverter.class)
    private Collection<? extends GrantedAuthority> authorities;
    private Boolean isAccountNonExpired = true;
    private Boolean isAccountNonLocked = true;
    private Boolean isCredentialsNonExpired = true;
    private Boolean isEnabled = true;

    private ApiHolder(String username, String apiKey, Collection<? extends GrantedAuthority> authorities) {
        this.username = username;
        this.apiKey = apiKey;
        this.authorities = authorities;
    }

    public static ApiHolder customApiUser(String username, String apiKey, Collection<? extends GrantedAuthority> authorities) {
        return new ApiHolder(username, apiKey, authorities);
    }

    public static ApiHolder user(String username, String apiKey) {
        return customApiUser(username, apiKey, List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    public static ApiHolder admin(String username, String apiKey) {
        return customApiUser(username, apiKey, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return apiKey;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }
}
