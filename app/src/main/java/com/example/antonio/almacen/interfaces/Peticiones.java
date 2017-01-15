package com.example.antonio.almacen.interfaces;

import com.example.antonio.almacen.modelo.Estado;
import com.example.antonio.almacen.utils.Constants;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;

/**
 * Created by antonio on 14/01/17.
 */

public interface Peticiones {

    @GET(Constants.uriEstado)
    Call<Estado> consultaAlmacen();

}
