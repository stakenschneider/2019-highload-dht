package ru.mail.polis.service.stakenschneider;

import java.util.NoSuchElementException;

public class NoSuchElementLite extends NoSuchElementException {

    public NoSuchElementLite(final String s) {
        super(s);
    }

    @Override
    public Throwable fillInStackTrace() {
            return this;
    }
}
