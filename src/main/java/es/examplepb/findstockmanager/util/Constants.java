package es.examplepb.findstockmanager.util;

/**
 * Clase de constantes para IDs y nombres fijos utilizados en la aplicación,
 * especialmente para las tareas programadas y la inicialización de datos.
 */
public final class Constants {

    private Constants() {
        // Evita la instanciación de la clase de utilidades
    }

    // IDs fijos para entidades predefinidas (de application.properties)
    public static final Long ALMACEN_ID = 1L;
    public static final Long TIENDA_ID = 1L;
    public static final Long SYSTEM_USER_ID = 999L;

    // Nombres de roles predefinidos (de data.sql)
    public static final String ROL_SYSTEM = "system";

    // Email del usuario del sistema (para búsqueda robusta)
    public static final String SYSTEM_USER_EMAIL = "system@findstock.com";

    // Descripciones de estados de pedido predefinidos (de data.sql)
    public static final String ESTADO_PENDIENTE = "Pendiente";
    public static final String ESTADO_COMPLETADO = "Completado";
    public static final String ESTADO_ENVIADO = "Enviado";
    public static final String ESTADO_CANCELADO = "Cancelado";
    public static final String ESTADO_EN_PROCESO = "En Proceso";


    // Descripciones de tipos de pedido predefinidos (de data.sql)
    public static final String TIPO_TIENDA_ALMACEN = "tienda-almacen";
    public static final String TIPO_ALMACEN_TIENDA = "almacen-tienda";
    public static final String TIPO_REPOSICION_STOCK_ALMACEN = "reposicion stock almacen";

    // Cantidad mínima de stock para activar la reposición (de application.properties)
    public static final Integer STOCK_MINIMO_REPOSICION = 100;
    // Cantidad máxima de unidades por artículo en un pedido a tienda
    public static final Integer MAX_UNIDADES_POR_ARTICULO_PEDIDO_TIENDA = 100;
}
