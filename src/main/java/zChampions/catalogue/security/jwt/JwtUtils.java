package zChampions.catalogue.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import zChampions.catalogue.security.config.MyUserDetails;

import java.security.Key;
import java.util.Date;
import java.util.List;


@Component
public class JwtUtils {
    @Value("${auth.token.jwtSecret}")
    private String jwtSecret;
    @Value("${auth.token.expiration}")
    private int lifetime;


    public String generateToken(Authentication authentication) {
        MyUserDetails userDetails = (MyUserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority).toList();



       String token =  Jwts.builder()
                .setSubject((userDetails.getEmail()))
                .claim("id", userDetails.getUserId())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + lifetime))
                .signWith( key(), SignatureAlgorithm.HS256)
                .compact();

return token;
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

    }

    public String getNameFromJwt(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException  |
                 IllegalArgumentException e) {
            throw new JwtException(e.getMessage());
        }
    }
}
