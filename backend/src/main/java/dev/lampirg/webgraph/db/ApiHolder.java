package dev.lampirg.webgraph.db;

import dev.lampirg.webgraph.db.convert.AuthorityConverter;
import lombok.*;
import org.springframework.data.convert.ValueConverter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Document
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ApiHolder {

    @MongoId
    private String username;
    private String apiKey;
    @ValueConverter(AuthorityConverter.class)
    private Collection<? extends GrantedAuthority> authorities;

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
}
