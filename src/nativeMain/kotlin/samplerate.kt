package lol.dimensional.test.native

import kotlinx.cinterop.*
import lib.jni.*
import lib.samplerate.*
import lol.dimensional.test.native.common.getPrimitiveArrayCritical
import lol.dimensional.test.native.common.releasePrimitiveArrayCritical
import lol.dimensional.test.native.common.setIntArrayRegion

@CName("Java_com_sedmelluq_discord_lavaplayer_natives_samplerate_SampleRateLibrary_create")
fun samplerateCreate(envPtr: CPointer<JNIEnvVar>, jclass: jclass, type: jint, channels: jint): jlong {
    val error = cValue<IntVar>()
    return src_new(type, channels, error).toLong()
}

@CName("Java_com_sedmelluq_discord_lavaplayer_natives_samplerate_SampleRateLibrary_destroy")
fun samplerateDestroy(envPtr: CPointer<JNIEnvVar>, jclass: jclass, instancePtr: jlong) {
    src_delete(instancePtr.toCPointer())
}

@CName("Java_com_sedmelluq_discord_lavaplayer_natives_samplerate_SampleRateLibrary_reset")
fun samplerateReset(envPtr: CPointer<JNIEnvVar>, jclass: jclass, instancePtr: jlong) {
    src_reset(instancePtr.toCPointer())
}

@CName("Java_com_sedmelluq_discord_lavaplayer_natives_samplerate_SampleRateLibrary_process")
fun samplerateProcess(envPtr: CPointer<JNIEnvVar>, jclass: jclass, instancePtr: jlong, input: jfloatArray, inputLength: jint, output: jfloatArray, outputOffset: jint, outputLength: jint, eof: jboolean, sourceRatio: jdouble, progressArray: jintArray): jint {
    val inPtr = requireNotNull(getPrimitiveArrayCritical(envPtr, input)) {
        "input array is null"
    }

    val outPtr = requireNotNull(getPrimitiveArrayCritical(envPtr, output)) {
        "output array is null"
    }

    val data = memScoped {
        alloc<SRC_DATA>().apply {
            data_in = inPtr.reinterpret()
            input_frames = inputLength.toLong()
            input_frames_used = 0
            end_of_input = eof.toInt()
            data_out = outPtr.reinterpret()
            output_frames = outputLength.toLong()
            output_frames_gen = 0
            src_ratio = sourceRatio
        }
    }

    val result = src_process(instancePtr.toCPointer(), data.ptr)
    releasePrimitiveArrayCritical(envPtr, input, inPtr, JNI_ABORT)
    releasePrimitiveArrayCritical(envPtr, input, inPtr, 0)

    val progress = intArrayOf(data.input_frames_used.toInt(), data.output_frames_gen.toInt())
    setIntArrayRegion(envPtr, progressArray, 0, progress.size, progress)

    return result
}
