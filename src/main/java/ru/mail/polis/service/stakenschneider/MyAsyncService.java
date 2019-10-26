package ru.mail.polis.service.stakenschneider;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import one.nio.http.*;
import one.nio.net.ConnectionString;
import one.nio.pool.PoolException;
import ru.mail.polis.Record;
import ru.mail.polis.dao.DAO;
import ru.mail.polis.service.Service;

import one.nio.net.Socket;
import one.nio.server.AcceptorConfig;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.NotNull;

import static java.util.logging.Level.INFO;


public class MyAsyncService extends HttpServer implements Service {
    @NotNull
    private final DAO dao;
    @NotNull
    private final Executor executor;
    private final Nodes nodes;
    private final Map<String, HttpClient> clusterClients;

    private static final Logger logger = Logger.getLogger(MyAsyncService.class.getName());

    /**
     * Simple Async HTTP server.
     */
    private MyAsyncService(final HttpServerConfig config, @NotNull final DAO dao,
                           @NotNull final Nodes nodes,
                           @NotNull final Map<String, HttpClient> clusterClients) throws IOException {
        super(config);
        this.dao = dao;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
                new ThreadFactoryBuilder().setNameFormat("worker").build());
        this.nodes = nodes;
        this.clusterClients = clusterClients;
    }

     public static Service create(final int port, @NotNull final DAO dao,
                                 @NotNull final Nodes nodes) throws IOException {
        final var acceptor = new AcceptorConfig();
        final var config = new HttpServerConfig();
        acceptor.port = port;
        config.acceptors = new AcceptorConfig[]{acceptor};
        config.maxWorkers = Runtime.getRuntime().availableProcessors();
        config.queueTime = 10;
        Map<String, HttpClient> clusterClients = new HashMap<>();
        for (final String it : nodes.getNodes()) {
            if (!nodes.getId().equals(it) && !clusterClients.containsKey(it)) {
                clusterClients.put(it, new HttpClient(new ConnectionString(it + "?timeout=100")));
            }
        }
        return new MyAsyncService(config, dao, nodes, clusterClients);
    }

    @Override
    public HttpSession createSession(final Socket socket) {
        return new StorageSession(socket, this);
    }

    /**
     * Life check.
     *
     * @return - response
     */
    @Path("/v0/status")
    public Response status() {
        return Response.ok("OK");
    }

    /**
     * Access to DAO.
     *
     * @param request - requests: GET, PUT, DELETE
     * @param session - HttpSession
     */
    private void entity(@NotNull final Request request, final HttpSession session) throws IOException {
        final String id = request.getParameter("id=");
        if (id == null || id.isEmpty()) {
            try {
                session.sendResponse(new Response(Response.BAD_REQUEST, Response.EMPTY));
            } catch (IOException e) {
                logger.log(INFO, "something has gone terribly wrong", e);
            }
            return;
        }

        final var key = ByteBuffer.wrap(id.getBytes(StandardCharsets.UTF_8));

        final String keyClusterPartition = nodes.primaryFor(key);
        if (!nodes.getId().equals(keyClusterPartition)) {
            executeAsync(session, () -> forwardRequestTo(keyClusterPartition, request));
            return;
        }

        try {
            switch (request.getMethod()) {
                case Request.METHOD_GET:
                    executeAsync(session, () -> get(key));
                    break;
                case Request.METHOD_PUT:
                    executeAsync(session, () -> put(key, request));
                    break;
                case Request.METHOD_DELETE:
                    executeAsync(session, () -> delete(key));
                    break;
                default:
                    session.sendError(Response.METHOD_NOT_ALLOWED, "Wrong method");
                    break;
            }
        } catch (IOException e) {
            session.sendError(Response.INTERNAL_ERROR, e.getMessage());
        }
    }

    @Override
    public void handleDefault(@NotNull final Request request, @NotNull final HttpSession session) throws IOException {
        switch (request.getPath()) {
            case "/v0/entity":
                entity(request, session);
                break;
            case "/v0/entities":
                entities(request, session);
                break;
            default:
                session.sendError(Response.BAD_REQUEST, "Wrong path");
                break;
        }
    }

    private void executeAsync(@NotNull final HttpSession session, @NotNull final Action action) {
        executor.execute(() -> {
            try {
                session.sendResponse(action.act());
            } catch (IOException e) {
                try {
                    session.sendError(Response.INTERNAL_ERROR, e.getMessage());
                } catch (IOException ex) {
                    logger.log(INFO, "something has gone terribly wrong", e);
                }
            }
        });
    }

    @FunctionalInterface
    interface Action {
        Response act() throws IOException;
    }

    private void entities(@NotNull final Request request, @NotNull final HttpSession session) throws IOException {
        final String start = request.getParameter("start=");
        if (start == null || start.isEmpty()) {
            session.sendError(Response.BAD_REQUEST, "No start");
            return;
        }

        if (request.getMethod() != Request.METHOD_GET) {
            session.sendError(Response.METHOD_NOT_ALLOWED, "Wrong method");
            return;
        }

        String end = request.getParameter("end=");
        if (end != null && end.isEmpty()) {
            end = null;
        }

        try {
            final Iterator<Record> records =
                    dao.range(ByteBuffer.wrap(start.getBytes(StandardCharsets.UTF_8)),
                            end == null ? null : ByteBuffer.wrap(end.getBytes(StandardCharsets.UTF_8)));
            ((StorageSession) session).stream(records);
        } catch (IOException e) {
            session.sendError(Response.INTERNAL_ERROR, e.getMessage());
        }
    }

    private Response forwardRequestTo(@NotNull final String cluster, final Request request) throws IOException {

        try {
            return clusterClients.get(cluster).invoke(request);
        } catch (InterruptedException | PoolException | HttpException e) {
            throw new IOException("Forwarding failed for..." + e.getMessage());
        }
    }

    private Response get(final ByteBuffer key) {
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

    private Response put(final ByteBuffer key, final Request request) throws IOException {
        dao.upsert(key, ByteBuffer.wrap(request.getBody()));
        return new Response(Response.CREATED, Response.EMPTY);
    }

    private Response delete(final ByteBuffer key) throws IOException {
        dao.remove(key);
        return new Response(Response.ACCEPTED, Response.EMPTY);
    }
}
