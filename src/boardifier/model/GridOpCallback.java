package boardifier.model;

@FunctionalInterface
public interface GridOpCallback {
    public void execute(GameElement element, GridElement gridDest, int rowDest, int colDest);
}
