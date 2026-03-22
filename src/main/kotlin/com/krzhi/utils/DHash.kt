package com.krzhi.utils

import com.sksamuel.scrimage.ImmutableImage
import java.awt.Image
import java.awt.image.BufferedImage

object DHash {

    fun calculate(imageBytes: ByteArray): String {
        val originalImage: BufferedImage = ImmutableImage.loader().fromBytes(imageBytes).awt()
            ?: throw IllegalArgumentException("Invalid image stream")
        return calculate(originalImage)
    }

    fun calculate(image: BufferedImage): String {
        // 1. Resize to 9x8
        val resizedImage = BufferedImage(9, 8, BufferedImage.TYPE_BYTE_GRAY)
        val graphics = resizedImage.createGraphics()
        graphics.drawImage(image.getScaledInstance(9, 8, Image.SCALE_SMOOTH), 0, 0, null)
        graphics.dispose()

        // 2. Compute hash
        val hashBuilder = StringBuilder()
        for (y in 0 until 8) {
            for (x in 0 until 8) {
                val leftPixel = resizedImage.raster.getSample(x, y, 0)
                val rightPixel = resizedImage.raster.getSample(x + 1, y, 0)
                if (leftPixel > rightPixel) {
                    hashBuilder.append("1")
                } else {
                    hashBuilder.append("0")
                }
            }
        }
        
        // Convert 64-bit binary string to 16-character hex string
        return binaryToHex(hashBuilder.toString())
    }

    private fun binaryToHex(binary: String): String {
        val hexBuilder = java.lang.StringBuilder()
        for (i in 0 until 64 step 4) {
            val chunk = binary.substring(i, i + 4)
            val hexChar = Integer.parseInt(chunk, 2).toString(16)
            hexBuilder.append(hexChar)
        }
        return hexBuilder.toString()
    }
}
