package ru.mail.polis.service.stakenschneider;

import one.nio.http.*;
import one.nio.server.AcceptorConfig;
import org.jetbrains.annotations.NotNull;
import ru.mail.polis.dao.DAO;
import ru.mail.polis.dao.NoSuchElementLite;
import ru.mail.polis.service.Service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class MyService extends HttpServer implements Service {
    private final DAO dao;

    public MyService(final int port,
                     @NotNull final DAO dao) throws IOException {
        super(getConfig(port));
        this.dao = dao;
    }

    @Path("/v0/status")
    public Response status() {
        return Response.ok("OK");
    }

    /**
     * @param request - requests: GET, PUT, DELETE
     * @param id      - id element
     * @return - response
     */
    @Path("/v0/entity")
    public Response entity(
            @Param("id") final String id,
            @NotNull final Request request
    ) {
        try {
            if (id == null || id.isEmpty()) {
                return new Response(Response.BAD_REQUEST, "Id must be not null".getBytes(StandardCharsets.UTF_8));
            }
            final var key = ByteBuffer.wrap(id.getBytes(StandardCharsets.UTF_8));
            switch (request.getMethod()) {
                case Request.METHOD_GET: {
                    return get(key);
                }

                case Request.METHOD_PUT: {
                    return put(key, request);
                }
                case Request.METHOD_DELETE: {
                    return delete(key);
                }

                default:
                    return new Response(Response.METHOD_NOT_ALLOWED, Response.EMPTY);
            }
        } catch (Exception ex) {
            return new Response(Response.INTERNAL_ERROR, Response.EMPTY);
        }
    }

    @Override
    public void handleDefault(final Request request, final HttpSession session) throws IOException {
        final var response = new Response(Response.BAD_REQUEST, Response.EMPTY);
        session.sendResponse(response);
    }

    private static HttpServerConfig getConfig(final int port) {
        if (port <= 1024 || 65536 <= port) {
            throw new IllegalArgumentException("invalid port");
        }
        final AcceptorConfig acceptor = new AcceptorConfig();
        acceptor.port = port;
        final HttpServerConfig config = new HttpServerConfig();
        config.acceptors = new AcceptorConfig[]{acceptor};
        return config;
    }

    private Response put(final ByteBuffer key, final Request request) throws IOException {
        dao.upsert(key, ByteBuffer.wrap(request.getBody()));
        return new Response(Response.CREATED, Response.EMPTY);
    }

    private Response delete(final ByteBuffer key) throws IOException {
        dao.remove(key);
        return new Response(Response.ACCEPTED, Response.EMPTY);
    }

    private Response get(final ByteBuffer key){
        try {
            final ByteBuffer value = dao.get(key);
            final ByteBuffer duplicate = value.duplicate();
            final var body = new byte[duplicate.remaining()];
            duplicate.get(body);
            return new Response(Response.OK, body);
        } catch (NoSuchElementLite | IOException ex) {
            return new Response(Response.NOT_FOUND, Response.EMPTY);
        }
    }
}