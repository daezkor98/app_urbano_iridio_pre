package com.urbanoexpress.iridio3.pre.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import com.urbanoexpress.iridio3.pre.R;
import com.urbanoexpress.iridio3.pre.ui.model.MotivoDescargaItem;

/**
 * Created by mick on 14/07/16.
 */
public class MotivoDescargaAdapter extends RecyclerView.Adapter<MotivoDescargaAdapter.ViewHolder> {

    private final String TAG = ParadaProgramadaAdapter.class.getSimpleName();
    private List<MotivoDescargaItem> data = Collections.emptyList();
    private LayoutInflater inflater;
    private Context context;

    public MotivoDescargaAdapter(Context context, List<MotivoDescargaItem> data) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.motivo_descarga_row, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MotivoDescargaItem item = data.get(position);
        holder.lblDescripcion.setText(item.getDescripcion());
        if (data.get(position).isSelected()) {
            holder.bgLinearLayout.setBackgroundColor(Color.parseColor("#CCCCCC"));
        } else {
            holder.bgLinearLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView lblDescripcion;
        LinearLayout bgLinearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            lblDescripcion = (TextView) itemView.findViewById(R.id.lblDescripcion);
            bgLinearLayout = (LinearLayout) itemView.findViewById(R.id.bgLinearLayout);
        }
    }
}
