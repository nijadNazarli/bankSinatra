package miw;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class BankSinatraApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.miw.BankSinatraApplication.class, args);
    }
}
