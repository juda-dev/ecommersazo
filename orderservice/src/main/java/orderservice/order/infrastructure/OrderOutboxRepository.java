package orderservice.order.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderOutboxRepository extends JpaRepository<OrderOutbox, String> {
}
