package ru.mail.polis.service.stakenschneider;

import java.util.NoSuchElementException;

@SuppressWarnings("serial")
public class NoSuchElementLite extends NoSuchElementException {

    public NoSuchElementLite(final String s) {
        super(s);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
            return this;
    }
}
