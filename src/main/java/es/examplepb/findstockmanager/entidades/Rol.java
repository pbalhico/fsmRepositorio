package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "rol")
@Data
@NoArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rol_id")
    private Integer rolId;

    @Column(name = "tipo_rol", length = 100)
    private String tipoRol;

}
