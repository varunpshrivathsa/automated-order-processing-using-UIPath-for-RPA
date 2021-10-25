package kinman;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ApiKeyHandlerInterceptor extends HandlerInterceptorAdapter {

    AccountRepository accountRepository;

    public ApiKeyHandlerInterceptor(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /*
     *  Ensure that there is a valid API key on the request. It must be in a custom header named "kinman-api-key".
     *  In a production-ready application, I'd use a standard OAuth2 implementation rather than API key + customer header.
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String apiKey = request.getHeader("kinman-api-key");

        if (apiKey == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        Account account = accountRepository.findByApiKey(apiKey);

        if (account == null) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        // once we've found the account, tie it to the request for use by controllers.
        request.setAttribute("account", account);
        return true;
    }

}
