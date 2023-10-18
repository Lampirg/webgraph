package dev.lampirg.webgraph.db.convert;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.convert.PropertyValueConverter;
import org.springframework.data.convert.ValueConversionContext;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;

public class AuthorityConverter implements PropertyValueConverter<Collection<? extends GrantedAuthority>, Collection<String>, ValueConversionContext<?>> {

    @Override
    public Collection<? extends GrantedAuthority> read(@NotNull Collection<String> value, @NotNull ValueConversionContext<?> context) {
        return value.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public Collection<String> write(@NotNull Collection<? extends GrantedAuthority> value, @NotNull ValueConversionContext<?> context) {
        return value.stream().map(GrantedAuthority::getAuthority).toList();
    }
}
