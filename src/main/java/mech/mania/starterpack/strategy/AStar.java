package mech.mania.starterpack.strategy;

import mech.mania.starterpack.game.util.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mech.mania.starterpack.game.GameState;
import mech.mania.starterpack.game.terrain.Terrain;

public class AStar {

  public static int[][] terrainMap = new int[100][100]; 
  private static boolean terrainMapped = false; // TODO get rid of this and have it update evey time it is bots turn

  public static Position nextMove(Position start, Position end, int speed, GameState state){

    if (dist(start, end) <= speed) return end; // if we are already close just go there lol

    List<Node> queue = new ArrayList<>();
    Set<Position> explored = new HashSet<>();
    Map<Position, Node> observed = new HashMap<>(); // extracts position as the position for easier lookup

    final Node startNode = new Node(start, end, null, 0);

    queue.add(startNode);
    observed.put(startNode.pos, startNode);

    while (true) { 
      final Node head = queue.get(0);
      if (dist(head.pos, end) <= speed) // we are able to jump to the end
        break;

      Set<Node> neighbors = head.neighbors(speed);


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

    Node current = queue.get(0);
    Node lookbehind = current.previous;
    while(lookbehind.previous != null){
      current = lookbehind;
      lookbehind = current.previous;
    }

    return current.pos;
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

  public static Set<Position> floodFill(Position start, int speed){ 
    final Set<Position> inside = new HashSet<>();
    final List<Position> queue = new ArrayList<>();
    
    queue.add(start);

    while (!queue.isEmpty()){
      final Position head = queue.get(0);
      inside.add(head);
      queue.remove(0);

      for(Position neighbor : immediateNeighbors(head)){
        if (!inside.contains(neighbor) && dist(start, neighbor) <= speed){
          queue.add(neighbor);
        }
      }
    }
    
    inside.remove(start); // the start point is not a neighbor
    return inside;
  }

  public static Set<Position> immediateNeighbors(Position pos){
    final Set<Position> out = new HashSet<>();

    if (pos.y() > 0) { // north
      final Position neighbor = new Position(pos.x(), pos.y() - 1);
      if (terrainMap[neighbor.y()][neighbor.x()] == 0)
        out.add(neighbor);
    }
    if (pos.x() > 0) { // east
      final Position neighbor = new Position(pos.x() - 1, pos.y());
      if (terrainMap[neighbor.y()][neighbor.x()] == 0)
        out.add(neighbor);
    }
    if (pos.y() < 99) { // south
      final Position neighbor = new Position(pos.x(), pos.y() + 1);
      if (terrainMap[neighbor.y()][neighbor.x()] == 0)
        out.add(neighbor);
    }
    if (pos.x() < 99) { // west
      final Position neighbor = new Position(pos.x() + 1, pos.y());
      if (terrainMap[neighbor.y()][neighbor.x()] == 0)
        out.add(neighbor);
    }

    return out;
  }

  public static void mapTerrain(GameState state){
    if (terrainMapped) return; // only map once

    for (Terrain t : state.terrains().values()){
      terrainMap[t.position().y()][t.position().x()] = t.health();
    }

    terrainMapped = true; 
  }

  private static class Node {
    final Position pos; 
    // final Map<Position, Integer> terrainChanges = new HashMap<>(); TODO

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

    Set<Node> neighbors(int speed){ 
      final Set<Position> positions = floodFill(pos, speed);
      final Set<Node> out = new HashSet<>();
      for (Position p : positions){
        final Node newNode = new Node(p, end, this, distance + 1);
        out.add(newNode);
      }

      return out;
    }
  }


}
