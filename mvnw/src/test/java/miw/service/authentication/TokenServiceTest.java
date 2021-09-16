package miw.service.authentication;

import com.miw.service.authentication.ByteArrayToHexHelper;
import com.miw.service.authentication.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import javax.xml.bind.DatatypeConverter;


class TokenServiceTest {

    ByteArrayToHexHelper byteArrayToHexHelper = new ByteArrayToHexHelper();
    TokenService tokenService = new TokenService();

    @Test
    void jwtBuilder() {
        String expiredJwt = tokenService.jwtBuilder("test@testen.nl", 1); //10 min

        String actualJwt = tokenService.jwtBuilder("test@testen.nl", 600000); //10 min



        System.out.println(actualJwt);

        String actualClaim = tokenService.decodeJWT(actualJwt).toString();
        String expected = "{iat=1630414071, sub=test@testen.nl, exp=1630424071}";

        System.out.println();


        Boolean expired = tokenService.decodeJWTBool(expiredJwt);
        System.out.println(expired);

        Boolean notExpired = tokenService.decodeJWTBool(actualJwt);
        System.out.println(notExpired);



        JwsHeader jwtHeader = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary("pepper"))
                .parseClaimsJws(actualJwt).getHeader();


        String userEmail = Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary("pepper"))
                .parseClaimsJws(actualJwt).getBody().getSubject().toString();


        System.out.println(jwtHeader);

        System.out.println("actualClaims = " + actualClaim);




        //TODO: expected jwt string toevoegen die rekening houdt met meegegeven tijd
        //String expectedJwt = "";
        //assertEquals(actualJwt, expectedJwt);
    }


    @Test
    public void decodeJWT() {
        //TODO: fixen van waarom onderstaande niet werkt..
        String jwtExample = "iOiJIUzI1NiJ9.eyJqdGkiOiIxMjMiLCJpYXQiOjE2MzA0MTMyODcsInN1YiI6InRlc3RAdGVzdGVuLm5sIiwiZXhwIjoxNjMwNDIzMjg3fQ.p5116Zlqjwg-HW-Qvj9RB3geZtSnkHJ5Ddb354bwZSk";

        Claims actualClaim = tokenService.decodeJWT("iOiJIUzI1NiJ9.eyJqdGkiOiIxMjMiLCJpYXQiOjE2MzA0MTMyODcsInN1YiI6InRlc3RAdGVzdGVuLm5sIiwiZXhwIjoxNjMwNDIzMjg3fQ.p5116Zlqjwg-HW-Qvj9RB3geZtSnkHJ5Ddb354bwZSk");
        String expectedCLaim = "header={alg=HS256},body={jti=123, iat=1630413287, sub=test@testen.nl, " +
                "exp=1630423287},signature=p5116Zlqjwg-HW-Qvj9RB3geZtSnkHJ5Ddb354bwZSk";

        //assertEquals(actualClaim, expectedCLaim);




//        Claims actualClaim = tokenService.decodeJWT("eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxMjMiLCJpYXQiOjE2MzAxNTQ4NzMsInN1YiI6InN1YmplY3QiLCJpc3MiOiJpc3N1ZXIifQ.G186f5H_aLbRFGPpAUmrNe9vWO2cWIToVfVq29dX7NY");
//        System.out.println(actualClaim);
//


    }
}