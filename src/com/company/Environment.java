package com.company;

import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * Environnement contenant tous les éléments du système (agents, grille, etc)
 */
@Getter
@Setter
public class Environment {

    // Nombre de lignes de la grille
    private int n;
    // Grille de n*n où le chiffre d'une case correspond à l'identifiant de l'agent sur la case
    private int[][] grid;
    // Lien entre identifiant de l'agent et l'objet Agent
    private HashMap<Integer, Agent> agents;
    // Position des agents en fonction de leur identifiant
    private HashMap<Integer, Point> agentsPosition;
    // Lien entre identifiant de l'agent et liste de ses messages reçus non-lus
    private HashMap<Integer, ArrayList<Message>> messages;

        // Tableau des positions finales souhaitées pour les agents
    private int[][] gridObjective;
    private HashMap<Integer, Point> agentsPositionObjective;

    /**
     * Constructeur de l'environnement
     *
     * @param n         Nombre de lignes de la grille
     * @param nbAgents  Nombre d'agents souhaités
     *
     * @throws InterruptedException
     */
    Environment(int n, int nbAgents) throws InterruptedException {

        // Initialisation des variables
        this.n = n;
        this.agents = new HashMap<>();
        this.agentsPosition = new HashMap<>();
        this.agentsPositionObjective = new HashMap<>();
        this.messages = new HashMap<>();

        // Remplissage des grilles avec la valeur (-1)
        // (-1) représente une case vide
        this.grid = new int[n][n];
        for(int[] row: this.grid) Arrays.fill(row, -1);
        this.gridObjective = new int[n][n];
        for(int[] row: this.gridObjective) Arrays.fill(row, -1);

        // Initialisation du placement initial et voulu des agents
        ArrayList<Point> placesObjectives = this.generatePlaces();
        ArrayList<Point> placesInit = this.generatePlaces();

        Random rand = new Random();
        for(int i = 0; i < nbAgents; i++) {

            // Initialisation d'éléments sur l'agent
            agents.put(i, new Agent(this, i));
            this.messages.put(i, new ArrayList<>());

            // Sélection d'une case finale au hasard
            int index = rand.nextInt(placesObjectives.size());
            Point randPoint = placesObjectives.get(index);
            this.gridObjective[randPoint.x][randPoint.y] = i;
            this.agentsPositionObjective.put(i, randPoint);
            placesObjectives.remove(index);

            // Sélection d'une case initiale pour l'agent
            index = rand.nextInt(placesInit.size());
            randPoint = placesInit.get(index);
            this.grid[randPoint.x][randPoint.y] = i;
            this.agentsPosition.put(i, randPoint);
            placesInit.remove(index);
        }

        // Activation des agents
        for(Agent a: agents.values()) a.start();

        /*while(true) {
            this.printGrid();
            Thread.sleep(100);
        }*/
    }

    /**
     * Génère une liste de n * n points
     *
     * @return ArrayList<Point>
     */
    private ArrayList<Point> generatePlaces() {

        ArrayList<Point> places = new ArrayList<>();

        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                places.add(new Point(i,j));
            }
        }

        return new ArrayList<>(places);
    }

    /**
     * Print la grilles de l'état actuelle et de la grille cible
     */
    public void printGrid() {
        for (int i = 0; i < n; i++) {
            // grid for actual position
            for (int j = 0; j < n; j++) {
                if (this.grid[i][j] == -1) {
                    System.out.print("[ ]");
                } else {
                    System.out.print("[" + this.grid[i][j] + "]");
                }
            }

            System.out.print("     ");

            // Target grid
            for (int j = 0; j < n; j++){
                if (this.gridObjective[i][j] == -1) {
                    System.out.print("[ ]");
                } else {
                    System.out.print("[" + this.gridObjective[i][j] + "]");
                }
            }
            System.out.println("");
        }
        System.out.println("---------------------------------");
    }

    /**
     * Définit si l'état des agents est satisfait
     *
     * @return boolean
     */
    public boolean satisfactionState() {

        // Parcours des agents
        for(int agentIndex: this.agents.keySet()) {

            // Récupération de la position actuelle et de l'objectif
            Point agentPosition = this.agentsPosition.get(agentIndex);
            Point agentPositionObjective = this.agentsPositionObjective.get(agentIndex);

            // On regarde si les positions sont différentes
            if(this.agentsPosition.get(agentIndex).equals(this.agentsPositionObjective.get(agentIndex))){
                return false;
            }

        }
        return true;
    }

    public boolean isPositionEmpty(int x, int y) {
        return this.grid[y][x] ==  -1;
    }

    /**
    *
     * @param agentId
     * @return Point
     */
    public Point getPosition(int agentId){
        return this.agentsPosition.get(agentId);
    }

    /**
     *
     * @param agentId
     * @return Point
     */
    public Point getTargetPosition(int agentId){
        return this.agentsPositionObjective.get(agentId);
    }
}
