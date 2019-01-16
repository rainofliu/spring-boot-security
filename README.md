# spring-boot-security
Spring Boot整合Spring Security的基本demo（作为和纯洁的微笑大佬学习的实践）


## 概述

开发工具：idea 2018

技术：Spring Boot 2.0 ，Spring Security，thymeleaf

## 整合步骤

### 1.配置pom文件

```xml
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-thymeleaf</artifactId>
        </dependency>     
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
```



### 2.配置application.properties

```properties
#测试环境下，设置thymeleaf不使用缓存
spring.thymeleaf.cache=false
#Spring Security 配置用户的用户名，密码和角色（单用户状态） 对于多用户状态可以在配置类SecurityConfig中配置
spring.security.user.name=user
spring.security.user.password=123456
spring.security.user.roles=USER
```

### 3.新建配置类SecurityConfig

```java
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
                .loginPage("/login") //默认的登录页面，就是"/login"请求对应的页面，所以这里可以注释掉
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .and()
                .csrf()
                .ignoringAntMatchers("/logout");
    }
    //角色权限(此处配置后就用不到application.properties中的配置了)
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

```

- @EnableWebSecurity，开启 Spring Security 权限控制和认证功能
- anyRequest().authenticated()，表示其他的请求都必须要有权限认证。(也就是说，需要登录才能访问到)
- formLogin()，定制登录信息。
- loginPage("/login")，自定义登录地址，若注释掉则使用默认登录页面。
- logout()，退出功能，Spring Security 自动监控了 /logout。
- ignoringAntMatchers("/logout")，Spring Security 默认启用了同源请求控制，在这里选择忽略退出请求的同源限制。
- antMatchers("/resources/** ", "/").permitAll()，地址 "/resources/ **" 和 "/" 所有用户都可访问，permitAll 表示该请求任何人都可以访问；
- antMatchers("/admin/** ").hasRole("ADMIN")，地址 "/admin/**" 开头的请求地址，只有拥有 ADMIN 角色的用户才可以访问；
- antMatchers("/content/** ").access("hasRole('ADMIN') or hasRole('USER')")，地址 "/content/**" 开头的请求地址，可以给角色 ADMIN 或者 USER 的用户来使用；
- antMatchers("/admin/**").hasIpAddress("192.168.11.11")，只有固定 IP 地址的用户可以访问。

### 4.页面创建(在resource/template文件夹中依次创建如下页面)

+ content.html

  ```xml
  <!DOCTYPE html>
  <html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
  <body>
  <h1>content</h1>
  <p>我是登录后才可以看的页面</p>
  <form method="post" action="/logout">
      <button  type="submit">退出</button>
  </form>
  </body>
  </html>
  ```

* admin.html

  ```xml
  <!DOCTYPE html>
  <html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org"
        xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
  <head>
      <title>admin</title>
  </head>
  <body>
  <h1>admin</h1>
  <p>管理员页面</p>
  <p>点击 <a th:href="@{/}">这里</a> 返回首页</p>
  </body>
  </html>
  ```

  

* login.html

  ```xml
  <!DOCTYPE html>
  <html xmlns="http://www.w3.org/1999/xhtml"
        xmlns:th="http://www.thymeleaf.org"
        xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
  <head>
      <title>login</title>
  </head>
  <body>
  <div th:if="${param.error}">
      用户名或密码错
  </div>
  <div th:if="${param.logout}">
      您已注销成功
  </div>
  <form th:action="@{/login}" method="post">
      <div><label> 用户名 : <input type="text" name="username"/> </label></div>
      <div><label> 密 码 : <input type="password" name="password"/> </label></div>
      <div><input type="submit" value="登录"/></div>
  </form>
  </body>
  </html>
  ```

* index.html（不知道是否需要，也放上来了）

  ```xml
  <!DOCTYPE html>
  <html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
  <head>
      <meta charset="UTF-8">
      <title>index page</title>
  </head>
  <body>
  <p>今天是美好的一天</p>
  <a href="#" th:href="@{/index}">点击会跳转</a>
  <hr/>
  </body>
  </html>
  ```

  

### 5.Web层创建

```java
@Controller
public class IndexController {

    @RequestMapping("/content")
    public String content(){
        return "content";
    }

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login(){
        return "login";
    }
    @RequestMapping("/admin")
    public String admin() {
        return "admin";
    }
}
```

## 入门后

> 给自己挖个坑，如果后续有时间，认真阅读下徐靖峰（徐妈）的[Spring Security系列教程](https://www.cnkirito.moe/tags/Spring-Security/)，并进行一些文字输出
