package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

/**
 * Clase que representa la entidad Usuario en la base de datos.
 * Esta entidad se utiliza para almacenar los usuarios del sistema.
 */
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private AlmacenEntity almacen;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private RolEntity rolEntity;

    @Column(name = "nombre", length = 50)
    private String nombre;

    @Column(name = "apellido", length = 50)
    private String apellido;
    @Column(name = "nif", length = 9)
    private String nif;
    @Column(name = "email", length = 50)
    private String email;
    @Column(name = "fotografia_usuario", length = 200)
    private String fotografiaUsuario;
    @Column(name = "password", length = 255)
    private String password;
}
