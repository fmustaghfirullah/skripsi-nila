package com.faisal.bangunruang;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.faisal.bangunruang.databinding.ActivityResultBinding;
import com.faisal.bangunruang.utils.ShapeData;

public class ResultActivity extends AppCompatActivity {

    private ActivityResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        String shapeName = getIntent().getStringExtra("shape_name");
        float confidence = getIntent().getFloatExtra("confidence", 0f);

        displayResult(shapeName, confidence);
        setupButtons(shapeName);
    }

    private void displayResult(String shapeName, float confidence) {
        ShapeData.ShapeInfo info = ShapeData.getShapeInfo(shapeName);
        String displayName = (info != null) ? info.getName() : shapeName;

        binding.tvShapeName.setText("🎯 " + displayName);
        binding.tvConfidence.setText(String.format("Tingkat keyakinan: %.1f%%", confidence));
        binding.progressConfidence.setProgress((int) confidence);

        int shapeIcon = getShapeIconResource(shapeName);
        if (shapeIcon != 0) {
            binding.ivResult.setImageResource(shapeIcon);
        }

        if (confidence >= 80) {
            binding.progressConfidence.setIndicatorColor(getColor(R.color.confidence_high));
        } else if (confidence >= 50) {
            binding.progressConfidence.setIndicatorColor(getColor(R.color.confidence_medium));
        } else {
            binding.progressConfidence.setIndicatorColor(getColor(R.color.confidence_low));
        }
    }

    private void setupButtons(String shapeName) {
        binding.btnLearn.setOnClickListener(v -> {
            Intent intent = new Intent(this, LearnActivity.class);
            intent.putExtra("shape_id", shapeName);
            startActivity(intent);
        });

        binding.btnChat.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatbotActivity.class);
            intent.putExtra("shape_context", shapeName);
            startActivity(intent);
        });
    }

    private int getShapeIconResource(String shapeId) {
        switch (shapeId.toLowerCase()) {
            case "kubus": return R.drawable.ic_shape_kubus;
            case "balok": return R.drawable.ic_shape_balok;
            case "kerucut": return R.drawable.ic_shape_kerucut;
            case "tabung": return R.drawable.ic_shape_tabung;
            case "bola": return R.drawable.ic_shape_bola;
            default: return 0;
        }
    }
}
