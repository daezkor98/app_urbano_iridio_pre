@file:JvmName("Val")
package com.urbanoexpress.iridio3.urbanocore.values


/**
 * Created by Brandon Quintanilla on March/15/2022.
 */
object Menu {
    const val PLAN_DE_VIAJE = "plan_de_viaje"
    const val RUTA_GESTOR = "ruta_gestor"
    const val RUTA_DEL_DIA = "ruta_del_dia"
    const val RESUMEN_RUTA = "resumen_ruta"
    const val RUTA_EXPRESS = "ruta_express"
    const val NOTIFICACIONES = "notificaciones"
    const val MIS_GANANCIAS = "my_revenue"
    const val RUTA_DIARIA = "ruta_diaria"
}

val weekDays: Map<Int, String> = mapOf(
    0 to "Domingo",
    1 to "Lunes",
    2 to "Martes",
    3 to "Miercoles",
    4 to "Jueves",
    5 to "Viernes",
    6 to "Sábado",
)

object FragmentTAG {
    //TODO
}

const val TRUE = "1"
const val FALSE = "0"
