package GUI;

import javafx.scene.layout.Pane;

public abstract class ViewController<T> {

    protected Pane rootView;
    protected T application;
    public ViewController(T application) {
        this.application = application;
    }

    public Pane getRootView() {
        return rootView;
    }
    public void setRootView(Pane rootView){ this.rootView = rootView;}

    abstract public void initialize();

    public void setApplication(T application) {
        this.application = application;
    }

}