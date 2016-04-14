import java.util.ArrayList;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.paint.Color;
import javafx.scene.Parent;
import javafx.geometry.Insets;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

// liste contenant les objet d'inventaire disponibles
public class Inventaire extends FlowPane{

	public Inventaire(){
		this.setVgap(20);
    	this.setHgap(20);
    	this.setPrefWrapLength(1067);
		this.setStyle("-fx-background-color: #222222");
		this.setPadding(new Insets(10));
		this.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderStroke.DEFAULT_WIDTHS)));
		this.relocate(0,30);
		this.setPickOnBounds(false);
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				event.consume(); //évite d'appeler le setOnMouseClicked de la class Level (et donc que le personnage ne se déplace par un clique dans l'inventaire)
        	}
		});
	}

	public void addChildren(Item i){
		this.getChildren().add(i);
		double w = i.getLayoutBounds().getWidth();
		if(w<50){
			w=(50-w)/2;
			this.setMargin(i,new Insets(0,w,0,w));
		}//Permet que chaque objet de l'inventaire possède le même espace autour de lui, peu importe sa taille
	}

	public void nettoie(){
		Level lvl=(Level)getParent();
		for (Parent x : lvl.getDernierCom())
			if(x!=this)
        		lvl.getChildren().remove(x);
        lvl.setDernierCom(new ArrayList<Parent>());
        lvl.getDernierCom().add(this);
	}
}