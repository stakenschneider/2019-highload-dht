package ru.mail.polis.dao;

import org.jetbrains.annotations.NotNull;
import org.rocksdb.BuiltinComparator;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import ru.mail.polis.Record;
import ru.mail.polis.service.stakenschneider.NoSuchElementLite;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

public final class RockDAO implements DAO {

    private final RocksDB db;

    private RockDAO(final RocksDB db) {
        this.db = db;
    }

    @NotNull
    @Override
    public Iterator<Record> iterator(@NotNull final ByteBuffer from) {
        final var iterator = db.newIterator();
        iterator.seek(ByteBuff.convertSub(from));
        return new RockRecIter(iterator);
    }

    @NotNull
    @Override
    public ByteBuffer get(@NotNull final ByteBuffer key) throws IOException {
        try {
            final var result = db.get(ByteBuff.convertSub(key));
            if (result == null) {
                throw new NoSuchElementLite("cant find element " + key.toString());
            }
            return ByteBuffer.wrap(result);
        } catch (RocksDBException exception) {
            throw new IOException(exception);
        }
    }

    @Override
    public void upsert(@NotNull final ByteBuffer key, @NotNull final ByteBuffer value) throws IOException {
        try {
            db.put(ByteBuff.convertSub(key), ByteBuff.array(value));
        } catch (RocksDBException exception) {
            throw new IOException(exception);
        }
    }

    @Override
    public void remove(@NotNull final ByteBuffer key) throws IOException {
        try {
            db.delete(ByteBuff.convertSub(key));
        } catch (RocksDBException exception) {
            throw new IOException(exception);
        }
    }

    @Override
    public void compact() throws IOException {
        try {
            db.compactRange();
        } catch (RocksDBException exception) {
            throw new IOException(exception);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            db.syncWal();
            db.closeE();
        } catch (RocksDBException exception) {
            throw new IOException(exception);
        }
    }

    static DAO create(final File data) throws IOException {
        RocksDB.loadLibrary();
        try {
            final var options = new Options()
                    .setCreateIfMissing(true)
                    .setComparator(BuiltinComparator.BYTEWISE_COMPARATOR);
            final var db = RocksDB.open(options, data.getAbsolutePath());
            return new RockDAO(db);
        } catch (RocksDBException exception) {
            throw new IOException(exception);
        }
    }
}
