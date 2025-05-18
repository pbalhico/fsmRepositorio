package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

/**
 * Clase que representa la entidad Seccion en la base de datos.
 * Esta entidad se utiliza para almacenar las secciones de los artículos.
 */
@Entity
@Table(name = "seccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeccionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(unique = true, length = 50)
    private String categoriaSeccion;
}
