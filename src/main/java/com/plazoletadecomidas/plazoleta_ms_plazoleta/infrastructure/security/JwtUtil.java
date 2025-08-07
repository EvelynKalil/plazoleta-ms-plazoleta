package com.plazoletadecomidas.plazoleta_ms_plazoleta.infrastructure.security;
import com.plazoletadecomidas.plazoleta_ms_plazoleta.domain.model.Role;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtil {
    private SecretKey secretKey;

    Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    String secret = dotenv.get("JWT_SECRET_KEY");

    @PostConstruct
    public void init() {
        byte[] keyBytes = secret.getBytes();
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UUID userId, Role role) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("role", role.name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1h
                .signWith(secretKey)
                .compact();
    }

    public SecretKey getSecretKey() {
        return this.secretKey;
    }
}
