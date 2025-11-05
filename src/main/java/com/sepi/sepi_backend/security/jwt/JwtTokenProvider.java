/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.sepi.sepi_backend.security.jwt;

import io.jsonwebtoken.*;
import java.util.Date;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 *
 * @author azm
 */
/**
 * Componente responsável por gerar, validar e extrair informações do JWT.
 */
@Component
public class JwtTokenProvider
{

    // Chave secreta (deve ser forte e armazenada com segurança - exemplo simplificado)
    @Value("${app.jwtSecret}")
    private String jwtSecret;

    // Tempo de expiração do token (em milissegundos)
    @Value("${app.jwtExpirationMs}")
    private int jwtExpirationMs;

    // Gera um token JWT
    public String generateToken (Authentication authentication)
    {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        String roles = userPrincipal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .setSubject(userPrincipal.getUsername()) // Email como Subject
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Obtém o nome de usuário (email) do token
    public String getUsernameFromJWT (String token)
    {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Valida o token
    public boolean validateToken (String authToken)
    {
        try
        {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        }
        catch (SignatureException ex)
        {
            // Log de erro
        }
        catch (MalformedJwtException ex)
        {
            // Log de erro
        }
        catch (ExpiredJwtException ex)
        {
            // Log de erro
        }
        catch (UnsupportedJwtException ex)
        {
            // Log de erro
        }
        catch (IllegalArgumentException ex)
        {
            // Log de erro
        }
        return false;
    }
}
