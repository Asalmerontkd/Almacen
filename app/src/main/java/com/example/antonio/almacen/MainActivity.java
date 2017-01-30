package com.example.antonio.almacen;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.example.antonio.almacen.interfaces.Peticiones;
import com.example.antonio.almacen.modelo.Estado;
import com.example.antonio.almacen.utils.Constants;
import com.github.javiersantos.materialstyleddialogs.MaterialStyledDialog;
import com.github.javiersantos.materialstyleddialogs.enums.Style;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.entrada)
    ImageView imgEntrada;

    @BindView(R.id.carga)
    ImageView imgCarga;

    @BindView(R.id.medio)
    ImageView imgMedio;

    @BindView(R.id.descarga)
    ImageView imgDescarga;

    @BindView(R.id.salida)
    ImageView imgSalida;

    boolean hilo=true;
    int estado = 0;

    Retrofit retrofit;
    Peticiones peticiones;
    int cambios = 0;
    int current = 0;

    boolean chepo=true;

    Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.urlBase);
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener(){

                @Override
                public void call(final Object... args) {
                    log("Entro eveto CONNECT");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            datos("Conectado ");
                        }
                    });
                }
            }).on("my response", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    log("Entro eveto emitido por chepox: "+args[0]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int entrada = Integer.parseInt(args[0].toString());
                            mostrar(entrada);
                        }
                    });

                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    log("Entro eveto DISCONNECT "+args[0]);
                }
            }).on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    log("Entro eveto MESSAGE "+args[0]);
                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    log("Entro eveto CONNECT_ERROR "+args[0]);
                }
            }).on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    log("Entro eveto RECONNECTING "+args[0]);
                }
            }).on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    log("Entro eveto RECONNECT_ERROR "+args[0]);
                }
            }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    log("Entro eveto EVENT_ERROR "+args[0]);
                }
            });
        } catch (URISyntaxException e) {
            log(e.getMessage());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //mSocket.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void datos(final String data)
    {
        log(data);
        Toast.makeText(getApplicationContext(), "mensaje: "+data, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hilos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chepo=false;
        mSocket.disconnect();
        hilo=false;
    }

    public void mostrar(int img)
    {
        Log.e("img ", "El valor del sensor: "+img);
        switch (img)
        {
            case 1:
                dialogos("Entrando ...", "Transporte de carga TRX2017\n\nConductor: Jose Angel\nCapacidad máxima: 1500 KG",1);
                current=1;
                imgEntrada.setVisibility(View.VISIBLE);
                imgDescarga.setVisibility(View.INVISIBLE);
                imgCarga.setVisibility(View.INVISIBLE);
                imgSalida.setVisibility(View.INVISIBLE);
                imgMedio.setVisibility(View.INVISIBLE);
                break;
            case 2:
                current=2;
                imgEntrada.setVisibility(View.INVISIBLE);
                imgDescarga.setVisibility(View.VISIBLE);
                imgCarga.setVisibility(View.INVISIBLE);
                imgSalida.setVisibility(View.INVISIBLE);
                imgMedio.setVisibility(View.INVISIBLE);
                break;
            case 3:
                current=3;
                imgEntrada.setVisibility(View.INVISIBLE);
                imgDescarga.setVisibility(View.INVISIBLE);
                imgCarga.setVisibility(View.INVISIBLE);
                imgSalida.setVisibility(View.INVISIBLE);
                imgMedio.setVisibility(View.VISIBLE);
                break;
            case 4:
                current=4;
                imgEntrada.setVisibility(View.INVISIBLE);
                imgDescarga.setVisibility(View.INVISIBLE);
                imgCarga.setVisibility(View.VISIBLE);
                imgSalida.setVisibility(View.INVISIBLE);
                imgMedio.setVisibility(View.INVISIBLE);
                break;
            case 5:
                current=5;
                dialogos("Saliendo ...", "Transporte de carga TRX2017\n\nConductor: Jose Angel\nCapacidad máxima: 1500 KG",1);
                imgEntrada.setVisibility(View.INVISIBLE);
                imgDescarga.setVisibility(View.INVISIBLE);
                imgCarga.setVisibility(View.INVISIBLE);
                imgSalida.setVisibility(View.VISIBLE);
                imgMedio.setVisibility(View.INVISIBLE);
                break;
            case 0:
                current=0;
                imgMedio.setVisibility(View.INVISIBLE);
                imgEntrada.setVisibility(View.INVISIBLE);
                imgDescarga.setVisibility(View.INVISIBLE);
                imgCarga.setVisibility(View.INVISIBLE);
                imgSalida.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void hilos()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int past=0;
                while (hilo)
                {
                    if (current!=0)
                    {
                        if (current==cambios)
                        {
                            if (current==past) //muy retrasado
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialogos("Alerta!!!","El transporte de carga TRX2017 se ha detenido más de lo planeado",3);
                                    }
                                });
                            }
                            else //tiene un ligero retraso
                            {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialogos("Retraso...","El transporte de carga TRX2017 tiene un ligero retraso.",2);
                                    }
                                });
                            }
                            past=current;
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        cambios=current;
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }


    @OnClick(R.id.fab)
    public void action()
    {
        mSocket.connect();
        //test();
    }

    int a=1;

    public void test()
    {
        if (a<=5)
        {
            mostrar(a);
            a++;
        }
        else a=0;

    }

    public void dialogos(String titulo, String message, int type)
    {
        int colors = 0;
        switch (type)
        {
            case 1:
                colors=R.color.statusOK;
                break;
            case 2:
                colors=R.color.statusAlert;
                break;
            default:
                colors=R.color.statusBad;
        }
        MaterialStyledDialog dialog = new MaterialStyledDialog.Builder(this)
                .setTitle(titulo)
                .setDescription(message)
                .setStyle(Style.HEADER_WITH_TITLE)
                .setHeaderColor(colors)
                .setScrollable(true)
                .withDialogAnimation(true)
                .setPositiveText("OK")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Log.e("Dialog", "pressed");
                    }
                })
                .build();
        dialog.show();
    }



    public void log(String mesage){
        Log.e("Pete debug",mesage);
    }


}
