package dev.Bsit1._A.Final_Project;

    import org.springframework.security.core.userdetails.User;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.core.userdetails.UsernameNotFoundException;
    import org.springframework.stereotype.Service;

    import java.util.ArrayList;

    @Service
    public class UserDetailsServiceImpl implements UserDetailsService {
        public UserDetailsServiceImpl() {

        }


        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            // Return a dummy user with username, password, and empty authorities
            return new User(username, "password", new ArrayList<>());
        }
    }




