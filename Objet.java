import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.Group;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


// un objet du niveau -> class intermédiaire, permet de séparer les fonctions par rapport 
public class Objet extends ImageView{

	private Boolean collision = true; //cet objet empêche-t-il qu'on marche dessus

	public Objet(Image i, Boolean c){
		super(i);
		this.collision =c;
	}

	public Objet(Image i){
		this(i,true);
	}

	public Boolean getCollision(){
		return this.collision;
	}
	public void setCollision(Boolean b){
		collision = b;
	}

	//durée que prendra un objet pour faire une translation en fonction de la distance, utile pour les animations
	static public Duration tempsDeTrajet(double x, double y){
		double distance = Math.sqrt(Math.pow(x,2)+Math.pow(y,2));
		return Duration.millis(distance*3);
	}
}