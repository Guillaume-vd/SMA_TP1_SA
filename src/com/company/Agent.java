package com.company;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Entité Agent du système
 */
public class Agent extends Thread{

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
            Thread.sleep(1000);
            this.path();
            //this.move();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupted();
        }
    }

    /**
     * Décision et déplacement de l'agent
     */
    private synchronized void move() {

        Point pos = this.environment.getPosition(this.id);

        ArrayList<Direction> path = this.path();
        Point nextPos;
        switch(path.get(0)) {
            case HAUT:
                nextPos = new Point(pos.x, pos.y - 1);
                break;
            case BAS:
                nextPos = new Point(pos.x, pos.y + 1);
                break;
            case GAUCHE:
                nextPos = new Point(pos.x - 1, pos.y);
                break;
            case DROITE:
                nextPos = new Point(pos.x = 1, pos.y);
                break;
        }

        /*if(this.environment.isPositionEmpty(nextPos.x, nextPos.y)) {

        }*/
    }

    /**
     *
     */
    public synchronized ArrayList<Direction> path(){
        Random random = new Random();
        ArrayList<Direction> path = new ArrayList<>();

        Point position = environment.getPosition(this.id);
        Point targetPosition = environment.getTargetPosition(this.id);
        int[][] grid = environment.getGrid();
        int x = (int) position.getX();
        int y = (int) position.getY();
        boolean isNotSatisfied = true;
        System.out.println("\n id: " + this.id);
        while (isNotSatisfied) {
            if (x == targetPosition.getX() && y == targetPosition.getY()) {
                isNotSatisfied = false;
            } else if(x == targetPosition.getX()){
                //Juste modif y
                if (y < targetPosition.y ) {
                    System.out.print("y++ ");
                    y++;
                    path.add(Direction.DROITE);
                } else {
                    System.out.print("y-- ");
                    y--;
                    path.add(Direction.GAUCHE);
                }
            } else {
                if (x < targetPosition.x ) {
                    System.out.print("x++ ");
                    x++;
                    path.add(Direction.BAS);
                } else {
                    System.out.print("x-- ");
                    x--;
                    path.add(Direction.HAUT);
                }
            }
        }

        for (int i = 0; i > path.size(); i++) {
            System.out.println(path.get(i));
        }

        return path;
    }

    /***
     * Interruption du thread courant
     */
    public void cancel() {
        Thread.currentThread().interrupt() ;
    }


    public Environment getEnvironment() {
        return environment;
    }
}
