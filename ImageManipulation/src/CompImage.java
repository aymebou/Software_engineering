import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompImage {

    /* Une CompImage contient une DisplayedImage (qui contient elle-meme la palette) et de la liste des indices
       des pixels dans la palette. */
    private DisplayedImage displayedImage;
    private List<Integer> pixels = new ArrayList<>();

    public CompImage(DisplayedImage image){
        displayedImage = image;
    }

    /* Créer une CompImage à partir d'un fichier, pour pouvoir ouvrir un fichier d'image compressée. */
    public CompImage(File file){
        int height;
        int width;
        int paletteSize;
        int[][] palette;
        byte[] buff1 = new byte[1];
        byte[] buff2 = new byte[2];

        try {
            FileInputStream input = new FileInputStream(file);
            try {

                /* Les deux premiers octets contiennent la hauteur */
                input.read(buff2);
                height = twoBytesToInt(buff2);

                /* Les deux octets suivants contiennent la largeur */
                input.read(buff2);
                width = twoBytesToInt(buff2);

                /* L'octet suivant contient la taille de la palette */
                input.read(buff1);
                paletteSize = byteToInt(buff1[0]);

                /* Chaque couleur de la palette est ensuite lue sur trois octets chacune */
                palette = new int[paletteSize][3];
                for (int i = 0 ; i < paletteSize ; i++){
                    for (int j = 0 ; j < 3 ; j++){
                        input.read(buff1);
                        palette[i][j] = byteToInt(buff1[0]);
                    }
                }

                /* Enfin, on lit les indices dans la palette de chaque pixel (codés sur un octet chacun) */
                for (int i = 0 ; i < height*width ; i++){
                    input.read(buff1);
                    pixels.add(byteToInt(buff1[0]));
                }

                /* On stocke les informations lues dans la DisplayedImage de notre ComImage */
                displayedImage = new DisplayedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                for (int x = 0 ; x < width ; x++){
                    for (int y = 0 ; y < height ; y++){
                        int index = pixels.get(x*height + y);
                        displayedImage.setPixelColor(x, y, new Color(palette[index][0], palette[index][1], palette[index][2]));
                    }
                }
            } catch(IOException e){}
        } catch(FileNotFoundException e){}



    }


    public DisplayedImage getDisplayedImage() {
        return displayedImage;
    }

    /* Sert à remplir la liste des indices à partir des données de la DisplayedImage */
    private void buildData(){
        int[] pix = new int[3];
        for (int x = 0 ; x < displayedImage.getBuffer().getWidth() ; x++){
            for (int y = 0 ; y < displayedImage.getBuffer().getHeight() ; y++){
                Color color = new Color(displayedImage.getBuffer().getRGB(x, y));
                pix[0] = color.getRed();
                pix[1] = color.getGreen();
                pix[2] = color.getBlue();
            }
            for (int i = 0 ; i < displayedImage.getPalette().length ; i++){
                if (pix == displayedImage.getPalette()[i]){
                    pixels.add(i);
                    break;
                }
            }
        }
    }

    /* Pour un pixel, trouve l'indice correspondant dans la palette */
    private int getIndex(int[] pix){
        for (int i = 0 ; i < displayedImage.getPalette().length ; i++){
            if (Arrays.equals(pix, displayedImage.getPalette()[i])){
                return i;
            }
        }
        return -1;
    }

    /* Fonctions de conversion d'entiers en octets et inversement */
    public static byte intToByte(int n){
        n &= 0xFF;
        return (byte)(n - 128);
    }

    public static int byteToInt(byte b){
        return (int)b + 128;
    }

    public static byte[] intToTwoBytes(int n) {
        int n0 = n & 0xFF;
        int n1 = (n >> 8) & 0xFF;
        byte b0 = (byte)(n0 - 128);
        byte b1 = (byte)(n1 - 128);
        return new byte[] {b0, b1};
    }

    public static int twoBytesToInt(byte[] b){
        int n0 = (int)b[0] + 128;
        int n1 = ((int)b[1] + 128) << 8;
        return n0 + n1;
    }

    /* Enregistre la CompImage dans le fichier file */
    public void save(File file){

        /* On commence par construire la liste des indices des pixels dans la palette */
        this.buildData();

        /* On crée un tableau d'octets que l'on finira par écrire dans le fichier.
         * On réserve 2 octets pour la hauteur, 2 octets pour la largeur, 1 pour la taille de la palette,
         * puis suffisammant pour stocker chaque couleur de la palette sur 3 octets, et enfin chaque indice
         * de la liste pixels sur 1 octet. */
        byte[] toWrite = new byte[2 + 2 + 1 + 3*displayedImage.getPalette().length + displayedImage.getBuffer().getWidth()*displayedImage.getBuffer().getHeight() ];

        int offset = 0;

        /* Conversion de la hauteur en octets */
        byte[] height = intToTwoBytes(displayedImage.getBuffer().getHeight());
        for (int i = 0 ; i < 2 ; i++){
            toWrite[offset+i] = height[i];
        }
        offset += 2;

        //Largeur
        byte[] width = intToTwoBytes(displayedImage.getBuffer().getWidth());
        for (int i = 0 ; i < 2 ; i++){
            toWrite[offset+i] = width[i];
        }
        offset += 2;

        //Taille de la palette
        toWrite[offset] = intToByte(displayedImage.getPalette().length);
        offset++;

        //Couleurs de la palette
        for (int i = 0 ; i < displayedImage.getPalette().length ; i++){
            for (int j = 0 ; j < 3 ; j++){
                toWrite[offset + 3*i + j] = intToByte(displayedImage.getPalette()[i][j]);
            }
        }
        offset += 3*displayedImage.getPalette().length;

        //Indices de la liste pixels
        for (int x = 0 ; x < displayedImage.getBuffer().getWidth() ; x++){
            for (int y = 0 ; y < displayedImage.getBuffer().getHeight() ; y++){
                int[] pix = displayedImage.getRGB(x, y);
                int index = getIndex(pix);
                toWrite[offset + x*displayedImage.getBuffer().getHeight() + y] = intToByte(index);
            }
        }
        offset += displayedImage.getBuffer().getHeight()*displayedImage.getBuffer().getWidth();

        /* Enfin, on écrit notre tableau d'octets dans le fichier */
        try {
            FileOutputStream output = new FileOutputStream(file.getPath());
            try {
                output.write(toWrite);
            } catch(IOException e){ }
        } catch(FileNotFoundException e){}
    }




}
