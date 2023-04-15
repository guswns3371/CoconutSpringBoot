package com.coconut.base.config;


import com.coconut.auth.ouath.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
// @EnableWebSecurity
// spring security 설정들을 활성화시켜준다.
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    // .csrf().disable().headers().frameOptions().disable()
    // h2-console 화면을 사용하기 위해서 해당 옵션들을 disable 한다.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()
                .and()
                    // authorizeRequests
                    // URL별 권한 관리를 설정하는 옵션의 시작점이다. authorizeRequests가 선언되어야만 antMatchers 옵션을 사용할 수 있다.
                    .authorizeRequests()
                    // antMatchers
                    // 권한 관리 대상을 지정하는 옵션이다.
                    // URL, HTTP 메소드별로 관리가 가능하다. 지정된 URL들은 permitAll() 옵션을 통해 전체 열람 관한을 준다.
                    .antMatchers(
                            "/socket-endpoint/**",
                            "/", "/uploads/**", "/css/**", "/js/**", "/h2-console?**","/api/**").permitAll()
//                    .antMatchers("/**").hasRole(Role.USER.name())
                // anyRequest
                .and()
                    // .logout().logoutSuccessUrl("/")
                    // 로그아웃 기능에 대한 여러 설정의 진입점이다. 로그아웃 성공시 "/" 주소로 이동한다.
                    .logout()
                    .logoutSuccessUrl("/")
                .and()
                    // oauth2Login
                    // OAuth 2 로그인 기능에 대한 여러 설정의 진입점이다.
                    // userInfoEndpoint
                    // OAuth 2 로그인 성공 이후 사용자 정보를 가져올 떄의 설정들을 담당한다.
                    .oauth2Login()
                        .userInfoEndpoint()
                            // userService
                            // 소셜 로그인 성공시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록한다.
                            // 리소스 서버(소셜 서비스들)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자하는 기능을 명시할 수 있다.
                            .userService(customOAuth2UserService);
    }
}
