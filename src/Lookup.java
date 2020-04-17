public class Lookup {
    int[] table;

    public Lookup(int rozmiar){
            this.table = new int[rozmiar];
            for(int i = 0; i<rozmiar; i++){
                this.table[i] = i;
            }
    }

    public int[] dimmingOrBrightening (double param){
        int[] tab = this.table;

        for (int i =0; i<tab.length; i++){
            tab[i]=(int)(tab[i]*param);
            if (tab[i]>255)
                tab[i]=255;
        }

        return tab;
    }

    public double[] strech ( int min, int max){
        double[] tab = new double[256];
        for (int i =0; i<tab.length; i++) {
            tab[i] = i;
        }
        System.out.println(min + "  " + max);
        for (int i =0; i<tab.length; i++){
            tab[i]=(double)255/(max-min);
        }

        return tab;
    }

    public double[] equalizeTab (int[] tab,int size){
        double[] equalizationTab = new double[256];

        double MinNonZero = 255;
        double Max=0;
        int suma=0;
        for (int i = 0; i< tab.length; i++){
            suma +=tab[i];
            equalizationTab[i]=(double)suma/size;
        }

        for (int i=0; i<equalizationTab.length;i++){
            equalizationTab[i]=Math.floor((equalizationTab[i]*255)+1);
        }

        for (int i = 0; i< tab.length; i++){
            if(equalizationTab[i]>Max)
                Max=equalizationTab[i];
            if(equalizationTab[i]<MinNonZero&&equalizationTab[i]!=0)
                MinNonZero=equalizationTab[i];
        }
        System.out.println("max"+Max);
        System.out.println("Min" + MinNonZero);


        for (int i=0; i<equalizationTab.length;i++){
            equalizationTab[i]=((equalizationTab[i]-MinNonZero)/255)*255;
            System.out.println(equalizationTab[i]);

        }

        return equalizationTab;
    }

}
