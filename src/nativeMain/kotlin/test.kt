package lol.dimensional.test.native

import kotlinx.cinterop.*
import lib.jni.JNIEnvVar
import lib.jni.jclass
import lib.jni.jstring

@CName("Java_lol_dimensional_test_native_TestLibrary_test")
fun test(env: CPointer<JNIEnvVar>, _jclass: jclass): jstring? {
    val string = "haha, cool am I right or am I wrong?"
    memScoped {
        return env.pointed.pointed!!.NewString!!.invoke(
            p1 = env,
            p3 = string.length,
            p2 = string.wcstr.ptr
        )
    }
}
