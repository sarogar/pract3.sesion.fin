package es.upv.etsit.aatt.paco.prediccindeltiempo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    boolean primera_vez = true;
    String TAG = "." ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /****/
        // Creación de tarea asíncrona

        TareaAsincrona tarea1 = new TareaAsincrona();

        // Ejecución de hilo de tarea asíncrona

        tarea1.execute("https://opendata.aemet.es/opendata/api/prediccion/especifica/municipio/diaria/23039?api_key=eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzYXJhcm9tZ2FyY0Bob3RtYWlsLmNvbSIsImp0aSI6ImUyYjc2M2Y3LTI1MDYtNDA0Mi1hMjY0LTNiYjhmY2E4ZWMyZCIsImlzcyI6IkFFTUVUIiwiaWF0IjoxNTg3NTUyNTMzLCJ1c2VySWQiOiJlMmI3NjNmNy0yNTA2LTQwNDItYTI2NC0zYmI4ZmNhOGVjMmQiLCJyb2xlIjoiIn0.bGERMGyBiVBqo5cin403IO5j8KP0F2EeBXOGHfmx7L4");

    }



    class TareaAsincrona extends AsyncTask<String,String,String> {



        @Override
        protected String doInBackground(String[] uri) {
            // Llamada a petición API-REST con la URI o URL indicada en el método
            // .execute. Por último, retorno del string entregado por la llamada
            // a la API-REST
            String respuesta = API_REST(uri[0]);
            Log.d(TAG, "respuesta: " +respuesta);

            return respuesta;
        }

        @Override
        protected void onPostExecute(String respuesta) {

            if (respuesta!=null) {
                try {

                    if (primera_vez) {
                        primera_vez = false;

                        // Obtención de la propiedad "datos" del JSON
                        // Creación de una nuevo objeto de TareaAsincrona
                        // Ejecución del hilo correspondiente
                        Log.d(TAG, " OnPostExecute respuestata : " + respuesta);

                        JSONObject jsonobjeto = new JSONObject(respuesta);

                        String url2 = jsonobjeto.getString("datos");

                        Log.d(TAG,"Url2:" + url2);

                        TareaAsincrona tarea2 = new TareaAsincrona();

                        tarea2.execute(url2);

                    } else { // segunda vez: recogida de respuesta de la segunda llamada

                        // Obtencion de las propiedades oportunas del JSON recibido
                        // Aquí ya se puede acceder a la UI, ya que estamos en el hilo
                        // convencional de ejecución, y por tanto ya se puede modificar
                        // el contenido de los TextView que contienen los valores de los datos.

                        Log.d(TAG, " OnPostExecute, url2 es : " + respuesta );

                        JSONArray jsonobject2 = new JSONArray(respuesta);

                        String localidad = jsonobject2.getJSONObject( 0).getString("nombre");
                        String temp = jsonobject2.getJSONObject( 0).getJSONObject("prediccion").getJSONArray( "dia").getJSONObject(1).getJSONArray( "temperatura").getJSONObject( 12).getString( "value");
                        String lluvia= jsonobject2.getJSONObject(0).getJSONObject("prediccion").getJSONArray("dia").getJSONObject(1).getJSONArray("probPrecipitacion").getJSONObject(3).getString("value");
                        String vientodirec= jsonobject2.getJSONObject(0).getJSONObject("prediccion").getJSONArray("dia").getJSONObject(1).getJSONArray("vientoAndRachaMax").getJSONObject(12).getJSONArray("direccion").getString(0);
                        String vientovelmax= jsonobject2.getJSONObject(0).getJSONObject("prediccion").getJSONArray("dia").getJSONObject(1).getJSONArray("vientoAndRachaMax").getJSONObject(12).getJSONArray("velocidad").getString(0);
                        String estcielo=jsonobject2.getJSONObject(0).getJSONObject("prediccion").getJSONArray("dia").getJSONObject(1).getJSONArray("estadoCielo").getJSONObject(12).getString("descripcion");


                        temp1.setText(temp);
                        prepci.setText(lluvia);
                        localidad1.setText(localidad);
                        viento.setText(vientodirec);
                        viento2.setText(vientovelmax);
                        cielo1.setText(estcielo);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Problemas decodificando JSON");
                }
            }

        } // onPostExecute


    } // TareaAsincrona




    /** La peticion del argumento es recogida y devuelta por el método API_REST.
     Si hay algun problema se retorna null */
    public String API_REST(String uri){

        StringBuffer response = null;

        try {
            url = new URL(uri);
            Log.d(TAG, "URL: " + uri);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // Detalles de HTTP
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            Log.d(TAG, "Codigo de respuesta: " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String output;
                response = new StringBuffer();

                while ((output = in.readLine()) != null) {
                    response.append(output);
                }
                in.close();
            } else {
                Log.d(TAG, "responseCode: " + responseCode);
                return null; // retorna null anticipadamente si hay algun problema
            }
        } catch(Exception e) { // Posibles excepciones: MalformedURLException, IOException y ProtocolException
            e.printStackTrace();
            Log.d(TAG, "Error conexión HTTP:" + e.toString());
            return null; // retorna null anticipadamente si hay algun problema
        }

        return new String(response); // de StringBuffer -response- pasamos a String

    } // API_REST


}
