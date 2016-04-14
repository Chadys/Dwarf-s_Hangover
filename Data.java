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

//Énumération du contenu du jeu, niveaux, décors, objets et personnage
public abstract class Data{

	//DECORS//

	private static final ImageView decor1(){
		ImageView cachot = new ImageView(new Image("/Ressources/level1/cachot.png"));
		return cachot;
	}
	private static final Objet murhaut(){
		Objet murhaut = new Objet(new Image("/Ressources/level1/murhaut.png"),true);
		return murhaut;
	}
	private static final Objet grillebas(){
		Objet grillebas = new Objet(new Image("/Ressources/level1/grillebas.png"),true);
		return grillebas;
	}
	private static final Objet grilledroite(){
		Objet grilledroite = new Objet(new Image("/Ressources/level1/grilledroite.png"),true);
		return grilledroite;
	}
	private static final Objet table(){
		Objet table = new Objet(new Image("/Ressources/level1/table.png"),true);
		return table;
	}
	private static final ImageView gigot(){
		ImageView gigot = new ImageView(new Image("/Ressources/level1/bouffe.png"));
		gigot.setMouseTransparent(true);
		return gigot;
	}
	private static final ImageView decor2(){
		ImageView rue = new ImageView(new Image("/Ressources/level2/rue.png"));
		return rue;
	}
	private static final Objet murdroite(){
		Objet murdroite = new Objet(new Image("/Ressources/level2/murdroite.png"),true);
		return murdroite;
	}
	private static final Objet murgauche(){
		Objet murgauche = new Objet(new Image("/Ressources/level2/murgauche.png"),true);
		return murgauche;
	}
	private static final Objet etalage(){
		Objet etalage = new Objet(new Image("/Ressources/level2/etalage.png"),true);
		return etalage;
	}
	private static final ImageView herbe(){
		ImageView herbe = new ImageView(new Image("/Ressources/level2/herbe.png"));
		return herbe;
	}
	private static final Objet toit(){
		Objet toit = new Objet(new Image("/Ressources/level2/toit.png"),false);
		toit.setMouseTransparent(true);
		return toit;
	}

	private static final ImageView CACHOT = decor1();
	private static final Objet MURHAUT = murhaut();
	private static final Objet GRILLEBAS = grillebas();
	private static final Objet GRILLEDROITE = grilledroite();
	private static final Objet TABLE = table();
	private static final ImageView GIGOT = gigot();
	private static final ImageView REBORD = new ImageView();
	
	private static final ImageView RUE = decor2(); 
	private static final Objet MURDROITE = murdroite();
	private static final Objet MURGAUCHE = murgauche();
	private static final Objet ETALAGE = etalage();
	private static final ImageView HERBE = herbe(); 
	private static final Objet TOIT = toit();


	//ITEMS//

	private static final Item choppe(){// Objet du début, la choppe de bière
		Item choppe = new Item ("une choppe", "Si seulement elle était encore pleine...", new Image("/Ressources/level1/choppe.png"));
		return choppe;
	}
	private static final Item choppepisse(){
		Item choppepisse = new Item ("une choppe\nde pisse", "Elle est pleine pour le coup... mais je ne boirais quand même pas ça.", new Image("/Ressources/level1/choppepisse.png"));
		return choppepisse;
	}
	private static final Item femur(){
		Item femur = new Item ("un os", "Pourquoi j'ai ramassé ça moi...", new Image("/Ressources/level1/os.png"));
		return femur;
	}

	private static final Item branche(){
		Item baton = new Item ("une branche", "Ça peut toujours servir.", new Image("/Ressources/level1/branche.png"));
		return baton;
	}
	private static final Item bouffe(){
		Item bouffe = new Item ("une cuisse\nde sanglier", "*Grrblrr* Miam...", new Image("/Ressources/level1/bouffe.png"));
		return bouffe;
	}
	private static final Item pagne(){
		Item pagne = new Item ("un pagne", "Pouah l'odeur ! Mais s'il le faut...", new Image("/Ressources/level1/pagne.png"));
		return pagne;
	}
	private static final Item totem(){
		Item totem = new Item ("un objet étrange", "Ça ressemble à un totem bizarre, je me demande bien ce qu'il fait.", new Image("/Ressources/level2/totem.png"));
		totem.setFitHeight(75);
		totem.setPreserveRatio(true);
		return totem;
	}
	public static final Item CHOPPE = choppe();
	public static final Item CHOPPEDEPISSE = choppepisse();
	public static final Item OS = femur();
	public static final Item BRANCHE = branche();
	public static final Item BOUFFE = bouffe();
	public static final Item PAGNE = pagne();
	public static final Item TOTEM = totem();


	//PERSO//
	private static final Perso perso(){
		Perso perso = new Perso("", "Eh oui c'est moi, le personnage principal de ce jeu,\n je parie que tu t'attendais pas à un charisme si magistral hein!", null, true, "Je crois que j'ai la gerbe.", "T'as qu'à regarder l'inventaire trouduc' !", "Mais t'es con ou quoi ?!", "NAIN.wav", "Censure");
		Action pagneact = () -> {
			perso.tenueProperty().set("un pagne");
			perso.setApparence("Pagne");
			perso.setImage(perso.getFace().get(0));
			perso.parler("...C'est mieux que rien...\nMais j'ai quand même l'air très con.");
		};
		perso.getInteract().put(PAGNE,pagneact);
		Action totemact = () -> {
			Game.censureProperty().set(!Game.censureProperty().get());
			perso.parler("?!? C'est sensé faire quelque chose cette merde ?");
			Game.getInventaire().getChildren().add(TOTEM);
		};
		perso.getInteract().put(TOTEM,totemact);
		Action bouffact = () -> {
			perso.parler("Je préfère garder ça pour plus tard.\nOn sait jamais.");
			Game.getInventaire().getChildren().add(BOUFFE);
		};
		perso.getInteract().put(BOUFFE,bouffact);
		return perso;
	}
	public static Perso PERSO = perso();


	//OBJETS//
		//Objets niveau 1//
	
	private static final Exit trou(){
		Exit trou = new Exit ("", "", null, false, () -> {
			if (PERSO.tenueProperty().get()=="nu") PERSO.parler("Pas que je sois franchement contre la nudité mais...\nj'ai pas trop envie de me trouver rebalancé ici de sitôt.");
			else {
				Game.loadLevel(2);
				LEVEL2.getChildren().remove(TOIT);
				LEVEL2.getChildren().add(TOIT); //pour que le toit apparaisse au dessus du personnage
        		PERSO.setTranslateX(0);
        		PERSO.setTranslateY(0);
				PERSO.relocate(330,Game.HEIGHT/2);
				if (Game.getEnding()) return; //évite une répétition de tout ce qui suit
				AudioClip son = new AudioClip(new File("Ressources/Sons/VICTORY.wav").toURI().toString());
				PERSO.moveonclick(PERSO.getLayoutX()+PERSO.getTranslateX()+(PERSO.getLayoutBounds().getWidth()/2),-50+(PERSO.getLayoutY()+PERSO.getTranslateY()+(PERSO.getLayoutBounds().getHeight()/2)),() -> {
					son.play();
					PERSO.setImage(PERSO.getFace().get(0));
					ArrayList<String> dialogue = new ArrayList<String>();
					dialogue.add("Eh beh, t'as pris ton temps !\nENFIN À MOI LA LIBERTÉ !!!");
					dialogue.add("Oui, oui, t'as bien compris trouduc,\nt'as fini le jeu... pour le moment.");
					dialogue.add("Pas si mal pour un cerveau d'elfe !");
					dialogue.add("Si tu veux continuer de glander ici,\ny'a sûrement encore une surprise à trouver.");
					dialogue.add("Enfin moi je dis ça comme ça hein...\nC'est peut-être juste pour te voir t'emmerder à chercher pour rien !");
					dialogue.add("Aller à plus l'anus !");
					Pnj.dialogue(dialogue,PERSO, PERSO, null);
					Game.setEnding(true);
				});
			}
		});
		return trou;
	}
	private static final Interactive tasdepaille(){
		Interactive tasdepaille = new Interactive ("un tas de Paille", "Cette paille semble bien usée.", new Image("/Ressources/level1/paille.png"), BRANCHE,false);
		return tasdepaille;
	}
	private static final Interactive seaudepisse(){
		Interactive seaudepisse = new Interactive ("un seau", "Un seau pour se soulager... Il a déjà servi.", new Image("/Ressources/level1/seau.png"), null,true);
		return seaudepisse;
	}
	private static final Interactive squelette(){
		Interactive squelette = new Interactive ("un squelette", "Il est resté là un moment lui. Je vais l'appeler Monticulus", new Image("/Ressources/level1/squelette.png"), false, OS,true);
		return squelette;
	}
	private static final Pnj barbareenpagne(){
		Pnj barbare = new Pnj ("un barbare", "Sexe indéterminé, ça n'a l'air ni intelligent ni commode. Un barbare quoi.", new Image("/Ressources/level1/barbare.png"), null,true,"Dégage !","Touche pas moi sinon je cogne toi !","Moi pas vouloir.", "BARBARE.wav");
		return barbare;
	}
	private static final Pnj ratmutant(){
		Pnj ratmutant = new Pnj ("un rat", "Ce rat me regarde bizarrement.", new Image("/Ressources/level1/rat.png"),null,true,"Couiiic !","Couiiiiiiicouic ><","Couic :(","SOURIS.wav");
		ratmutant.setScaleX(0.25);
		ratmutant.setScaleY(0.25);
		return ratmutant;
	}
	private static final Interactive assiette(){
		Interactive assiette = new Interactive ("une assiette", "Sûrement la nourriture des gardes. Si près et pourtant si loin...", new Image("/Ressources/level1/assiette.png"),null,false);
		return assiette;
	}
	private static final Pnj chiendegarde(){
		Pnj chiendegarde = new Pnj ("un chien", "Le chien de garde. Il dort.", new Image("/Ressources/level1/chiendort.png"),null,false,"zzZZZ","Grrrr","ZZzZzZz","CHIENSNORE.wav");
		return chiendegarde;
	}
	private static final Exit sortie(){
		Exit sortie = new Exit ("La porte", "Évidemment, elle est vérouillée.", new Image("/Ressources/level1/porte.png"),false,0,new Pair<Double,Double>(0.0,0.0));
		return sortie;
	}

		//Interactions//
	private static final Interactive seau(){
		Action seauact = () -> PERSO.parler("Bon puisqu'il le faut...\nMa pauvre choppe.",() -> Interactive.recoit(CHOPPEDEPISSE));
		Interactive seau = seaudepisse();
		seau.getInteract().put(CHOPPE,seauact);
		return seau;
	}

	private static final Pnj chien(){
		Pnj chien = chiendegarde();
		Action chienact = () -> {
			ArrayList<String> dialogue = new ArrayList<String>();
			dialogue.add("Si je te donne le nonos,\ntu va m'aider à sortir hein ?");
			dialogue.add("Zzz...!?!!");
			Action act = () -> {
				chien.setImage(new Image("/Ressources/level1/chien.png"));
				chien.setVoix("CHIEN.wav");
				((Level)chien.getParent()).nettoie();
				PauseTransition rotatechien = new PauseTransition();
				rotatechien.setDuration(Duration.seconds(1));
				rotatechien.setOnFinished(e -> chien.setImage(new Image("/Ressources/level1/chiendos.png")));
				TranslateTransition translatechien= new TranslateTransition();
				translatechien.setToY(-150);
				translatechien.setDuration(Objet.tempsDeTrajet(0,-150));
				translatechien.setOnFinished(e -> chien.setImage(new Image("/Ressources/level1/chien.png")));
				TranslateTransition translatechien2= new TranslateTransition();
				translatechien2.setToX(-75);
				translatechien2.setDuration(Objet.tempsDeTrajet(-75,0));
				translatechien2.setOnFinished(new EventHandler<ActionEvent>() {
 					@Override
    				public void handle(ActionEvent e) {
						ArrayList<String> dialogue2 = new ArrayList<String>();
						dialogue2.add("*soupir* Et tu t'attendais à quoi ?\nQu'il t'ouvre la porte ?");
						dialogue2.add("Wouaf !");
						Pnj.dialogue(dialogue2, PERSO,chien);
						chien.setAccessibleRoleDescription("Le chien de garde. Il ne dort plus, mais il n'a pas l'air plus efficace pour autant.");
						chien.setAtteignable(true);
						chien.setDialogue("Woof !");
						chien.setDialogueObjetPasOk("Grrrr");
					}
				});
				SequentialTransition animationchien = new SequentialTransition(rotatechien,translatechien,translatechien2);
				animationchien.setNode(chien);
				animationchien.play();
			};
			Pnj.dialogue(dialogue, PERSO,chien, act);
		};
		chien.getInteract().put(OS,chienact);
		return chien;
	}

	private static final Interactive plat(){
		PERSO.parler("NOURRITURE OUIII");
		Interactive assiette=assiette();
		Action bouffact = () -> {
			Interactive.recoit(BOUFFE);
			assiette.setAccessibleRoleDescription("Aussi vide que mon ventre...");
			((Level)GIGOT.getParent()).getChildren().remove(GIGOT);
		};
		assiette.getInteract().put(BRANCHE,bouffact);
		return assiette;
	}

	private static final Pnj barbare(){
		Pnj barbare=barbareenpagne();
		Action barbaract = () -> {
			ArrayList<String> dialogue = new ArrayList<String>();
			dialogue.add("Tiens brave barbare,\nde la bonne bière pour toi !");
			dialogue.add("Oh bière ! Copain !");
			dialogue.add("...");
			dialogue.add("Uhhh");
			Action act = () -> {
				LEVEL1.setMouseTransparent(true);
				barbare.setImage(new Image("/Ressources/level1/barbaredort.png"));
				Game.getInventaire().addChildren(CHOPPE);
				PAILLE.setAtteignable(true);
				barbare.setAccessibleRoleDescription("Il dort. C'est beaucoup mieux comme ça.");
				barbare.setFouille(PAGNE);
				barbare.setAtteignable(true);
				barbare.getInteract().remove(CHOPPEDEPISSE);
				barbare.setDialogue("Ron pshiiii");
				barbare.setDialogueFouille("RRbglll...");
				barbare.setDialogueObjetPasOk("...gnohblrrr...");
				barbare.setVoix("BARBARESNORE.wav");
				PauseTransition pause = new PauseTransition();
				pause.setDuration(Duration.seconds(1));
				pause.setOnFinished(e -> {
					LEVEL1.nettoie();
					PERSO.parler("... ouf il est tombé dans le panneau,\nvraiment con ce barbare.");
				});
				pause.play();
			};
			Pnj.dialogue(dialogue,PERSO, barbare,act);
		};
		barbare.getInteract().put(CHOPPEDEPISSE,barbaract);
		return barbare;
	}

	private static final Pnj rat(){
		Pnj rat = ratmutant();
		Action ratact = () -> {
			((Level)rat.getParent()).commentaire("Bon juste un petit bout...", () -> rat.parler("Couic <3"));
			PauseTransition pause = new PauseTransition();
			pause.setDuration(Duration.seconds(2.5));
			pause.setOnFinished(new EventHandler<ActionEvent>() {
				@Override
 				public void handle(ActionEvent e) {
					((Level)rat.getParent()).nettoie();
					PERSO.parler("C'est quoi ce bordel !?!", () -> ((Level)rat.getParent()).nettoie());
				}
			});
			ScaleTransition scale = new ScaleTransition();
			scale.setToX(1);
			scale.setToY(1);
			TranslateTransition translate1 = new TranslateTransition();
			translate1.setToX(-100);
			translate1.setDuration(Objet.tempsDeTrajet(-200,0));
			translate1.setOnFinished(e -> rat.setImage(new Image("/Ressources/level1/ratdos.png")));
			TranslateTransition translate2 = new TranslateTransition();
			translate2.setToY(-400);
			translate2.setDuration(Objet.tempsDeTrajet(0,-400));
			translate2.setOnFinished(new EventHandler<ActionEvent>() {
 				@Override
    			public void handle(ActionEvent e) {
    				AudioClip son = new AudioClip(new File("Ressources/Sons/EXPLODE.wav").toURI().toString());
					son.play();
					MUR.setImage(new Image("/Ressources/level1/trou.png"));
					MUR.setId("Sortie");
					MUR.ouvrir();
					REBORD.setImage(new Image("/Ressources/level1/rebord.png"));
				}
			});
			TranslateTransition translate3 = new TranslateTransition();
			translate3.setByY(-500);
			translate3.setDuration(Objet.tempsDeTrajet(0,-200));
			translate3.setOnFinished(new EventHandler<ActionEvent>() {
 				@Override
    			public void handle(ActionEvent e) {
					PERSO.parler("...Eh merde... il a tout bouffé ce con !");
				}
			});
			SequentialTransition animationrat = new SequentialTransition(pause,scale,translate1,translate2,translate3);
			animationrat.setNode(rat);
			animationrat.play();
		};
		rat.getInteract().put(BOUFFE,ratact);
		return rat;
	}

		//Objet niveau 2//

	private static final Exit prison(){
		Exit trou = new Exit ("Prison", "", TABLE.getImage(), true, 1, new Pair<Double,Double>(380.0,MURHAUT.getLayoutBounds().getHeight()-30));
		return trou;
	}
	private static final Interactive easteregg(){
		return new Interactive ("un objet étrange", "Ça ressemble à un totem bizarre, je me demande bien ce qu'il fait.", TOTEM.getImage(),false,TOTEM,true);
	}

	public static Exit MUR = trou();
	public static Interactive PAILLE = tasdepaille();
	public static Interactive SEAU = seau();
	public static Interactive SQUELETTE = squelette();
	public static Pnj BARBARE = barbare();
	public static Pnj RAT = rat();
	public static Interactive ASSIETTE = plat();
	public static Pnj CHIEN = chien();
	public static Exit PORTE = sortie();

	public static Exit PRISON = prison();
	public static Interactive EASTEREGG = easteregg();


		//Niveaux//

	private static final Level level1(){
		ArrayList<ImageView> contenu = new ArrayList<ImageView>();
		contenu.add(CACHOT);
		CACHOT.setFitHeight(Game.HEIGHT);
		contenu.add(MURHAUT);
		contenu.add(MUR);
		MUR.relocate(285,0);
		contenu.add(TABLE);
		TABLE.relocate(800,180);
		contenu.add(PAILLE);
		PAILLE.relocate(30,370);
		contenu.add(SEAU);
		SEAU.relocate(90,110);
		contenu.add(SQUELETTE);
		SQUELETTE.relocate(480,250);
		contenu.add(BARBARE);
		BARBARE.relocate(30,370);
		contenu.add(RAT);
		RAT.relocate(450,480);
		contenu.add(ASSIETTE);
		ASSIETTE.relocate(830,190);
		contenu.add(CHIEN);
		CHIEN.relocate(880,600);
		contenu.add(GIGOT);
		GIGOT.relocate(830,190);
		contenu.add(REBORD);
		REBORD.relocate(285,0);
		contenu.add(GRILLEDROITE);
		GRILLEDROITE.setFitHeight(Game.HEIGHT);
		GRILLEDROITE.relocate(700,0);
		contenu.add(GRILLEBAS);
		GRILLEBAS.relocate(0,Game.HEIGHT-GRILLEBAS.getLayoutBounds().getHeight());
		contenu.add(PORTE);
		PORTE.relocate(270,Game.HEIGHT-PORTE.getLayoutBounds().getHeight());
		Level level = new Level (contenu);
		return level;
	}

	private static final Level level2(){
		ArrayList<ImageView> contenu = new ArrayList<ImageView>();
		contenu.add(RUE);
		RUE.setFitHeight(Game.HEIGHT);
		contenu.add(MURDROITE);
		MURDROITE.setFitHeight(Game.HEIGHT);
		MURDROITE.relocate(800,0);
		contenu.add(MURGAUCHE);
		MURGAUCHE.setFitHeight(Game.HEIGHT);
		contenu.add(EASTEREGG);
		EASTEREGG.relocate(830,350);
		contenu.add(ETALAGE);
		ETALAGE.relocate(680,0);
		contenu.add(HERBE);
		contenu.add(TOIT);
		TOIT.relocate(0,Game.HEIGHT-TOIT.getLayoutBounds().getHeight());
		contenu.add(PRISON);
		PRISON.relocate(330,Game.HEIGHT-PRISON.getLayoutBounds().getHeight());
		Level level = new Level (contenu);
		return level;
	}

	public static Level LEVEL1 = level1();
	public static Level LEVEL2 = level2();
}