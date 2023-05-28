package ru.demichev.movies.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.demichev.movies.domain.Role;
import ru.demichev.movies.domain.UserPrincipal;
import ru.demichev.movies.repository.UserRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserAuthService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Optional<UserPrincipal> optionalUser = userRepository.findUserByLogin(login);
        if(optionalUser.isPresent()){
            UserPrincipal user = optionalUser.get();
            Set<String> roleList = new HashSet<>();
            for (Role role : user.getRoles()){
                roleList.add(role.getName());
            }
            //user.getRoles().stream().map(role -> roleList.add(role.getName()));
            log.info("role_list - {}",roleList);
            return User.builder()
                    .username(user.getLogin())
                    .password(user.getPassword())
                    .disabled(user.isDisabled())
                    .accountExpired(user.isAccountExpired())
                    .accountLocked(user.isAccountLocked())
                    .credentialsExpired(user.isCredentialsExpired())
                    .roles(roleList.toArray(new String[0]))
                    .build();
        }else{
            throw new UsernameNotFoundException("User Login is not Found");
        }
    }
}
