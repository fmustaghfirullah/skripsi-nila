# Assets - Bangun Ruang

## CNN Model
File model ONNX sudah ditempatkan di folder ini: `model_6juli_6kelas.onnx`

Model memiliki:
- Input: gambar dengan edge detection preprocessing (Grayscale + Canny + Sobel), format NHWC: [1, 224, 224, 3]
- Output: 5 kelas (balok, bola, kerucut, kubus, tabung)

## 3D Model
Model 3D dibuat secara programatik menggunakan Interactive3DView.
Tidak memerlukan file .glb eksternal.

Fitur interaktif:
- Putar model dengan drag/geser
- Zoom dengan pinch (dua jari)
- Ketuk titik sudut untuk melihat info titik sudut
- Ketuk rusuk untuk melihat info rusuk
