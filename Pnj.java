import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.shape.Shape;
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.scene.control.Control;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.util.Duration;
import javafx.animation.*;
import javafx.scene.image.Image;

// un Interactive qui peut "parler"
public class Pnj extends Interactive{

	private String dialogue; //ce qu'il dit quand on le fait parler
	private String dialoguefouille; //ce qu'il dit si on essaye de le fouiller
	private String dialogueobjetpasok;//si on utilise un mauvais objet dessus
	private String voix; //le nom du fichier contenant le bruit du personnage qui parle
	
	public Pnj(String n, String d, Image i, Item f, Boolean a, String dia, String diafou, String dpo,String son){
		super(n,d,i,true,f,a);
		this.dialogue=dia;
		this.dialoguefouille=diafou;
		this.dialogueobjetpasok=dpo;
		this.voix = son;
	}

	@Override
	public ContextMenu createmenuclick(){
		ContextMenu menuclick = super.createmenuclick();
		MenuItem parler = new MenuItem("Parler");
		parler.setOnAction(new EventHandler<ActionEvent>() {
    		public void handle(ActionEvent e) {
        		parler(dialogue);
    		}
		});
		menuclick.getItems().get(1).setOnAction(new EventHandler<ActionEvent>() {
    		public void handle(ActionEvent e) {
        		if(!getAtteignable()) ((Level)getParent()).commentaire("Je ne peux pas l'atteindre.");
        		else {
		    		parler(dialoguefouille);
        			if(getFouille()!=null){
        				Game.getInventaire().addChildren(getFouille());
        				((Level)getParent()).commentaire("J'ai récupéré "+getFouille().getId()+" !");
        				setFouille(null);
        			}
        		}
    		}
		});
		menuclick.getItems().add(parler);
		return menuclick;
	}

	//affiche texte au dessus de l'objet (le fait parler)
	public void parler(String s, Action act){
		try {
			Level.texte(this,s,act);
		}
		catch (NullPointerException e){}
	}
	public void parler(String s){
		parler(s, null);
	}

	public static void dialogue(ArrayList<String> texte, Pnj locuteur1, Pnj locuteur2, Action act){
		locuteur1.getParent().setMouseTransparent(true);
		String parole = texte.get(0);
		texte.remove(parole);
		if (texte.isEmpty()) locuteur1.parler(parole, act);
		else locuteur1.parler(parole, () -> {
			locuteur1.getParent().setMouseTransparent(true);
			PauseTransition pause = new PauseTransition();
			pause.setDuration(Duration.seconds(1));
			pause.setOnFinished(e -> {
				((Level)Game.getNiveaux().get(Game.getCurrentLevel())).nettoie();
				dialogue(texte, locuteur2, locuteur1,act);
			});
			pause.play();
		});
	}
	public static void dialogue(ArrayList<String> texte, Pnj locuteur1, Pnj locuteur2){
		dialogue(texte,locuteur1,locuteur2,null);
	}


	public void setDialogue(String d){
		this.dialogue=d;
	}
	public void setDialogueFouille(String d){
		this.dialoguefouille=d;
	}
	public void setDialogueObjetPasOk(String d){
		this.dialogueobjetpasok=d;
	}
	public String getDialogueObjetPasOk(){
		return this.dialogueobjetpasok;
	}
	public String getVoix(){
		return this.voix;
	}
	public void setVoix(String son){
		this.voix=son;
	}
}