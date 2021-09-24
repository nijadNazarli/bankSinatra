package com.miw.service.authentication;

import com.miw.database.JdbcTokenDao;
import com.miw.database.JdbcUserDao;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TokenService {
    private JdbcTokenDao jdbcTokenDao;
    private static JdbcUserDao jdbcUserDao;
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Autowired
    public TokenService(JdbcTokenDao jdbcTokenDao, JdbcUserDao jdbcUserDao) {
        this.jdbcTokenDao = jdbcTokenDao;
        this.jdbcUserDao = jdbcUserDao;
        logger.info("New Tokenservice created.");

    }

    //generating secret key for JWT signature
    public static Key generateKey(){
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(new PepperService().getPepper());
        return new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }


    public static String jwtBuilder(int userID, String role, long expTime){ // input: Role role (nieuwe klasse Role?)
        return jwtBuilderSetDate(userID, role, System.currentTimeMillis(), expTime);
    }


    //JWT builder with set date (necessary for testing)
    public static String jwtBuilderSetDate(int userID, String role, long msNow, long expTime){
        // creating specific claims to add
        Map<String, Object> tokenClaims = new HashMap<>();
        tokenClaims.put("userrole", role);

        //set JWT Claims and set to compact, URL-safe string
        JwtBuilder builder = Jwts.builder()
                .setClaims(tokenClaims)
                .setHeaderParam("typ", "JWT")
                .setIssuedAt(new Date(msNow))
                .setSubject(String.valueOf(userID))
                .setExpiration(new Date(msNow + expTime))
                .signWith(SignatureAlgorithm.HS256, generateKey());
        return builder.compact();
    }

    //Will throw an exception JWT is expired or invalid
    public static Claims decodeJWT(String jwt) {
        //TODO: splitsen op spatie en Bearer weghalen
        return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(new PepperService().getPepper()))
                .parseClaimsJws(jwt).getBody();
    }

    public static boolean validateJWT(String jwt) {
        int userID = Integer.valueOf(decodeJWT(jwt).getSubject());
        if (jdbcUserDao.checkIfBlockedByID(userID)){
            return false;
        }
        try {
            decodeJWT(jwt);
        } catch (ExpiredJwtException expired) {
            // TODO: checken of refreshmenttoken nog geldig is?
            return false;
        }
        return true;
    }

    // Will return userID if JWT is valid.
    public static int getValidUserID(String jwt) {
        int userID = Integer.valueOf(decodeJWT(jwt).getSubject());
        try {
            return Integer.valueOf(decodeJWT(jwt).getSubject());
        } catch (ExpiredJwtException expired) {
            return 0;
        }
    }

    // Will only return userrole if JWT is valid
    public static String getRole(String jwt) {
        try {
            return decodeJWT(jwt).get("userrole").toString();
        } catch (ExpiredJwtException invalid) {
            logger.info("No userrole found. JWT could be expired.");
            return "no userrole found";
        }
    }

    public static boolean validateAdmin(String jwt) {
        int userID = Integer.valueOf(decodeJWT(jwt).getSubject());
        if (jdbcUserDao.checkIfBlockedByID(userID)){
            return false;
        }
        try {
            return TokenService.getRole(jwt).equals("admin");
        } catch (ExpiredJwtException invalid) {
            logger.info("Either you are no admin or your token is expired and you need to log in");
            return false;
        }
    }

    public static boolean validateClient(String jwt) {
        int userID = Integer.valueOf(decodeJWT(jwt).getSubject());
        if (jdbcUserDao.checkIfBlockedByID(userID)){
            return false;
        }
        try {
            return TokenService.getRole(jwt).equals("client");
        } catch (ExpiredJwtException invalid) {
            logger.info("Either you are no client or your token is expired and you need to log in");
            return false;
        }
    }
} // end of main