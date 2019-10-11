package ru.mail.polis.dao;

import org.jetbrains.annotations.NotNull;
import org.rocksdb.*;
import org.rocksdb.util.BytewiseComparator;
import ru.mail.polis.Record;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

public class RockDAO implements DAO {

    private final RocksDB db;

    private RockDAO(RocksDB db) {
        this.db = db;
    }

    @NotNull
    @Override
    public Iterator<Record> iterator(@NotNull ByteBuffer from) {
        final var iterator = db.newIterator();
        iterator.seek(from.array());
        return new RockRecIter(iterator);
    }

    @NotNull
    @Override
    public ByteBuffer get(@NotNull ByteBuffer key) throws IOException {
        try {
            final var result = db.get(key.array());
            if (result == null) {
                throw new NoSuchElementLite("cant find element " + key.toString());
            }
            return ByteBuffer.wrap(result);
        } catch (RocksDBException exception) {
            throw new IOException("", exception);
        }
    }

    @Override
    public void upsert(@NotNull ByteBuffer key, @NotNull ByteBuffer value) throws IOException {
        try {
            db.put(key.array(), value.array());
        } catch (RocksDBException exception) {
            throw new IOException("", exception);
        }
    }

    @Override
    public void remove(@NotNull ByteBuffer key) throws IOException {
        try {
            db.delete(key.array());
        } catch (RocksDBException exception) {
            throw new IOException("", exception);
        }
    }

    @Override
    public void compact() throws IOException {
        try {
            db.compactRange();
        } catch (RocksDBException exception) {
            throw new IOException("", exception);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            db.syncWal();
            db.closeE();
        } catch (RocksDBException exception) {
            throw new IOException("", exception);
        }
    }

    static DAO create(File data) throws IOException {
        RocksDB.loadLibrary();
        try {
            final var comparator = new BytewiseComparator(new ComparatorOptions());
            final var options = new Options()
                    .setCreateIfMissing(true)
                    .setComparator(comparator);
            final var db = RocksDB.open(options, data.getAbsolutePath());
            return new RockDAO(db);
        } catch (RocksDBException exception) {
            throw new IOException("", exception);
        }
    }
}