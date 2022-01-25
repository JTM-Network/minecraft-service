package com.jtm.minecraft.entrypoint.configuration

import com.jtm.minecraft.data.manager.AuthenticationManager
import com.jtm.minecraft.data.security.SecurityContextRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.server.SecurityWebFilterChain
import reactor.core.publisher.Mono

@Configuration
@EnableWebFluxSecurity
open class SecurityConfiguration {

    @Autowired
    private lateinit var manager: AuthenticationManager

    @Autowired
    private lateinit var contextRepository: SecurityContextRepository

    @Bean
    open fun webFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .exceptionHandling()
            .authenticationEntryPoint { exchange, _ -> Mono.fromRunnable { exchange.response.statusCode = HttpStatus.UNAUTHORIZED } }
            .accessDeniedHandler { exchange, _ -> Mono.fromRunnable { exchange.response.statusCode = HttpStatus.UNAUTHORIZED } }
            .and()
            .csrf().disable()
            .authenticationManager(manager)
            .securityContextRepository(contextRepository)
            .authorizeExchange()
            .pathMatchers(HttpMethod.OPTIONS).permitAll()


            .pathMatchers("/**").permitAll()
            .anyExchange().authenticated()
            .and()
            .build()
    }
}