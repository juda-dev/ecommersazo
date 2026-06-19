package dev.juda.customerservice.customer.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface CustomerRepository extends MongoRepository<CustomerDocument, String> {
    Optional<CustomerDocument> findByEmail(String email);

    @Query("{ '$or': [ "
            + "{ 'first_name': { '$regex': ?0, '$options': 'i' } }, "
            + "{ 'last_name': { '$regex': ?0, '$options': 'i' } } "
            + "] }")
    Page<CustomerDocument> searchByName(String term, Pageable pageable);

    Page<CustomerDocument> findByActiveTrue(Pageable pageable);

    boolean existsByEmail(String email);
}
