package ru.mail.polis.dao;

import org.jetbrains.annotations.NotNull;
import org.rocksdb.RocksIterator;
import ru.mail.polis.Record;

import java.nio.ByteBuffer;
import java.util.Iterator;

public class RockRecIter implements Iterator<Record>, AutoCloseable {

    private final RocksIterator iterator;

    RockRecIter(@NotNull final RocksIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public boolean hasNext() {
        return iterator.isValid();
    }

    @Override
    public Record next() throws IllegalStateException {
        if (!hasNext()) {
            throw new IllegalStateException("");
        }

        final var key = ByteBuff.convertAdd(iterator.key());
        final var record = Record.of(key, ByteBuffer.wrap(iterator.value()));
        iterator.next();
        return record;
    }

    @Override
    public void close() {
        iterator.close();
    }
}