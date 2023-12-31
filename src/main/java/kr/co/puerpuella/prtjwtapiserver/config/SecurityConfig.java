package kr.co.puerpuella.prtjwtapiserver.config;

import kr.co.puerpuella.prtjwtapiserver.common.enums.Roles;
import kr.co.puerpuella.prtjwtapiserver.security.JwtAccessDeniedHandler;
import kr.co.puerpuella.prtjwtapiserver.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    /**
     * BCrypt암호화 알고리즘을 이용한 암호화 변환기
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, HandlerMappingIntrospector introspector) throws Exception {

        httpSecurity
                //csrf 설정 :: disable
                .csrf(AbstractHttpConfigurer::disable)

                .exceptionHandling(authenticationManager -> authenticationManager
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

                .sessionManagement(sessionManager -> {
                    sessionManager.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })

                .authorizeHttpRequests(authorizeHttpRequests -> {

                    authorizeHttpRequests.requestMatchers(new AntPathRequestMatcher("/h2/**")).permitAll();

                    authorizeHttpRequests.requestMatchers(new MvcRequestMatcher(introspector,"/main")).authenticated();
                    authorizeHttpRequests.anyRequest().permitAll();
                })
        ;


        return httpSecurity.build();
    }
}
