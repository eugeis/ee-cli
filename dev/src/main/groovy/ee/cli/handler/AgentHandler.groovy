package ee.cli.handler

import com.sun.net.httpserver.Headers
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import ee.cli.Ee
import ee.cli.core.Handler
import ee.cli.core.Valid
import ee.cli.core.model.Base
import ee.cli.core.model.Context
import ee.cli.core.model.Result

import javax.annotation.PostConstruct
import javax.inject.Inject
import java.util.concurrent.Executors

class AgentHandler extends Base implements Handler {

    @Inject
    int port = 8889

    @Inject
    Ee ee

    HttpServer server

    Thread thread

    @PostConstruct
    @Valid
    void init() {
    }

    Result execute(Context context, def source) {
        Result ret
        Deque params = context.commands
        if (params) {
            try {
                ret = doExecute(context, source)
            } catch (e) {
                ret = new Result(context: context, ok: false, error: e, failure: "Executing agains the $context failed, because of exception: $e")
                log.error ret, e
            }
        } else {
            ret = new Result(context: context, info: "No class defined to execute for $context triggered by $source.")
            log.warn ret
        }
        ret
    }

    protected Result doExecute(Context context, source) {
        Result ret
        String callable = context.commands.peek()

        if ('start'.equals(callable)) {
            start()
        } else if ('stop'.equals(callable)) {
            stop()
        }

        ret
    }

    def start() {

        thread = new Thread(new Runnable() {
            @Override
            void run() {

                try {
                    InetSocketAddress addr = new InetSocketAddress(port)
                    server = com.sun.net.httpserver.HttpServer.create(addr, 0)
                    server.with {
                        createContext('/', new HttpHandler() {

                            @Override
                            void handle(HttpExchange httpExchange) throws IOException {
                                String requestMethod = httpExchange.requestMethod
                                if (requestMethod.equalsIgnoreCase("GET")) {
                                    Headers responseHeaders = httpExchange.responseHeaders
                                    responseHeaders.set("Content-Type", "text/plain");
                                    OutputStream responseBody = httpExchange.responseBody
                                    Result ret
                                    try {
                                        final String query = httpExchange.requestURI.rawQuery
                                        if (query) {
                                            def args = query.split('%20').findAll { !it.equals('agent') } as String[]
                                            ret = ee.execute(args)
                                        } else {
                                            ret = new Result(ok: false, info: 'Please provide ee command.')
                                        }
                                    } catch (e) {
                                        e.printStackTrace()
                                        ret = new Result(ok: false, error: '$e')
                                    }
                                    httpExchange.sendResponseHeaders(200, 0)
                                    String json = ret.toJson()
                                    responseBody.write(json.bytes)
                                    responseBody.close()
                                }
                            }
                        })
                        setExecutor(Executors.newCachedThreadPool())
                        start()
                    }
                } catch (e) {
                    e.printStackTrace()
                    println "Start problem $e"
                }
            }
        })
        thread.start()
        synchronized (this) {
            wait()
        }
    }

    def stop() {
        server?.stop(0)
    }
}