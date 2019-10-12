package ru.mail.polis.dao;

import java.util.NoSuchElementException;

public class NoSuchElementLite extends NoSuchElementException {

    NoSuchElementLite(final String s) {
        super(s);
    }

    @Override
    public Throwable fillInStackTrace() {
            return this;
    }
}