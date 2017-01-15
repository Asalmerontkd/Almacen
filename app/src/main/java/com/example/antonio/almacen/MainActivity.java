package com.example.antonio.almacen;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.antonio.almacen.interfaces.Peticiones;
import com.example.antonio.almacen.modelo.Estado;
import com.example.antonio.almacen.utils.Constants;
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

    @BindView(R.id.descarga)
    ImageView imgDescarga;

    @BindView(R.id.salida)
    ImageView imgSalida;



    int estado = 0;

    Retrofit retrofit;
    Peticiones peticiones;

    boolean chepo=true;

    Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.urlBase);
            mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener(){

                @Override
                public void call(Object... args) {
                    log("Entro eveto CONNECT");
                    mSocket.emit("my event", "Hola chepox");
                }
            }).on("my response", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    //JSONObject obj = (JSONObject)args[0];
                    log("Entro eveto emitido por chepox: "+args[0]);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            datos(""+args[0]);
                            int entrada = Integer.parseInt(args[0].toString());
                            mostrar(entrada);
                        }
                    });

                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //JSONObject obj = (JSONObject)args[0];
                    log("Entro eveto DISCONNECT "+args[0]);
                }
            }).on(Socket.EVENT_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //JSONObject obj = (JSONObject)args[0];
                    log("Entro eveto MESSAGE "+args[0]);
                }
            }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //JSONObject obj = (JSONObject)args[0];
                    log("Entro eveto CONNECT_ERROR "+args[0]);
                }
            }).on(Socket.EVENT_RECONNECTING, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //JSONObject obj = (JSONObject)args[0];
                    log("Entro eveto RECONNECTING "+args[0]);
                }
            }).on(Socket.EVENT_RECONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //JSONObject obj = (JSONObject)args[0];
                    log("Entro eveto RECONNECT_ERROR "+args[0]);
                }
            }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    //JSONObject obj = (JSONObject)args[0];
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
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //initRetrofit();
        mSocket.connect();
    }


    public void datos(final String data)
    {
        log(data);
        Toast.makeText(getApplicationContext(), "mensaje: "+data, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                while (chepo)
                {
                    actualiza();
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chepo=false;
        mSocket.disconnect();
    }

    public void initRetrofit()
    {
        retrofit = new Retrofit.Builder()
                .baseUrl(Constants.urlBase)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        peticiones = retrofit.create(Peticiones.class);
    }

    public void mostrar(int img)
    {
        Log.e("img ", "El valor de: "+img);
        switch (img)
        {
            case 1:
                imgEntrada.setVisibility(View.VISIBLE);
                break;
            case 2:
                imgEntrada.setVisibility(View.INVISIBLE);
                imgDescarga.setVisibility(View.VISIBLE);
                break;
            case 3:
                imgDescarga.setVisibility(View.INVISIBLE);
                imgCarga.setVisibility(View.VISIBLE);
                break;
            case 4:
                imgCarga.setVisibility(View.INVISIBLE);
                imgSalida.setVisibility(View.VISIBLE);
                break;
            case 0:
                imgSalida.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void actualiza()
    {
        //Todo hacer petición al servidor de chepo
        Call<Estado> call = peticiones.consultaAlmacen();
        call.enqueue(new Callback<Estado>() {
            @Override
            public void onResponse(Call<Estado> call, Response<Estado> response) {
                log(""+response.code());
                log(""+response.body());
                try {
                    estado = Integer.parseInt(response.body().getState());
                    mostrar(estado);
                }
                catch (Exception e)
                {
                    log(e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Estado> call, Throwable t) {
                log(" "+t.getMessage());
                Toast.makeText(getApplicationContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @OnClick(R.id.fab)
    public void action()
    {
        //actualiza();
    }



    public void log(String mesage){
        Log.e("Pete debug",mesage);
    }


}
