package com.faisal.bangunruang.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.faisal.bangunruang.R;
import com.faisal.bangunruang.model.Shape;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class ShapeAdapter extends RecyclerView.Adapter<ShapeAdapter.ViewHolder> {

    private List<Shape> shapes;
    private OnShapeClickListener listener;

    public interface OnShapeClickListener {
        void onShapeClick(Shape shape);
    }

    public ShapeAdapter(List<Shape> shapes, OnShapeClickListener listener) {
        this.shapes = shapes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shape, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Shape shape = shapes.get(position);
        holder.tvName.setText(shape.getName());
        holder.tvDesc.setText(shape.getDescription());
        holder.ivShape.setImageResource(shape.getIconResId());
        ((MaterialCardView) holder.itemView).setCardBackgroundColor(shape.getCardColor());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShapeClick(shape);
            }
        });
    }

    @Override
    public int getItemCount() {
        return shapes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivShape;
        TextView tvName;
        TextView tvDesc;

        ViewHolder(View itemView) {
            super(itemView);
            ivShape = itemView.findViewById(R.id.iv_shape);
            tvName = itemView.findViewById(R.id.tv_shape_name);
            tvDesc = itemView.findViewById(R.id.tv_shape_desc);
        }
    }
}
