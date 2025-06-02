package es.examplepb.findstockmanager.seguridad;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    private final DataSource dataSource;

    public SeguridadConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Order(1)
    public SecurityFilterChain h2ConsoleFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(PathRequest.toH2Console())
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(PathRequest.toH2Console()).permitAll()) //acceder a h2
                .csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));
        return http.build();
    }

    @Bean
    @Order(2) // Se aplica después de la cadena de H2
    public SecurityFilterChain applicationSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilitar CSRF (considera habilitarlo en producción)
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF completamente para la aplicación.
                // ¡Cuidado! En producción, deberías configurarlo correctamente o habilitarlo.

                // Define las reglas de autorización para las rutas
                .authorizeHttpRequests(auth -> auth
                        // Rutas estáticas y páginas públicas
                        .requestMatchers("/", "/webjars/**", "/index", "/listaPedidosTienda", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // Rutas de login/logout
                        .requestMatchers("/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        // Rutas específicas de tu aplicación
                        .requestMatchers("/articulos/{id}").permitAll() // Permite acceso a /articulos/{id} para ver el detalle
                        // Protege el nuevo endpoint para crear pedidos manuales
                        .requestMatchers(HttpMethod.POST, "/pedidos/crear-reposicion-manual").authenticated() // Roles para tu endpoint
                        // Aquí puedes añadir más reglas específicas con .requestMatchers().hasRole/hasAnyRole etc.
                        // EJEMPLO: .requestMatchers("/pedidos/**").hasAnyRole("ALMACENERO", "ADMIN")
                        // Cualquier otra solicitud requiere autenticación
                        .anyRequest().authenticated()
                )
                // Configura el formulario de login
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // URL a la que se envía el formulario de login (Spring Security la maneja)
                        .defaultSuccessUrl("/", true) // Redirigir a la raíz después de login exitoso
                        .failureUrl("/login?error") // Redirigir en caso de login fallido
                        .permitAll() // Permite el acceso a la página de login y al proceso de login
                )
                // Configura el logout
                .logout(out -> out
                        .logoutUrl("/logout") // URL para cerrar sesión
                        .logoutSuccessUrl("/login?logout") // Redirigir después de cerrar sesión
                        .invalidateHttpSession(true) // Invalidar sesión
                        .deleteCookies("JSESSIONID") // Eliminar cookies de sesión
                        .permitAll() // Permite acceso a la URL de logout
                );

        return http.build();
    }

    @Bean //para que Spring Security lo utilice en la autenticación
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean //Configura las consultas SQL para obtener los usuarios y roles desde la base de datos
    public JdbcDaoImpl jdbcDao() {
        JdbcDaoImpl jdbcDao = new JdbcDaoImpl();
        jdbcDao.setDataSource(dataSource);
        jdbcDao.setUsersByUsernameQuery("SELECT email, password, true FROM USUARIO WHERE email = ?");
        jdbcDao.setAuthoritiesByUsernameQuery("SELECT u.email, r.tipo_rol FROM USUARIO u JOIN ROL r ON u.rol_id = r.id WHERE u.email = ?");
        return jdbcDao;
    }
}
