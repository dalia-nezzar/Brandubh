package view;

import boardifier.model.Model;
import boardifier.view.RootPane;
import boardifier.view.View;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class BRBView extends View {

    private MenuItem menuStart;
    private MenuItem menuIntro;
    private MenuItem menuQuit;
    private MenuItem menuPvP;
    private MenuItem menuPvE;
    private MenuItem menuEvP;
    private MenuItem menuEvE;
    private MenuItem IA1;
    private MenuItem IA2;
    private MenuItem IA3;

    public BRBView(Model model, Stage stage, RootPane rootPane) {
        super(model, stage, rootPane);
    }

    @Override
    protected void createMenuBar() {
        menuBar = new MenuBar();
        Menu menu1 = new Menu("Game");
        menuIntro = new MenuItem("Intro");
        menuQuit = new MenuItem("Quit");
        menu1.getItems().add(menuIntro);
        menu1.getItems().add(menuQuit);

        Menu menuNewGame = new Menu("New Game");
        menuPvP = new MenuItem("PvP");
        menuEvP = new MenuItem("EvP");
        menuPvE = new MenuItem("PvE");
        menuEvE = new MenuItem("EvE");
        menuNewGame.getItems().add(menuPvP);
        menuNewGame.getItems().add(menuEvP);
        menuNewGame.getItems().add(menuPvE);
        menuNewGame.getItems().add(menuEvE);

        Menu menuSelectIA = new Menu("Select IA");
        IA1 = new MenuItem("IA Random");
        IA2 = new MenuItem("IA Smart");
        IA3 = new MenuItem("IA EAT");
        menuSelectIA.getItems().add(IA1);
        menuSelectIA.getItems().add(IA2);
        menuSelectIA.getItems().add(IA3);

        menuBar.getMenus().add(menu1);
        menuBar.getMenus().add(menuNewGame);
        menuBar.getMenus().add(menuSelectIA);
    }

    public MenuItem getMenuIntro() {
        return menuIntro;
    }

    public MenuItem getMenuQuit() {
        return menuQuit;
    }

    public MenuItem getMenuPvP() {
        return menuPvP;
    }

    public MenuItem getMenuPvE() {
        return menuPvE;
    }

    public MenuItem getMenuEvP() {
        return menuEvP;
    }

    public MenuItem getMenuEvE() {
        return menuEvE;
    }

    public MenuItem getMenuAI1() {
        return IA1;
    }

    public MenuItem getMenuAI2() {
        return IA2;
    }

    public MenuItem getMenuAI3() {
        return IA3;
    }
}
