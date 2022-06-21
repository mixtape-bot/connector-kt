package lol.dimensional.test.native.opus

import kotlinx.cinterop.*
import lib.jni.JNIEnvVar
import lib.jni.jclass
import lib.jni.jint
import lib.jni.jobject
import lib.opus.*
import lol.dimensional.test.native.common.getDirectBufferAddress
import lol.dimensional.test.native.common.isNull

@CName("Java_com_sedmelluq_discord_lavaplayer_natives_opus_OpusDecoderLibrary_create")
fun decoderCreate(envPtr: CPointer<JNIEnvVar>, jclass: jclass, sampleRate: Int, channelCount: Int): Long {
    println("(opus) create decoder, sampleRate: $sampleRate, channelCount: $channelCount")

    val (decoder, error_code) = memScoped {
        val error_code = cValue<IntVar>()
        val decoder = opus_decoder_create(sampleRate, channelCount, error_code)

        decoder to error_code.ptr.pointed.value
    }

    if (error_code == OPUS_OK || !decoder.rawValue.isNull()) {
        return decoder.toLong()
    }

    return error_code.toLong()
}

@CName("Java_com_sedmelluq_discord_lavaplayer_natives_opus_OpusDecoderLibrary_destroy")
fun decoderDestroy(envPtr: CPointer<JNIEnvVar>, jclass: jclass, decoderPtr: Long) {
    println("(opus) destroy decoder, decoderPtr: $decoderPtr")

    opus_decoder_destroy(decoderPtr.toCPointer())
}

@CName("Java_com_sedmelluq_discord_lavaplayer_natives_opus_OpusDecoderLibrary_decode")
fun decoderDecode(envPtr: CPointer<JNIEnvVar>, jclass: jclass, decoderPtr: Long, inputBuffer: jobject, inputSize: jint, outputBuffer: jobject, outputSize: jint): jint {
    if (decoderPtr == 0L) {
        return 0
    }

    val input = requireNotNull(getDirectBufferAddress(envPtr, inputBuffer)) {
        "unable to get input buffer address"
    }

    val output = requireNotNull(getDirectBufferAddress(envPtr, outputBuffer)) {
        "unable to get output buffer address"
    }

    return opus_decode(decoderPtr.toCPointer(), input.reinterpret(), inputSize, output.reinterpret(), outputSize, 0)
}
