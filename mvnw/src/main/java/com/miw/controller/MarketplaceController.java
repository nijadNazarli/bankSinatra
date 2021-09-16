package miw.controller;

import com.miw.database.RootRepository;
import com.miw.model.Asset;
import com.miw.service.authentication.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MarketplaceController {

    private RootRepository rootRepository;
    private final Logger logger = LoggerFactory.getLogger(MarketplaceController.class);

    @Autowired
    public MarketplaceController(RootRepository rootRepository){
        this.rootRepository = rootRepository;
        logger.info("New MarketplaceController-object created");
    }

    @PostMapping("/requestCryptos")
    public ResponseEntity<?> getAssetsForSale(@RequestHeader("Authorization") String token, @RequestBody String symbol){
        if (!TokenService.validateJWT(token)) {
            return new ResponseEntity<>("Invalid login credentials, try again", HttpStatus.UNAUTHORIZED);
        }
        Integer userId = TokenService.getValidUserID(token);
        if (userId == null){
            return new ResponseEntity<>("booh", HttpStatus.UNAUTHORIZED);
        }

        int accountId = rootRepository.getAccountById(TokenService.getValidUserID(token)).getAccountId();

        List<Asset> assetsForSale = rootRepository.getAllAssetsForSaleBySymbol(symbol, accountId);
        if (assetsForSale != null){
            return new ResponseEntity<>(assetsForSale, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No assets available for sale", HttpStatus.BAD_REQUEST); //TODO: checken of dit de juiste statuscode is
        }
    }

}
