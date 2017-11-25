package com.example.carrent.carrent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.annotation.Nullable;
import android.os.Message;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.carrent.carrent.com.example.carrent.carrent.commons.BTActivity;
import com.example.carrent.carrent.com.example.carrent.carrent.commons.TrackingItem;

public class TrackingFragment extends Fragment {

    private ProgressDialog dialog;
    private BTActivity btActivity;
    private TrackingRecyclerViewAdapter adapter;
    private List<TrackingItem> datos;

    public TrackingFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btActivity = (BTActivity)getActivity();
        btActivity.btService.setHandler(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch(msg.what) {
                    case 1:
                        datos.clear();
                        datos.addAll((ArrayList<TrackingItem>)msg.obj);
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
        btActivity.btService.actions.getFileTracking();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tracking_list, container, false);

        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Cargando...");
        dialog.setCancelable(true);
        // Set the adapter
        if (view instanceof RecyclerView) {
            this.datos = new ArrayList<TrackingItem>();//DummyContent.ITEMS_TRACKING;
            this.adapter = new TrackingRecyclerViewAdapter(this.datos);
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(adapter);
        }
        return view;
    }
}
