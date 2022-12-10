import java.util.Base64;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JwtUtil {
    private static final String JWT_SECRET = "my_secret";
    private static final int JWT_EXPIRATION_IN_MILLISECONDS = 60 * 60 * 1000; // 1 hour

    public static String generateJwt(Map<String, Object> claims) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(JWT_SECRET), signatureAlgorithm.getJcaName());

        JwtBuilder builder = Jwts.builder()
                                 .setClaims(claims)
                                 .setIssuedAt(now)
                                 .signWith(signatureAlgorithm, secretKey);

        if (JWT_EXPIRATION_IN_MILLISECONDS >= 0) {
            long expMillis = nowMillis + JWT_EXPIRATION_IN_MILLISECONDS;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

        return builder.compact();
    }

    public static Claims decodeJwt(String jwt) {
        return Jwts.parser()
                   .setSigningKey(Base64.getDecoder().decode(JWT_SECRET))
                   .parseClaimsJws(jwt)
                   .getBody();
    }
}
