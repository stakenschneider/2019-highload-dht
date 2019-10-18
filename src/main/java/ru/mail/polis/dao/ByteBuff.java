package ru.mail.polis.dao;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.util.Arrays;

import static java.lang.Byte.MIN_VALUE;

final class ByteBuff {

    private ByteBuff(){}

    static byte[] array(@NotNull final ByteBuffer buffer) {
        final var copy = buffer.duplicate();
        final var arr = new byte[copy.remaining()];
        copy.get(arr);
        return arr;
    }

    static byte[] convertSub(@NotNull final ByteBuffer byteBuffer) {
            final var arr = array(byteBuffer);
            for (int i = 0; i < arr.length; i++) {
                arr[i] -= MIN_VALUE;
            }
            return arr;
    }

    static ByteBuffer convertAdd(@NotNull final byte[] array) {
        final var clone = Arrays.copyOf(array,array.length);
        for (int i = 0; i < array.length; i++) {
            clone[i] += MIN_VALUE;
        }
        return ByteBuffer.wrap(clone);
    }
}
