package com.tech.perfumos.ui.qrcode_scanner

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import android.graphics.*
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
class QrCodeAnalyzer(
    private val barcodeType: Int, // Add barcode type parameter
    private val barcodeListener: (barcode: String) -> Unit
) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(getOptions(barcodeType))

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val height = mediaImage.height
            val width = mediaImage.width

            // Define the crop area
            val c1x = (width * 0.125).toInt() + 150
            val c1y = (height * 0.25).toInt() - 25
            val c2x = (width * 0.875).toInt() - 150
            val c2y = (height * 0.75).toInt() + 25

            val rect = Rect(c1x, c1y, c2x, c2y)

            val ori: Bitmap = imageProxy.toBitmap()
            val crop = Bitmap.createBitmap(ori, rect.left, rect.top, rect.width(), rect.height())
//            val rImage = crop.rotate(90F)

            val image: InputImage =
                InputImage.fromBitmap(crop, imageProxy.imageInfo.rotationDegrees)

            // Pass the image to the scanner
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcodeListener(barcode.rawValue ?: "")
                        imageProxy.close()
                    }
                }
                .addOnFailureListener {
                    imageProxy.close()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    private fun getOptions(type: Int): BarcodeScannerOptions {
        return when (type) {
            1 -> {
                // Micro QR Code
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_PDF417,
                        Barcode.FORMAT_AZTEC,
                        Barcode.FORMAT_DATA_MATRIX
                    )
                    .build()
            }

            else -> {
                // Only  (Aztec)
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(
                        Barcode.FORMAT_QR_CODE,
                        Barcode.FORMAT_AZTEC,
                        Barcode.FORMAT_PDF417,
                        Barcode.FORMAT_DATA_MATRIX
                    ).build()
            }
        }
    }
}


/*
class QrCodeAnalyzer(private val barcodeListener: (barcode: String) -> Unit) : ImageAnalysis.Analyzer {

    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC,
            Barcode.FORMAT_PDF417 ,
            Barcode.FORMAT_DATA_MATRIX
        ).build()
    private val scanner = BarcodeScanning.getClient(options)


    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if(mediaImage != null) {

            val height = mediaImage.height
            val width = mediaImage.width

            //Since in the end the image will rotate clockwise 90 degree
            //left -> top, top -> right, right -> bottom, bottom -> left

            //Top    : (far) -value > 0 > +value (closer)
            val c1x = (width * 0.125).toInt() + 150
            //Right  : (far) -value > 0 > +value (closer)
            val c1y = (height * 0.25).toInt() - 25
            //Bottom : (closer) -value > 0 > +value (far)
            val c2x = (width * 0.875).toInt() - 150
            //Left   : (closer) -value > 0 > +value (far)
            val c2y = (height * 0.75).toInt() + 25

            val rect = Rect(c1x, c1y, c2x, c2y)

            val ori: Bitmap = imageProxy.toBitmap()!!
            val crop = Bitmap.createBitmap(ori, rect.left, rect.top, rect.width(), rect.height())
            val rImage = crop.rotate(90F)

            val image: InputImage = InputImage.fromBitmap(rImage, imageProxy.imageInfo.rotationDegrees)

            // Pass image to the scanner and have it do its thing
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    // Task completed successfully
                    for (barcode in barcodes) {
                        barcodeListener(barcode.rawValue ?: "")
                        imageProxy.close()
                    }
                }

                .addOnFailureListener {
                    // You should really do something about Exceptions
                    imageProxy.close()
                }

                .addOnCompleteListener {
                    // It's important to close the imageProxy
                    imageProxy.close()
                }
        }
    }
}*/
