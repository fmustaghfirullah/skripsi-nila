package com.faisal.bangunruang;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.faisal.bangunruang.adapter.ShapeAdapter;
import com.faisal.bangunruang.databinding.ActivityMainBinding;
import com.faisal.bangunruang.model.Shape;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ShapeAdapter.OnShapeClickListener {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupClickListeners();
        setupShapeList();
        setupBottomNavigation();
    }

    private void setupClickListeners() {
        binding.cardScan.setOnClickListener(v -> {
            startActivity(new Intent(this, ScanActivity.class));
        });

        binding.cardLearn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LearnActivity.class);
            intent.putExtra("shape_id", "kubus");
            startActivity(intent);
        });

        binding.cardChat.setOnClickListener(v -> {
            startActivity(new Intent(this, ChatbotActivity.class));
        });
    }

    private void setupShapeList() {
        List<Shape> shapes = new ArrayList<>();
        shapes.add(new Shape("kubus", "Kubus", "6 sisi persegi kongruen",
                R.drawable.ic_shape_kubus, getColor(R.color.card_kubus)));
        shapes.add(new Shape("balok", "Balok", "6 sisi persegi panjang",
                R.drawable.ic_shape_balok, getColor(R.color.card_balok)));
        shapes.add(new Shape("kerucut", "Kerucut", "Alas lingkaran, satu titik puncak",
                R.drawable.ic_shape_kerucut, getColor(R.color.card_kerucut)));
        shapes.add(new Shape("tabung", "Tabung", "Dua lingkaran sejajar",
                R.drawable.ic_shape_tabung, getColor(R.color.card_tabung)));
        shapes.add(new Shape("bola", "Bola", "Bidang lengkung sempurna",
                R.drawable.ic_shape_bola, getColor(R.color.card_bola)));

        ShapeAdapter adapter = new ShapeAdapter(shapes, this);
        binding.rvShapes.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvShapes.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_scan) {
                startActivity(new Intent(this, ScanActivity.class));
                return true;
            } else if (id == R.id.nav_learn) {
                Intent intent = new Intent(this, LearnActivity.class);
                intent.putExtra("shape_id", "kubus");
                startActivity(intent);
                return true;
            } else if (id == R.id.nav_chat) {
                startActivity(new Intent(this, ChatbotActivity.class));
                return true;
            }
            return id == R.id.nav_home;
        });
    }

    @Override
    public void onShapeClick(Shape shape) {
        Intent intent = new Intent(this, LearnActivity.class);
        intent.putExtra("shape_id", shape.getId());
        startActivity(intent);
    }
}
