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

        this.printGrid();

        // Activation des agents
        for(Agent a: agents.values()) a.start();

        // Vérification régulière de l'état
        while(true) {
            if(this.satisfactionState()) {
                System.out.println("REUSSI");
                for(Agent a: agents.values()) a.stop();
                break;
            };
            Thread.sleep(1000);
        }
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
     * Affiche la grille actuelle et la grille cible
     */
    public void printGrid() {
        for (int i = 0; i < n; i++) {
            // grid for actual position
            for (int j = 0; j < n; j++) {
                if (this.grid[i][j] == -1) {
                    System.out.print("[  ]");
                } else {
                    if (this.grid[i][j] > 9)
                        System.out.print("[" + this.grid[i][j] + "]");
                    else
                        System.out.print("[ " + this.grid[i][j] + "]");
                }
            }

            System.out.print("     ");

            // Target grid
            for (int j = 0; j < n; j++){
                if (this.gridObjective[i][j] == -1) {
                    System.out.print("[  ]");
                } else {
                    if (this.gridObjective[i][j] > 9)
                        System.out.print("[" + this.gridObjective[i][j] + "]");
                    else
                        System.out.print("[ " + this.gridObjective[i][j] + "]");
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
            if(!agentPosition.equals(agentPositionObjective)){
                return false;
            }
        }

        return true;
    }

    /**
     * Retourne TRUE si aucun agent n'est sur la position donnée
     *
     * @param x Position en x
     * @param y Position en y
     *
     * @return boolean
     */
    public boolean isPositionEmpty(int x, int y) {
        if (x >= grid.length || x < 0 || y >= grid.length || y < 0){
            return false;
        }
        return this.grid[x][y] ==  -1;
    }

    /**
     * Retourne la position d'un agent
     *
     * @param agentId Identifiant de l'agent
     *
     * @return Point
     */
    public Point getPosition(int agentId){
        return this.agentsPosition.get(agentId);
    }

    /**
     * Retourne la position cible d'un agent
     *
     * @param agentId Identifiant de l'agent
     *
     * @return Point
     */
    public Point getTargetPosition(int agentId){
        return this.agentsPositionObjective.get(agentId);
    }

    /**
     * Met à jour la position d'un agent
     *
     * @param agentId Identifiant de l'agent
     *
     * @param nextPos Position suivante de l'agent
     */
    public void setPosition(int agentId, Point nextPos) {

        // Récupération de la position courante de l'agent
        Point currentPos = this.agentsPosition.get(agentId);
        // MAJ de la position dans la HashMap
        this.agentsPosition.put(agentId, nextPos);

        // MAJ de la position dans la grille pour affichage console
        this.grid[currentPos.x][currentPos.y] = -1;
        this.grid[nextPos.x][nextPos.y] = agentId;
    }

    public void sendMessage(Message m) {
        int receiverId = m.getReceiverId();

        ArrayList<Message> receiverMessages = this.messages.get(receiverId);
        receiverMessages.add(m);
    }

    public void deleteMessage(Message m) {
        int receiverId = m.getReceiverId();
        ArrayList<Message> receiverMessages = this.messages.get(receiverId);
        receiverMessages.remove(0);
    }

    public int getAgentIdByPosition(Point p) {
        return this.grid[p.x][p.y];
    }

    public int getAgentAroundMe(Point p) {
        Random random = new Random();
        int res = -1;

        while (true){
            int r = random.nextInt(4);
            if (r == 0) {
                res = this.getAgentIdByPosition(new Point(p.x - 1, p.y));
            } else if (r == 1) {
                res = this.getAgentIdByPosition(new Point(p.x + 1, p.y));
            } else if (r == 2) {
                res = this.getAgentIdByPosition(new Point(p.x, p.y - 1));
            } else if (r == 3) {
                res = this.getAgentIdByPosition(new Point(p.x, p.y + 1));
            }

            if (res != -1){
                return res;
            }
        }

    }
}
