package lol.dimensional.test.native.opus

import kotlinx.cinterop.*
import lib.jni.*
import lib.opus.*
import lol.dimensional.test.native.common.getDirectBufferAddress
import lol.dimensional.test.native.common.isNull

@CName("Java_com_sedmelluq_discord_lavaplayer_natives_opus_OpusEncoderLibrary_create")
fun encoderCreate(envPtr: CPointer<JNIEnvVar>, jclass: jclass, sampleRate: jint, channels: jint, application: jint, quality: jint): jlong {
    println("(opus) encoder create, sampleRate: $sampleRate, channels: $channels, application: $application, quality: $quality")

    val error = cValue<IntVar>()

    /* create the encoder */
    val encoder = opus_encoder_create(sampleRate, channels, application, error)
    if (!encoder.rawValue.isNull()) {
        opus_encoder_ctl(encoder, OPUS_SET_COMPLEXITY_REQUEST, quality)
    }

    return encoder.toLong()
}

@CName("Java_com_sedmelluq_discord_lavaplayer_natives_opus_OpusEncoderLibrary_destroy")
fun encoderDestroy(envPtr: CPointer<JNIEnvVar>, jclass: jclass, encoderPtr: jlong) {
    println("(opus) encoder destroy, encoderPtr: $encoderPtr")

    opus_encoder_destroy(encoderPtr.toCPointer())
}

@CName("Java_com_sedmelluq_discord_lavaplayer_natives_opus_OpusEncoderLibrary_encode")
fun encoderEncode(envPtr: CPointer<JNIEnvVar>, _jclass: jclass, encoderPtr: jlong, inputBuffer: jobject, inputSize: jint, outputBuffer: jobject, outputLength: jint): Int {
    if (encoderPtr == 0L) {
        return 0
    }

    val input = getDirectBufferAddress(envPtr, inputBuffer)!!
    val output = getDirectBufferAddress(envPtr, outputBuffer)!!

    return opus_encode(encoderPtr.toCPointer(), input.reinterpret(), inputSize, output.reinterpret(), outputLength)
}
