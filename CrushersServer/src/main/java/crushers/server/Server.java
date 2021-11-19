package crushers.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

import crushers.models.Duck;
import crushers.services.ducks.DuckRouter;
import crushers.services.ducks.DuckService;
import crushers.storage.MemoryStorage;

/**
 * The actual http server of which the port can be configured in the constructor.
 */
public class Server {
  
  public final int port;
  private final HttpServer httpServer;

  public Server(int port) throws IOException {
    this.port = port;
    this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
    addServices();
  }

  /**
   * Here we add our services to the server so that they can be accessed via http.
   */
  private void addServices() {
    final DuckService duckService = new DuckService(new MemoryStorage<Duck>());

    new DuckRouter(duckService).addEndpoints(this.httpServer);
  }

  /**
   * Starts the server.
   */
  public void start() {
    httpServer.start();
    System.out.println("Server started at http://localhost:" + port);
  }

}