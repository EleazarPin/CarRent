package com.example.carrent.carrent;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.carrent.carrent.com.example.carrent.carrent.commons.EventoItem;
import com.example.carrent.carrent.com.example.carrent.carrent.commons.TrackingItem;

import java.util.List;

public class EventoRecyclerViewAdapter extends RecyclerView.Adapter<EventoRecyclerViewAdapter.ViewHolder> {

    private final List<EventoItem> mValues;

    public EventoRecyclerViewAdapter(List<EventoItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_evento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        EventoItem item = mValues.get(position);
        holder.fecha.setText(item.fecha == null ? "Sin registro horario" : item.fecha.toString());
        holder.accion.setText(item.accion);
        holder.mensaje.setText(item.mensaje);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView fecha;
        public final TextView accion;
        public final TextView mensaje;
        public EventoItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            fecha = (TextView) view.findViewById(R.id.tv_fecha);
            accion = (TextView) view.findViewById(R.id.tv_accion);
            mensaje = (TextView) view.findViewById(R.id.tv_mensaje);
        }
    }
}
