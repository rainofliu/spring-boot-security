package com.tianba.springboot.springsecurity.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * @Auther: ajin
 * @Date: 2019/1/16 19:35
 * @Description:
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/resources/**","/").permitAll()//不用登陆也可以访问的页面(其中home这个请求不存在)
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/content/**").access("hasRole('ADMIN')or hasRole('USER')")
                .anyRequest().authenticated()//其他请求，只有登录后才能访问到
                .and()
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .csrf()
                .ignoringAntMatchers("/logout");
    }
    //角色权限
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
            auth.inMemoryAuthentication()
                    .passwordEncoder(new BCryptPasswordEncoder())
                    .withUser("user")
                        .password(new BCryptPasswordEncoder()
                            .encode("123456")).roles("USER")
                    .and()
                    .withUser("admin")
                        .password(new BCryptPasswordEncoder()
                                .encode("admin")).roles("ADMIN","USER");

    }
}
