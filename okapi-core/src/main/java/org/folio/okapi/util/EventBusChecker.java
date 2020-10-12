package org.folio.okapi.util;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import java.util.LinkedList;
import java.util.List;

public class EventBusChecker {
  private static final String EVENT_NODE_CHECK = "org.folio.okapi.node.check.";

  /**
   * Check that event bus is operational by sending a message to each party.
   * @param vertx Vert.x handle
   * @param clusterManager manager ; null if not in clustered mode
   * @return async result
   */
  public static Future<Void> check(Vertx vertx, ClusterManager clusterManager) {
    String thisNode;
    List<String> nodes;
    if (clusterManager == null) {
      thisNode = "localhost";
      nodes = new LinkedList<>();
      nodes.add(thisNode);
    } else {
      thisNode = clusterManager.getNodeID();
      nodes = clusterManager.getNodes();
    }
    return check(vertx, thisNode, thisNode, nodes);
  }

  static Future<Void> check(Vertx vertx, String thisNode, String reply, List<String> nodes) {
    DeliveryOptions options = new DeliveryOptions().setSendTimeout(30);
    vertx.eventBus().consumer(EVENT_NODE_CHECK + thisNode, message -> {
      message.reply(reply);
    });
    List<Future> futures = new LinkedList<>();
    for (String node : nodes) {
      futures.add(vertx.eventBus().request(EVENT_NODE_CHECK + node, "").compose(res -> {
        String replyNode = (String) res.body();
        if (!node.equals(replyNode)) {
          return Future.failedFuture("Send " + node + " but got reply " + replyNode);
        }
        return Future.succeededFuture();
      }));
    }
    return CompositeFuture.all(futures).mapEmpty();
  }
}
