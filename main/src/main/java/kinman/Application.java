package kinman;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.sql.DataSource;

@SpringBootApplication
public class Application {

	@Autowired
    OrderRepository orderRepository;

    @Autowired
    DataSource dataSource;

    @Bean
    OrderService orderService() { return new OrderService(orderRepository, inventoryService()); }

    @Bean
    InventoryService inventoryService() { return new InventoryService(dataSource); }

    /*
     * Configure our webapp with a request interceptor that verifies API keys.
     */
    public static class WebConfig extends WebMvcConfigurerAdapter {
        @Autowired
        AccountRepository accountRepository;

        @Bean
        ApiKeyHandlerInterceptor apiKeyInterceptor() {
            return new ApiKeyHandlerInterceptor(accountRepository);
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(apiKeyInterceptor());
        }
    }

    @Bean
    CommandLineRunner init(AccountRepository accountRepository) {
        /*
         * Seed the system with a couple of client accounts.
         */
        accountRepository.save(new Account("The Green Grocer", "greengrocer123"));
        accountRepository.save(new Account("The Produce Stand", "producestand456"));
        return null;
    }

    public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
