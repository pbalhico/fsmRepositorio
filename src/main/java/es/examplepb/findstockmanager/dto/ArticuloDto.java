package es.examplepb.findstockmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * DTO para la entidad Articulo.
 * Utilizado para transferir datos de Articulo de forma más ligera,
 * evitando la exposición de toda la entidad y las relaciones cíclicas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArticuloDto {
    @NotBlank(message = "El ID del artículo no puede estar vacío.")
    @Size(max = 20, message = "El ID del artículo no puede exceder los 20 caracteres.")
    private String id; // ID que se usará para la URL

    @NotBlank(message = "El nombre del artículo no puede estar vacío.")
    @Size(max = 50, message = "El nombre del artículo no puede exceder los 50 caracteres.")
    private String nombreArticulo;
    @NotBlank(message = "El color no puede estar vacío.")
    @Size(max = 20, message = "El color no puede exceder los 20 caracteres.")
    private String color;
    @NotNull(message = "El precio no puede ser nulo.")
    @Min(value = 0, message = "El precio debe ser un valor positivo.")
    private Double precio;
    @Size(max = 3, message = "La talla no puede exceder los 3 caracteres.")
    private String talla;
    @NotNull(message = "El stock no puede ser nulo.")
    @Min(value = 0, message = "El stock debe ser un valor no negativo.")
    private Integer stock;
    private MultipartFile fotografiaArtFile;
    private String fotografiaArt;
    private String categoriaSeccion; // Nombre de la sección para mostrar

    //aqui se va a referir a un desplegable de secciones, no a una entidad completa
    @NotNull(message = "La sección no puede ser nula.")
    private Integer seccionId; // Solo el ID de la sección para el formulario
}







