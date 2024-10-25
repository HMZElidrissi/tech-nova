package ma.hmzelidrissi.models;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name = "users")
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "first_name")
  private String firstName;

  @Column(name = "last_name")
  private String lastName;

  @Column(name = "identification_number", unique = true)
  private String identificationNumber;

  @Column(name = "nationality")
  private String nationality;

  @Column(name = "registration_date")
  private LocalDate registrationDate;

  @Column(name = "expiration_date")
  private LocalDate expirationDate;
}
