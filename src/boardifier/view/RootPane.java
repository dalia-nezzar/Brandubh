package boardifier.view;

import java.util.List;

public class RootPane {

    private String[][] viewPort;
    private int width;
    private int height;

    public RootPane(int width, int height) {
        this.width = width;
        this.height = height;
        viewPort = new String[height][width];
        clearViewPort();
    }

    public RootPane() {
        this(1,1);
    }

    public void clearViewPort() {
        for(int i=0;i<height;i++) {
            for(int j=0;j<width;j++) {
                viewPort[i][j] = " ";
            }
        }
    }

    public void udpate(GameStageView gameStageView) {
        // first, determine the size of the view
        int w = 0;
        int h = 0;
        List<ElementLook> looks = gameStageView.getLooks();
        for (ElementLook look : looks) {
            if ((look.width+look.getElement().getX()) > w) {
                w = (int)(look.width + look.getElement().getX());
            }
            if ((look.height+look.getElement().getY()) > h) {
                h = (int)(look.height + look.getElement().getY());
            }
        }
        if ((w != width) || (h != height)) {
            width = w;
            height = h;
            viewPort = new String[height][width];
            clearViewPort();
        }
        // now put looks on the pane
        for  (ElementLook look : looks) {
            for(int i=0;i<look.height;i++) {
                for(int j=0;j<look.width;j++) {
                    viewPort[(int)(look.getElement().getY()+i)][(int)(look.getElement().getX()+j)] = look.getShapePoint(j,i);
                }
            }
        }
    }

    public void print() {
        for(int i=0;i<height;i++) {
            for(int j=0;j<width;j++) {
                System.out.print(viewPort[i][j]);
            }
            System.out.println();
        }
    }
}
