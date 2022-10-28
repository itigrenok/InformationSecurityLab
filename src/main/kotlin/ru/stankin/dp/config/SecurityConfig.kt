package ru.stankin.dp.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import ru.stankin.dp.handler.CustomAccessDeniedHandler
import ru.stankin.dp.handler.CustomFilter
import ru.stankin.dp.handler.CustomLogoutHandler
import ru.stankin.dp.service.UserRepository


@Configuration
class SecurityConfig(
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
    private val userRepository: UserRepository,
    private val customFilter: CustomFilter,
    private val customLogoutHandler: CustomLogoutHandler
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf().disable()
            .authorizeRequests()
            .antMatchers("/**/*.js", "/**/*.css").permitAll()
            .antMatchers("/", "/about", "/cipher").permitAll()
            .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
            .antMatchers("/admin/**").hasAnyRole("ADMIN")
            .anyRequest().authenticated().and()
            .formLogin()
            .loginPage("/login")
            .defaultSuccessUrl("/user")
            .permitAll()
            .and()
            .logout()
                .logoutUrl("/logout")
                .addLogoutHandler(customLogoutHandler)
                .logoutSuccessUrl("/")
                .permitAll().and()
            .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler).and()
            .addFilterAfter(customFilter, BasicAuthenticationFilter::class.java)

        return http.build()
    }

    @Bean
    fun inMemoryUserDetailsManager(): InMemoryUserDetailsManager {
        return InMemoryUserDetailsManager(userRepository.findUserDetails())
    }
}