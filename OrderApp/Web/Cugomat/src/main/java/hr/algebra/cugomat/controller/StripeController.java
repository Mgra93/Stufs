package hr.algebra.cugomat.controller;


import hr.algebra.cugomat.dto.StripeCheckoutResponseDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe")
public class StripeController {
    public StripeController() {
        Stripe.apiKey = "";
    }

    @PostMapping("/payment")
    public ResponseEntity<StripeCheckoutResponseDTO> createCheckoutSession(@RequestParam double amount) throws StripeException {
        try {
            long centAmount = (long) (amount * 100);

            String url_success = "https://cugomat.com/success";
            String url_cancel = "https://cugomat.com/cancel";
            String session_name = "Cugomat order";
            String currency = "eur";

            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(url_success)
                            .setCancelUrl(url_cancel)
                            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency(currency)
                                                            .setUnitAmount(centAmount)
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName(session_name)
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .build();

            Session session = Session.create(params);

            StripeCheckoutResponseDTO responseDTO = new StripeCheckoutResponseDTO(session.getUrl(), session.getId());
            return ResponseEntity.ok(responseDTO);
        } catch (StripeException ex) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
