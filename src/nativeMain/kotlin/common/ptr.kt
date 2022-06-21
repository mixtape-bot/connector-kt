package lol.dimensional.test.native.common

import kotlinx.cinterop.*

fun NativePtr.isNull(): Boolean =
    this == nativeNullPtr

fun CPointer<CPointed>?.toLong(): Long =
    rawValue.toLong()
