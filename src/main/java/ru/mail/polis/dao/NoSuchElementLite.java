package ru.mail.polis.dao;

import java.util.NoSuchElementException;

public class NoSuchElementLite extends NoSuchElementException {

    NoSuchElementLite(String s) { super(s); }

    @Override
    public synchronized Throwable fillInStackTrace() { return this; }
}