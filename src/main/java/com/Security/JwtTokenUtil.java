package com.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil  implements Serializable {
    static final String CLAM_KEY_USERNAME = "sub";
    static  final String CLAM_KEY_AUDIENCE= "audience";
    private final String CLAM_KEY_CREATED ="created";
    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expiration;
    public String getUserNameFromToken(String token) {
        String username=null;
        try {
final Claims claims= getClaimsFromToken(token);
username=claims.getSubject();
        }
        catch (Exception e){
username=null;
        }
return  username;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims=null;
        try {
claims= Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        }
        catch (Exception e){
            claims=null;
        }
        return  claims;
    }

    public boolean validateToken(String token, UserDetails userDetails) {
JwtUser user= (JwtUser)userDetails;
final  String username= getUserNameFromToken(token);
return (username.equals(user.getUsername())&& !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        final Date expiration= getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    private Date getExpirationDateFromToken(String token) {
        Date expiration = null;
        try {
            final Claims claims = getClaimsFromToken(token);
            if (claims != null) {
                expiration = claims.getExpiration();
            } else {
                expiration = null;
            }
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;

    }

    public String generateToken(JwtUser useDetails) {
        Map<String,Object> clamis= new HashMap<String,Object>();
        clamis.put(CLAM_KEY_USERNAME,useDetails.getUsername());
        clamis.put(CLAM_KEY_CREATED,new Date());
        return  generateToken(clamis);

    }

    private String generateToken(Map<String, Object> clamis) {
       return  Jwts.builder().setClaims(clamis).setExpiration(generateExpirationDate()).signWith(SignatureAlgorithm.ES512,secret).compact();
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis()+expiration*1000);
    }
}
