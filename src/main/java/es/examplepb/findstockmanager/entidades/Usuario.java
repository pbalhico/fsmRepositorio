package es.examplepb.findstockmanager.entidades;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    @ManyToOne
    @JoinColumn(name = "almacen_id")
    private Almacen almacen;

    @ManyToOne
    @JoinColumn(name = "rol_id")
    private Rol rol;

    private String nombre;
    private String fotografiaUsuario;
    private String password;
}
