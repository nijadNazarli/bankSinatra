package com.miw.service.authentication;
import com.miw.database.JdbcTokenDao;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.crypto.spec.SecretKeySpec;
import javax.management.relation.Role;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

//TODO: constructor en autowire @service etc.

@Service
public class TokenService {

    private JdbcTokenDao jdbcTokenDao;

    @Autowired
    public TokenService(JdbcTokenDao jdbcTokenDao) {
        this.jdbcTokenDao = jdbcTokenDao;
    }

    public TokenService() {
    }


    public String jwtBuilder(String userEmail, long expTime){
        //generating secret key for JWT signature
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("d24145c413bac64082d2a9681e20890a");
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());

        //set JWT Claims
        JwtBuilder builder = Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setSubject(userEmail)
                .setExpiration(new Date(System.currentTimeMillis() + expTime))
                //TODO: voeg rol toe in payload.
                //.setClaims("roles", jdbcUserDao.getRoleByEmail(userEmail))
                .signWith(SignatureAlgorithm.HS256, signingKey);

        //Building JWT set to compact, URL-safe string
        return builder.compact();
    }

    public static Claims decodeJWT(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary("d24145c413bac64082d2a9681e20890a"))
                .parseClaimsJws(jwt).getBody();
    }

    public Boolean decodeJWTBool(String jwt) {
        //This line will throw an exception if it is not a signed JWS (as expected)
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary("d24145c413bac64082d2a9681e20890a"))
                    .parseClaimsJws(jwt).getBody();
        } catch (ExpiredJwtException expired) {
            // checken of refreshmenttoken nog geldig is?

            return false;
        }
        return true;
    }


    public String generateRefreshToken() {
        return UUID.randomUUID().toString();
    }

    public boolean validateRefreshToken(String token) {
        // TODO: checken op datum?
        return jdbcTokenDao.retrieveToken(token) != null;
    }

    // TODO: methode creeren die token meegeeft via de header --> IN HTML


}


///////         NOTES           //////



//Base64.Decoder decoder;
//return Base64.getUrlEncoder().encodeToString(bytes);

/*
 * JWT:
 * token is een string bestaande uit 3 delen (gescheiden door een punt -> .)
 * wordt aangemaakt tijdens het inloggen
 *
 * deel 1: HEADER: algoritme en token type (JWT)
 * deel 2: value/data of the token: bijv naam, rol, id en email, time, (nothing confidential, no password)
 * just enough data of user to verivy who that person is
 * deel 3: Verify signature: (header, payload en secret --> annaloog aan pepper
 * deze info wordt meegegeven vanuit de website
 * en is de signature waaraan de site kan zien dat de JWT legitiem is (crytocraphic hash)
 *
 * Header and payload zijn omgezet in een string aan de hand van Based64 encoded
 * (niet als beveiliging maar voor de convienence) -> voor de server to validate if info is correct.
 * Verify signature: created by server with help of the secret (pepper)
 * is gelinkt aan de header en payload
 *
 * FLow:
 * - user logt in
 * - gegevens worden gecheckt (authenticatie)
 * - jwt wordt gecreeert
 * - jwt wordt terug gestuurd naar de user (local storage of cookie)
 * - With every new user request:
 *       - jwt gaat terug naar de server
 *       - via HTTP HEADER: key/value names: key: Authoraziation, value: Bearer JWT
 *       - server checkt JWT
 *           1. splitsen in drie delen
 *           2. based64 decoding of part 1 en 2
 *           3. calculates signature of this decoding
 *           4. checks if this calculated signature matches the given signature (part 3)
 *
 * Expiration payload: (little tricky)
 * - Blacklisted JWT --> JWT's that are no longer valid
 * */

// header en payload omzetten in json