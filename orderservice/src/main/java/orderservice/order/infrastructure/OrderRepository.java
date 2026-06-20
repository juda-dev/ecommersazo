package orderservice.order.infrastructure;

import orderservice.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
    Page<Order> findByCustomerId(String customerId, Pageable pageable);
}

