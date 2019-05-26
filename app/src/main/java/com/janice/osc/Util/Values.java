package com.janice.osc.Util;

public class Values {
    public static final int PICK_IMAGE = 100;
    public static final int PICK_IMAGE_REQUEST = 1;
    public static final int ACTIVO = 1;
    public static final int INACTIVO = 0;
    public static final int PENDIENTE = 0;
    public static final int COMPLETO = 1;
    public static final int AGREGAR = 1;
    public static final int EDITAR = 2;
    public static final int EFECTIVO = 1;
    public static final int GOOGLE_PAY = 2;

    public static String valueName(int estado){
        switch (estado){
            case 0:
                return "Pendiente";
            case 1:
                return "Completo";

            default:
                return "Not a value";
        }
    }
}
