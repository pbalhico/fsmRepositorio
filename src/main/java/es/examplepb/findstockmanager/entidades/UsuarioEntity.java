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
@Builder
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Usamos Long para que coincida con BIGINT en SQL

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private AlmacenEntity almacen;

    @ManyToOne
    @JoinColumn(name = "rol_id", nullable = false)
    private RolEntity rolEntity;

    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "apellido", length = 50)
    private String apellido;
    @Column(name = "nif", length = 9)
    private String nif;
    @Column(name = "email", length = 50, unique = true, nullable = false)
    private String email;
    @Column(name = "fotografia_usuario", length = 200)
    private String fotografiaUsuario;
    @Column(name = "password", length = 255, nullable = false)
    private String password;

    // Constructor adicional para el DataLoader, si no quieres usar el AllArgsConstructor
    public UsuarioEntity(Long id, RolEntity rolEntity, String nombre, String email, String password) {
        this.id = id;
        this.rolEntity = rolEntity;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        // Los otros campos se inicializarán a null por defecto
    }
}
