package com.urbanoexpress.iridio.model.dto

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject


/**
 * Created by Brandon Quintanilla on March/02/2022.
 */
class ResponseOf<DataType> {
    val success: Boolean? = null
    val data: DataType? = null
}

inline fun <reified T> JSONObject.toInstance(): T? {
    return try {
//        Gson().fromJson<T>(this.toString(), object : TypeToken<T>() {}.type)//TODO use singleton
        Gson().fromJson<T>(JSON, object : TypeToken<T>() {}.type)//TODO use singleton
    } catch (e: Exception) {
        Log.e("TAG", "toInstance: $this", e)
        null
    }
}

const val JSON = "{\n" +
        "    \"success\": true,\n" +
        "    \"msg_error\": \"DB NULL.\",\n" +
        "    \"data\": {\n" +
        "        \"Periods\": [\n" +
        "            {\n" +
        "                \"error_sql\": \"0\",\n" +
        "                \"error_isam\": \"0\",\n" +
        "                \"error_info\": \"\",\n" +
        "                \"periodo\": \"0\",\n" +
        "                \"nombre_periodo\": \"Semanal\",\n" +
        "                \"fecha_inicio\": \"14/03/2022\",\n" +
        "                \"fecha_fin\": \"17/03/2022\",\n" +
        "                \"per_id\": \"5446\",\n" +
        "                \"prov_nombre\": \"\",\n" +
        "                \"liquidacion\": \"0\",\n" +
        "                \"cert_estado\": \"\",\n" +
        "                \"cert_fecha\": \"\",\n" +
        "                \"fecha_contable\": \"\",\n" +
        "                \"entregas\": \"\",\n" +
        "                \"monto_entregas\": \"\",\n" +
        "                \"no_entregas\": \"\",\n" +
        "                \"monto_no_entregas\": \"\",\n" +
        "                \"monto\": \"0\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"error_sql\": \"0\",\n" +
        "                \"error_isam\": \"0\",\n" +
        "                \"error_info\": \"\",\n" +
        "                \"periodo\": \"1\",\n" +
        "                \"nombre_periodo\": \"Semanal\",\n" +
        "                \"fecha_inicio\": \"07/03/2022\",\n" +
        "                \"fecha_fin\": \"13/03/2022\",\n" +
        "                \"per_id\": \"5446\",\n" +
        "                \"prov_nombre\": \"\",\n" +
        "                \"liquidacion\": \"0\",\n" +
        "                \"cert_estado\": \"\",\n" +
        "                \"cert_fecha\": \"\",\n" +
        "                \"fecha_contable\": \"\",\n" +
        "                \"entregas\": \"0.00\",\n" +
        "                \"monto_entregas\": \"0.00\",\n" +
        "                \"no_entregas\": \"0.00\",\n" +
        "                \"monto_no_entregas\": \"0.00\",\n" +
        "                \"monto\": \"0.00\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"error_sql\": \"0\",\n" +
        "                \"error_isam\": \"0\",\n" +
        "                \"error_info\": \"\",\n" +
        "                \"periodo\": \"2\",\n" +
        "                \"nombre_periodo\": \"Semanal\",\n" +
        "                \"fecha_inicio\": \"28/02/2022\",\n" +
        "                \"fecha_fin\": \"06/03/2022\",\n" +
        "                \"per_id\": \"5446\",\n" +
        "                \"prov_nombre\": \"\",\n" +
        "                \"liquidacion\": \"0\",\n" +
        "                \"cert_estado\": \"\",\n" +
        "                \"cert_fecha\": \"\",\n" +
        "                \"fecha_contable\": \"\",\n" +
        "                \"entregas\": \"0.00\",\n" +
        "                \"monto_entregas\": \"0.00\",\n" +
        "                \"no_entregas\": \"0.00\",\n" +
        "                \"monto_no_entregas\": \"0.00\",\n" +
        "                \"monto\": \"0.00\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"error_sql\": \"0\",\n" +
        "                \"error_isam\": \"0\",\n" +
        "                \"error_info\": \"\",\n" +
        "                \"periodo\": \"3\",\n" +
        "                \"nombre_periodo\": \"Semanal\",\n" +
        "                \"fecha_inicio\": \"21/02/2022\",\n" +
        "                \"fecha_fin\": \"27/02/2022\",\n" +
        "                \"per_id\": \"5446\",\n" +
        "                \"prov_nombre\": \"\",\n" +
        "                \"liquidacion\": \"0\",\n" +
        "                \"cert_estado\": \"\",\n" +
        "                \"cert_fecha\": \"\",\n" +
        "                \"fecha_contable\": \"\",\n" +
        "                \"entregas\": \"0.00\",\n" +
        "                \"monto_entregas\": \"0.00\",\n" +
        "                \"no_entregas\": \"0.00\",\n" +
        "                \"monto_no_entregas\": \"0.00\",\n" +
        "                \"monto\": \"0.00\"\n" +
        "            },\n" +
        "            {\n" +
        "                \"error_sql\": \"0\",\n" +
        "                \"error_isam\": \"0\",\n" +
        "                \"error_info\": \"\",\n" +
        "                \"periodo\": \"4\",\n" +
        "                \"nombre_periodo\": \"Semanal\",\n" +
        "                \"fecha_inicio\": \"14/02/2022\",\n" +
        "                \"fecha_fin\": \"20/02/2022\",\n" +
        "                \"per_id\": \"5446\",\n" +
        "                \"prov_nombre\": \"\",\n" +
        "                \"liquidacion\": \"0\",\n" +
        "                \"cert_estado\": \"\",\n" +
        "                \"cert_fecha\": \"\",\n" +
        "                \"fecha_contable\": \"\",\n" +
        "                \"entregas\": \"0.00\",\n" +
        "                \"monto_entregas\": \"0.00\",\n" +
        "                \"no_entregas\": \"0.00\",\n" +
        "                \"monto_no_entregas\": \"0.00\",\n" +
        "                \"monto\": \"0.00\"\n" +
        "            }\n" +
        "        ]\n" +
        "    },\n" +
        "    \"code_error\": \"\"\n" +
        "}"