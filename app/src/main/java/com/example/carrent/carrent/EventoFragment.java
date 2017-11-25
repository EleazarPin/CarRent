package com.example.carrent.carrent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carrent.carrent.com.example.carrent.carrent.commons.BTActivity;
import com.example.carrent.carrent.com.example.carrent.carrent.commons.EventoItem;

import java.util.ArrayList;
import java.util.List;

public class EventoFragment extends Fragment {

    private ProgressDialog dialog;
    private BTActivity btActivity;
    private EventoRecyclerViewAdapter adapter;
    private List<EventoItem> datos;

    public EventoFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btActivity = (BTActivity)getActivity();
        btActivity.btService.setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 2:
                        datos.clear();
                        datos.addAll((ArrayList<EventoItem>)msg.obj);
                        adapter.notifyDataSetChanged();
                        dialog.dismiss();
                        break;
                    default:
                        Toast.makeText(getActivity(), msg.what + " " + (String)msg.obj, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        dialog.show();
        btActivity.btService.actions.getFileEventos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evento_list, container, false);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Cargando...");
        dialog.setCancelable(true);
        // Set the adapter
        if (view instanceof RecyclerView) {
            this.datos = new ArrayList<EventoItem>();//DummyContent.ITEMS_EVENTOS;
            this.adapter = new EventoRecyclerViewAdapter(this.datos);
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }
        return view;
    }
}
