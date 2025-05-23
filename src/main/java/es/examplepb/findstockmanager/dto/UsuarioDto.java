package es.examplepb.findstockmanager.dto;

import es.examplepb.findstockmanager.entidades.RolEntity;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UsuarioDto {

    //solo necesito id si es para editar
    //private Integer id;

    // Cambiamos a recibir el ID del almacén
    private Long almacenId;
    // Cambiamos a recibir el ID del rol
    private Integer rolId;


    private String nombreAlmacen; // Campo para mostrar el nombre del almacén
    private String rolTipo;       // Campo para mostrar el tipo de rol (String)
    // private RolEntity rolEntity; // Eliminado: no se suele pasar la entidad completa en el DTO

    private String nombre;
    private String apellido;
    private String nif; // Si esta propiedad existe en tu entidad UsuarioEntity, asegúrate de que esté allí.
    private String email;
    // Campo para recibir el archivo subido directamente en el DTO
    private MultipartFile fotografiaUsuarioFile;
    private String password;

    // Campo para almacenar la ruta/nombre de la fotografía después de guardarla (para toDto)
    private String fotografiaUsuario;

}
