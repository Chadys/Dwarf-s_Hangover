import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.Group;
import javafx.util.Pair;
import javafx.scene.image.Image;

// objet qui permet de changer de niveau (une porte, un couloir,...)
public class Exit extends Interactive{

	private Action sortir; //ce qu'il se passe si on emprunte cette sortie
	// lvl = niveau auquel on accède par cette sortie
	// open = sortie ouverte ou fermée
	// coord = les coordonnées du personnage dans le nouveau niveau

	public Exit(String n, String d, Image i, Boolean open, Action s){
		super(n,d,i,true,null,true);
		this.sortir=s;
		if(open) ouvrir();
		else fermer();
	}

	// Sortie de base sans action précisée, si ouverte change simplement de niveau quand on clique dessus
	public Exit(String n, String d, Image i, Boolean open, int lvl, Pair<Double,Double> coord){
		this(n,d,i,open, () -> {
			Game.loadLevel(lvl);
        	Data.PERSO.setTranslateX(0);
        	Data.PERSO.setTranslateY(0);
			Data.PERSO.relocate(coord.getKey(),coord.getValue());
		});
	}

	public void ouvrir(){
		if(getParent()!=null) ((Level)getParent()).getObstacles().remove(this.getBoundsInParent());
		this.setCollision(false); //si la sortie est ouverte, pas de collision
	 	this.setOnMouseClicked(event -> {
				 event.consume(); //evite que le texte ne s'affiche pas a cause du setOnMouseClicked de la class Level qui fait un nettoie()
				((Level)getParent()).nettoie();
				Data.PERSO.moveonclick(getLayoutX()+getLayoutBounds().getMinX()+(getLayoutBounds().getWidth()/2),getLayoutY()+getLayoutBounds().getMinY()+(getLayoutBounds().getHeight()/2),sortir);
        });
	}

	public void fermer(){
		if(getParent()!=null) ((Level)getParent()).getObstacles().add(this.getBoundsInParent());
		this.setCollision(true);
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				event.consume();
				((Level)getParent()).nettoie();
        	    Data.PERSO.moveonclick(event.getSceneX(),event.getSceneY(),() -> ((Level)getParent()).commentaire(getAccessibleRoleDescription()));
        	}
		});
	}
}