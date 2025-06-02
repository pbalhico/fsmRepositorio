package es.examplepb.findstockmanager.config;

import org.springframework.beans.factory.annotation.Value; // Importa esta anotación
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry; // Importa esta clase
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Inyecta la propiedad 'upload.path.images' aquí
    @Value("${upload.path.images}")
    private String uploadImagePath;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Tu configuración existente para las vistas
        registry.addViewController("/").setViewName("redirect:/index");
        registry.addViewController("/error/acceso-denegado").setViewName("error/acceso-denegado");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 1. Manejador para las imágenes de artículos subidas
        // Estas imágenes serán accesibles a través de la URL /imagenes-articulos/**
        // Por ejemplo, si guardas una imagen como 'abcd-123.jpg', se accederá en:
        // http://localhost:8080/imagenes-articulos/abcd-123.jpg
        registry.addResourceHandler("/imagenes-articulos/**")
                // 'file:' indica que la ruta es un directorio del sistema de archivos.
                // Asegúrate de que 'uploadImagePath' termina con '/' para que sea un directorio válido.
                .addResourceLocations("file:" + uploadImagePath + "/");

        // 2. Manejador para tus recursos estáticos estándar (CSS, JS, imágenes preexistentes en /static)
        // ¡Esto es crucial para que tus estilos y scripts sigan funcionando!
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // Opcional: Si tienes recursos en la raíz de /static y quieres que sean accesibles sin /static/
        // registry.addResourceHandler("/**")
        //         .addResourceLocations("classpath:/static/");
    }
}