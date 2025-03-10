package academy.devdojo.domain;

import jakarta.persistence.*;
import lombok.*;

@With
@Getter
@Setter
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
}
