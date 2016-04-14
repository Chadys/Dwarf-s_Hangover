import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Shape;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.text.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.Cursor;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Item extends Interactive{

	private double startX;
	private double startY; //coordonnées de la souris par rapport à l'item au commencement du drag

	// objets d'Inventaire
	public Item(String n, String d, Image i){
		super(n,d,i,null,true);

		this.setCursor(Cursor.OPEN_HAND);

		//rétrécissement de la police de l'étiquette aka le nom d l'objet qui s'affiche
		((Text)this.getEtiquette().getChildren().get(0)).setFont(new Font(10));
		
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				((Inventaire)getParent()).nettoie();
        	    getLevel().commentaire(getAccessibleRoleDescription());
        	}
		});

		this.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
        	    getLevel().getChildren().remove(getEtiquette());
				startX=event.getX();
				startY=event.getY();
				setCursor(Cursor.CLOSED_HAND);
        	}
        });
		this.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				setMouseTransparent(true);
        	    startFullDrag();
        	}
        });// Permet d'envoyer les MouseDragEvent, rend l'objet transparent à la souris pour que les évènement soit relayés aux nodes du dessous

        this.setOnMouseReleased(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
        	    setTranslateX(0);
        	    setTranslateY(0);
        	    setMouseTransparent(false);
        	    setCursor(Cursor.OPEN_HAND);
        	}
        });//fin du drag, l'objet reçoit à nouveau les évènements de la souris et revient à sa place

        this.setOnMouseDragged(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
        	    setTranslateX(getTranslateX()+event.getX()-startX);
        	    setTranslateY(getTranslateY()+event.getY()-startY);
        	}
        }); //bouge l'item en fonction de la position de la souris

        this.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
        	@Override
			public void handle(MouseDragEvent event) {
				Game.getInventaire().nettoie();
        	    getLevel().getChildren().remove(getEtiquette());
				actiononclick((Item)event.getGestureSource());
        	}
        });//pour le pas que le perso bouge quand on fait un drop sur un objet d'inventaire
	}
	@Override
	protected Level getLevel(){
		return ((Level)Game.getNiveaux().get(Game.getCurrentLevel()));
	}
}