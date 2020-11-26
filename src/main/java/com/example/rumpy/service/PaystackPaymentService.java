package com.example.rumpy.service;

import com.example.rumpy.exceptions.PaymentServiceException;
import com.example.rumpy.model.CustomerOrder;
import com.example.rumpy.model.TransactionPaymentInterface;
import com.example.rumpy.model.TransactionStatus;
import com.example.rumpy.model.User;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.net.SocketException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class PaystackPaymentService implements PaymentServiceInterface {
    Logger logger = LoggerFactory.getLogger(PaystackPaymentService.class);

    private final String SECRET_KEY;
    private final String BASE_URL = "https://api.paystack.co";
    private final String INITIALIZE_TRANSACTION_URL = "/transaction/initialize";
    private final String VERIFY_TRANSACTION_URL = "/transaction/verify/{reference}";

    private final WebClient webClient;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    public PaystackPaymentService(@Value("${paystack.secret_key}") String secretKey) {
        this.SECRET_KEY = secretKey;
        webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set(HttpHeaders.AUTHORIZATION, "Bearer " + SECRET_KEY);
                    httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                    httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                })
                .build();
    }

    @Override
    public <T extends TransactionPaymentInterface> String initializeTransaction(T customerOrder, String callbackUrl)
            throws PaymentServiceException {
        User user = userService.getAuthenticatedUser().get();

        JSONObject parsedJSON = new JSONObject();

        parsedJSON.put("amount", customerOrder.getAmount());
        parsedJSON.put("currency", "NGN");
        parsedJSON.put("email", user.getEmail());
        parsedJSON.put("reference", customerOrder.getReference());

        if (!(callbackUrl == null || "".equals(callbackUrl)))
            parsedJSON.put("callback_url", callbackUrl);

        JSONObject responseJSON = null;

        try{
            responseJSON = webClient.post()
                    .uri(INITIALIZE_TRANSACTION_URL)
                    .body(Mono.just(parsedJSON), JSONObject.class)
                    .retrieve()
                    .bodyToMono(JSONObject.class)
                    .block();
        }catch(WebClientResponseException e){
            e.printStackTrace();
            throw new PaymentServiceException(e);
        }

        if (!responseJSON.containsKey("status"))
            throw new PaymentServiceException("Invalid json response");

        if (!((boolean) responseJSON.get("status")))
            throw new PaymentServiceException(String.format("%s : There was an error in the passed values => %s", PaystackPaymentService.class.toString(), responseJSON.get("message")));

        HashMap<String, String> data = (HashMap<String, String>) responseJSON.get("data");

        return data.get("authorization_url");
    }

    @Override
    public TransactionStatus getTransactionStatus(TransactionPaymentInterface transaction) throws PaymentServiceException {
        JSONObject responseJSON = webClient.get()
                .uri(VERIFY_TRANSACTION_URL, transaction.getReference())
                .retrieve()
                .bodyToMono(JSONObject.class)
                .block();

        if (!responseJSON.containsKey("status"))
            throw new PaymentServiceException("Invalid json response");

        if (!((boolean) responseJSON.get("status")))
            throw new PaymentServiceException(String.format("%s : There was an error in the passed values => %s", PaystackPaymentService.class.toString(), responseJSON.get("message")));

        HashMap<String, String> data = (HashMap<String, String>) responseJSON.get("data");
        String status = data.get("status");

        return switch (status) {
            case "success" -> TransactionStatus.SUCCESS;
            case "failed" -> TransactionStatus.DECLINED;
            case "abandoned" -> TransactionStatus.ABANDONED;
            default -> TransactionStatus.PENDING;
        };
    }

    @Override
    public void webhook(JSONObject requestJSON, HttpServletRequest request) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logger.info("This thing is safe =====================================>>>>>>>>>>>>>>>>>>>");
                HashMap<String, String> data = (HashMap<String, String>) requestJSON.get("data");
                final String reference = data.get("reference");
                CustomerOrder customerOrder = orderService.findByOrderId(reference).get();
                orderService.validateTransactionStatus(customerOrder);
                logger.info("===========================iiiiiiiiiiiiiiiiiiiiiiiiiii=======>");
            }
        };
        executor.submit(runnable);
    }//end method webhook
}//end class PaymentService
