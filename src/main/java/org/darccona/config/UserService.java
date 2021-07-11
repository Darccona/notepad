package org.darccona.config;


import org.darccona.database.UserEntity;
import org.darccona.database.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserEntity user = (UserEntity) repository.findByName(username);
        if (user == null)
            throw new UsernameNotFoundException("Неизвестный логин: " + username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getName())
                .password(user.getPassword())
                .roles("user")
                .build();
    }
}