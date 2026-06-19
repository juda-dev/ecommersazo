package dev.juda.customerservice.customer.infrastructure;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@Document(collection = "customers")
public class CustomerDocument {

    @Id
    private String id;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    @Indexed(unique = true)
    @Field("email")
    private String email;

    @Field("phone")
    private String phone;

    @Field("addresses")
    private List<AddressDocument> addresses = new ArrayList<>();

    @Field("preferences")
    private PreferencesDocument preferences;

    @Field("active")
    private boolean active = true;

    @Field("created_at")
    private Instant createdAt;

    @Field("updated_at")
    private Instant updatedAt;

    @Getter
    @Setter
    public static class AddressDocument {
        private String type;
        private String street;
        private String city;
        private String state;
        private String zip;
        private String country;
        private boolean isDefault;
    }

    @Getter
    @Setter
    public static class PreferencesDocument {
        private String language;
        private String currency;
        private boolean emailNotifications;
        private boolean smsNotifications;
    }

}