package es.examplepb.findstockmanager.dto;

import es.examplepb.findstockmanager.entidades.RolEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {

    //solo necesito id si es para editar
    //private Integer id;

    // Cambiamos a recibir el ID del almacén
    private Integer almacenId;
    // Cambiamos a recibir el ID del rol
    private Integer rolId;

    private String nombreAlmacen;
    private RolEntity rolEntity;
    private String nombre;
    private String apellido;
    private String nif;
    private String email;
    // Campo para recibir el archivo subido directamente en el DTO
    private MultipartFile fotografiaUsuarioFile;
    private String password;

    // Si necesitas 'nombreAlmacen' o 'fotografiaUsuario' (String) para *mostrar*
    // información en otras pantallas o en el DTO de salida, puedes añadirlos,
    // pero no son necesarios para recibir datos del formulario de creación.
    // private String nombreAlmacen;
    // private String fotografiaUsuario; // Para almacenar la ruta/nombre después de guardar el archivo

}
