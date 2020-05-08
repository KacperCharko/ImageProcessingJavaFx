import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


import java.util.Arrays;


public class Filter {
    public static int[] prewitt = {1,0,-1,1,0,-1,1,0,-1};
    public static int[] laplace = {0,-1,0,-1,4,-1,0,-1,0};
    public static int[] sobel = {1,2,1,0,0,0,-1,-2,-1};
    public static int[] low = {1,1,1,1,1,1,1,1,1};
    public static int[] detect = {-1,-1,1,-1,-2,1,1,1,1};


    public static Color processMaskPixel (int[] mask, int[][] pixels){

        int wagesSum=0;
        int s1 = 0;
        int s2 = 0;
        int s3 = 0;
        for (int x :mask) {
            wagesSum += x;
        }
        if (wagesSum==0)
            wagesSum=1;

        for(int i = 0; i<pixels.length; i++) {
            s1 += (int)((pixels[i][0]*mask[i])/wagesSum);
            s2 += (int)((pixels[i][1]*mask[i])/wagesSum);
            s3 += (int)((pixels[i][2]*mask[i])/wagesSum);
        }
        if(s1<0)
        s1=0;
        if(s2<0)
            s2=0;
        if(s3<0)
            s3=0;
        if(s1>255)
            s1=255;
        if(s2>255)
            s2=255;
        if(s3>255)
            s3=255;

        return Color.rgb(s1,s2,s3);

    }

    public static WritableImage maskFilter (Image image, int[] mask){
        int maskSize = (int)Math.sqrt(mask.length);
        System.out.println(maskSize);
        WritableImage img = new WritableImage(image.getPixelReader(), (int)image.getWidth(), (int)image.getHeight());

        double x = img.getWidth();
        double y = img.getHeight();

        for(int i = maskSize; i < y-maskSize; i++){
            for(int j = maskSize; j < x-maskSize; j++){
                int[][] pixels = new int[mask.length][3];
                int counter =0;
                for (int ii=i-maskSize/2; ii<=i+maskSize/2; ii++)
                    for(int jj=j-maskSize/2; jj<=j+maskSize/2; jj++){
                       // System.out.println(counter++);
                        pixels[counter++] = getColor(image,(double)ii,(double)jj);
                    }

                img.getPixelWriter().setColor(j,i,processMaskPixel(mask,pixels));
            }
        }
        return img;
    }

    public static WritableImage medianFilter (Image image, int size){
        int maskSize = size;
        System.out.println(maskSize);
        WritableImage img = new WritableImage(image.getPixelReader(), (int)image.getWidth(), (int)image.getHeight());

        double x = img.getWidth();
        double y = img.getHeight();

        for(int i = maskSize; i < y-maskSize; i++){
            for(int j = maskSize; j < x-maskSize; j++){
                int[][] pixels = new int[maskSize*maskSize][3];
                int counter =0;
                for (int ii=i-maskSize/2; ii<=i+maskSize/2; ii++)
                    for(int jj=j-maskSize/2; jj<=j+maskSize/2; jj++){
                        pixels[counter++] = getColor(image,(double)ii,(double)jj);
                    }

                img.getPixelWriter().setColor(j,i,processMedianPixel(pixels));
            }
        }
        return img;
    }

    private static Color processMedianPixel(int[][] pixels) {
        int wagesSum=0;
        int R = 0;  int G = 0;  int B = 0;

        int[] Rtab = new int[pixels.length];    int[] Gtab = new int[pixels.length];    int[] Btab = new int[pixels.length];

        for (int i =0; i < pixels.length; i++){
                Rtab[i]=pixels[i][0];
                Gtab[i]=pixels[i][1];
                Btab[i]=pixels[i][2];
        }

        Arrays.sort(Rtab);  Arrays.sort(Gtab);  Arrays.sort(Rtab);

        R = Rtab[pixels.length/2];  G = Gtab[pixels.length/2];  B = Btab[pixels.length/2];

        return Color.rgb(R,G,B);
    }

    public static WritableImage kuwahara (Image image, int maskSize){
        WritableImage img = new WritableImage(image.getPixelReader(), (int)image.getWidth(), (int)image.getHeight());

        double x = img.getWidth();
        double y = img.getHeight();



        for(int i = maskSize; i < y-maskSize; i++){
            for(int j = maskSize; j < x-maskSize; j++){
                int[][][] pixels = new int[maskSize][maskSize][3];
                int w =0;
                for (int ii=i-maskSize/2; ii<=i+maskSize/2; ii++){
                    int h =0;
                    for(int jj=j-maskSize/2; jj<=j+maskSize/2; jj++){
                        pixels[w][h++] = getColor(image,(double)ii,(double)jj);
                    }
                    w++;

                img.getPixelWriter().setColor(j,i,processKuwahara(pixels));
            }
            }
        }
        return img;
    }

    public static Color  processKuwahara (int[][][] pixels){

        int whalf = pixels.length/2;
        int hhalf = pixels[0].length/2;

        int[][] tables = new int[][]{
                { 0, whalf+1, 0 , hhalf+1 },
                { whalf, pixels.length, 0, hhalf+1},
                { 0, whalf+1, hhalf, pixels[0].length},
                { whalf, pixels.length, hhalf, pixels[0].length}
        };

       double resultR=0; double resultG=0; double resultB=0;
       double r=999; double g=999; double b=999;

        for (int i = 0; i<tables.length; i++) {
            double[][] result = processPortionKuwahara(pixels,tables[i][0],tables[i][1],tables[i][2],tables[i][3]);
            if(r>result[0][0]){
                r=result[0][0];
                resultR = result[0][1];
            }
            if(g>result[1][0]) {
                g = result[1][0];
                resultG = result[1][1];
            }
            if(b>result[2][0]) {
                b = result[2][0];
                resultB = result[2][1];
            }
        }
        return Color.rgb((int)resultR,(int)resultG,(int)resultB);

    }

    private static double[][] processPortionKuwahara(int[][][] pixels, int begin, int whalf, int begin1, int hhalf) {
        double RMean=0; double GMean=0; double BMean=0;

        double[][] result = new double[3][2];
        int size =0;
        for (int i= begin; i < whalf; i++) {
            for (int j = begin1; j < hhalf; j++) {
                RMean += pixels[i][j][0];
                GMean += pixels[i][j][1];
                BMean += pixels[i][j][2];
                size++;
            }
        }

        RMean = RMean/size;  GMean = GMean/size; BMean = BMean/size;

        for (int i= begin; i < whalf; i++) {
            for (int j = begin1; j < hhalf; j++) {
                result[0][0] += Math.pow(RMean-pixels[i][j][0],2)/size;
                result[1][0] += Math.pow(GMean-pixels[i][j][1],2)/size;
                result[2][0] += Math.pow(BMean-pixels[i][j][2],2)/size;

            }
            result[0][1] = RMean;
            result[1][1] = GMean;
            result[2][1] = BMean;
        }
        return result;
    }


    private  static int[] getColor(Image image ,double pixelHeight, double pixelWidth) {
        int [] RGB = new int [3];
        // Getting pixel color by position x=100 and y=40
        PixelReader pixelReader = image.getPixelReader();
        int col=  pixelReader.getArgb((int)(pixelWidth),(int)(pixelHeight));
        RGB[0]  = (col & 0x00ff0000) >> 16;
        RGB[1] = (col & 0x0000ff00) >> 8;
        RGB[2]  =  col & 0x000000ff;
        return  RGB;
    }

}
