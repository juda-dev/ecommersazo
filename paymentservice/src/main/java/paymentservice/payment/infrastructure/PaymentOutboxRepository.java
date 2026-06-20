package paymentservice.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentOutboxRepository extends JpaRepository<PaymentOutbox, String> {
}
