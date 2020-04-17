import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageProccess {

    Image image;

    public int getMinR() {
        return minR;
    }

    public int getMaxR() {
        return maxR;
    }

    public int getMinG() {
        return minG;
    }

    public int getMaxG() {
        return maxG;
    }

    public int getMinB() {
        return minB;
    }

    public int getMaxB() {
        return maxB;
    }

    int minR;
    int maxR;
    int minG;
    int maxG;
    int minB;
    int maxB;

    public ImageProccess(Image image){
       this.image=image;
   }

   public void process(int pMin, int pMax){
       double x = image.getWidth();
       double y = image.getHeight();

       int minR =255;
       int maxR = 0;
       int minG=255;
       int maxG= 0;
       int minB=255;
       int maxB= 0;
       for(int i = 0; i < y; i++){
           for(int j = 0; j < x; j++){
               int[] RGB = getColor(image,(double)i, (double)j);
               if(minR > RGB[0] && RGB[0] >= pMin)
                   minR=RGB[0];
               if(maxR < RGB[0] && RGB[0] <=pMax)
                   maxR=RGB[0];
               if(minG > RGB[1] && RGB[1] >= pMin)
                   minG=RGB[1];
               if(maxG < RGB[1] && RGB[1] <=pMax)
                   maxG=RGB[1];
               if(minB > RGB[2] && RGB[2] >= pMin)
                   minB=RGB[2];
               if(maxB < RGB[2] && RGB[2] <=pMax)
                   maxB=RGB[2];
           }
       }
       this.maxB = maxB;
       this.maxG = maxG;
       this.maxR = maxR;
       this.minB = minB;
       this.minG = minG;
       this.minR = minR;
   }

   public int[] getDistribution (int canal){
       double x = image.getWidth();
       double y = image.getHeight();

        int pixelCount = (int)(x*y);
        double distTab[] = new double[256];

        int colorCount[] = new int [256];

       for(int i = 0; i < y; i++){
           for(int j = 0; j < x; j++){
               int[] RGB = getColor(image,(double)i, (double)j);
               int hue = RGB[canal];
               colorCount[hue]++;
           }
       }

        for (int i =0; i<256; i++){
            distTab[i] = (double)colorCount[i]/pixelCount;
          //  System.out.println(distTab[i]);
        }
    return  colorCount;
   }

    private  int[] getColor(Image image ,double pixelHeight, double pixelWidth) {
        int [] RGB = new int [3];
        // Getting pixel color by position x=100 and y=40
        PixelReader pixelReader = image.getPixelReader();
        int col=  pixelReader.getArgb((int)(pixelWidth),(int)(pixelHeight));
        RGB[0]  = (col & 0x00ff0000) >> 16;
        RGB[1] = (col & 0x0000ff00) >> 8;
        RGB[2]  =  col & 0x000000ff;
        return  RGB;
    }

    public Image returnGrayScaleImage (Image image){
        double x = image.getWidth();
        double y = image.getHeight();

        PixelReader pixelReader = image.getPixelReader();

        WritableImage writableImage = new WritableImage(
                pixelReader,
                (int)image.getWidth(),
                (int)image.getHeight());

        PixelWriter writer = writableImage.getPixelWriter();

        for(int i = 0; i < y; i++){
            for(int j = 0; j < x; j++){
                int[] RGB = getColor(image,i,j);
                int color = (RGB[0]+RGB[1]+RGB[2])/3;
                writer.setColor(j,i,Color.rgb(color,color,color));
            }
        }

        return writableImage;
    }

    public WritableImage simpleBinarizationWithTreshold (Image image, int treshhold){
        double x = image.getWidth();
        double y = image.getHeight();

        PixelReader pixelReader = image.getPixelReader();

        WritableImage writableImage = new WritableImage(
                pixelReader,
                (int)image.getWidth(),
                (int)image.getHeight());

        PixelWriter writer = writableImage.getPixelWriter();

        for(int i = 0; i < y; i++){
            for(int j = 0; j < x; j++){
                int[] RGB = getColor(image,i,j);
                int color = (RGB[0]+RGB[1]+RGB[2])/3;
                if(color > treshhold)
                    color = 255;
                else
                    color = 0;
                writer.setColor(j,i,Color.rgb(color,color,color));
            }
        }

        return writableImage;
    }

    public WritableImage otsuBinarization (Image image){
        Image img = returnGrayScaleImage(image);

        double x = image.getWidth();
        double y = image.getHeight();
        int totalNumberOfPixels = (int)(x*y);
        int[] table = new int [256];

        for(int i = 0; i < y; i++){
            for(int j = 0; j < x; j++){
                int[] RGB = getColor(image,i, j);
                int hue = (RGB[0]);
                table[hue]++;
            }
        }
        int index =0;

        double choosenVariance = 999999999;
        for(int i = 0; i < 256; i++){
            double backgroundWeight=0;
            double backgroundMean=0;
            double backgroundVariance=0;
            double foregorundWeight=0;
            double foregroundMean=0;
            double foregroundVariance=0;

            //background
            for (int j = 0; j<i; j++){
                backgroundWeight +=table[j];
                backgroundMean += table[j]*j;
            }
            backgroundMean = backgroundMean/backgroundWeight;
            int pixelsInBackgroundCount = (int)backgroundWeight;
            backgroundWeight = backgroundWeight / totalNumberOfPixels;
            for (int jj = 0; jj <i; jj++){
                backgroundVariance += (Math.pow((jj - backgroundMean),2))*table[jj];
            }
            backgroundVariance = backgroundVariance/pixelsInBackgroundCount;



            for (int k = i; k<=255; k++){
                foregorundWeight +=table[k];
                foregroundMean += table[k]*k;
            }
            foregroundMean = foregroundMean/foregorundWeight;
            int pixelsInForeground = (int)foregorundWeight;
            foregorundWeight = foregorundWeight / totalNumberOfPixels;
            for (int kk = i; kk <=255; kk++){
                foregroundVariance += (Math.pow((kk - foregroundMean),2))*table[kk];
            }
            foregroundVariance = foregroundVariance/pixelsInForeground;

            //final calculating

            double withinVar = (backgroundWeight*backgroundVariance) + (foregorundWeight*foregroundVariance);

            if(choosenVariance > withinVar){
                choosenVariance = withinVar;
                index = i;
            }
        }

        WritableImage img1 = simpleBinarizationWithTreshold(image,index);


        return img1;
    }

    private int calculateTrashold (Image image, double param){
        Image img = returnGrayScaleImage(image);

        double x = image.getWidth();
        double y = image.getHeight();
        int totalNumberOfPixels = (int)(x*y);
        int[] table = new int [256];
        for(int i = 0; i < y; i++){
            for(int j = 0; j < x; j++){
                int[] RGB = getColor(img,i, j);
                int hue = (RGB[0]);

                table[hue]++;
            }
        }
        int trashold=0;
        double Mean=0;
            double Variance=0;


            for (int j = 0; j<256; j++){

                Mean += table[j]*j;
            }
            Mean = Mean/totalNumberOfPixels;
            for (int jj = 0; jj <256; jj++){
                Variance += (Math.pow((jj - Mean),2))*table[jj];
            }
            Variance = Variance/totalNumberOfPixels;
            Variance = Math.sqrt(Variance);
            trashold = (int)(Mean + (param*Variance));

        return trashold;
    }

    public WritableImage niblackTrasholding (Image image, double k, int size){
        Image img = returnGrayScaleImage(image);

        double x = img.getWidth();
        double y = img.getHeight();

        System.out.println(x);
        System.out.println(y);

        PixelReader pixelReader = image.getPixelReader();

        WritableImage writableImage = new WritableImage(
                pixelReader,
                (int)image.getWidth(),
                (int)image.getHeight());

        int indexI = 0;
        int indexII = 0;
        for(int i = 0; i < y; i++){
            for(int j =0; j < x; j++){
                WritableImage writable = new WritableImage((int)size,(int)size);
                indexI = 0;
                for(int ii=i-size/2; ii<=i+size/2 && ii < y && x >0; ii++){
                    indexII = 0;
                    for(int jj=j-size/2; jj<=j+size/2 && jj<x && jj>0; jj++){
                        if(jj>0 && ii>0)
                        writable.getPixelWriter().setColor(indexI,indexII++,img.getPixelReader().getColor(jj,ii));
                    }
                    indexI++;
                }
            int trashold = calculateTrashold(writable,k);
                int[] RGB = getColor(img,i,j);
                if(RGB[0]>=trashold)
                    writableImage.getPixelWriter().setColor(j,i,Color.rgb(255,255,255));
                else
                    writableImage.getPixelWriter().setColor(j,i,Color.rgb(0,0,0));


            }

        }

        return writableImage;
    }
}
