import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;



public class Filter {
    public static int[] P0 = {1,0,-1,1,0,-1,1,0,-1};

    public static Color processPixel (int[] mask, int[][] pixels){

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

    public static WritableImage prewitt (Image image, int[] mask){
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

                img.getPixelWriter().setColor(j,i,processPixel(P0,pixels));
            }
        }
        return img;
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
