package sketchclonev1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.*;
import java.util.Optional;

public class sketchclone extends Application {
    // Initialize class variables
    private int width = 1000;
    private int height = 800;
    private int maxProperty = 5000;
    private double stroke = 10;
    private Canvas canvas = new Canvas(width, height);
    private Pane canvasHolder = new Pane();
    private GraphicsContext gc = canvas.getGraphicsContext2D();
    private Color c = Color.BLACK;
    private Circle circle = new Circle(stroke * 2);
    private Circle paint = new Circle(stroke * 2, new RadialGradient(0, 0, .5, .5, 0.6, true,
            CycleMethod.NO_CYCLE, new Stop(0, c), new Stop(1, Color.TRANSPARENT)));
    private Rectangle rectangle = new Rectangle(stroke * 3, stroke * 1.5);
    private Rectangle line = new Rectangle(stroke * 3, stroke / 3);
    private ToggleButton circleSelect = new ToggleButton();
    private ToggleButton paintSelect = new ToggleButton();
    private ToggleButton rectSelect = new ToggleButton();
    private ToggleButton lineSelect = new ToggleButton();
    private ToggleGroup shapeToggle = new ToggleGroup();
    private double XX, YY;
    private TextField newWidth = new TextField(String.valueOf(width));
    private TextField newHeight = new TextField(String.valueOf(height));

    // create the sidebar menu with color, shape, size options
    public VBox menu() {
        // Declare menu
        VBox buttonHolder = new VBox();
        buttonHolder.setMinWidth(200);
        buttonHolder.setMaxWidth(200);
        // Color Picker
        Label cpLabel = new Label("Color");
        cpLabel.setFont(Font.font("Montserrat", FontWeight.EXTRA_BOLD, 12));
        ColorPicker cp = new ColorPicker();
        cp.setTooltip(new Tooltip("Click dropdown to select color"));
        cp.setValue(Color.BLACK);
        c = cp.getValue();
        cp.setOnAction((e) -> {
            c = cp.getValue();
        });
        // Size Selection
        Label sizeSliderLabel = new Label("Shape/Stroke Size: " + stroke + "px");
        sizeSliderLabel.setFont(Font.font("Montserrat", FontWeight.EXTRA_BOLD, 12));
        Slider sizeSlider = new Slider(1, 100, 10);
        sizeSlider.setTooltip(new Tooltip("Drag to specify size of shape (in pixels)"));
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setBlockIncrement(1.0);
        sizeSlider.setOnMouseDragged((e) -> {
            stroke = Math.round(sizeSlider.getValue());
            sizeSliderLabel.setText("Shape/Stroke Size: " + stroke + "px");
        });
        sizeSlider.setOnMousePressed((e) -> {
            stroke = Math.round(sizeSlider.getValue());
            sizeSliderLabel.setText("Shape/Stroke Size: " + stroke + "px");
        });
        // Shape Selection
        Label shapesLabel = new Label("Shapes");
        shapesLabel.setFont(Font.font("Montserrat", FontWeight.EXTRA_BOLD, 12));
        StackPane cs = new StackPane(circle);
        cs.setMinSize(40.0, 40.0);
        circleSelect.setGraphic(cs);
        circleSelect.setToggleGroup(shapeToggle);
        circleSelect.setSelected(true);
        StackPane ps = new StackPane(paint);
        ps.setMinSize(40.0, 40.0);
        paintSelect.setGraphic(ps);
        paintSelect.setToggleGroup(shapeToggle);
        StackPane rs = new StackPane(rectangle);
        rs.setMinSize(40.0, 40.0);
        rectSelect.setGraphic(rs);
        rectSelect.setToggleGroup(shapeToggle);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        StackPane ls = new StackPane(line);
        ls.setMinSize(40.0, 40.0);
        lineSelect.setGraphic(ls);
        lineSelect.setToggleGroup(shapeToggle);
        VBox shapesSelect = new VBox(circleSelect, paintSelect, rectSelect, lineSelect);
        shapesSelect.setAlignment(Pos.TOP_CENTER);
        shapesSelect.setSpacing(10.0);
        // Add nodes to menu and finalize formatting
        buttonHolder.getChildren().addAll(cpLabel, cp, sizeSliderLabel, sizeSlider, shapesLabel,
                shapesSelect);
        buttonHolder.setAlignment(Pos.TOP_CENTER);
        buttonHolder.setSpacing(5.0);

        return buttonHolder;
    }

    // create the header menu + actions
    public VBox header() {
        // toolbar buttons - new, open, and save
        Button newButton = new Button("New");
        newButton.setPrefSize(50, 22);
        newButton.setOnAction((e) -> {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.setTitle("Are you sure?");
            a.setHeaderText("All changes will be lost.");
            a.setContentText("Please cancel and save your work!");
            Optional<ButtonType> buttonType = a.showAndWait();
            if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
                newImage();
            }
        });
        Button openButton = new Button("Open");
        openButton.setPrefSize(50, 22);
        openButton.setOnAction((e) -> openImage());
        Button saveButton = new Button("Save");
        saveButton.setPrefSize(50, 22);
        saveButton.setOnAction((e) -> saveImage());
        ToolBar tb = new ToolBar(newButton, openButton, saveButton);
        // generate title
        Label titleLabel = new Label("SKETCH CLONE (by Sean Miller)");
        titleLabel.setFont(Font.font("Montserrat", FontWeight.EXTRA_BOLD, 20.0));
        titleLabel.setAlignment(Pos.CENTER);
        // change width property when user presses ENTER
        newWidth.setOnKeyReleased((e) -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                try {
                    int eval = Integer.parseInt(newWidth.getText());
                    if (eval > maxProperty) {
                        newWidth.setText(String.valueOf(width));
                    } else {
                        width = eval;
                        canvasHolder.setMaxWidth(width);
                        canvasHolder.setMinWidth(width);
                    }
                } catch (NumberFormatException nf) {
                    newWidth.setText(String.valueOf(width));
                }
            }
        });
        // change height property when user presses ENTER
        newHeight.setOnKeyReleased((e) -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                try {
                    int eval = Integer.parseInt(newHeight.getText());
                    if (eval > maxProperty) {
                        newHeight.setText(String.valueOf(height));
                    } else {
                        height = eval;
                        canvasHolder.setMaxHeight(height);
                        canvasHolder.setMinHeight(height);
                    }
                } catch (NumberFormatException nf) {
                    newHeight.setText(String.valueOf(height));
                }
            }
        });
        HBox canvasResize = new HBox(newWidth, new Label(" x "), newHeight);
        canvasResize.setAlignment(Pos.BOTTOM_RIGHT);
        VBox top = new VBox(tb, titleLabel, canvasResize);
        top.setAlignment(Pos.CENTER);
        top.setSpacing(5.0);
        top.setMinWidth(1200);
        return top;
    }

    // define method for saving images when user selects SAVE in toolbar
    public void saveImage() {
        FileChooser save = new FileChooser();
        save.setTitle("Save Sketch Clone Canvas");
        save.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
        File f = save.showSaveDialog(null);
        if (f != null) {
            try {
                WritableImage w = new WritableImage(width, height);
                canvas.snapshot(null, w);
                RenderedImage r = SwingFXUtils.fromFXImage(w, null);
                ImageIO.write(r, "PNG", f);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // define method for opening images when user selects OPEN in toolbar
    public void openImage() {
        FileChooser open = new FileChooser();
        open.setTitle("Open .PNG for Sketch Clone Canvas");
        open.getExtensionFilters()
                .add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
        File f = open.showOpenDialog(null);
        if (f != null) {
            try {
                gc.clearRect(0, 0, width, height);
                FileInputStream fs = new FileInputStream(f);
                Image image = new Image(fs);
                width = (int) image.getWidth() + 2;
                height = (int) image.getHeight() + 2;
                canvasHolder.setMaxWidth(width);
                canvasHolder.setMinWidth(width);
                newWidth.setText(String.valueOf(width - 2));
                canvasHolder.setMaxHeight(height);
                canvasHolder.setMinHeight(height);
                newHeight.setText(String.valueOf(height - 2));
                Platform.runLater(() -> {
                    gc.drawImage(image, 1, 1);
                });
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    // define method for generating a blank canvas when user selects NEW in toolbar
    public void newImage() {
        gc.clearRect(0, 0, width, height);
        canvasHolder.setMaxWidth(1000);
        canvasHolder.setMinWidth(1000);
        canvasHolder.setMaxHeight(800);
        canvasHolder.setMinHeight(800);
        newWidth.setText(String.valueOf(1000));
        newHeight.setText(String.valueOf(800));
    }

    @Override
    public void start(final Stage stage) throws Exception {
        canvas.widthProperty().bind(canvasHolder.widthProperty());
        canvas.heightProperty().bind(canvasHolder.heightProperty());
        canvasHolder.getChildren().add(canvas);
        canvasHolder.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        canvasHolder.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        canvasHolder.setMaxSize(width, height);
        canvasHolder.setMinSize(width, height);
        // define canvas actions for mouse press
        canvas.setOnMousePressed((e) -> {
            if (circleSelect.isSelected()) {
                gc.setFill(c);
                gc.fillOval(e.getX() - (stroke / 2), e.getY() - (stroke / 2), stroke, stroke);
            } else if (paintSelect.isSelected()) {
                gc.setFill(new RadialGradient(0, 0, .5, .5, 0.6, true, CycleMethod.NO_CYCLE,
                        new Stop(0, c), new Stop(1, Color.TRANSPARENT)));
                gc.fillOval(e.getX() - (stroke / 2), e.getY() - (stroke / 2), stroke, stroke);
            } else if (rectSelect.isSelected()) {
                gc.setFill(c);
                gc.fillRect(e.getX() - (stroke / 2), e.getY() - (stroke / 4), stroke, stroke / 2);
            } else {
                XX = e.getX();
                YY = e.getY();
            }
        });

        // define canvas actions for mouse drag
        canvas.setOnMouseDragged((e) -> {
            if (circleSelect.isSelected()) {
                gc.setFill(c);
                gc.fillOval(e.getX() - (stroke / 2), e.getY() - (stroke / 2), stroke, stroke);
            } else if (paintSelect.isSelected()) {
                gc.setFill(new RadialGradient(0, 0, .5, .5, 0.6, true, CycleMethod.NO_CYCLE,
                        new Stop(0, c), new Stop(1, Color.TRANSPARENT)));
                gc.fillOval(e.getX() - (stroke / 2), e.getY() - (stroke / 2), stroke, stroke);
            } else if (rectSelect.isSelected()) {
                gc.setFill(c);
                gc.fillRect(e.getX() - (stroke / 2), e.getY() - (stroke / 4), stroke, stroke / 2);
            } else {
                gc.setLineWidth(stroke);
                gc.setLineCap(StrokeLineCap.ROUND);
                gc.setStroke(c);
                gc.strokeLine(XX, YY, e.getX(), e.getY());
                XX = e.getX();
                YY = e.getY();
            }
        });
        
        // define BorderPane and ScrollPane
        BorderPane bp = new BorderPane();
        bp.setCenter(canvasHolder);
        bp.setTop(header());
        bp.setLeft(menu());
        BorderPane.setAlignment(canvasHolder, Pos.TOP_LEFT);
        ScrollPane sp = new ScrollPane(bp);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        // define and initialize Scene
        final Scene scene = new Scene(sp, 1200, 1000);
        stage.setScene(scene);
        stage.setTitle("Sketch Clone v0.1");
        stage.show();
    }

    public static void main(final String[] args) {
        launch(args);
    }
}
