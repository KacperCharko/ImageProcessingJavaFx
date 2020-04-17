
import javafx.application.Application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.awt.image.PixelGrabber;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;


public class MyFirstApp extends Application {

    private PixelReader pixelReader;
    private PixelWriter pixelWriter;
    private ImageView imageView;
    private ScrollPane scrollPane;
    private WritableImage writableImage;
    private ColorPicker colorPicker;
    private Image imagePicked;
    private Image imageTransformed;
    private Image imageActiveRightNow;

    private File file;
    private Button saveBtn;
    private  Button showChart;
    private  Button dimOrBright;
    private Button stretch;
    private Button equalization;
    private Button binarizationWithTreshold;
    private Button otsuBinarization;
    private Button niblackBinarization;
    private Stage stage;
    private Label colorLabel;
    private double width;
    private double height;
    private double resizedWidth;
    private double resizedHeight;
    private  Slider sliderBrightDim;
    private  Slider sliderLevel;
    private Slider binarizationTresholdSlider;
    private Slider niblackKSlider;
    private Slider niblackSizeSlider;
  //  private  Slider sliderBrightDim;



    @Override
    public void init() throws Exception {
        System.out.println("Before");
    }

    public static void openFileLocation(String path) {

    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Halko");
        System.out.println("start");
        stage.setWidth(600);
        stage.setHeight(700);
        this.stage=stage;


        scrollPane = new ScrollPane();
        scrollPane.setPrefSize(500, 500);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setContent(imageView);

       colorPicker = new ColorPicker();


        VBox root = new VBox();
        imageView = new ImageView();
        ButtonBar btnBar = new ButtonBar();
        Button SettsBtn = new javafx.scene.control.Button("Załaduj zdjęcie");
        Button ZoomIn = new Button("Zoom+");
        Button ZoomOff = new Button("Zoom-");
        saveBtn = new Button("Zapisz");
        saveBtn.setVisible(false);
        saveBtn.setOnMouseClicked(this::saveFile);

        showChart = new Button("pokaz wykres");
        showChart.setVisible(true);
        showChart.setOnMouseClicked(this::generateChartClick);

        dimOrBright = new Button("rozjasnij");
        dimOrBright.setVisible(true);
        dimOrBright.setOnMouseClicked(this::brightClick);

        stretch = new Button("rozciągnij ");
        stretch.setVisible(true);
        stretch.setOnMouseClicked(this::stretchClick);

        equalization = new Button("wyrównaj ");
        equalization.setVisible(true);
        equalization.setOnMouseClicked(this::equalizationClick);

        binarizationWithTreshold = new Button ("Binaryzacja");
        binarizationWithTreshold.setOnMouseClicked(this::simpleBinarizationClick);

        otsuBinarization = new Button ("Binaryzacja");
        otsuBinarization.setOnMouseClicked(this::otsuBinarizationClick);

        niblackBinarization = new Button ("Binaryzacja");
        niblackBinarization.setOnMouseClicked(this::niblackBinarization);

        colorLabel = new Label("");

        sliderBrightDim  = new Slider(0,3,1);
        sliderLevel = new Slider (0,126,0);
        binarizationTresholdSlider = new Slider(0,255,127);
        niblackKSlider = new Slider(-1,-0.001,-0.5);
        niblackSizeSlider = new Slider(1,23,7);
//        hSlider
//        hSlider.setShowTickMarks(true);
//        hSlider.setShowTickLabels(true);
//        hSlider.setBlockIncrement(10);

        final Label BrightDimLabel = new Label("parametr rozjaśniania/ściemnienia:  ");
        final Label StrechingLabel = new Label("Zakres wartości do rozciągnięcia: ");
        final Label EqualizationLabel = new Label("Wyrównaj histogram");
        final Label ShowDiagramLabel = new Label("pokaż histogramy");
        final Label simpleBinarizationWithTresholdLabel = new Label("binaryzacja z progiem:");
        final Label otsuBinarizationWithTresholdLabel = new Label("binaryzacja z wyznaczaniem progu przy pomocy Otsu:");
        final Label niblackBinarizationWithTresholdLabel = new Label("binaryzacja z wyznaczaniem progu przy pomocy Otsu:");
        final Label niblackK = new Label("parametr k:");
        final Label niblackSize = new Label("szerokosc okna:");


        final Label brightDimVal = new Label(
                Double.toString(sliderBrightDim.getValue()));
        final Label strechVal = new Label(
                Double.toString(sliderLevel.getValue()));
        final Label binarizationTresh = new Label(
                Double.toString(binarizationTresholdSlider.getValue()));

        final Label niblackKVal = new Label(
                Double.toString(niblackKSlider.getValue()));

        final Label niblackSizeVal = new Label(
                Double.toString(niblackSizeSlider.getValue()));



        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(70);

      //  BrightDimLabel.setTextFill(Color.B);
        GridPane.setConstraints(BrightDimLabel, 0, 1);
        grid.getChildren().add(BrightDimLabel);


        sliderBrightDim.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                brightDimVal.setText(String.format("%.2f", new_val));
            }
        });

        GridPane.setConstraints(sliderBrightDim, 1, 1);
        grid.getChildren().add(sliderBrightDim);

        //brightDimVal.setTextFill(textColor);
        GridPane.setConstraints(brightDimVal, 2, 1);
        grid.getChildren().add(brightDimVal);
        GridPane.setConstraints(dimOrBright,3,1);
        grid.getChildren().add(dimOrBright);


        GridPane.setConstraints(StrechingLabel, 0, 2);
        grid.getChildren().add(StrechingLabel);


        sliderLevel.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                strechVal.setText(String.format("%.2f", new_val));
            }
        });

        GridPane.setConstraints(sliderLevel, 1, 2);
        grid.getChildren().add(sliderLevel);

        //brightDimVal.setTextFill(textColor);
        GridPane.setConstraints(strechVal, 2, 2);
        grid.getChildren().add(strechVal);
        GridPane.setConstraints(stretch,3,2);
        grid.getChildren().add(stretch);

        GridPane.setConstraints(EqualizationLabel, 0, 3);
        grid.getChildren().add(EqualizationLabel);
        GridPane.setConstraints(equalization, 3, 3);
        grid.getChildren().add(equalization);

        GridPane.setConstraints(ShowDiagramLabel, 0, 4);
        grid.getChildren().add(ShowDiagramLabel);
        GridPane.setConstraints(showChart, 3, 4);
        grid.getChildren().add(showChart);

///////////////////////
        GridPane.setConstraints(simpleBinarizationWithTresholdLabel, 4, 1);
        grid.getChildren().add(simpleBinarizationWithTresholdLabel);

        binarizationTresholdSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                binarizationTresh.setText(String.format("%.2f", new_val));
            }
        });

        GridPane.setConstraints(binarizationTresholdSlider, 5, 1);
        grid.getChildren().add(binarizationTresholdSlider);

//        //brightDimVal.setTextFill(textColor);
        GridPane.setConstraints(binarizationTresh, 6, 1);
        grid.getChildren().add(binarizationTresh);
        GridPane.setConstraints(binarizationWithTreshold,7,1);
        grid.getChildren().add(binarizationWithTreshold);

/// otsu

        GridPane.setConstraints(otsuBinarizationWithTresholdLabel, 4, 2);
        grid.getChildren().add(otsuBinarizationWithTresholdLabel);
        GridPane.setConstraints(otsuBinarization,7,2);
        grid.getChildren().add(otsuBinarization);

//niblack
        GridPane.setConstraints(niblackBinarizationWithTresholdLabel, 4, 3);
        grid.getChildren().add(niblackBinarizationWithTresholdLabel);

        niblackKSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                niblackKVal.setText(String.format("%.2f", new_val));
            }
        });
        GridPane.setConstraints(niblackK, 4, 4);
        grid.getChildren().add(niblackK);

        GridPane.setConstraints(niblackKSlider, 5, 4);
        grid.getChildren().add(niblackKSlider);

        GridPane.setConstraints(niblackKVal, 6, 4);
        grid.getChildren().add(niblackKVal);

        niblackSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                                Number old_val, Number new_val) {
                niblackSizeVal.setText(String.format("%.2f", new_val));
            }
        });

        GridPane.setConstraints(niblackSizeSlider, 5, 5);
        grid.getChildren().add(niblackSizeSlider);

        GridPane.setConstraints(niblackSizeVal, 6, 5);
        grid.getChildren().add(niblackSizeVal);

        GridPane.setConstraints(niblackSize, 4, 5);
        grid.getChildren().add(niblackSize);

        GridPane.setConstraints(niblackBinarization, 7, 5);
        grid.getChildren().add(niblackBinarization);


        btnBar.getButtons().setAll(colorLabel,ZoomIn,ZoomOff,colorPicker);
        root.getChildren().addAll(SettsBtn,btnBar,scrollPane,saveBtn,grid);
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();

        imageView.setOnMouseClicked(this::handleClick);
        imageView.setOnMouseMoved(this::getOnMouseMoveColor);
        ZoomIn.setOnMouseClicked(this::handleZoomIn);
        ZoomOff.setOnMouseClicked(this::handleZoomOff);
        SettsBtn.setOnMouseClicked(this::loadFile);
    }

    private void niblackBinarization(MouseEvent mouseEvent) {
        ImageProccess imageProccess = new ImageProccess(imageActiveRightNow);
        WritableImage image = imageProccess.niblackTrasholding(imageActiveRightNow, niblackKSlider.getValue(), (int)niblackSizeSlider.getValue());

        imageView.setImage(image);
        pixelReader = image.getPixelReader();
        imageActiveRightNow = image;
        writableImage = image;
    }

    private void otsuBinarizationClick(MouseEvent mouseEvent) {
        ImageProccess imageProccess = new ImageProccess(imageActiveRightNow);
        WritableImage image = imageProccess.otsuBinarization(imageActiveRightNow);

        imageView.setImage(image);
        pixelReader = image.getPixelReader();
        imageActiveRightNow = image;
        writableImage = image;
    }

    private void simpleBinarizationClick(MouseEvent mouseEvent) {
        ImageProccess imageProccess = new ImageProccess(imageActiveRightNow);
        WritableImage image = imageProccess.simpleBinarizationWithTreshold(imageActiveRightNow, (int)binarizationTresholdSlider.getValue());
       // WritableImage image =  imageProccess.niblackTrasholding(imageActiveRightNow, -0.5, 7);
        imageView.setImage(image);
        pixelReader = image.getPixelReader();
        imageActiveRightNow = image;
        writableImage = image;
    }

    private void equalizationClick(MouseEvent mouseEvent) {
        equalization(imageActiveRightNow);
    }

    private void stretchClick(MouseEvent mouseEvent) {
         histogramStreching(imageActiveRightNow,(int)sliderLevel.getValue(),(int)(255-sliderLevel.getValue()));
    }

    private void brightClick(MouseEvent mouseEvent) {
        brghtOrDim(imageActiveRightNow,sliderBrightDim.getValue());
    }

    private void generateChartClick(MouseEvent mouseEvent) {
        printHistograms(imageActiveRightNow);
    }

    private void getOnMouseMoveColor(MouseEvent mouseEvent) {
        double clickedHeight = (mouseEvent.getY()/resizedHeight)*height;
        double clickedWidth = (mouseEvent.getX()/resizedWidth)*width;
        int[] rgb = getColor(clickedHeight, clickedWidth);
        colorLabel.setText("R,G,B: {"+rgb[0]+","+rgb[1]+","+rgb[2]+"}");
    }


    private  int[] getColor(double pixelHeight, double pixelWidth) {
        int [] RGB = new int [3];
        // Getting pixel color by position x=100 and y=40
        pixelReader = imageActiveRightNow.getPixelReader();
        int col=  pixelReader.getArgb((int)(pixelWidth),(int)(pixelHeight));
        RGB[0]  = (col & 0x00ff0000) >> 16;
        RGB[1] = (col & 0x0000ff00) >> 8;
        RGB[2]  =  col & 0x000000ff;
       return  RGB;



    }

    private static BufferedImage map( int sizeX, int sizeY ){
        final BufferedImage res = new BufferedImage( sizeX, sizeY, BufferedImage.TYPE_INT_RGB );
        for (int x = 0; x < sizeX; x++){
            for (int y = 0; y < sizeY; y++){
                res.setRGB(x, y, 124321);
            }
        }
        return res;
    }

    private void saveFile(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("gif", ".gif"),
                new FileChooser.ExtensionFilter("PNG", ".png"),
                new FileChooser.ExtensionFilter("BMP", ".bmp"),
                new FileChooser.ExtensionFilter("JPEG", ".jpeg"),
                new FileChooser.ExtensionFilter("tiff", ".tiff")
        );


        File dest = fileChooser.showSaveDialog(stage);
        if (dest != null) {
            try {
              FileChooser.ExtensionFilter extensionFilter = fileChooser.getSelectedExtensionFilter();
                String extension = extensionFilter.getExtensions().get(0).substring(1);
                System.out.println(extension);

                File fileToSave = new File(extensionFilter.getDescription()+"."+extension);
                BufferedImage buff = map((int)imageActiveRightNow.getHeight(),(int)imageActiveRightNow.getWidth());
                RenderedImage reneredImage = SwingFXUtils.fromFXImage(writableImage, buff);

                try {
                    ImageIO.write(
                            reneredImage,
                            extension,
                            fileToSave);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Files.copy(fileToSave.toPath(), dest.toPath());
            } catch (IOException ex) {
                // handle exception...
            }
        }
    }
    private void loadFile(MouseEvent mouseEvent){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Szukaj mnie");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.jpg","*.png","*.jpeg","*.bmp","*.tiff","*.tif","*.gif"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        file = fileChooser.showOpenDialog(stage);
        if (file != null){
            BufferedImage buffered=null;
            try {
                buffered = ImageIO.read(file);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            imagePicked = SwingFXUtils.toFXImage(buffered,null);
            imageTransformed= imagePicked;
            imageActiveRightNow = imagePicked;

            width = imagePicked.getWidth();
            resizedWidth=width;
            height = imagePicked.getHeight();
            resizedHeight=height;

            imageView.setImage(imagePicked);
            resizeImageView(height,width);
            pixelReader = imagePicked.getPixelReader();

            writableImage = new WritableImage(
                    pixelReader,
                    (int)imagePicked.getWidth(),
                    (int)imagePicked.getHeight());

            this.pixelWriter = writableImage.getPixelWriter();

            scrollPane.setContent(null);
            scrollPane.setContent(imageView);

            saveBtn.setVisible(true);
        }
    }
    private void handleClick(MouseEvent e) {
          double clickedHeight = e.getY()/resizedHeight;
          double clickedWidth = e.getX()/resizedWidth;
          changeColor(imageActiveRightNow, clickedHeight,clickedWidth);



//        int rows = 2;
//        int cols = 2;
//        double clickedHeight = e.getY()/resizedHeight;
//        double clickedWidth = e.getX()/resizedWidth;
//
//        for(int i=-(rows/2); i<rows/2; i++)
//            for(int j=-(cols/2); j<cols/2; j++)
//            {
//                pixelWriter.setColor(
//                        (int)(clickedWidth*width),
//                        (int)(clickedHeight*height),
//                         colorPicker.getValue());
//            }
//        pixelReader=writableImage.getPixelReader();
//        imageView.setImage(writableImage);
    }

    private void changeColor(Image image,double clickedHeight, double clickedWidth) {
        int rows = 2;
        int cols = 2;

        pixelReader = image.getPixelReader();

        writableImage = new WritableImage(
                pixelReader,
                (int)image.getWidth(),
                (int)image.getHeight());

        this.pixelWriter = writableImage.getPixelWriter();


        for(int i=-(rows/2); i<rows/2; i++)
            for(int j=-(cols/2); j<cols/2; j++)
            {
                pixelWriter.setColor(
                        (int)(clickedWidth*width),
                        (int)(clickedHeight*height),
                         colorPicker.getValue());
            }
        pixelReader=writableImage.getPixelReader();
        imageView.setImage(writableImage);
        imageActiveRightNow = writableImage;
    }


    private void handleZoomIn(MouseEvent mouseEvent) {
        resizedWidth +=20;
        resizedHeight+=20;
        resizeImageView(resizedHeight,resizedWidth);
    }

    private void handleZoomOff(MouseEvent mouseEvent) {
        resizedWidth -=20;
        resizedHeight-=20;
        resizeImageView(resizedHeight,resizedWidth);
    }

    private void resizeImageView(double resizedHeight, double resizedWidth){
        imageView.setFitHeight(resizedHeight);
        imageView.setFitWidth(resizedWidth);
        imageView.preserveRatioProperty().setValue(false);
    }

    @Override
    public void stop() throws Exception {
        System.out.println("after");
    }

    public boolean chechIsMonochroma(Image image){
        double x = image.getWidth();
        double y = image.getHeight();
        int[] table = new int [256];
        boolean isMono = true;
        for(int i = 1; i < y; i++){
            for(int j = 1; j < x; j++){
                int[] RGB = getColor((double)i, (double)j);

                if(RGB[0]!=RGB[1] || RGB[1]!=RGB[2])
                {
                    isMono = false;
                    break;
                }
            }
            if(isMono==false)
                break;
        }
        return  isMono;
    }

    public void strrechMono (Image image){
        double x = image.getWidth();
        double y = image.getHeight();

        int[] table = new int [256];

        WritableImage writable = new WritableImage((int)x,(int)y);
        PixelWriter writer = writable.getPixelWriter();


        for(int i = 0; i < y; i++){
            for(int j = 0; j < x; j++){
                int[] RGB = getColor((double)i, (double)j);
                int hue = (RGB[0]+RGB[1]+RGB[2])/3;
                writer.setColor(j,i,Color.rgb(hue,hue,hue));
                table[hue]++;
            }
        }
        imageView.setImage(writable);
    }

    public void MonochromaHistogram (Image image){
        double x = image.getWidth();
        double y = image.getHeight();

        int[] table = new int [256];

        for(int i = 0; i < y; i++){
            for(int j = 0; j < x; j++){
                int[] RGB = getColor(i, j);
                int hue = (RGB[0]);
                table[hue]++;
            }
        }
        XYChart chart = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(255).height((int)x*(int)y).build();
        chart.addSeries("xd",table);

        new SwingWrapper<XYChart>(chart).displayChart();
    }

    public void colorHistogram(Image image){
        double x = image.getWidth();
        double y = image.getHeight();
        System.out.println("color");
        int[] tableR = new int [256];
        int[] tableG = new int [256];
        int[] tableB = new int [256];
        int[] tableRGB = new int [256];

        for(int i = 0; i < y; i++){
            for(int j = 0; j < x; j++){
                int[] RGB = getColor(i, j);
                int R = (RGB[0]);
                int G = (RGB[1]);
                int B = (RGB[2]);
                int rgb =(RGB[0]+RGB[1]+RGB[2])/3;
                tableR[R]++;
                tableG[G]++;
                tableB[B]++;
                tableRGB[rgb]++;
            }
        }
        List<XYChart> charts = new ArrayList<XYChart>();
        System.out.println(tableR[5]+" "+
        tableG[5]+" "+
        tableB[5]+" "+
        tableRGB[5]);


        XYChart chartR = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(255).height((int)x*(int)y).build();

        chartR.addSeries("R",tableR);

        charts.add(chartR);


        XYChart chartG = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(255).height((int)x*(int)y).build();
        chartG.addSeries("G",tableG);
        charts.add(chartG);

        XYChart chartB = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(255).height((int)x*(int)y).build();
        chartB.addSeries("B",tableB);
        charts.add(chartB);

        XYChart chartRGB = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(255).height((int)x*(int)y).build();
        chartRGB.addSeries("RGB",tableRGB);
        charts.add(chartRGB);


        new SwingWrapper<XYChart>(charts).displayChartMatrix();
    }

    public void printHistograms(Image image){

        boolean isMono = chechIsMonochroma(image);

        if (isMono) {
            MonochromaHistogram(image);
        }
        else{
            colorHistogram(image);
        }

    }



    public void histogramStreching (Image image, int xx, int yy){
        Lookup lookup = new Lookup(256);
        int pMax = yy;
        int pMin = xx;
        ImageProccess imageProccess = new ImageProccess(image);
        imageProccess.process(pMin,pMax);
        int minR = imageProccess.getMinR();
        int minG = imageProccess.getMinG();
        int minB = imageProccess.getMinB();

        double[] tabR = lookup.strech(minR, imageProccess.getMaxR());
        double[] tabG = lookup.strech(minG, imageProccess.getMaxG());
        double[] tabB = lookup.strech(minB, imageProccess.getMaxB());


        double x = image.getWidth();
        double y = image.getHeight();

        pixelReader = image.getPixelReader();

        writableImage = new WritableImage(
                pixelReader,
                (int)image.getWidth(),
                (int)image.getHeight());

        this.pixelWriter = writableImage.getPixelWriter();

        WritableImage writable = new WritableImage((int)x,(int)y);

        PixelWriter writer = writable.getPixelWriter();

        for(int i = 0; i < y; i++){
            for(int j = 0; j < x; j++){
                int[] RGB = getColor((double)i, (double)j);
                int Red = RGB[0];
                int Green = RGB[1];
                int Blue = RGB[2];
                if(Red <= pMin)
                {
                    Red = pMin;
                }
                else if (Red >= pMax){
                    Red= pMax;
                }
                if(Green <= pMin)
                {
                    Green = pMin;
                }
                else if (Green >= pMax){
                    Green= pMax;
                }
                if(Blue <= pMin)
                {
                    Blue = pMin;
                }
                else if (Blue >= pMax){
                    Blue = pMax;
                }

                int R = (int)(tabR[Red]*(Red-minR));
                int G = (int)(tabG[Green]*(Green-minG));
                int B = (int)(tabB[Blue]*(Blue-minB));
                if(R < 0)
                {
                    R = 0;
                }
                else if (R > 255){
                    R= 255;
                }
                if(G < 0)
                {
                    G = 0;
                }
                else if (G > 255){
                    G=255;
                }
                if(B < 0)
                {
                    B = 0;
                }
                else if (B > 255){
                    B = 255;
                }

                writer.setColor(j,i,Color.rgb(R,G,B));
            }
        }
        imageView.setImage(writable);
        pixelReader = writable.getPixelReader();
        imageActiveRightNow = writable;
    }

    public void brghtOrDim(Image image, double parametr){

        Lookup lookup = new Lookup(256);
        int[] lookupTab = lookup.dimmingOrBrightening(parametr);

        double x = image.getWidth();
        double y = image.getHeight();


        pixelReader = image.getPixelReader();

        writableImage = new WritableImage(
                pixelReader,
                (int)image.getWidth(),
                (int)image.getHeight());

        this.pixelWriter = writableImage.getPixelWriter();

        WritableImage writable = new WritableImage((int)x,(int)y);

        PixelWriter writer = writable.getPixelWriter();

        for(int i = 0; i < y; i++){
            for(int j = 0; j < x; j++){
                int[] RGB = getColor((double)i, (double)j);
                int R = lookupTab[RGB[0]];
                int G = lookupTab[RGB[1]];
                int B = lookupTab[RGB[2]];
                writer.setColor(j,i,Color.rgb(R,G,B));
            }
        }
        imageView.setImage(writable);
        pixelReader = writable.getPixelReader();
        imageActiveRightNow = writable;

    }

    public void generateChart (Image image){
        double x = image.getWidth();
        double y = image.getHeight();
        PixelReader pixelReader = image.getPixelReader();
        int[] table = new int [256];

        WritableImage writable = new WritableImage((int)x,(int)y);
        PixelWriter writer = writable.getPixelWriter();


        for(int i = 0; i < y; i++){
            for(int j = 0; j < x; j++){
                int[] RGB = getColor((double)i, (double)j);
                int hue = (RGB[0]+RGB[1]+RGB[2])/3;
                writer.setColor(j,i,Color.rgb(hue,hue,hue));
                table[hue]++;
            }
        }

        imageView.setImage(writable);
        XYChart chart = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(255).height((int)x*(int)y).build();
        chart.addSeries("xd",table);
        new SwingWrapper<XYChart>(chart).displayChart();


    }

    public void equalization (Image image){
        ImageProccess imageProccess = new ImageProccess(image);
        int[] distTabR = imageProccess.getDistribution(0);
        int[] distTabG = imageProccess.getDistribution(1);
        int[] distTabB = imageProccess.getDistribution(2);
        double x = image.getWidth();
        double y = image.getHeight();
        Lookup lookup = new Lookup(256);
        double[] RedLookUp = lookup.equalizeTab(distTabR, (int)(x*y));
        double[] GreenLookUp = lookup.equalizeTab(distTabG, (int)(x*y));
        double[] BlueLookUp = lookup.equalizeTab(distTabB, (int)(x*y));

        pixelReader = image.getPixelReader();

        writableImage = new WritableImage(
                pixelReader,
                (int)image.getWidth(),
                (int)image.getHeight());

        this.pixelWriter = writableImage.getPixelWriter();

        WritableImage writable = new WritableImage((int)x,(int)y);

        PixelWriter writer = writable.getPixelWriter();

        for(int i = 0; i < y; i++){
            for(int j = 0; j < x; j++){
                int[] RGB = getColor((double)i, (double)j);
                int R = (int)RedLookUp[RGB[0]];
                int G = (int)GreenLookUp[RGB[1]];
                int B = (int)BlueLookUp[RGB[2]];
                writer.setColor(j,i,Color.rgb(R,G,B));
            }
        }
        imageView.setImage(writable);
        pixelReader = writable.getPixelReader();
        imageActiveRightNow = writable;


    }

    public void otsuBinarization (Image image){

    }

}
