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

    public BRBView(Model model, Stage stage, RootPane rootPane) {
        super(model, stage, rootPane);
    }

    @Override
    protected void createMenuBar() {
        menuBar = new MenuBar();
        Menu menu1 = new Menu("Game");
        menuStart = new MenuItem("New game");
        menuIntro = new MenuItem("Intro");
        menuQuit = new MenuItem("Quit");
        menu1.getItems().add(menuStart);
        menu1.getItems().add(menuIntro);
        menu1.getItems().add(menuQuit);

        Menu menuOptions = new Menu("Options");
        menuPvP = new MenuItem("PvP");
        menuEvP = new MenuItem("EvP");
        menuPvE = new MenuItem("PvE");
        menuEvE = new MenuItem("EvE");
        menuOptions.getItems().add(menuPvP);
        menuOptions.getItems().add(menuEvP);
        menuOptions.getItems().add(menuPvE);
        menuOptions.getItems().add(menuEvE);

        Menu menuSelectIA = new Menu("Select IA");
        IA1 = new MenuItem("IA1");
        menuSelectIA.getItems().add(IA1);


        menuBar.getMenus().add(menu1);
        menuBar.getMenus().add(menuOptions);
        menuBar.getMenus().add(menuSelectIA);
    }

    public MenuItem getMenuStart() {
        return menuStart;
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
}
