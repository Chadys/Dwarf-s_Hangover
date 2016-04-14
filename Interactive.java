import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.shape.Shape;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.shape.Shape;
import javafx.scene.control.Control;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.Cursor;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.scene.image.Image;


//Un Objet sur lequel on peut faire des drop
//->trois sortes d'interactions possible dans ce cas : pas d'interactions, nouvel objet créé ou modification de cet Interactive
public class Interactive extends Objet{
	
	private StackPane etiquette;
	private HashMap<Item,Action> interact =  new HashMap<Item,Action>(1);  // map des interactions possibles avec cet objet, key = item qui active l'interaction, value = action à faire (souvent ajout d'un objet)
	private ContextMenu menuclick; //choix "voir ou "fouiller à afficher quand on clique sur l'objet
	private Boolean atteignable; //objet atteignable ou non
	private Item fouille; //Item optionnel qu'on récupère en fouillant l'objet

	
	public Interactive(String n, String d, Image i, Boolean c, Item f, Boolean a){//crée un nouvel objet avec échange d'objet et action
		super(i,c);
		this.fouille=f;
		this.atteignable=a;
		this.menuclick=createmenuclick();

		setId(n); // l'Id correspondra au nom de l'objet
		setAccessibleRoleDescription(d); // description de l'objet

		//vignette où s'affiche le nom
		this.etiquette = new StackPane();
		this.etiquette.setStyle("-fx-background-color: #FFFFFF");
		this.etiquette.setMouseTransparent(true);
		Text nom = new Text(n);
		nom.setTextAlignment(TextAlignment.CENTER);
		this.etiquette.setMaxSize(nom.getLayoutBounds().getWidth(),nom.getLayoutBounds().getHeight());
		this.etiquette.getChildren().add(nom);
		this.etiquette.relocate(this.getLayoutBounds().getMinX()+(this.getLayoutBounds().getWidth()/2)-(nom.getLayoutBounds().getWidth()/2)+this.getTranslateX(),this.getLayoutBounds().getMinY()+(this.getLayoutBounds().getHeight()/2)-(nom.getLayoutBounds().getHeight()/2)+this.getTranslateY());
		
		this.idProperty().addListener((observable, oldvalue, newvalue) -> {
			nom.setText(getId());
		});//update de l'étiquette si changement du nom de l'objet
		nom.textProperty().addListener((observable, oldvalue, newvalue) -> {
			updateNom();
		});//update de l'étiquette si changement du contenu de l'étiquette
		this.boundsInParentProperty().addListener((observable, oldvalue, newvalue) -> {
			updateNom();
		});//update de l'étiquette si changement de la position ou taille de l'objet
		

		//affiche le nom de l'objet au dessus de lui si la souris le touche grâce à son etiquette
		this.setOnMouseEntered(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				getLevel().getChildren().add(etiquette);
			}	
    	});
        
        //retire l'etiquette quand la souris le quitte
		this.setOnMouseExited(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
        	    getLevel().getChildren().remove(etiquette);
        	}
		});

		this.setCursor(Cursor.HAND);

		//action de base quand on clique sur un objet = afficher les choix d'action
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				actiononclick(event);
        	}
		});
		//affiche "Utiliser sur"+ le nom de l'objet au dessus de lui si la souris passe dessus en mode drag
		this.setOnMouseDragEntered(new EventHandler<MouseDragEvent>() {
			public void handle(MouseDragEvent event) {
				nom.setText("Utiliser sur "+getId());
				getLevel().getChildren().add(etiquette);
			}
    	});
        //retire l'etiquette de drag quand la souris le quitte
		this.setOnMouseDragExited(new EventHandler<MouseDragEvent>() {
			public void handle(MouseDragEvent event) {
        	    getLevel().getChildren().remove(etiquette);
        	    nom.setText(getId());
        	}
		});
		//si on relâche un Item sur cet objet, on tente une interaction
		this.setOnMouseDragReleased(new EventHandler<MouseDragEvent>() {
        	@Override
			public void handle(MouseDragEvent event) {
				getLevel().nettoie();
        	    getLevel().getChildren().remove(etiquette);
				Data.PERSO.moveonclick(event.getSceneX(),event.getSceneY(),() -> { actiononclick((Item)event.getGestureSource()); });
        	}
        });
	}
	public Interactive(String n, String d, Image i, Item f, Boolean b){
		this(n,d,i,true,f,b);
	}
	

	public ContextMenu createmenuclick(){
		ContextMenu menuclick = new ContextMenu();
		MenuItem voir = new MenuItem("Voir");
		voir.setOnAction(new EventHandler<ActionEvent>() {
    		public void handle(ActionEvent e) {
        		getLevel().commentaire(getAccessibleRoleDescription());
    		}
		});
		MenuItem fouiller = new MenuItem("Fouiller");
		fouiller.setOnAction(new EventHandler<ActionEvent>() {
    		public void handle(ActionEvent e) {
    			if(!atteignable) getLevel().commentaire("Je ne peux pas l'atteindre.");
        		else if(getFouille()!=null){
        			Game.getInventaire().addChildren(getFouille());
        			getLevel().commentaire("J'ai récupéré "+getFouille().getId()+" !");
        			setFouille(null);
        		}
        		else getLevel().commentaire("Il n'y à rien à récupérer.");
    		}
		});
		menuclick.getItems().addAll(voir,fouiller);
		return menuclick;
	}

	public void actiononclick(MouseEvent event){
		this.menuclick.show(this,event.getScreenX(),event.getScreenY());
	}

	//Ce qu'il se passe si on drop un item de l'inventaire sur cet objet
	public void actiononclick(Item i){
		Action act=this.interact.get(i);
		//si on ne met pas cette ligne, le MouseReleasedAvent étant devenu un MouseDragReleasedEvent, l'Item restera MouseTransparent et ne reviendra pas à sa place
		Event.fireEvent(i,new MouseEvent(MouseEvent.MOUSE_RELEASED, 0, 0, 0, 0, MouseButton.PRIMARY, 1, false, false, false, false,true, false, false, false, false, true, null));
		if(act!=null){
			getLevel().nettoie();
			Game.getInventaire().getChildren().remove(i);
			act.action();
		}
		else{
			if(!(this instanceof Pnj))
				getLevel().commentaire("Je ne peux pas utiliser "+i.getId()+" sur "+this.getId());
			else ((Pnj)this).parler(((Pnj)this).getDialogueObjetPasOk());
		}
	}

	public static void recoit(Item i){
		Game.getInventaire().addChildren(i);
		((Level)Game.getNiveaux().get(Game.getCurrentLevel())).commentaire("Et voilà, "+i.getId()+" !");
	}

	public Boolean getAtteignable(){
		return this.atteignable;
	}
	public void setAtteignable(Boolean b){
		this.atteignable=b;
	}
	public Item getFouille(){
		return this.fouille;
	}
	public void setFouille(Item f){
		this.fouille=f;
	}
	public HashMap<Item,Action>  getInteract(){
		return this.interact;
	}
	public StackPane getEtiquette(){
		return this.etiquette;
	}
	//appelée si modification du nom ou de la position de l'objet 
	protected void updateNom(){
		Text nom=((Text)this.etiquette.getChildren().get(0));
		this.etiquette.setMaxSize(nom.getLayoutBounds().getWidth(),nom.getLayoutBounds().getHeight());
		etiquette.relocate(this.getLayoutBounds().getMinX()+this.getLayoutX()+(this.getLayoutBounds().getWidth()/2)-(nom.getLayoutBounds().getWidth()/2)+getTranslateX(),this.getLayoutBounds().getMinY()+this.getLayoutY()+(this.getLayoutBounds().getHeight()/2)-(nom.getLayoutBounds().getHeight()/2)+getTranslateY());
	}
	protected Level getLevel(){
		return (Level)this.getParent();
	}
}