package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

/**
 * Clase que representa la entidad Rol en la base de datos.
 * Esta entidad se utiliza para almacenar los roles de los usuarios.
 */
@Entity
@Table(name = "rol")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tipo_rol", length = 10)
    private String tipoRol;

}
