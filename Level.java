import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Bounds;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Pair;
import javafx.util.Duration;
import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import java.io.File;

//Créateur d'un layout regroupant décor et objets aka un niveau
public class Level extends Pane{

	private static AnchorPane bardemenu = createmenu();
	private ArrayList<Parent> derniercom = new ArrayList<Parent>(); //garde trace des derniers ajouts inventaire, dialogue, commentaire pour pouvoir les effacer
	private ArrayList<Bounds> obstacles = new ArrayList<Bounds>(); //liste des obstacles du niveau

	// Creer le niveau à partir sa liste d'éléments et de l'image en fond (decor), ajoute ces éléments au decor
	public Level(ArrayList<ImageView> objets){
		super();
		for(ImageView x : objets){
			this.getChildren().add(x);
			if (x instanceof Objet) if (((Objet)x).getCollision()) obstacles.add(x.getBoundsInParent());;
		}
		this.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				if(event.isStillSincePress()){
					nettoie();
					Data.PERSO.moveonclick(event.getSceneX(),event.getSceneY(),null);
				}
        	}
		});//vire le dernier commentaire ajouté ou l'inventaire quand on clique sur le decor
	}

	public void nettoie(){
        for (Parent x : derniercom)
        	this.getChildren().remove(x);
        derniercom = new ArrayList<Parent>();
	}

	public static AnchorPane createmenu(){
		AnchorPane menu = new AnchorPane();
		menu.setMaxHeight(30);
		menu.setMinHeight(30);
		menu.setMinWidth(1067);
		menu.setStyle("-fx-background-color: #000088");
		menu.setPadding(new Insets(10));
		menu.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderStroke.DEFAULT_WIDTHS)));
		Button pause = new Button("Menu");
		pause.setOnAction(e -> {
				Game.loadLevel(0);//retour au menu principal (sans changer le currentlevel)
		});
		Button inventaire= new Button("Inventaire");
		inventaire.setOnAction(new EventHandler<ActionEvent>() {
 			@Override
    		public void handle(ActionEvent e) {
				if(!Game.getNiveaux().get(Game.getCurrentLevel()).getChildren().contains(Game.getInventaire())){
					Game.getNiveaux().get(Game.getCurrentLevel()).getChildren().add(Game.getInventaire());
					((Level)Game.getNiveaux().get(Game.getCurrentLevel())).getDernierCom().add(Game.getInventaire());
				}
				else ((Level)Game.getNiveaux().get(Game.getCurrentLevel())).nettoie();
			}
		});
		menu.getChildren().addAll(inventaire,pause);
		menu.setLeftAnchor(inventaire,0.0);
		menu.setRightAnchor(pause,0.0);
		return menu;
	}

		//affiche texte en haut du jeu (description et pensée du joueur)
	public static void texte(Node node, String s, Action act){
		AudioClip son;
		Level lvl;
		if (node instanceof Level){
			lvl= (Level)node;
			son = new AudioClip(new File("Ressources/Sons/TYPEWRITER.wav").toURI().toString());
		}else{
			lvl = ((Level)node.getParent());
			son = new AudioClip(new File("Ressources/Sons/"+((Pnj)node).getVoix()).toURI().toString());
		}
		lvl.setMouseTransparent(true);
		son.setCycleCount(AudioClip.INDEFINITE);
		StackPane affiche = new StackPane(); // vignettes par défaut qui contiendra du texte à afficher sur les niveaux
		affiche.setStyle("-fx-background-color: #FFFFFF");
		affiche.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderStroke.THIN)));
		affiche.setMouseTransparent(true);
		Text detail = new Text(s);
		detail.setTextAlignment(TextAlignment.CENTER);
		affiche.setMaxSize(detail.getLayoutBounds().getWidth()+4,detail.getLayoutBounds().getHeight()+4);
		affiche.getChildren().add(detail);
		detail.setText("");
		lvl.derniercom.add(affiche);
		if (node instanceof Level){
			if (!lvl.getChildren().contains(Game.getInventaire()))
				affiche.relocate(lvl.getLayoutBounds().getWidth(),40);
			else 
				affiche.relocate(lvl.getLayoutBounds().getWidth()/2,35+Game.getInventaire().getLayoutBounds().getHeight());
			detail.layoutBoundsProperty().addListener((observable, oldvalue, newvalue) -> {
				affiche.setLayoutX(lvl.getLayoutBounds().getWidth()/2-(newvalue.getWidth()/2));
			}); //met à jour le texte chaque fois qu'il grandit
			lvl.getChildren().add(affiche);
		}
		else{
			affiche.relocate(node.getLayoutBounds().getMinX()+(node.getLayoutBounds().getWidth()/2)+node.getTranslateX()-(detail.getLayoutBounds().getWidth()/2),node.getLayoutBounds().getMinY()+node.getTranslateY()+((node.getLayoutBounds().getHeight()-(node.getLayoutBounds().getHeight()*node.getScaleY()))/2)-detail.getLayoutBounds().getHeight());
			((Level)node.getParent()).getChildren().add(affiche);
			((Level)node.getParent()).getDernierCom().add(affiche);
		}
		son.play();
		// la création de cette transition est reprise et adaptée de la doc Oracle de la class Transition
		Animation type = new Transition(){
			final private int length = s.length();
			{
       			setCycleDuration(Duration.millis(s.length()*50)); // 0.05 sec / lettre
    		}
			protected void interpolate(double frac) {
       			final int n = Math.round(length * (float) frac);
       			detail.setText(s.substring(0, n));
       			if (node instanceof Pnj){
       				double x=node.getLayoutBounds().getMinX()+node.getLayoutX()+(node.getLayoutBounds().getWidth()/2)+node.getTranslateX()-(detail.getLayoutBounds().getWidth()/2);
       				if(x<0) x=0.0;
       				if (x>Game.WIDTH-detail.getLayoutBounds().getWidth()) x=Game.WIDTH-detail.getLayoutBounds().getWidth();
       				double y=node.getLayoutBounds().getMinY()+node.getLayoutY()+node.getTranslateY()+((node.getLayoutBounds().getHeight()-(node.getLayoutBounds().getHeight()*node.getScaleY()))/2)-detail.getLayoutBounds().getHeight();
       				if (y<0) y=0.0;
       				if (y>Game.HEIGHT-detail.getLayoutBounds().getHeight()) y=Game.HEIGHT-detail.getLayoutBounds().getHeight();
       				//Met à jour le texte quand il grandit en fonction de la position du personnage (qui peut bouger).
       				//Si le texte dépasse le cadre du jeu, il se met de manière à ne pas le faire.
       				affiche.relocate(x,y);
       			}
    		}
		};
		if (act!=null) type.setOnFinished(e -> {
			son.stop();
			lvl.setMouseTransparent(false);
			act.action();
		});
		else type.setOnFinished(e -> {
			lvl.setMouseTransparent(false);
			son.stop();
		});
		type.play();
	}
	public void commentaire(String s, Action act){
		texte(this,s,act);
	}
	public void commentaire(String s){
		commentaire(s,null);
	}
	public ArrayList<Parent> getDernierCom(){
		return this.derniercom;
	}
	public void setDernierCom(ArrayList<Parent> c){
		this.derniercom=c;
	}
	public static AnchorPane getBarDeMenu(){
		return bardemenu;
	}
	public ArrayList<Bounds> getObstacles(){
		return this.obstacles;
	}
	public void setObstacles(ArrayList<Bounds> o){
		this.obstacles=o;
	}
}