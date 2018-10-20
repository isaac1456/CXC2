package com.example.izxxc.cxc;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Button btnBuscar, btnGuardar, btnListar;
    EditText etfechaFin, etnombCliente, etResta, etCuota, etIdCliente, etAbono, etfecha, etIdPres;
    RelativeLayout layout;
    String codigoPrestamo;
    int abonoDia = 0;
    int Resta = 0;
    int VPrestamoT = 0;
    int VCuota = 0;
    int NuCuotas = 0;
    String Estado = "";
    TextView tvEstado;
    String tipo = "";
    String fechaAboDia = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnBuscar = findViewById(R.id.btnBuscar);
        btnGuardar = findViewById(R.id.btnGuardar);
        etIdCliente = findViewById(R.id.etIdCliente);
        etnombCliente = findViewById(R.id.etNombCliente);
        etfechaFin = findViewById(R.id.etFechaPre);
        etResta = findViewById(R.id.etResta);
        etCuota = findViewById(R.id.etCuota);
        etAbono = findViewById(R.id.etAbono);
        layout = findViewById(R.id.layout);
        tvEstado = findViewById(R.id.tvEstado);
        btnListar = findViewById(R.id.btnListar);
        etfecha = findViewById(R.id.etfecha);
        etIdPres = findViewById(R.id.etIdPres);


        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etIdCliente.getText().toString().equals("")) {
                    Mensaje("Digite un ID");
                } else {
                    ConsultarDatos();
                    fechaAbonoDia(abonoDia);

                }

            }
        });
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                System.out.println("" + etAbono.getText());

              Abono();
                //  System.out.println(fechaAboDia);
            }
        });
        btnListar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                DialogCustomDate newFragment = new DialogCustomDate();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
            }
        });

    }

    private void ConsultarNombre(String id) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String URL = "http://localhost:8080/conNombUsuario.php?id=" + id;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //     textview1.setText("Resultado: "+response);
                JSONArray ja = null;
                String nameFull = "";
                try {
                    ja = new JSONArray(response);
                    nameFull = ja.getString(1) + " " + ja.getString(2);
                    etnombCliente.setText(nameFull);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Mensaje("Error al Consultar Nombre");


            }
        });

        queue.add(stringRequest);
    }

    private void ConsultarDatos() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String URL = "http://localhost:8080/consulta.php?id=" + etIdCliente.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //     textview1.setText("Resultado: "+response);
                JSONArray ja = null;
                String nameFull = "";
                Log.d("Response", response);
                try {

                    ja = new JSONArray(response);
                    etfechaFin.setText(ja.getString(7));
                    etCuota.setText(ja.getString(9));
                    etResta.setText(ja.getString(3));
                    codigoPrestamo = ja.getString(0);
                    ConsultarNombre(ja.getString(11));
                    BuscarAbonoDia(ja.getString(0));
                    Resta = ja.getInt(3);
                    VPrestamoT = ja.getInt(1) + ja.getInt(2);
                    Estado = ja.getString(6);
                    tipo = ja.getString(4);
                    tvEstado.setText(Estado);
                    NuCuotas = ja.getInt(5);
                    ColorEstado(Estado);
                    Log.d("Prestamo", codigoPrestamo + " -" + Resta + "- " + VPrestamoT + "-" + Estado + "- " + VCuota);

                    etfecha.setText(ja.getString(0));
                } catch (JSONException e) {
                    Mensaje("Cliente No tiene Prestamo");
                    Limpiar();
                    e.printStackTrace();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Mensaje("Error al Consultar Datos");


            }
        });

        queue.add(stringRequest);
    }

    public void Mensaje(String mensaje) {

        Toast.makeText(MainActivity.this, mensaje, Toast.LENGTH_SHORT).show();

    }

    public void Abono() {
        VCuota = Integer.parseInt(etCuota.getText().toString());
        BuscarAbonoDia(codigoPrestamo);
        //  DateFormat fecha = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaAbono = new Date();
        int abono = Integer.parseInt(etAbono.getText().toString());
        //int abonDia = abonoDia - 1;
        System.out.println(abono);
        String fechaCuo = "";
        Date fechaCuota;
        if (Resta != 0) {

            if (abono > VCuota) {
                System.out.println("entro1");
                float Ncuota = (NuCuotas * abono) / VPrestamoT;
                // abonodia = abonoDia + 1;
                for (int i = 0; i < Ncuota; i++) {
                    // fechaCuo = (String)
                    // model.getValueAt(abonodia,
                    // 4);

                    fechaAbonoDia(abonoDia);
                    fechaCuota = ParseFecha(fechaAboDia);
                    System.out.println("" + fechaCuota);
                    Resta = (Resta - VCuota);


                    etResta.setText("" + Resta);
                    guardarAbono(VCuota, Fecha(fechaAbono), Resta, estado(restarDiasAFecha(fechaCuota, tipo)),
                            restarDiasAFecha(fechaCuota, tipo));
                    abonoDia = abonoDia + 1;
                    if (Resta == 0) {
                        etResta.setText("" + Resta);
                        abonoDia--;
                        guardarAbono(0, Fecha(fechaAbono), 0, "Pagado", 0);
                    }

                }
                abonoDia = abonoDia - 1;
            } else if (abono < VCuota) {
                // fechaCuo = (String) model.getValueAt(abonodia,
                // 4);
                System.out.println("entro2");
                fechaAbonoDia(abonoDia);
                fechaCuota = ParseFecha(fechaAboDia);
                System.out.println("" + fechaCuota);
                System.out.println("" + fechaCuota);
                Resta = (Resta - abono);
                etResta.setText("" + Resta);
                guardarAbono(VCuota, Fecha(fechaAbono), Resta, estado(restarDiasAFecha(fechaCuota, tipo)),
                        restarDiasAFecha(fechaCuota, tipo));

            } else {
                // fechaCuo = (String) model.getValueAt(abonodia,
                // 4);
                System.out.println("entro3");
                fechaAbonoDia(abonoDia);
                fechaCuota = ParseFecha(fechaAboDia);
                System.out.println("fecha Cuota" + fechaCuota);

                Resta = (Resta - VCuota);


                etResta.setText("" + Resta);
                guardarAbono(VCuota, Fecha(fechaAbono), Resta, estado(restarDiasAFecha(fechaCuota, tipo)),
                        restarDiasAFecha(fechaCuota, tipo));

            }
            abonoDia = abonoDia + 1;
            //

            // llenarTabla();
        } else {
            btnGuardar.setEnabled(false);
            Mensaje("prestamo pagado ");
            abonoDia--;
            guardarAbono(0, Fecha(fechaAbono), 0, "Pagado", 0);
        }

        ConsultarDatos();

    }

    public static Date ParseFecha(String fecha) {
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaDate = null;
        try {
            fechaDate = formato.parse(fecha);
        } catch (ParseException ex) {
            System.out.println(ex);
        }
        return fechaDate;
    }

    static String Fecha(Date date) {
        String fecha = "";


        DateFormat hourdateFormat = new SimpleDateFormat("yyyy-MM-dd");
        fecha = "" + hourdateFormat.format(date);

        return fecha;

    }

    String estado(Long diasMoras) {
        String estado = "";
        if (diasMoras > 0) {
            return estado = "Moroso";
        } else if (Resta == 0) {
            return estado = "Pagado";

        } else if (diasMoras == 0) {
            return estado = "AlDia";
        }

        return estado;
    }

    public static long restarDiasAFecha(Date fechaCuota, String tipo) {

        System.out.println("" + fechaCuota + "-" + tipo);
        Date fecha = new Date();
        long diferencia = 0;
        switch (tipo) {
            case "Diario":
                System.out.println("entro Diario");
                long startTime = fechaCuota.getTime();
                long endTime = fecha.getTime();
                long diffTime = endTime - startTime;
                diferencia = (int) TimeUnit.DAYS.convert(diffTime, TimeUnit.MILLISECONDS);
                if (diferencia <= 0) {
                    return 0;
                } else {
                    System.out.println(diferencia);
                    return diferencia;
                }
            case "Semanal":
                System.out.println("entro Semanal");
                diferencia = (long) ((fecha.getTime() - fechaCuota.getTime()) / 604800000);
                if (diferencia <= 0) {
                    return 0;
                } else {
                    System.out.println(diferencia);
                    return diferencia;
                }
            case "Quincenal":
                System.out.println("entro quincenal");
                diferencia = (long) ((fecha.getTime() - fechaCuota.getTime()) / 1296000000);
                if (diferencia <= 0) {
                    return 0;
                } else {
                    System.out.println(diferencia);
                    return diferencia;
                }
            case "Mensual":
                System.out.println("entro Mensual");
                // Fecha inicio en objeto Calendar
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.setTime(fecha);
                // Fecha finalización en objeto Calendar
                Calendar endCalendar = Calendar.getInstance();
                endCalendar.setTime(fechaCuota);
                // Cálculo de meses para las fechas de inicio y finalización
                int startMes = (startCalendar.get(Calendar.YEAR) * 12) + startCalendar.get(Calendar.MONTH);
                int endMes = (endCalendar.get(Calendar.YEAR) * 12) + endCalendar.get(Calendar.MONTH);
                // Diferencia en meses entre las dos fechas
                diferencia = startMes - endMes;

                if (diferencia <= 0) {
                    return 0;
                } else {
                    System.out.println(diferencia);
                    return diferencia;
                }
        }
        System.out.println(diferencia);
        return diferencia;

    }

    public void BuscarAbonoDia(String codigo) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String URL = "http://localhost:8080/consulta1.php?id=" + etIdCliente.getText() + "&idPres=" + codigo;
        System.out.println("codigo" + codigo);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //     textview1.setText("Resultado: "+response);
                JSONArray ja = null;

                try {
                    ja = new JSONArray(response);

                    abonoDia = Integer.parseInt(ja.getString(0));
                    System.out.println("Dia: " + ja.getString(0));
                    System.out.println("Dia: " + response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Mensaje("Error al Consultar abono dia");


            }
        });

        queue.add(stringRequest);


    }

    public void fechaAbonoDia(int NumAbonoDIa) {
        System.out.println("num: "+ NumAbonoDIa);
        System.out.println("codPres: "+codigoPrestamo);
        RequestQueue queue = Volley.newRequestQueue(this);
        String URL = "http://localhost:8080/fechaAbo.php?Nu=" + NumAbonoDIa + "&idPres=" + codigoPrestamo;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                //     textview1.setText("Resultado: "+response);
                JSONArray ja = null;

                try {
                    System.out.println(response);
                    ja = new JSONArray(response);

                    String aux = ja.getString(0);
                    fechaAboDia = aux;
                    System.out.println(fechaAboDia);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Mensaje("Error al Consultar fecha abono");


            }
        });

        queue.add(stringRequest);

    }

    private void guardarAbono(int abono, String fechaAbo, int resta, String estado, long CuotasMora) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String URL = "http://localhost:8080/GuardarAbono.php?idPres=" + codigoPrestamo + "&Nu=" + abonoDia + "&abono=" + abono + "&fechaAbo=" + fechaAbo + "&resta=" + resta + "&estado=" + estado + "&cuotasMora=" + CuotasMora;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.equals("Ok")) {
                    Mensaje("Guardado Exitoso");
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Mensaje("Error al Guardar");


            }
        });

        queue.add(stringRequest);


    }

    private void Limpiar() {
        etnombCliente.setText("");
        etAbono.setText("");
        etResta.setText("");
        etfechaFin.setText("");
        etCuota.setText("");

    }

    private void ColorEstado(String Estado) {
        switch (Estado) {
            case "Moroso":
                tvEstado.setBackgroundColor(Color.RED);
                break;
            case "AlDia":
                tvEstado.setBackgroundColor(Color.GREEN);
                break;
            case "NoEsta":

                tvEstado.setBackgroundColor(Color.rgb(255, 255, 255));
                break;
        }


    }
}
