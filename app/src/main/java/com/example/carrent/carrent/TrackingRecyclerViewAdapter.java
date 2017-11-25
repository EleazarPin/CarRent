package com.example.carrent.carrent;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.carrent.carrent.com.example.carrent.carrent.commons.TrackingItem;

import java.util.List;

public class TrackingRecyclerViewAdapter extends RecyclerView.Adapter<TrackingRecyclerViewAdapter.ViewHolder> {

    private final List<TrackingItem> mValues;

    public TrackingRecyclerViewAdapter(List<TrackingItem> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_tracking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.fecha.setText(mValues.get(position).fecha.toString());
        holder.posicion.setText(mValues.get(position).posicion);
        holder.velocidad.setText(mValues.get(position).velocidad);
        holder.altura.setText(mValues.get(position).altura);
        holder.grados.setText(mValues.get(position).grados);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView fecha;
        public final TextView posicion;
        public final TextView velocidad;
        public final TextView altura;
        public final TextView grados;
        public TrackingItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            fecha = (TextView) view.findViewById(R.id.tv_fecha);
            posicion = (TextView) view.findViewById(R.id.tv_posicion);
            velocidad = (TextView) view.findViewById(R.id.tv_velocidad);
            altura = (TextView) view.findViewById(R.id.tv_altura);
            grados = (TextView) view.findViewById(R.id.tv_grados);
        }
    }
}
