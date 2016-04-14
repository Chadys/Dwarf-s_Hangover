import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.paint.Color;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import java.util.ArrayList;
import javafx.animation.*;
import javafx.geometry.Bounds;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.binding.Bindings;
import java.util.concurrent.Callable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.beans.property.SimpleStringProperty;

// le personnage de ce jeu, unique dans un point&click
public class Perso extends Pnj{
	private SimpleStringProperty tenue = new SimpleStringProperty("nu"); //dans quelle tenue est actuellement le personnage

	private ObservableBooleanValue colliding;

	//images du perso
	private ArrayList<ImageView> dos; //listes des images du personnage, la position statique à l'index 0, la marche ensuite
	private ArrayList<ImageView> droite;
	private ArrayList<ImageView> gauche;
	private ArrayList<ImageView> face;
	private ArrayList<ImageView> diaghautgauche;
	private ArrayList<ImageView> diaghautdroite;
	private ArrayList<ImageView> diagbasgauche;
	private ArrayList<ImageView> diagbasdroite;
	private ImageView couche;

	//pour tester, simple triangle en personnage
	public Perso(String n, String d, Item f, Boolean a, String dia, String diafou, String dpo, String son, String dossier){
		super(n,d,new Image("/Ressources/"+dossier+"/face.png"),f,a,dia,diafou,dpo,son);
    	this.setScaleX(0.5);
    	this.setScaleY(0.5);
		//Detecte les collision, repris et adapté de la réponse de James_D ici :http://stackoverflow.com/questions/20779880/how-to-achieved-collision-detection-in-translate-transition-in-javafx
		this.colliding = Bindings.createBooleanBinding(new Callable<Boolean>() {
			@Override
        	public Boolean call() {
        		if ((Level)getParent() != null)
					for (Bounds obstacle : ((Level)getParent()).getObstacles())
            			if (getBoundsInParent().intersects(obstacle)){
            				return true;
            			};;
            	return false;
        	}
    	}, this.boundsInParentProperty(),this.parentProperty());

		this.tenue.set("nu");
    	this.setApparence(dossier);


    	Game.censureProperty().addListener((observable, oldvalue, newvalue) -> {
    		if (tenue.get() == "nu"){
				if (newvalue == false) setApparence("Nu");
				else setApparence("Censure");
			}
		});
	}

	@Override
	public ContextMenu createmenuclick(){
		ContextMenu menuclick = super.createmenuclick();
		MenuItem deshabille = new MenuItem("Déshabiller");
		deshabille.setOnAction(new EventHandler<ActionEvent>() {
    		public void handle(ActionEvent e) {
    			if (tenue.get()=="nu") parler("Comment veux-tu que je sois\nplus à poil que ça, abruti !");
    			else {
    				if (tenue.get()=="un pagne") Game.getInventaire().getChildren().add(Data.PAGNE);
        			tenue.set("nu");
              parler("Ah bah bravo !");
        			if (Game.censureProperty().get()) setApparence("Censure");
        			else setApparence("Nu");
        		}
    		}
		});
		menuclick.getItems().add(deshabille);;
		return menuclick;
	}


	public void moveonclick(double x, double y, Action act){
        this.getParent().setMouseTransparent(true);
		double deltaX = x-(this.getLayoutX()+this.getTranslateX()+(this.getLayoutBounds().getWidth()/2));
		double deltaY = y-(this.getLayoutY()+this.getTranslateY()+(this.getLayoutBounds().getHeight()/2));//donne la translation a effectuer pour que le centre du personnage se retrouve aux coordonnées x,y
		double startX = this.getTranslateX();
		double startY = this.getTranslateY();
		Perso perso = this;

		// ce code se repose sur le code de la classe TranslateTransition
		Animation marche = new Transition(){
			private int CURRENTANIMATION = 1; //n° de la frame d'animation courante
			private int TOURDEBOUCLE = 0;
			private ArrayList<ImageView> direction;
			private double lastX;//valeur du dernier déplacement
			private double lastY;
			{
			    setCycleDuration(Objet.tempsDeTrajet(deltaX,deltaY).multiply(3));

      			//Calcul de la direction dans laquelle va aller le personnage à l'aide d'un calcul de la tangente afin d'utiliser les images du sprite appropriées
      			if (deltaX == 0){
          			if (deltaY>0) direction = face;
         			else direction = dos;
      			}
      			else{
          			final double TAN = deltaY/deltaX;
          			final double PETIT = Math.tan(Math.toRadians(20));
          			final double GRAND = Math.tan(Math.toRadians(70));
          			if (-PETIT <=TAN && TAN <=PETIT){
              			if (deltaX>0) direction = droite;
              			else direction = gauche;
        	  		}
     	     		else {
     	         		if (TAN <= -GRAND || GRAND <= TAN){
      	            		if (deltaY<0) direction = dos;
      	            		else direction = face;
      	        		}
              			else{
                  			if (-GRAND < TAN && TAN < -PETIT){
                      			if (deltaX>0) direction = diaghautdroite;
                      			else direction = diagbasgauche;
                  			}
                  			else {
                      			if (deltaY>0) direction = diagbasdroite;
                      			else direction = diaghautgauche;
                  			};
              			}
          			}
          			}
      			setOnFinished(e -> {
          			perso.setImage(direction.get(0));
          			perso.getParent().setMouseTransparent(false);
          			if(act!=null) act.action();
      			});
    		}

			protected void interpolate(double frac) {
				//Tous les 10 tours, change l'image du personnage pour donner l'impression qu'il marche
				if (TOURDEBOUCLE == 10){
      				if(CURRENTANIMATION < direction.size()) perso.setImage(direction.get(CURRENTANIMATION));
      				if (CURRENTANIMATION == direction.size()-1) CURRENTANIMATION = 1;
      				else ++CURRENTANIMATION;
      				TOURDEBOUCLE=0;
      			}
      			else ++TOURDEBOUCLE;

      				lastX=getTranslateX();
      				lastY=getTranslateY();

      				if (deltaX !=0){
         				perso.setTranslateX(startX + frac * deltaX);
         				lastX=getTranslateX()-lastX;
      				}

      				if (deltaY !=0){
          				perso.setTranslateY(startY + frac * deltaY);
          				lastY=getTranslateY()-lastY;
      				}


      				if (colliding.get()){

      					if (deltaX !=0)
         					perso.setTranslateX(getTranslateX() -lastX);
         				if (deltaY !=0)
          					perso.setTranslateY(getTranslateY() -lastY);

                perso.setImage(direction.get(0));
                perso.getParent().setMouseTransparent(false);
                if(act!=null) act.action();
                stop();
      				}
    		}
		};
		marche.play();
	}


	public ArrayList<ImageView> getDos(){
		return this.dos;
	}
	public ArrayList<ImageView> getDroite(){
		return this.droite;
	}
	public ArrayList<ImageView> getGauche(){
		return this.gauche;
	}
	public ArrayList<ImageView> getFace(){
		return this.face;
	}
	public ArrayList<ImageView> getDiagHautGauche(){
		return this.diaghautgauche;
	}
	public ArrayList<ImageView> getDiagHautDroite(){
		return this.diaghautdroite;
	}
	public ArrayList<ImageView> getDiagBasGauche(){
		return this.diagbasgauche;
	}
	public ArrayList<ImageView> getDiagBasDroite(){
		return this.diagbasdroite;
	}
	public ImageView getCouche(){
		return this.couche;
	}
	public SimpleStringProperty tenueProperty(){
		return this.tenue;
	}

	public void setApparence (String dossier){
		//parcours le dossier et remplace les images
    	Image cote = new Image("/Ressources/"+dossier+"/cote.png");
    	Image cote1 = new Image("/Ressources/"+dossier+"/cote1.png");
    	Image cote2 = new Image("/Ressources/"+dossier+"/cote2.png");
    	Image couche = new Image("/Ressources/"+dossier+"/couche.png");
		  Image diagbas = new Image("/Ressources/"+dossier+"/diagbas.png");
    	Image diagbas1 = new Image("/Ressources/"+dossier+"/diagbas1.png");
    	Image diagbas2 = new Image("/Ressources/"+dossier+"/diagbas2.png");
    	Image diaghaut = new Image("/Ressources/"+dossier+"/diaghaut.png");
    	Image diaghaut1 = new Image("/Ressources/"+dossier+"/diaghaut1.png");
    	Image diaghaut2 = new Image("/Ressources/"+dossier+"/diaghaut2.png");
   		Image dos = new Image("/Ressources/"+dossier+"/dos.png");
    	Image dos1 = new Image("/Ressources/"+dossier+"/dos1.png");
    	Image face = new Image("/Ressources/"+dossier+"/face.png");
    	Image face1 = new Image("/Ressources/"+dossier+"/face1.png");
    	
    	this.dos = new ArrayList<ImageView>();
    	this.dos.add(new ImageView(dos));
    	this.dos.add(new ImageView(dos1));
    	this.dos.add(new ImageView(dos1));
    	this.dos.get(2).setScaleX(-1);

		  this.droite = new ArrayList<ImageView>();
    	this.droite.add(new ImageView(cote));
    	this.droite.get(0).setScaleX(-1);
    	this.droite.add(new ImageView(cote1));
    	this.droite.get(1).setScaleX(-1);
    	this.droite.add(new ImageView(cote2));
    	this.droite.get(2).setScaleX(-1);

		  this.gauche = new ArrayList<ImageView>();
    	this.gauche.add(new ImageView(cote));
    	this.gauche.add(new ImageView(cote1));
    	this.gauche.add(new ImageView(cote2));
	
		  this.face = new ArrayList<ImageView>();
    	this.face.add(new ImageView(face));
    	this.face.add(new ImageView(face1));
    	this.face.add(new ImageView(face1));
    	this.face.get(2).setScaleX(-1);
	
		  this.diaghautgauche = new ArrayList<ImageView>();
    	this.diaghautgauche.add(new ImageView(diaghaut));
    	this.diaghautgauche.get(0).setScaleX(-1);
    	this.diaghautgauche.add(new ImageView(diaghaut1));
    	this.diaghautgauche.get(1).setScaleX(-1);
    	this.diaghautgauche.add(new ImageView(diaghaut2));
    	this.diaghautgauche.get(2).setScaleX(-1);
	
		  this.diaghautdroite = new ArrayList<ImageView>();
    	this.diaghautdroite.add(new ImageView(diaghaut));
    	this.diaghautdroite.add(new ImageView(diaghaut1));
    	this.diaghautdroite.add(new ImageView(diaghaut2));

		  this.diagbasgauche = new ArrayList<ImageView>();
    	this.diagbasgauche.add(new ImageView(diagbas));
    	this.diagbasgauche.add(new ImageView(diagbas1));
    	this.diagbasgauche.add(new ImageView(diagbas2));
		
      this.diagbasdroite = new ArrayList<ImageView>();
    	this.diagbasdroite.add(new ImageView(diagbas));
    	this.diagbasdroite.get(0).setScaleX(-1);
    	this.diagbasdroite.add(new ImageView(diagbas1));
    	this.diagbasdroite.get(1).setScaleX(-1);
    	this.diagbasdroite.add(new ImageView(diagbas2));
    	this.diagbasdroite.get(2).setScaleX(-1);

		  this.couche = new ImageView(couche);

		  this.setImage(this.face.get(0));
    }

    public void setImage(ImageView img){
      this.setImage(img.getImage());
      this.setScaleX(img.getScaleX()*0.5);
    }
}