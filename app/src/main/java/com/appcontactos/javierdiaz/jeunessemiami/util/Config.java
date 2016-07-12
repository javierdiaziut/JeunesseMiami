package com.appcontactos.javierdiaz.jeunessemiami.util;

/**
 * Created by Javier on 31/05/2016.
 */
public class Config {
    //URL de los servicios
    public static String url = "http://104.131.98.76/api-lp/cliente_user.php?";

    //URL de los servicios para sincronizar contactos
    public static String url_sincronizar = "http://104.131.98.76/api-lp/cliente.php?";

    //Endpoint para cargar contactos
    public static String metodo_contactos = "method=addContactsLot&";

    //Endpoint para hacer login
    public static String metodo_login = "method=login&";

    //Endpoint para hacer logout
    public static String metodo_logout = "method=logout&";

    //Endpoint para traer los mensajes
    public static String url_mensaje ="http://104.131.98.76/api-lp/cliente_sms.php";


}
