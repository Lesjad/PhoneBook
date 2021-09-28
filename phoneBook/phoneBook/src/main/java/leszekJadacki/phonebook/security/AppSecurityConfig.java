package leszekJadacki.phonebook.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AppSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AppSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                //.and()
                //above I tried to achieve possibility of login from client on another port.
                //unfortunately current requirements regarding cookies cannot be applied this way.
                //see: https://github.com/spring-projects/spring-boot/issues/15047
                //I'd have to implement the client on the same origin as backend
                .csrf().disable() //disabling csrf for now to be able to use my prepared client as it was
                .cors()
                .and()
                .authorizeRequests()
                .anyRequest()
                .authenticated()
                .and()
                .httpBasic();
    }


    @Override
    @Bean
    protected UserDetailsService userDetailsService() {

        UserDetails testUser = User.builder()
                .username("testUser")
                .password(passwordEncoder.encode("password1"))
                .authorities(AppUserRole.ADMIN.getGrantedAuthorities())
                .build();

        return new InMemoryUserDetailsManager(testUser);
        //return new JdbcDaoImpl();
    }
}
