package com.faisal.bangunruang;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.faisal.bangunruang.databinding.ActivityLearnBinding;
import com.faisal.bangunruang.renderer.Interactive3DView;
import com.faisal.bangunruang.utils.ShapeData;
import com.google.android.material.tabs.TabLayout;

public class LearnActivity extends AppCompatActivity {

    private ActivityLearnBinding binding;
    private String shapeId;
    private ShapeData.ShapeInfo shapeInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLearnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        shapeId = getIntent().getStringExtra("shape_id");
        if (shapeId == null) shapeId = "kubus";

        shapeInfo = ShapeData.getShapeInfo(shapeId);
        if (shapeInfo == null) {
            finish();
            return;
        }

        binding.tvShapeTitle.setText("📐 " + shapeInfo.getName());

        setup3DView();
        setupTabs();
        showProperties();
    }

    private void setup3DView() {
        binding.view3d.setShape(shapeId);
        binding.view3d.setOnInfoClickListener(info -> {
            binding.tvTapInfoText.setText("ℹ️ " + info);
        });
    }

    private void setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: showProperties(); break;
                    case 1: showFormulas(); break;
                    case 2: showExamples(); break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void showProperties() {
        StringBuilder sb = new StringBuilder();
        sb.append("📖 Definisi:\n");
        sb.append(shapeInfo.getName()).append(" adalah ").append(shapeInfo.getDefinition());
        sb.append("\n\n⭐ Sifat-sifat:\n");
        for (String prop : shapeInfo.getProperties()) {
            sb.append("• ").append(prop).append("\n");
        }
        sb.append("\n🔍 Unsur-unsur:\n");
        for (String elem : shapeInfo.getElements()) {
            sb.append("• ").append(elem).append("\n");
        }
        binding.tvInfoContent.setText(sb.toString());
    }

    private void showFormulas() {
        StringBuilder sb = new StringBuilder();
        sb.append("🧮 Rumus ").append(shapeInfo.getName()).append(":\n\n");
        sb.append("📦 Volume:\n").append(shapeInfo.getVolumeFormula()).append("\n\n");
        sb.append("📐 Luas Permukaan:\n").append(shapeInfo.getSurfaceAreaFormula()).append("\n\n");
        if (shapeInfo.getBaseAreaFormula() != null) {
            sb.append("⬜ Luas Alas:\n").append(shapeInfo.getBaseAreaFormula()).append("\n\n");
        }
        sb.append("📝 Keterangan:\n").append(shapeInfo.getFormulaDescription());
        binding.tvInfoContent.setText(sb.toString());
    }

    private void showExamples() {
        StringBuilder sb = new StringBuilder();
        sb.append("🏠 Contoh benda berbentuk ").append(shapeInfo.getName()).append(":\n\n");
        for (String example : shapeInfo.getExamples()) {
            sb.append("• ").append(example).append("\n");
        }
        sb.append("\n✂️ Jaring-jaring:\n");
        sb.append(shapeInfo.getNetDescription());
        binding.tvInfoContent.setText(sb.toString());
    }
}
