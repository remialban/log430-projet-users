package ca.log430.users.adapter.messaging;

import ca.log430.users.domain.model.User;
import ca.log430.users.ports.out.UserRepositoryOut;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class KafkaConsumerService {
    private final UserRepositoryOut userRepository;
    private final ObjectMapper objectMapper;

    public KafkaConsumerService(UserRepositoryOut userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "transactionCompleted", groupId = "users")
    public void onTransactionCompleted(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            Integer buyerId = jsonNode.get("buyerUserId").asInt();
            Integer sellerId = jsonNode.get("sellerUserId").asInt();
            BigDecimal amount = new BigDecimal(jsonNode.get("amount").asText());

            Optional<User> buyerOptional = userRepository.findById(buyerId);
            Optional<User> sellerOptional = userRepository.findById(sellerId);

            if (buyerOptional.isPresent() && sellerOptional.isPresent()) {
                User buyer = buyerOptional.get();
                User seller = sellerOptional.get();

                buyer.setBalance(buyer.getBalance().subtract(amount));
                seller.setBalance(seller.getBalance().add(amount));

                userRepository.save(buyer);
                userRepository.save(seller);
            }


        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
