import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.*;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import javafx.scene.Parent;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import java.util.ArrayList;
import javafx.util.Duration;
import javafx.animation.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Group;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.media.AudioClip;
import java.io.File;

public final class Game extends Application{
	private static ArrayList<Pane> niveaux=new ArrayList<Pane>(); // Liste de chaque niveau, correspond à l'index (index 0 = menu, index 1 = level 1 etc)
	private static int currentlevel=0; //jamais 0 sauf au lancement d'une partie
	private static Stage primarystage;
	private static Inventaire inventaire=new Inventaire();
	private static SimpleBooleanProperty censure = censureProperty();
	private static Boolean ending=false;
	private static AudioClip son = new AudioClip(new File("Ressources/Sons/HYMNE.wav").toURI().toString());
	public static final double WIDTH=1067;
	public static final double HEIGHT=750;


	@Override
	public void start(Stage jeu){
		jeu.setResizable(false);
		jeu.setTitle("A Dwarf's Hangover");
		primarystage=jeu;
		niveaux.add(menuprincipal());//rempli la liste des niveaux disponible avec le menu
		niveaux.add(Data.LEVEL1); //rempli la liste des niveaux disponible avec le niveau 1
		niveaux.add(Data.LEVEL2); //rempli la liste des niveaux disponible avec le niveau 2
		son.setCycleCount(AudioClip.INDEFINITE);
		son.setVolume(0.8);
		son.play();

		Scene scenejeu = new Scene(niveaux.get(0),WIDTH, HEIGHT);
		jeu.setScene(scenejeu); // met le menu dans la fenêtre principale
		jeu.show();
	}


	// Menu du jeu, avec 3 boutons "nouvelle partie", "règles" et "quitter"//
	public StackPane menuprincipal(){
        StackPane root = new StackPane();
		/* Stop[] stops = new Stop[] { new Stop(0, Color.BLUE), new Stop(1, Color.RED)};
		LinearGradient lg = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
		root.setBackground(new Background(new BackgroundFill(lg,null, new Insets(0))));*/
		//Voir ligne du dessous, fait la même chose
		root.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 100%, #6872FF, #000762)");
		
		VBox menu = new VBox();
		menu.setPadding(new Insets(50,0,100,0)); // marge vbox/elements
		menu.setSpacing(20);// marge inter-elements
		menu.setStyle("-fx-background-color: rgb(150,150,255,0.4)");
		menu.setAlignment(Pos.CENTER);
		menu.setMaxSize(350,500);
		menu.setBorder(new Border(new BorderStroke(Color.BLACK,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderStroke.DEFAULT_WIDTHS)));
		Button buttonNew = new Button("Nouvelle Partie");
		buttonNew.setPrefHeight(50);
		buttonNew.setFont(Font.font("Futura", FontWeight.BOLD, 24));
		buttonNew.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	if(Data.PERSO.getId()!="") confirmpopup(); //si une partie est déjà en cours, l'utilisateur devra validé pour en commencer une nouvelle
            	if(Data.PERSO.getId()=="") ouvrirlogin();
            }
        });
        Button buttonContinue = new Button("Continuer");
		buttonContinue.setFont(Font.font("Futura", FontWeight.BOLD, 24));
		buttonContinue.setPrefHeight(35);
		buttonContinue.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	son.stop();
            	loadLevel(currentlevel);
            }
        });
        buttonContinue.setDisable(true);
		Button buttonRules = new Button("Règles");
		buttonRules.setFont(Font.font("Futura", FontWeight.BOLD, 20));
		buttonRules.setPrefHeight(20);
		buttonRules.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	rules();
            }
        });
		Button buttonQuit = new Button("Quitter");
		buttonQuit.setFont(Font.font("Futura", FontWeight.BOLD, 20));
		buttonQuit.setPrefHeight(20);
		buttonQuit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
            	Platform.exit();
            }
        });
		menu.getChildren().addAll(buttonNew, buttonContinue, buttonRules, buttonQuit);
		Text disclaimer = new Text("© Julie Rymer 2015\nVersion 0.2");
		disclaimer.setTextAlignment(TextAlignment.RIGHT);
		Image im1 = new Image("/Ressources/marche.png");
		Image im2 = new Image("/Ressources/marche1.png");
		Image im3 = new Image("/Ressources/bois.png");
		Image im4 = new Image("/Ressources/bois1.png");
		Image im5 = new Image("/Ressources/marchepagne.png");
		Image im6 = new Image("/Ressources/marche1pagne.png");
		Image im7 = new Image("/Ressources/boispagne.png");
		ImageView marche = new ImageView(im1);
        marche.setFitWidth(400);
        marche.setPreserveRatio(true);
		ImageView bois = new ImageView(im3);
        bois.setFitWidth(425);
        bois.setPreserveRatio(true);
		Rectangle cache = new Rectangle(80,50,Color.BLACK);
		ImageView marchehabit = new ImageView();
        marchehabit.setFitWidth(204);
        marchehabit.setPreserveRatio(true);
		ImageView boishabit = new ImageView();
        boishabit.setFitWidth(205);
        boishabit.setPreserveRatio(true);
		this.censure.addListener((observable, oldvalue, newvalue) -> {
			if(!newvalue)cache.setFill(null);
			else if (marchehabit.getImage()==null) cache.setFill(Color.BLACK);;
		});
        Data.PERSO.tenueProperty().addListener((observable, oldvalue, newvalue) -> {
        	if (newvalue=="un pagne"){
        		marchehabit.setImage(im5);
        		boishabit.setImage(im7);
        		cache.setFill(null);
        	} else {
        		marchehabit.setImage(null);
        		boishabit.setImage(null);
        		if (censure.get()) cache.setFill(Color.BLACK);
        	}
        });

		root.getChildren().addAll(menu,marche,bois,disclaimer,cache,boishabit,marchehabit);
		root.setAlignment(Pos.CENTER_LEFT);
		root.setAlignment(menu,Pos.CENTER);
		root.setAlignment(bois,Pos.CENTER_RIGHT);
		root.setAlignment(boishabit,Pos.CENTER_RIGHT);
		root.setAlignment(disclaimer,Pos.BOTTOM_RIGHT);
		root.setPadding(new Insets(0,0,0,10));
		root.setMargin(disclaimer,new Insets(0,5,5,0));
		root.setMargin(cache,new Insets(350,0,0,160));
		root.setMargin(marchehabit,new Insets(355,0,0,98));
		root.setMargin(boishabit,new Insets(340,110,0,0));
		AnimationTimer anim = new AnimationTimer() {
			int i = 1;
			public void handle(long now){
				switch(i){
					case 1 : 
						marche.setImage(im1);
						if(marchehabit.getImage()!=null) marchehabit.setImage(im5);
						bois.setImage(im3);
						i++;
						break;
					case 10 : 
						marche.setImage(im2);
						if(marchehabit.getImage()!=null) marchehabit.setImage(im6);
						bois.setImage(im4);
        				marche.setScaleX(1);
        				marchehabit.setScaleX(1);
						i++;
						break;
					case 20 : 
						marche.setImage(im1);
						if(marchehabit.getImage()!=null) marchehabit.setImage(im5);
						bois.setImage(im3);
						i++;
						break;
					case 30 : 
						marche.setImage(im2);
						if(marchehabit.getImage()!=null) marchehabit.setImage(im6);
						bois.setImage(im4);
        				marche.setScaleX(-1);
        				marchehabit.setScaleX(-1);
						i++;
						break;
					case 40 : 
						i=1;
						break;
					default :
						i++;
				}
			}
		};
		anim.start();
		return root;
	}


	// Ouvre une nouvelle fenêtre pour choisir le nom du personnage //
	public void ouvrirlogin(){
		Stage login = new Stage();
		login.initStyle(StageStyle.UTILITY);
		login.setTitle("Nouveau personnage");
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25));
		Label username = new Label("Veuillez choisir le nom de votre personnage :");
		grid.add(username,0,0);
		TextField nom = new TextField("Bjarnok");
		grid.add(nom,0,1);
		Button enter = new Button("Valider");
		HBox hbenter = new HBox(10);
		Text invalide = new Text();
		invalide.setFill(Color.FIREBRICK);
        grid.add(invalide, 0,4);
		hbenter.setAlignment(Pos.CENTER_RIGHT);
		hbenter.getChildren().addAll(invalide,enter);
		grid.add(hbenter,0,3);
		EventHandler<ActionEvent> validation = (ActionEvent event) -> {
			//Vérifie qu'un nom a bien été entré, si oui enregistre le nom et démarre le jeu
			String n = nom.getText();
			if(n.length() == 0)
				invalide.setText("Écrivez quelque chose !");
			else{
				Data.PERSO.setId(n);
				login.close();
			}
		};
        enter.setOnAction(validation);
		nom.setOnAction(validation);
		nom.setOnKeyTyped(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent e) {
				// Empèche le joueur de mettre un nom trop long
				if(nom.getLength() > 15){
					nom.deleteText(15,16);
					invalide.setText("Trop long !");
				}
				else invalide.setText("");
			}
		});
		Scene scenelogin = new Scene(grid);
		login.setScene(scenelogin);
		login.initModality(Modality.APPLICATION_MODAL); // Fait attendre les autres fenêtres de l'appli tant que celle-là n'a pas fini
		login.showAndWait();
		if(Data.PERSO.getId()!=""){ //si un nom a été entré, démarrage du jeu et activation du bouton Continue
			((VBox)niveaux.get(0).getChildren().get(0)).getChildren().get(1).setDisable(false);
			son.stop();
			demarrejeu();
		}
	}


	// Popup qui demande confirmation au lancement d'une nouvelle partie s'in y en a déjà une en cours //
	public void confirmpopup(){
		Stage confirm = new Stage();
		confirm.initStyle(StageStyle.UNDECORATED);
		GridPane grid = new GridPane();
		for(int i=0;i<4;i++)
			grid.getColumnConstraints().add(new ColumnConstraints(100));
     	grid.setAlignment(Pos.CENTER);
		//grid.setGridLinesVisible(true);
		//grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25));
		Label info = new Label("Une partie est déjà en cours.");
		Label sur = new Label("L'écrasement n'est pas encore au point.");
		Label yolo = new Label("Veux-tu changer le nom du personnage ?");
		grid.add(info,1,0,2,1);
		grid.add(sur,0,1,GridPane.REMAINING,1);
		grid.add(yolo,0,2,GridPane.REMAINING,1);
		Button non = new Button("Non");
		Button oui = new Button("Oui");
        grid.add(non, 1,3);
        grid.add(oui, 2,3);
        for (Node x : grid.getChildren())
			grid.setHalignment(x,HPos.CENTER);
        non.setOnAction(new EventHandler<ActionEvent>() {
 			@Override
    		public void handle(ActionEvent e) {
				confirm.close();
			}
		});
		oui.setOnAction(new EventHandler<ActionEvent>() {
 			@Override
    		public void handle(ActionEvent e) {
    			Data.PERSO.setId("");
				confirm.close();
			}
		});
		Scene sceneconfirm = new Scene(grid);
		confirm.setScene(sceneconfirm);
		confirm.initModality(Modality.APPLICATION_MODAL);
		confirm.showAndWait();
	}

	public static void loadLevel(int lvl){
		primarystage.getScene().setRoot(niveaux.get(lvl));
		if (lvl>0){
			currentlevel=lvl;

			if(!niveaux.get(lvl).getChildren().contains(Data.PERSO)) niveaux.get(lvl).getChildren().add(Data.PERSO);
			if(!niveaux.get(lvl).getChildren().contains(Level.getBarDeMenu())) niveaux.get(lvl).getChildren().add(Level.getBarDeMenu());
		}
		else son.play();
	}
	
	private static void demarrejeu(){
		if(currentlevel!=0){ //si une partie est déjà en cours, revient dessus
			primarystage.getScene().setRoot(niveaux.get(currentlevel));
			return;
		}
		loadLevel(1);
		inventaire.addChildren(Data.CHOPPE);
		Level lvl = (Level) niveaux.get(1);
		lvl.setMouseTransparent(true);
		Data.PERSO.relocate(300,280);
		Data.PERSO.setImage(Data.PERSO.getCouche());
		Rectangle haut = new Rectangle(WIDTH,HEIGHT/2, Color.BLACK);
		Rectangle bas = new Rectangle(WIDTH,HEIGHT/2, Color.BLACK);
		lvl.getChildren().addAll(haut,bas);
		bas.relocate(0,HEIGHT/2);
		ArrayList<String> intro = new ArrayList<String>();
		intro.add("...");
		intro.add("Oh putain ma tête.");
		intro.add("J'aurais peut-être pas dû voler la bière de ce troll.");
		intro.add("Et où est-ce que je suis putain ?!");
		intro.add("Eh merde mais je suis à poil ?!");
		intro.add("Mais qu'est-ce que j'ai foutu\npour me retrouver en taule moi putain !?");
		intro.add("Il faut que je sorte !");


		Timeline tl2 = new Timeline();
		KeyValue kv3 = new KeyValue(haut.heightProperty(),0,Interpolator.EASE_IN);
		KeyValue kv4 = new KeyValue(bas.heightProperty(),0,Interpolator.EASE_IN);
		KeyValue kv5 = new KeyValue(bas.yProperty(),HEIGHT/2,Interpolator.EASE_IN);
		KeyFrame kf2 = new KeyFrame(Duration.seconds(3.5),kv3,kv4,kv5);
		tl2.getKeyFrames().addAll(kf2);
		tl2.setOnFinished(e -> {
			lvl.getChildren().removeAll(haut,bas);
			Pnj.dialogue(intro,Data.PERSO,Data.PERSO,() -> {
				Data.PERSO.setImage(Data.PERSO.getFace().get(0));
				Data.PERSO.relocate(325,280);
			});
		});

		Timeline tl = new Timeline();
		tl.setAutoReverse(true);
		tl.setCycleCount(2);
		KeyValue kv1 = new KeyValue(haut.heightProperty(),HEIGHT/2-30);
		KeyValue kv2 = new KeyValue(bas.heightProperty(),HEIGHT/2-30);
		KeyValue kv22 = new KeyValue(bas.yProperty(),30);
		KeyFrame kf1 = new KeyFrame(Duration.seconds(2.5),kv1,kv2,kv22);

		tl.getKeyFrames().addAll(kf1);
		tl.setOnFinished(e -> tl2.play());
		tl.play();
	}

	private static void rules(){
		Group rules = new Group();
		ImageView fond = new ImageView(new Image("/Ressources/Regles/fond1.png"));
		fond.setFitHeight(HEIGHT);
		ImageView tete = new ImageView(new Image("/Ressources/Regles/tete.png"));
		tete.relocate(675,300);
		ImageView bulle = new ImageView(new Image("/Ressources/Regles/texte1.png"));
		bulle.relocate(450,2);

		Text cont = new Text("Cliquer pour continuer...");
		cont.setFont(new Font(20));
		cont.relocate(450+bulle.getLayoutBounds().getWidth()/2-cont.getLayoutBounds().getWidth()/2,315);
		FadeTransition ft = new FadeTransition(Duration.millis(1000), cont);
    	ft.setFromValue(1.0);
    	ft.setToValue(0.0);
    	ft.setCycleCount(Animation.INDEFINITE);
    	ft.setAutoReverse(true);
	 	ft.play();
	 	rules.getChildren().addAll(fond,tete,bulle,cont);
	 	primarystage.getScene().setRoot(rules);

		ArrayList<Timeline> tl = new ArrayList<Timeline>();
		KeyValue fond2 = new KeyValue(fond.imageProperty(),new Image("/Ressources/Regles/fond2.png"));
		KeyValue fond3 = new KeyValue(fond.imageProperty(),new Image("/Ressources/Regles/fond3.png"));
		KeyValue fond4 = new KeyValue(fond.imageProperty(),new Image("/Ressources/Regles/fond4.png"));

		KeyValue texte2 = new KeyValue(bulle.imageProperty(),new Image("/Ressources/Regles/texte2.png"));
		KeyValue texte3 = new KeyValue(bulle.imageProperty(),new Image("/Ressources/Regles/texte3.png"));
		KeyValue texte4 = new KeyValue(bulle.imageProperty(),new Image("/Ressources/Regles/texte4.png"));
		KeyValue texte5 = new KeyValue(bulle.imageProperty(),new Image("/Ressources/Regles/texte5.png"));
		KeyValue texte6 = new KeyValue(bulle.imageProperty(),new Image("/Ressources/Regles/texte6.png"));
		KeyValue texte7 = new KeyValue(bulle.imageProperty(),new Image("/Ressources/Regles/texte7.png"));
		KeyValue texte8 = new KeyValue(bulle.imageProperty(),new Image("/Ressources/Regles/texte8.png"));
		KeyValue texte9 = new KeyValue(bulle.imageProperty(),new Image("/Ressources/Regles/texte9.png"));
		KeyValue texte10 = new KeyValue(bulle.imageProperty(),new Image("/Ressources/Regles/texte10.png"));

		tl.add(new Timeline(new KeyFrame(Duration.seconds(0.5),texte2)));
		tl.add(new Timeline(new KeyFrame(Duration.seconds(0.5),texte3)));
		tl.add(new Timeline(new KeyFrame(Duration.seconds(0.5),texte4,fond2)));
		tl.add(new Timeline(new KeyFrame(Duration.seconds(0.5),texte5)));
		tl.add(new Timeline(new KeyFrame(Duration.seconds(0.5),texte6)));
		tl.add(new Timeline(new KeyFrame(Duration.seconds(0.5),texte7,fond3)));
		tl.add(new Timeline(new KeyFrame(Duration.seconds(0.5),texte8)));
		tl.add(new Timeline(new KeyFrame(Duration.seconds(0.5),texte9,fond4)));
		tl.add(new Timeline(new KeyFrame(Duration.seconds(0.5),texte10)));
		Timeline t = new Timeline(new KeyFrame(Duration.seconds(0.5),new KeyValue(bulle.opacityProperty(),0)));
		t.setAutoReverse(true);
		t.setCycleCount(2);
		rules.setOnMouseClicked(new EventHandler<MouseEvent>() {
			private int i=0;
			public void handle(MouseEvent event) {
				if(i==9){
					i=0;
					fond.setImage(new Image("/Ressources/Regles/fond1.png"));
					tete.setImage(new Image("/Ressources/Regles/tete.png"));
					bulle.setImage(new Image("/Ressources/Regles/texte1.png"));
	 				primarystage.getScene().setRoot(niveaux.get(0));
				}
				tl.get(i).play();
				t.play();
				i++;
			}
		});
	}


	public  static ArrayList<Pane> getNiveaux(){
		return niveaux;
	}
	public static int getCurrentLevel(){
		return currentlevel;
	}
	public static Inventaire getInventaire(){
		return inventaire;
	}
	public static Boolean getEnding(){
		return ending;
	}
	public static void setEnding(Boolean b){
		ending = b;
	}
	public static SimpleBooleanProperty censureProperty(){
		if (censure == null) censure = new SimpleBooleanProperty(true);
		return censure;
	}

	public static void main(String[] args){
		launch(args);
	}
}