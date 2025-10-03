package SocialNetwork.SocialNetwork.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

@Service
public class JwtService {
    private final KeyPair keyPair;

    public JwtService() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            this.keyPair = generator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Tạo token
    public String generateToken(String subject, long expirationMinutes) {
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.RS256);
            long expirationMillis = expirationMinutes * 60 * 1000;
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(subject)
                .issuer("social-network")
                .expirationTime(new Date(System.currentTimeMillis() + expirationMillis)) // 7 ngày
                .build();

                SignedJWT signedJWT = new SignedJWT(header, claims);
                signedJWT.sign(new RSASSASigner(keyPair.getPrivate()));

            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Xác minh token
    public String validateToken(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            boolean verified = signedJWT.verify(new RSASSAVerifier((RSAPublicKey) keyPair.getPublic()));
            if (!verified) {
                System.out.println("❌ Invalid signature");
                return null;
            }
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();
            if(!"social-network".equals(claims.getIssuer())){
                System.out.println("❌ Invalid issuer: " + claims.getIssuer());
                return null;
            }
            Date now = new Date();
            if(claims.getExpirationTime() == null || now.after(claims.getExpirationTime())){
                System.out.println("❌ Token expired");
                return null;
            }
            if (verified && new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime())) {
                return signedJWT.getJWTClaimsSet().getSubject(); // username
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
