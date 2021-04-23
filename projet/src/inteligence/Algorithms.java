package inteligence;
import iniz.*;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import storage.*;

public class Algorithms{
	
	private Maps copy;
	private int[][] computedMap;
	private ArrayList<Noeud> openList = new ArrayList<Noeud>();
	private ArrayList<Noeud> closedList = new ArrayList<Noeud>();
	private ArrayList<Noeud> path = new ArrayList<Noeud>();
	private String[][] verticalMaps = new String[16][16];

	
	public Algorithms(Maps copy){
		this.copy=copy;
		this.computedMap = getEmptyMap();
	}
	
	/**
		REMPLIS DE 0 LA COMPUTEDMAP, GRILLE DE TAILLE 16X16
	 */
	
	
	private ArrayList<Point> getBeginPJ(){
		ArrayList<Point> PJs = new ArrayList<Point>();
		for(int i = 0; i < Donnees.colorList.length;i++){
			PJs.add(copy.getModele().getPJ(Donnees.colorList[i]).getCoord());
		}
		return PJs;
	}
	
	private int[][] getEmptyMap(){
		int[][] Map = new int[16][16];
		for(int x=0;x<16;x++){
			for(int y=0;y<16;y++){
				Map[x][y]=0;
			}
		}
		return Map;
	}
	
	
	public int[][] getComputedMap(){
		return computedMap;
	}
	

	/**
	 PARCOURS MULTIDIRECTIONNEL QUI EXPLORE EN BAS, HAUT, DROITE, ET A GAUCHE
	 POUR INSERER DES 1 SUR LES LIGNES DE DEPLACEMENTS EN PARTANT DU POINT QU'ON LUI
	 ENVOIE
	 */
	private void depthOne(Point p){
		for(int i=0;i < Donnees.direction.length;i++){
			if(this.copy.getMaps()[p.x][p.y].getMurs()[i] == false){
				fill(p.x,p.y,Donnees.direction[i],1);		
			}
		}
	}
	
	/**
	 EXPORE LES AUTRES PROFONDEURS A PARTIR DE CHAQUE 1 COMME POINT DE DEPART ET VEILLANT A PARTIR DANS LE BON SENS 
	 GRACE A UNE GRILLE CONTENANT LES DIRECTIONS QUE L'ONT STOCKS AU MOMENT OU L'ON PLACE CHAQUE POINT
	 */
	private void depthOthers(int d){
		for(int i = 0; i < computedMap.length;i++){
			for(int j = 0; j < computedMap[i].length;j++){
				if(computedMap[i][j] == d-1 && verticalMaps[i][j] == "horizontal"){
					fill(i,j,"down",d);
					fill(i,j,"up",d);
				}
				if(computedMap[i][j] == d-1 && verticalMaps[i][j] == "vertical"){
					fill(i,j,"right",d);
					fill(i,j,"left",d);
				}

			}
		}
	}

	/**
	 APPELE LES METHODES DE REMPLISSAGE DES 4 DIRECTIONS EN FONCTION 
	 */
	
	private void fill(int x,int y, String direction, int depth){
		if(direction == "up"){
			goUp(x,y,depth);
		}
		if(direction == "down"){
			goDown(x,y,depth);
		}
		if(direction == "right"){
			goRight(x,y,depth);
		}
		if(direction == "left"){
			goLeft(x,y,depth);
		}
		this.computedMap[copy.searchPO().x][copy.searchPO().y] = 0;
	}
	
	/**
	 LANCEMENT DES METHODES POUR INSERER LES DIFFERENTS PRONFONDEUR DANS LA CARTE HEURISTIQUE <ComputedMap>
	 */
	private void delineate(){
		depthOne(copy.searchPO());
		for(int d=2;d < 6;d++){
			 depthOthers(d);
		}
		
	}
	
	
	/**
	 MÉTHODES POUR REMPLIR LA COMPUTEDMAP DANS UNE DIRECTION DONNÉE.
	 */
	
	private void goUp(int x,int y, int count){
		boolean stop = false;
		while(!stop){
			if(this.computedMap[x][y] == 0){
				this.computedMap[x][y] = count;
				this.verticalMaps[x][y] = "vertical";
			}
			if(this.copy.getMaps()[x][y].getMurs()[0] == true){
				break;
			}
			x-=1;
		}
	}
	private void goDown(int x,int y, int count){
		boolean stop = false;
		while(!stop){
			if(this.computedMap[x][y] == 0){
				this.computedMap[x][y] = count;
				this.verticalMaps[x][y] = "vertical";	
			}
			if(this.copy.getMaps()[x][y].getMurs()[2] == true){
				break;
			}
			x+=1;
		}	
	}
	private void goRight(int x,int y, int count){
		boolean stop = false;
		while(!stop){
			if(this.computedMap[x][y] == 0){
				this.computedMap[x][y] = count;
				this.verticalMaps[x][y] = "horizontal";
			}
			if(this.copy.getMaps()[x][y].getMurs()[1] == true){
				break;
			}
			y+=1;
		}
	}
	private void goLeft(int x,int y, int count){
		boolean stop = false;
		while(!stop){
			if(this.computedMap[x][y] == 0){
				this.computedMap[x][y] = count;
				this.verticalMaps[x][y] = "horizontal";
			}
			if(copy.getMaps()[x][y].getMurs()[3] == true){
				break;
			}
			y-=1;
		}	
	}
	
	/**
	 RENVOIE LA CIBLE SOIT LE PION OBJECTIF A ATTEINDRE
	 */

	public int whoIsTarget(){
		return copy.searchPOIndice();
	}
	
	

	/*
	 * AJOUTE DE NOUVEAU NOEUD CREER A PARTIR D'UN NOEUD RECU QUI S'APPEL <PARENT> 
	 */
	public void makeChildrens(Noeud parent){
		ArrayList<Noeud> childrens = new ArrayList<Noeud>();
		ArrayList<Point> PJ = parent.getPJ();
		

		for(int i = 0; i < PJ.size();i++){
			ArrayList<Point> direction = getMovesFromPJ(PJ.get(i), parent);
			for(int j = 0; j < direction.size();j++){
				ArrayList<Point> tmpPJ = new ArrayList<Point>();
				for(int a = 0; a < PJ.size();a++){
					if(i==a){
						tmpPJ.add(new Point(direction.get(j)));
					}
					else{
						tmpPJ.add(new Point(parent.getPJ().get(a).x, parent.getPJ().get(a).y));	
					}
					
				}
				int count = parent.getCount()+1;
				Noeud next = new Noeud(parent, tmpPJ, count);
				childrens.add(next);
				
			}
		}
		addToopenList(childrens);
	}


	
	public void addToopenList(ArrayList<Noeud> children){
		for(int i = 0; i < children.size();i++){
			this.openList.add(children.get(i));
		}
	}
	
	
	public ArrayList<Point> getMovesFromPJ(Point p, Noeud parent){
		ArrayList<Point> allowedMoves = new ArrayList<Point>();
		for(int i = 0; i < 4;i++){
			Point dir = copy.movedir(p,i,parent.getPJ());
			if(!dir.equals(p)){
				allowedMoves.add(dir);
			}
		}
		return allowedMoves;
	}
		
	/* 
	 * Mute l'attribut cout de chaque Noeud dans l'openList, une addiction de l'heuristique et de la profondeur de celui-ci.
	 */
	public void addDistanceToNoeud(){
		int sum = 0;
		int x = 0;
		int y = 0;
		for(int i = 0; i < openList.size();i++){
			
				sum=0;
			
				if(this.openList.get(i).getCost() == 0){
					x = this.openList.get(i).getTarget().x;
					y = this.openList.get(i).getTarget().y;
					sum = (this.openList.get(i).getCount() + this.computedMap[x][y]);	
					this.openList.get(i).setCost(sum);					
				}
				
				if(this.openList.get(i).getAverageCost() == 0){
					for(int a = 0; a < openList.get(i).getPJ().size();a++){
						if(a != copy.searchPOIndice()){
							x = this.openList.get(i).getPJ().get(a).x;
							y = this.openList.get(i).getPJ().get(a).y;
							sum += (this.openList.get(i).getCount() + this.computedMap[x][y]);		
						}
						
					}
					this.openList.get(i).setAverageCost(sum/3);
					
				}

			}
		
	}
	
		
	public boolean isInClosedList(Noeud n){
		for(int a = 0; a < closedList.size(); a++){
			if(n.equals(closedList.get(a))){
				return true;
			}
		}
		return false;
	}
	
	
	
	/*
	 * RECUPERE LE NOEUD AYANT LE COUT LE PLUS FAIBLE MAIS AUSSI AYANT DEPLACER LE PION DE MEME COULEUR QUE LE PION OBJECTIF
	 */
	public Noeud shortest(){
		int tmp=100;
		int indice=-1;
		for(int i = 0; i < this.openList.size();i++){

			int cost = this.openList.get(i).getCost();

			if(cost < tmp && this.openList.get(i).whoMoves() == whoIsTarget()){
				tmp = cost;
				indice = i;
			}
			
			
		}
		
		return this.openList.get(indice);
		
	}
	

	
	public boolean checkOver(Noeud end){
		
		if(end.getPJByName(copy.searchPOIndice()).equals(copy.searchPO())){
			return true;
			
		}
		this.closedList.add(end);
		this.openList.remove(end);
		return false;
	}
	
	
	/*
	 * Ajoute a la list Path tout les parents d'un object Noeud donnée, jusqu'a que son parent soit null.
	 */
	public void addPath(Noeud n){
		path = new ArrayList<Noeud>();
		while(n.getParent() != null){
			path.add(n);
			n=n.getParent();
		}
	}
	
	


	public void execute(){
		delineate();
		Noeud selected  = new Noeud(getBeginPJ(), whoIsTarget());
		makeChildrens(selected);
		addDistanceToNoeud();
		selected = shortest();


		while(!checkOver(selected)){
			makeChildrens(selected);
			addDistanceToNoeud();
			selected = shortest();
			
			//Condition d'arret pour ne pas qu'il boucle beaucoup trop longtemps dans le cas ou il doit deplacer 2 robots, se referer au rapport.
			if(openList.size() > 100000){
				System.out.println("L'algorithme n'as pas reussit à trouver la solution, car il doit déplacer 2 robots");
				break;
			}
		}
		
		addPath(selected);
		Collections.reverse(this.path);
		showPath();
		
	}
	
	public void showPath(){
		if(openList.size() < 100000){
			System.out.println("L'algorithme a résolut en : " + path.size() + " coup(s).");
			System.out.println();
			
			for(int i = 0; i < path.size();i++){
				int x = path.get(i).getPJ().get(copy.searchPOIndice()).y+1;
				int y = path.get(i).getPJ().get(copy.searchPOIndice()).x+1;
				System.out.println("Le pion de couleur " + copy.getPOColor() + " doit se déplacer "  + "ce déplace en" + " ( " + x + " ; " + y + " )");
			}
			System.out.println("______________________________________");	
		}
		
		
		
	}
}
