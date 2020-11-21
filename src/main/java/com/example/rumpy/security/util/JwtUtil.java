package com.example.rumpy.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtUtil {
    // TODO: change the SECRET_KEY to something more secure
    private String SECRET_KEY = "THisshouldbeasecurestringforthesecretstring!#@#$@%@#$@#$@#%@#@#@##skdfb2@3 as)23092baukjbfda[asdfbajsfkadsf{}asbdfj";

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }//end method extractUsername

    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }//end method extractExpiration

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }//end method extractClass

    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).parseClaimsJws(token).getBody();
    }//end method extractAllClaims

    private Boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }//end method isTokenExpired

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        claims.put("authorities", authorities);

        return createToken(claims, userDetails.getUsername());
    }//end method generateToken

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10 * 1000))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY.getBytes()).compact();
    }//end method createToken

    public Boolean validateToken(String token, UserDetails userDetails){
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }//end method validateToken
}//end class JwtUtil

