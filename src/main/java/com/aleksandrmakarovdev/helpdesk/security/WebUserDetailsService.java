package com.aleksandrmakarovdev.helpdesk.security;

import com.aleksandrmakarovdev.helpdesk.user.entity.User;
import com.aleksandrmakarovdev.helpdesk.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WebUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> foundUser = userRepository.findByUsername(username);

        if (foundUser.isEmpty()) {
            throw new UsernameNotFoundException(String.format("User '%s' not found.", username));
        }

        return new WebUserDetails(foundUser.get());
    }
}
