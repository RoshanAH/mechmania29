package mech.mania.starterpack.strategy;

import mech.mania.starterpack.game.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import mech.mania.starterpack.game.GameState;
import mech.mania.starterpack.game.terrain.Terrain;

public class AStar {

  public static List<Position> path(Position start, Position end, GameState state, Map<Position, Terrain> terrain, Function<Terrain, Integer> terrainNav){
    
    if (start.equals(end)) return List.of(start);

    List<Node> queue = new ArrayList<>();
    Set<Position> explored = new HashSet<>();
    Map<Position, Node> observed = new HashMap<>(); // extracts position as the position for easier lookup

    final Node startNode = new Node(start, end, null, 0);

    queue.add(startNode);
    observed.put(startNode.pos, startNode);

    while (true) { 
      final Node head = queue.get(0);
      if (head.pos.equals(end)) // we are able to jump to the end
        break;

      Set<Node> neighbors = head.neighbors(terrain, terrainNav);

      for(Node n : neighbors){
        if (explored.contains(n.pos)) continue;
        final Node existing = observed.get(n.pos); 
        if (existing != null){ // if we have already seen this node, compare distances
          if (n.distance < existing.distance) {
            queue.remove(existing);
            insert(queue, n);
          }
        } else {
          observed.put(n.pos, n);
          insert(queue, n);
        }

      }

      explored.add(head.pos);
      queue.remove(head);
    }

    List<Position> out = new ArrayList<>();

    Node current = queue.get(0);
    while(current != null){
      out.add(0, current.pos);
      current = current.previous;
    }

    return out;

  }

  public static void insert(List<Node> list, Node newNode){ // inserts the node in the correct order to make sure the list stays sorted
    for (int i = 0; i < list.size(); i++){
      final Node n = list.get(i);
      if (n.distance + n.heuristic > newNode.distance + newNode.heuristic){
        list.add(i, newNode);
        return;
      }
    }

    list.add(newNode);
  }

  public static int dist(Position first, Position second){
    return Math.abs(first.x() - second.x()) + Math.abs(first.y() - second.y());
  }

  public static Map<Position, Terrain> mapTerrain(GameState state){
    Map<Position, Terrain> out = new HashMap<>();

    for (Terrain t : state.terrains().values()){
      out.put(t.position(), t);
    }

    return out;
  }

  private static class Node {
    final Position pos; 

    final int distance;
    final int heuristic;
    final Node previous;
    final Position end;

    Node(Position pos, Position end, Node previous, int distance){
      this.pos = pos;
      this.end = end;
      this.previous = previous;
      this.distance = distance;
      this.heuristic = dist(pos, end);
    }

    private void safeAdd(Map<Position, Terrain> terrainMap, Function<Terrain, Integer> nav, Position neighbor, Set<Node> out){
      final Terrain terrain = terrainMap.get(neighbor);
      if (terrain == null){
        out.add(new Node(neighbor, end, this, distance + 1));
      } else {
        if (terrain.health() != Integer.MAX_VALUE){
          final int cost = nav.apply(terrain);
          if (cost != Integer.MAX_VALUE)
            out.add(new Node(neighbor, end, this, distance + 1 + cost));
        }
      }
    }

    Set<Node> neighbors(Map<Position, Terrain> terrainMap, Function<Terrain, Integer> nav){ 
      final Set<Node> out = new HashSet<>();

      if (pos.y() > 0) { // north
        final Position neighbor = new Position(pos.x(), pos.y() - 1);
        safeAdd(terrainMap, nav, neighbor, out);
      }

      if (pos.x() > 0) { // east
        final Position neighbor = new Position(pos.x() - 1, pos.y());
        safeAdd(terrainMap, nav, neighbor, out);
      }

      if (pos.y() < 99) { // south
        final Position neighbor = new Position(pos.x(), pos.y() + 1);
        safeAdd(terrainMap, nav, neighbor, out);
      }

      if (pos.x() < 99) { // west
        final Position neighbor = new Position(pos.x() + 1, pos.y());
        safeAdd(terrainMap, nav, neighbor, out);
      }

      return out;
    }
  }


}
