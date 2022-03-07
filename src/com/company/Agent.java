package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Entité Agent du système
 */
public class Agent extends Thread{

    // Objet permettant la synchronisation des actions des agents
    public static Object lock = new Object();

    // Pointeur vers l'environnement
    private Environment environment;
    // Identifiant de l'agent
    private int id;

    /**
     * Constructeur de l'agent
     *
     * @param environment   Pointeur de l'environnement
     * @param id            Identifiant de l'agent
     */
    Agent(Environment environment, int id) {
        this.environment = environment;
        this.id = id;
    }

    /**
     * Boucle de fonctionnement de l'agent
     */
    public void run() {
        try {
            while(true) {
                Thread.sleep(2000);
                this.decide();
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            interrupted();
        }
    }

    /**
     * Décision de l'agent
     */
    private void decide() {

        synchronized(lock) {
            System.out.println("id : " + this.id);

            Point pos = this.getPosition();

            ArrayList<Direction> path = this.path();
            Point nextPos = null;
            if(path.size() != 0) {
                switch(path.get(0)) {
                    case HAUT:
                        nextPos = new Point(pos.x - 1, pos.y);
                        break;
                    case BAS:
                        nextPos = new Point(pos.x + 1, pos.y);
                        break;
                    case GAUCHE:
                        nextPos = new Point(pos.x, pos.y - 1);
                        break;
                    case DROITE:
                        nextPos = new Point(pos.x, pos.y + 1);
                        break;
                }

                System.out.println(path.get(0));
                System.out.println(this.getPosition().x + " " +this.getPosition().y);
                System.out.println(nextPos.x + " " + nextPos.y);

                this.move(nextPos);

                this.environment.printGrid();
            }

        }
    }

    private void move(Point nextPos) {
        if(this.environment.isPositionEmpty(nextPos.x, nextPos.y)) {
            this.setPosition(nextPos);
        }
        else {
            int receiverId = this.environment.getAgentIdByPosition(nextPos);
            Message m = new Message(this.id, receiverId, nextPos);
        }
    }
    /**
     * Algo de basic pour atteindre la cible sans prendre en compte les autres
     * @Return: ArrayList<Direction>
     */
    public synchronized ArrayList<Direction> path(){
        Random random = new Random();
        ArrayList<Direction> path = new ArrayList<>();

        Point position = environment.getPosition(this.id);
        Point targetPosition = environment.getTargetPosition(this.id);
        int[][] grid = environment.getGrid();
        int x = (int) position.getX();
        int y = (int) position.getY();

        while (x != targetPosition.getX() || y != targetPosition.getY()) {
            if(random.nextInt(2) == 0){
                //Juste modif y
                if (y < targetPosition.y ) {
                    y++;
                    path.add(Direction.DROITE);
                } else if (y > targetPosition.y){
                    y--;
                    path.add(Direction.GAUCHE);
                }
            } else {
                if (x < targetPosition.x ) {
                    x++;
                    path.add(Direction.BAS);
                } else if (x > targetPosition.x ){
                    x--;
                    path.add(Direction.HAUT);
                }
            }

            if (x > grid.length && x < 0 && y > grid.length && y < 0) {
                System.out.println("error in path");
                break;
            }
        }

        //Print du chemin avec l'id puisque des appeles peuvent avoir lieu simultanément
        /*for (int i = 0; i < path.size(); i++) {
            System.out.println("id:" + this.id + " " + path.get(i) + " ");
        }*/

        return path;
    }

    /**
     * Algo de basic pour atteindre la cible en tenant compte les autres
     * @Return: ArrayList<Direction>
     */
    public synchronized ArrayList<Direction> sneackyPath(){
        Random random = new Random();
        ArrayList<Direction> path = new ArrayList<>();

        Point position = environment.getPosition(this.id);
        Point targetPosition = environment.getTargetPosition(this.id);
        int[][] grid = environment.getGrid();
        int x = (int) position.getX();
        int y = (int) position.getY();

        int count = 0;

        while (x != targetPosition.getX() || y != targetPosition.getY()) {
            if(random.nextInt(2) == 0){
                if (y < targetPosition.y) {
                    if (environment.isPositionEmpty(x, y + 1)){
                        y++;
                        path.add(Direction.DROITE);
                    } else if (x == targetPosition.getX()) {
                        if (x > 0 && environment.isPositionEmpty(x - 1, y)){
                            x--;
                            path.add(Direction.HAUT);
                        }
                        else if (x < grid.length && environment.isPositionEmpty(x + 1, y)){
                            x++;
                            path.add(Direction.BAS);
                        }
                    }
                } else if (y > targetPosition.y){
                    if (environment.isPositionEmpty(x, y - 1)){
                        y--;
                        path.add(Direction.GAUCHE);
                    } else if (x == targetPosition.getX()) {
                        if (environment.isPositionEmpty(x - 1, y)) {
                            x--;
                            path.add(Direction.HAUT);
                        }
                        else if (environment.isPositionEmpty(x + 1, y)){
                            x++;
                            path.add(Direction.BAS);
                        }
                    }
                }
            }
            else {
                if (x < targetPosition.x ) {
                    if (environment.isPositionEmpty(x+1, y)){
                        x++;
                        path.add(Direction.BAS);
                    } else if (y == targetPosition.getY()){
                        if (environment.isPositionEmpty(x, y-1)){
                            y--;
                            path.add(Direction.GAUCHE);
                        } else if (environment.isPositionEmpty(x, y+1)){
                            y++;
                            path.add(Direction.DROITE);
                        }
                    }
                } else if (x > targetPosition.x ){
                    if (environment.isPositionEmpty(x-1, y)) {
                        x--;
                        path.add(Direction.HAUT);
                    } else if (y == targetPosition.getY()) {
                        if (environment.isPositionEmpty(x, y-1)){
                            y--;
                            path.add(Direction.GAUCHE);
                        } else if (environment.isPositionEmpty(x, y+1)){
                            y++;
                            path.add(Direction.DROITE);
                        }
                    }
                }
            }

            if (count >= 30) {
                System.out.println("to much");
                break;
            }

            count ++;
        }

        return path;
    }

    /**
     * Retourne la position courante de l'agent
     *
     * @return Point
     */
    private Point getPosition() {
        return this.environment.getPosition(this.id);
    }

    /**
     * Met à jour la position de l'agent
     *
     * @param p Nouvelle position
     */
    private void setPosition(Point p) {
        this.environment.setPosition(this.id, p);
    }

    private Message readMessage() {
        List<Message> messages = environment.getMessages().get(this.id);

        if (messages.size() > 0) {
            return messages.get(0);
        }

        return null;
    }
}
