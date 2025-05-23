package es.examplepb.findstockmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // La ruta /login y /home ya son manejadas por MainController, así que las eliminamos de aquí.
        // registry.addViewController("/login").setViewName("login");
        // registry.addViewController("/home").setViewName("home");

        // Esta redirección de la raíz sigue siendo útil si no la manejas en otro controlador.
        registry.addViewController("/").setViewName("redirect:/index");

        // Las rutas /error y /access-denied han sido eliminadas según tu solicitud.
    }
}

