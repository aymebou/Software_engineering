import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CompImage {

    private DisplayedImage displayedImage;
    private List<Integer> pixels = new ArrayList<>();

    public CompImage(DisplayedImage image){
        displayedImage = image;
    }

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

                input.read(buff2);
                height = twoBytesToInt(buff2);

                input.read(buff2);
                width = twoBytesToInt(buff2);

                input.read(buff1);
                paletteSize = byteToInt(buff1[0]);

                palette = new int[paletteSize][3];
                for (int i = 0 ; i < paletteSize ; i++){
                    for (int j = 0 ; j < 3 ; j++){
                        input.read(buff1);
                        palette[i][j] = byteToInt(buff1[0]);
                    }
                }

                for (int i = 0 ; i < height*width ; i++){
                    input.read(buff1);
                    pixels.add(byteToInt(buff1[0]));
                }

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


    private void buildData(){
        for (int x = 0 ; x < displayedImage.getWidth() ; x++){
            int[] pix = new int[3];
            for (int y = 0 ; y < displayedImage.getHeight() ; y++){
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

    private int getIndex(int[] pix){
        for (int i = 0 ; i < displayedImage.getPalette().length ; i++){
            if (pix == displayedImage.getPalette()[i]){
                return i;
            }
        }
        return -1;
    }

    public static byte intToByte(int n){
        n &= 0xFF;
        return (byte)(n - 128);
    }

    public static int byteToInt(byte b){
        return (int)b + 128;
    }

    public static byte[] intToTwoBytes(int n) {
        int n0 = n & 0xFF;
        int n1 = (n >> 4) & 0xFF;
        byte b0 = (byte)(n0 - 128);
        byte b1 = (byte)(n1 - 128);
        return new byte[] {b0, b1};
    }

    public static int twoBytesToInt(byte[] b){
        int n0 = (int)b[0] + 128;
        int n1 = ((int)b[1] + 128) << 8;
        return n0 + n1;
    }

    public void save(File file){

        this.buildData();

        byte[] toWrite = new byte[2 + 2 + 1 + 3*displayedImage.getPalette().length + displayedImage.getWidth()*displayedImage.getHeight() ];

        int offset = 0;

        byte[] height = intToTwoBytes(displayedImage.getHeight());
        for (int i = 0 ; i < 2 ; i++){
            toWrite[offset+i] = height[i];
        }
        offset += 2;

        byte[] width = intToTwoBytes(displayedImage.getWidth());
        for (int i = 0 ; i < 2 ; i++){
            toWrite[offset+i] = width[i];
        }
        offset += 2;

        toWrite[offset] = intToByte(displayedImage.getPalette().length);
        offset++;

        for (int i = 0 ; i < displayedImage.getPalette().length ; i++){
            for (int j = 0 ; j < 3 ; j++){
                toWrite[offset + 3*i + j] = intToByte(displayedImage.getPalette()[i][j]);
            }
        }
        offset += 3*displayedImage.getPalette().length;

        for (int x = 0 ; x < displayedImage.getWidth() ; x++){
            for (int y = 0 ; y < displayedImage.getHeight() ; y++){
                int[] pix = displayedImage.getRGB(x, y);
                int index = getIndex(pix);
                toWrite[offset + x*displayedImage.getHeight() + y] = intToByte(index);
            }
        }
        offset += displayedImage.getHeight()*displayedImage.getWidth();

        try {
            FileOutputStream output = new FileOutputStream(file.getPath());
            try {
                output.write(toWrite);
            } catch(IOException e){ }
        } catch(FileNotFoundException e){}
    }




}
