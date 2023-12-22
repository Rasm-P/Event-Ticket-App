package com.example.eventticket.utils

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

// Adapted from Dev Genius by Hyzam Ali: https://blog.devgenius.io/qr-code-scanner-with-jetpack-compose-camerax-and-ml-kit-8e5a1d4a2fc9 (Accessed: 25-10-2023)

@androidx.camera.core.ExperimentalGetImage
class CodeAnalyser(val onCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {
    override fun analyze(imageProxy: ImageProxy) {
        val options = BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
        val barcodeScanner = BarcodeScanning.getClient(options)
        val image = imageProxy.image
        image?.let {
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            barcodeScanner.process(inputImage)
                .addOnSuccessListener { qrCodes ->
                    if (qrCodes.size > 0) {
                        val value = qrCodes[0].displayValue
                        if (value != null) {
                            onCodeScanned(value)
                        }
                    }
                }
        }
        imageProxy.close()
    }
}