package lol.dimensional.test.native.common

import kotlinx.cinterop.*
import lib.jni.*

fun unbox(ptr: CPointer<JNIEnvVar>): JNINativeInterface_ =
    ptr.pointed.pointed ?: error("jni env ptr is null")

fun getDirectBufferAddress(env: CPointer<JNIEnvVar>, buf: jobject): COpaquePointer? =
    unbox(env).GetDirectBufferAddress!!.invoke(env, /* buf = */buf)

fun getPrimitiveArrayCritical(env: CPointer<JNIEnvVar>, jarr: jarray): COpaquePointer? =
    unbox(env).GetPrimitiveArrayCritical!!.invoke(env, jarr, /* isCopy = */null)

fun releasePrimitiveArrayCritical(env: CPointer<JNIEnvVar>, jarr: jarray, carr: COpaquePointer, mode: jint): Unit =
    unbox(env).ReleasePrimitiveArrayCritical!!.invoke(env, /* jarray = */jarr, /* carray = */carr, /* mode = */mode)

fun setIntArrayRegion(env: CPointer<JNIEnvVar>, jarr: jintArray, start: jsize, len: jsize, buf: IntArray): Unit = memScoped {
    unbox(env).SetIntArrayRegion!!.invoke(env, /* jarray = */jarr, start, len, buf.toCValues().ptr)
}
