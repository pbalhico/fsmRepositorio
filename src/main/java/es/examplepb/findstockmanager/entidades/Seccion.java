package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "seccion")
@Data
@NoArgsConstructor
public class Seccion {

    @Id
    private Integer seccionId;

    @Column(unique = true)
    private String categoriaSeccion;
}
