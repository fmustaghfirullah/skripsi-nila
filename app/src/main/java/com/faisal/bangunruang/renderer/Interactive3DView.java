package com.faisal.bangunruang.renderer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.animation.ValueAnimator;
import android.view.animation.OvershootInterpolator;

import java.util.ArrayList;
import java.util.List;

public class Interactive3DView extends View {

    private float rotationX = 25f;
    private float rotationY = -35f;
    private float scale = 1.0f;
    private float prevX, prevY;

    private Paint fillPaint;
    private Paint edgePaint;
    private Paint vertexPaint;
    private Paint labelPaint;
    private Paint labelBgPaint;
    private Paint highlightPaint;

    private List<float[]> vertices3D = new ArrayList<>();
    private List<int[]> edges = new ArrayList<>();
    private List<int[]> faces = new ArrayList<>();
    private List<int[]> faceColors = new ArrayList<>();
    private List<String> vertexLabels = new ArrayList<>();
    private List<String> edgeLabels = new ArrayList<>();
    private List<String> faceLabels = new ArrayList<>();

    private int selectedVertex = -1;
    private int selectedEdge = -1;
    private int selectedFace = -1;
    private String currentInfo = null;

    private float animProgress = 0f;
    private ValueAnimator animator;

    private ScaleGestureDetector scaleDetector;
    private GestureDetector tapDetector;
    private OnInfoClickListener infoClickListener;

    public interface OnInfoClickListener {
        void onInfoClick(String info);
    }

    public Interactive3DView(Context context) {
        super(context);
        init(context);
    }

    public Interactive3DView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Interactive3DView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setStyle(Paint.Style.FILL);

        edgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        edgePaint.setStyle(Paint.Style.STROKE);
        edgePaint.setStrokeWidth(3f);
        edgePaint.setColor(Color.parseColor("#1565C0"));

        vertexPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        vertexPaint.setStyle(Paint.Style.FILL);
        vertexPaint.setColor(Color.parseColor("#D32F2F"));

        labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelPaint.setTextSize(32f);
        labelPaint.setColor(Color.WHITE);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        labelBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        labelBgPaint.setStyle(Paint.Style.FILL);
        labelBgPaint.setColor(Color.parseColor("#CC333333"));

        highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint.setStyle(Paint.Style.FILL);
        highlightPaint.setColor(Color.parseColor("#FFFF00"));

        scaleDetector = new ScaleGestureDetector(context, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scale *= detector.getScaleFactor();
                scale = Math.max(0.5f, Math.min(scale, 3.0f));
                invalidate();
                return true;
            }
        });

        tapDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                handleTap(e.getX(), e.getY());
                return true;
            }
        });

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(600); // 0.6 detik
        animator.setInterpolator(new android.view.animation.AccelerateDecelerateInterpolator());
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.addUpdateListener(animation -> {
            animProgress = (float) animation.getAnimatedValue();
            invalidate();
        });
    }

    public void setOnInfoClickListener(OnInfoClickListener listener) {
        this.infoClickListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleDetector.onTouchEvent(event);
        tapDetector.onTouchEvent(event);

        if (event.getPointerCount() == 1) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    prevX = event.getX();
                    prevY = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (!scaleDetector.isInProgress()) {
                        float dx = event.getX() - prevX;
                        float dy = event.getY() - prevY;
                        rotationY += dx * 0.5f;
                        rotationX += dy * 0.5f;
                        rotationX = Math.max(-90f, Math.min(90f, rotationX));
                        invalidate();
                    }
                    prevX = event.getX();
                    prevY = event.getY();
                    break;
            }
        }
        return true;
    }

    private void handleTap(float tapX, float tapY) {
        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float baseScale = Math.min(getWidth(), getHeight()) * 0.25f * scale;

        selectedVertex = -1;
        selectedEdge = -1;
        selectedFace = -1;
        currentInfo = null;
        
        animator.cancel();
        animProgress = 0f;

        for (int i = 0; i < vertices3D.size(); i++) {
            PointF projected = project(vertices3D.get(i), centerX, centerY, baseScale);
            float dist = (float) Math.sqrt(Math.pow(tapX - projected.x, 2) + Math.pow(tapY - projected.y, 2));
            if (dist < 40f) {
                if (i < vertexLabels.size() && vertexLabels.get(i) != null) {
                    selectedVertex = i;
                    currentInfo = vertexLabels.get(i);
                    if (infoClickListener != null) {
                        infoClickListener.onInfoClick(currentInfo);
                    }
                    break;
                }
            }
        }

        if (selectedVertex == -1) {
            for (int i = 0; i < edges.size(); i++) {
                int[] edge = edges.get(i);
                PointF p1 = project(vertices3D.get(edge[0]), centerX, centerY, baseScale);
                PointF p2 = project(vertices3D.get(edge[1]), centerX, centerY, baseScale);
                float dist = pointToLineDistance(tapX, tapY, p1.x, p1.y, p2.x, p2.y);
                if (dist < 30f) {
                    if (i < edgeLabels.size() && edgeLabels.get(i) != null) {
                        selectedEdge = i;
                        currentInfo = edgeLabels.get(i);
                        if (infoClickListener != null) {
                            infoClickListener.onInfoClick(currentInfo);
                        }
                        break;
                    }
                }
            }
        }

        if (selectedVertex == -1 && selectedEdge == -1) {
            for (int i = 0; i < faces.size(); i++) {
                int[] face = faces.get(i);
                List<PointF> polygon = new ArrayList<>();
                for (int vIdx : face) {
                    polygon.add(project(vertices3D.get(vIdx), centerX, centerY, baseScale));
                }
                
                if (isPointInPolygon(tapX, tapY, polygon)) {
                    if (i < faceLabels.size() && faceLabels.get(i) != null) {
                        selectedFace = i;
                        currentInfo = faceLabels.get(i);
                        if (infoClickListener != null) {
                            infoClickListener.onInfoClick(currentInfo);
                        }
                        break;
                    }
                }
            }
        }

        if (selectedVertex != -1 || selectedEdge != -1 || selectedFace != -1) {
            animator.start();
        }

        invalidate();
    }

    private boolean isPointInPolygon(float x, float y, List<PointF> polygon) {
        boolean inside = false;
        for (int i = 0, j = polygon.size() - 1; i < polygon.size(); j = i++) {
            float xi = polygon.get(i).x, yi = polygon.get(i).y;
            float xj = polygon.get(j).x, yj = polygon.get(j).y;

            boolean intersect = ((yi > y) != (yj > y))
                    && (x < (xj - xi) * (y - yi) / (yj - yi) + xi);
            if (intersect) inside = !inside;
        }
        return inside;
    }

    private float pointToLineDistance(float px, float py, float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        float lengthSq = dx * dx + dy * dy;
        if (lengthSq == 0) return (float) Math.sqrt(Math.pow(px - x1, 2) + Math.pow(py - y1, 2));

        float t = Math.max(0, Math.min(1, ((px - x1) * dx + (py - y1) * dy) / lengthSq));
        float closestX = x1 + t * dx;
        float closestY = y1 + t * dy;
        return (float) Math.sqrt(Math.pow(px - closestX, 2) + Math.pow(py - closestY, 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#F5F5F5"));

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float baseScale = Math.min(getWidth(), getHeight()) * 0.25f * scale;

        drawFaces(canvas, centerX, centerY, baseScale);
        drawEdges(canvas, centerX, centerY, baseScale);
        drawVertices(canvas, centerX, centerY, baseScale);
    }

    private void drawFaces(Canvas canvas, float cx, float cy, float s) {
        if (selectedFace != -1) {
            String selLabel = faceLabels.get(selectedFace);
            if (selLabel != null) {
                int[] rgb = {21, 101, 192};
                if (selectedFace < faceColors.size()) {
                    rgb = faceColors.get(selectedFace);
                }
                draw2DShape(canvas, cx, cy, selLabel, rgb, s);
            }
            return;
        }

        if (selectedEdge != -1 || selectedVertex != -1) {
            return;
        }
        
        for (int i = 0; i < faces.size(); i++) {
            int[] face = faces.get(i);
            Path path = new Path();
            PointF first = project(vertices3D.get(face[0]), cx, cy, s);
            path.moveTo(first.x, first.y);
            for (int j = 1; j < face.length; j++) {
                PointF p = project(vertices3D.get(face[j]), cx, cy, s);
                path.lineTo(p.x, p.y);
            }
            path.close();

            if (i < faceColors.size()) {
                int[] rgb = faceColors.get(i);
                fillPaint.setColor(Color.argb(80, rgb[0], rgb[1], rgb[2]));
            } else {
                fillPaint.setColor(Color.argb(60, 21, 101, 192));
            }
            fillPaint.setStyle(Paint.Style.FILL);
            canvas.drawPath(path, fillPaint);
        }
    }

    private void draw2DShape(Canvas canvas, float cx, float cy, String label, int[] rgb, float baseScale) {
        int alpha = 150 + (int)(105 * animProgress); 
        fillPaint.setColor(Color.argb(alpha, rgb[0], rgb[1], rgb[2]));
        fillPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        fillPaint.setStrokeWidth(5f);

        float size = baseScale * 1.5f * (1f + 0.15f * animProgress);

        if (label.contains("Kubus")) {
            canvas.drawRect(cx - size, cy - size, cx + size, cy + size, fillPaint);
        } else if (label.contains("Selimut Tabung")) {
            float w = size * 1.8f;
            canvas.drawRect(cx - w, cy - size, cx + w, cy + size, fillPaint);
        } else if (label.contains("Balok") || label.contains("Depan") || label.contains("Belakang") || label.contains("Alas/Tutup") || label.contains("Samping")) {
            float w = size * 1.5f;
            canvas.drawRect(cx - w, cy - size, cx + w, cy + size, fillPaint);
        } else if (label.contains("Selimut Kerucut")) {
            float r = size * 1.5f;
            android.graphics.RectF oval = new android.graphics.RectF(cx - r, cy - r, cx + r, cy + r);
            canvas.drawArc(oval, 225, 90, true, fillPaint);
        } else {
            // Alas, Tutup, atau Permukaan Bola (semuanya adalah Lingkaran 2D)
            canvas.drawCircle(cx, cy, size, fillPaint);
        }
    }

    private void drawEdges(Canvas canvas, float cx, float cy, float s) {
        if (selectedFace != -1) return;
        
        boolean isAnythingSelected = (selectedEdge != -1 || selectedVertex != -1);
        
        for (int i = 0; i < edges.size(); i++) {
            if (isAnythingSelected && i != selectedEdge) continue;
            
            int[] edge = edges.get(i);
            PointF p1 = project(vertices3D.get(edge[0]), cx, cy, s);
            PointF p2 = project(vertices3D.get(edge[1]), cx, cy, s);

            if (i == selectedEdge) {
                Paint selectedPaint = new Paint(edgePaint);
                selectedPaint.setColor(Color.parseColor("#FF5722")); 
                selectedPaint.setStrokeWidth(8f + 6f * animProgress);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, selectedPaint);
            } else {
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, edgePaint);
            }
        }
    }

    private void drawVertices(Canvas canvas, float cx, float cy, float s) {
        if (selectedFace != -1) return;

        boolean isAnythingSelected = (selectedEdge != -1 || selectedVertex != -1);
        
        for (int i = 0; i < vertices3D.size(); i++) {
            if (isAnythingSelected && i != selectedVertex) continue;
            
            if (i >= vertexLabels.size() || vertexLabels.get(i) == null) continue;
            
            PointF p = project(vertices3D.get(i), cx, cy, s);
            if (i == selectedVertex) {
                highlightPaint.setAlpha((int)(100 + 155 * (1f - animProgress)));
                canvas.drawCircle(p.x, p.y, 20f + 15f * animProgress, highlightPaint);
                canvas.drawCircle(p.x, p.y, 12f + 4f * animProgress, vertexPaint);
            } else {
                canvas.drawCircle(p.x, p.y, 8f, vertexPaint);
            }
        }
    }

    private PointF project(float[] point3D, float cx, float cy, float s) {
        float x = point3D[0];
        float y = point3D[1];
        float z = point3D[2];

        float radY = (float) Math.toRadians(rotationY);
        float cosY = (float) Math.cos(radY);
        float sinY = (float) Math.sin(radY);
        float newX = x * cosY - z * sinY;
        float newZ = x * sinY + z * cosY;

        float radX = (float) Math.toRadians(rotationX);
        float cosX = (float) Math.cos(radX);
        float sinX = (float) Math.sin(radX);
        float newY = y * cosX - newZ * sinX;
        float finalZ = y * sinX + newZ * cosX;

        float perspective = 1f + finalZ * 0.1f;
        float projX = cx + newX * s / perspective;
        float projY = cy - newY * s / perspective;

        return new PointF(projX, projY);
    }

    public void setShape(String shapeId) {
        vertices3D.clear();
        edges.clear();
        faces.clear();
        faceColors.clear();
        vertexLabels.clear();
        edgeLabels.clear();
        faceLabels.clear();
        selectedVertex = -1;
        selectedEdge = -1;
        currentInfo = null;

        switch (shapeId) {
            case "kubus": buildKubus(); break;
            case "balok": buildBalok(); break;
            case "kerucut": buildKerucut(); break;
            case "tabung": buildTabung(); break;
            case "bola": buildBola(); break;
        }

        invalidate();
    }

    private void buildKubus() {
        float s = 1f;
        // 8 vertices kubus
        vertices3D.add(new float[]{-s, -s, -s}); // 0 - A (depan kiri bawah)
        vertices3D.add(new float[]{ s, -s, -s}); // 1 - B (depan kanan bawah)
        vertices3D.add(new float[]{ s,  s, -s}); // 2 - C (depan kanan atas)
        vertices3D.add(new float[]{-s,  s, -s}); // 3 - D (depan kiri atas)
        vertices3D.add(new float[]{-s, -s,  s}); // 4 - E (belakang kiri bawah)
        vertices3D.add(new float[]{ s, -s,  s}); // 5 - F (belakang kanan bawah)
        vertices3D.add(new float[]{ s,  s,  s}); // 6 - G (belakang kanan atas)
        vertices3D.add(new float[]{-s,  s,  s}); // 7 - H (belakang kiri atas)

        vertexLabels.add("Titik Sudut\nSudut: 90°\n3 rusuk bertemu di titik ini");
        for (int i = 1; i < 8; i++) vertexLabels.add(null);

        // 12 rusuk
        edges.add(new int[]{0, 1}); edges.add(new int[]{1, 2}); edges.add(new int[]{2, 3}); edges.add(new int[]{3, 0});
        edges.add(new int[]{4, 5}); edges.add(new int[]{5, 6}); edges.add(new int[]{6, 7}); edges.add(new int[]{7, 4});
        edges.add(new int[]{0, 4}); edges.add(new int[]{1, 5}); edges.add(new int[]{2, 6}); edges.add(new int[]{3, 7});

        edgeLabels.add("Rusuk\nPanjang = s");
        for (int i = 1; i < 12; i++) edgeLabels.add(null);

        // 6 sisi
        faces.add(new int[]{0, 1, 2, 3}); // depan
        faceLabels.add("Sisi Kubus\nL = s × s\nLuas Permukaan = 6 × s²\nVolume = s³");
        faces.add(new int[]{4, 5, 6, 7}); // belakang
        faceLabels.add(null);
        faces.add(new int[]{0, 1, 5, 4}); // bawah
        faceLabels.add(null);
        faces.add(new int[]{2, 3, 7, 6}); // atas
        faceLabels.add(null);
        faces.add(new int[]{0, 3, 7, 4}); // kiri
        faceLabels.add(null);
        faces.add(new int[]{1, 2, 6, 5}); // kanan
        faceLabels.add(null);

        faceColors.add(new int[]{33, 150, 243});  // biru
        faceColors.add(new int[]{76, 175, 80});   // hijau
        faceColors.add(new int[]{255, 152, 0});   // orange
        faceColors.add(new int[]{156, 39, 176});  // ungu
        faceColors.add(new int[]{244, 67, 54});   // merah
        faceColors.add(new int[]{0, 150, 136});   // teal
    }

    private void buildBalok() {
        float p = 1.5f, l = 0.8f, t = 1.0f;
        // 8 vertices balok
        vertices3D.add(new float[]{-p, -t, -l}); // 0 - A
        vertices3D.add(new float[]{ p, -t, -l}); // 1 - B
        vertices3D.add(new float[]{ p,  t, -l}); // 2 - C
        vertices3D.add(new float[]{-p,  t, -l}); // 3 - D
        vertices3D.add(new float[]{-p, -t,  l}); // 4 - E
        vertices3D.add(new float[]{ p, -t,  l}); // 5 - F
        vertices3D.add(new float[]{ p,  t,  l}); // 6 - G
        vertices3D.add(new float[]{-p,  t,  l}); // 7 - H

        vertexLabels.add("Titik Sudut\nPertemuan rusuk p, l, dan t");
        for (int i = 1; i < 8; i++) vertexLabels.add(null);

        // 12 rusuk
        edges.add(new int[]{0, 1}); edges.add(new int[]{1, 2}); edges.add(new int[]{2, 3}); edges.add(new int[]{3, 0});
        edges.add(new int[]{4, 5}); edges.add(new int[]{5, 6}); edges.add(new int[]{6, 7}); edges.add(new int[]{7, 4});
        edges.add(new int[]{0, 4}); edges.add(new int[]{1, 5}); edges.add(new int[]{2, 6}); edges.add(new int[]{3, 7});

        edgeLabels.add("Rusuk Panjang (p)");
        edgeLabels.add("Rusuk Tinggi (t)");
        edgeLabels.add(null); edgeLabels.add(null); edgeLabels.add(null); edgeLabels.add(null);
        edgeLabels.add(null); edgeLabels.add(null);
        edgeLabels.add("Rusuk Lebar (l)");
        edgeLabels.add(null); edgeLabels.add(null); edgeLabels.add(null);

        faces.add(new int[]{0, 1, 2, 3});
        faceLabels.add("Sisi Depan/Belakang\nL = p × t");
        faces.add(new int[]{4, 5, 6, 7});
        faceLabels.add(null);
        faces.add(new int[]{0, 1, 5, 4});
        faceLabels.add("Sisi Alas/Tutup\nL = p × l\nLuas Permukaan = 2(pl+pt+lt)");
        faces.add(new int[]{2, 3, 7, 6});
        faceLabels.add(null);
        faces.add(new int[]{0, 3, 7, 4});
        faceLabels.add("Sisi Samping\nL = l × t\nVolume = p × l × t");
        faces.add(new int[]{1, 2, 6, 5});
        faceLabels.add(null);

        faceColors.add(new int[]{33, 150, 243});
        faceColors.add(new int[]{76, 175, 80});
        faceColors.add(new int[]{255, 152, 0});
        faceColors.add(new int[]{156, 39, 176});
        faceColors.add(new int[]{244, 67, 54});
        faceColors.add(new int[]{0, 150, 136});
    }

    private void buildKerucut() {
        int segments = 24;
        float radius = 1.0f;
        float height = 1.8f;

        // Titik puncak
        vertices3D.add(new float[]{0, height, 0}); // 0 - Puncak
        vertexLabels.add("Titik Puncak (T)\nTitik tertinggi kerucut\nGaris pelukis bertemu di sini");

        // Titik-titik pada lingkaran alas
        for (int i = 0; i < segments; i++) {
            float angle = (float) (2 * Math.PI * i / segments);
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            vertices3D.add(new float[]{x, -height / 2, z});

            if (i == 0 || i == segments / 4 || i == segments / 2 || i == 3 * segments / 4) {
                vertexLabels.add("Titik Sudut Alas\nJari-jari = r");
            } else {
                vertexLabels.add(null);
            }
        }

        // Pusat alas
        vertices3D.add(new float[]{0, -height / 2, 0});
        vertexLabels.add("Pusat Alas (O)\nTitik pusat lingkaran alas\nTinggi kerucut diukur dari sini ke puncak");

        int centerIdx = segments + 1;

        // Rusuk selimut (dari puncak ke titik alas)
        for (int i = 0; i < segments; i++) {
            if (i % (segments / 4) == 0) {
                edges.add(new int[]{0, i + 1});
                if (i == 0 || i == segments / 2) {
                    edgeLabels.add("Garis Pelukis (s)\ns = √(r² + t²)\nGaris dari puncak ke tepi alas");
                } else {
                    edgeLabels.add(null);
                }
            }
        }

        // Rusuk keliling alas
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            edges.add(new int[]{i + 1, next + 1});
            if (i == 0) {
                edgeLabels.add("Keliling Alas\nK = 2πr\nRusuk lengkung kerucut");
            } else {
                edgeLabels.add(null);
            }
        }

        // Garis tinggi
        edges.add(new int[]{0, centerIdx});
        edgeLabels.add("Tinggi Kerucut (t)\nGaris tegak lurus dari puncak ke pusat alas");

        // Face selimut
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            faces.add(new int[]{0, i + 1, next + 1});
            faceColors.add(new int[]{255, 152, 0});
            faceLabels.add("Selimut Kerucut\nL = π × r × s\ns = Garis pelukis");
        }

        // Face alas
        int[] alasIndices = new int[segments];
        for (int i = 0; i < segments; i++) alasIndices[i] = i + 1;
        faces.add(alasIndices);
        faceColors.add(new int[]{255, 87, 34});
        faceLabels.add("Alas Kerucut\nL = π × r²\nLuas Total = πr(r+s)");
    }

    private void buildTabung() {
        int segments = 24;
        float radius = 0.9f;
        float height = 1.6f;

        // Titik-titik lingkaran atas
        for (int i = 0; i < segments; i++) {
            float angle = (float) (2 * Math.PI * i / segments);
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            vertices3D.add(new float[]{x, height / 2, z});
            if (i == 0 || i == segments / 4 || i == segments / 2 || i == 3 * segments / 4) {
                vertexLabels.add("Titik Sudut Tutup\nJari-jari = r");
            } else {
                vertexLabels.add(null);
            }
        }

        // Titik-titik lingkaran bawah
        for (int i = 0; i < segments; i++) {
            float angle = (float) (2 * Math.PI * i / segments);
            float x = radius * (float) Math.cos(angle);
            float z = radius * (float) Math.sin(angle);
            vertices3D.add(new float[]{x, -height / 2, z});
            if (i == 0 || i == segments / 4 || i == segments / 2 || i == 3 * segments / 4) {
                vertexLabels.add("Titik Sudut Alas\nJari-jari = r");
            } else {
                vertexLabels.add(null);
            }
        }

        // Pusat atas dan bawah
        vertices3D.add(new float[]{0, height / 2, 0});
        vertexLabels.add("Pusat Tutup\nTitik pusat lingkaran tutup");
        vertices3D.add(new float[]{0, -height / 2, 0});
        vertexLabels.add("Pusat Alas\nTitik pusat lingkaran alas");

        int topCenter = segments * 2;
        int botCenter = segments * 2 + 1;

        // Rusuk keliling atas
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            edges.add(new int[]{i, next});
            if (i == 0) {
                edgeLabels.add("Rusuk Lengkung Tutup\nK = 2πr\nKeliling lingkaran tutup");
            } else {
                edgeLabels.add(null);
            }
        }

        // Rusuk keliling bawah
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            edges.add(new int[]{segments + i, segments + next});
            if (i == 0) {
                edgeLabels.add("Rusuk Lengkung Alas\nK = 2πr\nKeliling lingkaran alas");
            } else {
                edgeLabels.add(null);
            }
        }

        // Garis-garis tegak (selimut)
        for (int i = 0; i < segments; i++) {
            if (i % (segments / 4) == 0) {
                edges.add(new int[]{i, segments + i});
                if (i == 0) {
                    edgeLabels.add("Tinggi Tabung\nt = Jarak alas ke tutup");
                } else {
                    edgeLabels.add(null);
                }
            }
        }

        // Garis tinggi pusat
        edges.add(new int[]{topCenter, botCenter});
        edgeLabels.add("Tinggi Tabung (t)\nJarak antara pusat tutup dan pusat alas");

        // Face selimut
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            faces.add(new int[]{i, next, segments + next, segments + i});
            faceColors.add(new int[]{156, 39, 176});
            faceLabels.add("Selimut Tabung\nL = 2πrt\nBerbentuk persegi panjang jika dibuka");
        }

        // Face tutup atas
        int[] topIndices = new int[segments];
        for (int i = 0; i < segments; i++) topIndices[i] = i;
        faces.add(topIndices);
        faceColors.add(new int[]{186, 104, 200});
        faceLabels.add("Tutup Tabung\nL = π × r²");

        // Face alas bawah
        int[] botIndices = new int[segments];
        for (int i = 0; i < segments; i++) botIndices[i] = segments + i;
        faces.add(botIndices);
        faceColors.add(new int[]{123, 31, 162});
        faceLabels.add("Alas Tabung\nL = π × r²\nLuas Total = 2πr(r+t)");
    }

    private void buildBola() {
        int latSegments = 16;
        int lonSegments = 24;
        float radius = 1.2f;

        // Generate vertices
        // Kutub atas
        vertices3D.add(new float[]{0, radius, 0});
        vertexLabels.add("Kutub Atas\nTitik tertinggi pada bola\nJarak ke pusat = r");

        // Titik-titik lintang
        for (int i = 1; i < latSegments; i++) {
            float lat = (float) (Math.PI * i / latSegments);
            float y = radius * (float) Math.cos(lat);
            float ringRadius = radius * (float) Math.sin(lat);

            for (int j = 0; j < lonSegments; j++) {
                float lon = (float) (2 * Math.PI * j / lonSegments);
                float x = ringRadius * (float) Math.cos(lon);
                float z = ringRadius * (float) Math.sin(lon);
                vertices3D.add(new float[]{x, y, z});

                if (i == latSegments / 2 && (j == 0 || j == lonSegments / 4 || j == lonSegments / 2 || j == 3 * lonSegments / 4)) {
                    vertexLabels.add("Titik Permukaan (Ekuator)\nJari-jari = r");
                } else {
                    vertexLabels.add(null);
                }
            }
        }

        // Kutub bawah
        vertices3D.add(new float[]{0, -radius, 0});
        vertexLabels.add("Kutub Bawah\nTitik terendah pada bola\nJarak ke pusat = r");

        // Pusat bola
        vertices3D.add(new float[]{0, 0, 0});
        vertexLabels.add("Pusat Bola (O)\nSemua titik permukaan berjarak r dari sini\nBola memiliki simetri putar tak hingga");

        int bottomIdx = 1 + (latSegments - 1) * lonSegments;
        int centerIdx = bottomIdx + 1;

        // Edges - garis lintang (horizontal)
        for (int i = 1; i < latSegments; i++) {
            for (int j = 0; j < lonSegments; j++) {
                int next = (j + 1) % lonSegments;
                int idx = 1 + (i - 1) * lonSegments + j;
                int nextIdx = 1 + (i - 1) * lonSegments + next;
                edges.add(new int[]{idx, nextIdx});

                if (i == latSegments / 2 && j == 0) {
                    edgeLabels.add("Lingkaran Besar (Ekuator)\nK = 2πr\nLingkaran terbesar pada bola");
                } else {
                    edgeLabels.add(null);
                }
            }
        }

        // Edges - garis bujur (vertikal)
        for (int j = 0; j < lonSegments; j += 8) {
            edges.add(new int[]{0, 1 + j});
            edgeLabels.add(null);

            for (int i = 1; i < latSegments - 1; i++) {
                int idx = 1 + (i - 1) * lonSegments + j;
                int nextIdx = 1 + i * lonSegments + j;
                edges.add(new int[]{idx, nextIdx});
                edgeLabels.add(null);
            }

            int lastRingIdx = 1 + (latSegments - 2) * lonSegments + j;
            edges.add(new int[]{lastRingIdx, bottomIdx});
            edgeLabels.add(null);
        }

        // Diameter
        edges.add(new int[]{0, bottomIdx});
        edgeLabels.add("Diameter (d)\nd = 2r\nGaris melalui pusat dari kutub ke kutub");

        // Jari-jari
        edges.add(new int[]{centerIdx, 0});
        edgeLabels.add("Jari-jari (r)\nJarak dari pusat ke permukaan\nSemua jari-jari sama panjang");

        // Faces (triangulated)
        // Top cap
        for (int j = 0; j < lonSegments; j++) {
            int next = (j + 1) % lonSegments;
            faces.add(new int[]{0, 1 + j, 1 + next});
            faceColors.add(new int[]{244, 67, 54});
            faceLabels.add("Permukaan Bola\nL = 4πr²\nVolume = 4/3 πr³");
        }

        // Middle bands
        for (int i = 0; i < latSegments - 2; i++) {
            for (int j = 0; j < lonSegments; j++) {
                int next = (j + 1) % lonSegments;
                int tl = 1 + i * lonSegments + j;
                int tr = 1 + i * lonSegments + next;
                int bl = 1 + (i + 1) * lonSegments + j;
                int br = 1 + (i + 1) * lonSegments + next;
                faces.add(new int[]{tl, tr, br, bl});
                faceColors.add(new int[]{244, 67, 54});
                faceLabels.add("Permukaan Bola\nL = 4πr²\nVolume = 4/3 πr³");
            }
        }

        // Bottom cap
        for (int j = 0; j < lonSegments; j++) {
            int next = (j + 1) % lonSegments;
            int lastRing = 1 + (latSegments - 2) * lonSegments;
            faces.add(new int[]{lastRing + j, lastRing + next, bottomIdx});
            faceColors.add(new int[]{183, 28, 28});
            faceLabels.add("Permukaan Bola\nL = 4πr²\nVolume = 4/3 πr³");
        }
    }
}

