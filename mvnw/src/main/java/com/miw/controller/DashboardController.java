package miw.controller;

import com.miw.controller.LoginController;
import com.miw.database.JdbcAccountDao;
import com.miw.database.JdbcAssetDao;
import com.miw.service.authentication.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DashboardController {

    private final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private TokenService tokenService;
    private JdbcAccountDao jdbcAccountDao;
    private JdbcAssetDao jdbcAssetDao;

    @Autowired
    public DashboardController(TokenService tokenService, JdbcAccountDao jdbcAccountDao, JdbcAssetDao jdbcAssetDao) {
        super();
        this.tokenService = tokenService;
        this.jdbcAccountDao = jdbcAccountDao;
        this.jdbcAssetDao = jdbcAssetDao;
        logger.info("New DashboardController created");
    }

    //TODO: waarom komt token niet binnen???
    @PostMapping("/getBalance")
    public double getBalance(@RequestBody String token) {
        int ID = TokenService.getValidUserID(token);
        return jdbcAccountDao.getAccountByUserID(ID).getBalance();
    }


//    @PostMapping("/getPortfolioValue")
//    public double getPortfolioValue(@RequestBody String token) {
//        int ID = TokenService.getValidUserID(token);
//        List<Asset> clientAssets = jdbcAssetDao.getAssets(ID);
//        double totalValue = 0.00;
//        for (Asset asset: clientAssets) {
//            totalValue += asset.getUnits() * asset.getCurrentValue();
//        }
//        return totalValue;
//    }





}


