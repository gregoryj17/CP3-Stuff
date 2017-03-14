import java.awt.image.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;

public class Program {
    static int d=10012;
    static int cityx;
    static int cityy;
    static boolean[][] touched=new boolean[d][d];

    public static void addDirt(short[][] terrain,int x,int y){
        terrain[x][y]+=1;
        touched[x][y]=true;
        int[] dx={1,0,-1,0};
        int[] dy={0,1,0,-1};
        for(int i=0;i<4;i++){
            try{
                if(terrain[x][y]>terrain[x+dx[i]][y+dy[i]]+1){
                    addDirt(terrain,x+dx[i],y+dy[i]);
                }
            }catch(Exception e){}
        }
    }

    private static void setHeight(short[][] terrain, int x, int y, short height) {
        terrain[x][y]=height;
        touched[x][y]=true;
    }

    public static short[][] getImageArray(String filename)
    {
        int d=10012;
        short[][] output = new short[d][d];
        try
        {
            BufferedImage original= ImageIO.read(new File(filename));
            BufferedImage img= new BufferedImage(d, d, BufferedImage.TYPE_3BYTE_BGR);
            img.getGraphics().drawImage(original, 0, 0, null);
            for(int i=0;i<d;i++)
                for(int j=0;j<d;j++)
                    output[i][j]=(short)(img.getRGB(i,j)&0xFF);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println("Importing "+filename+" isn't your thing.");
        }
        return output;
    }

    public static boolean safe(short[][] terrain, int x, int y){
        if(x>0&&Math.abs(terrain[x-1][y]-terrain[x][y])>1)return false;
        if(y>0&&Math.abs(terrain[x][y-1]-terrain[x][y])>1)return false;
        if(x<d-1&&Math.abs(terrain[x+1][y]-terrain[x][y])>1)return false;
        if(y<d-1&&Math.abs(terrain[x][y+1]-terrain[x][y])>1)return false;
        return true;
    }

    public static void saveImage(String filename, short[][] terrain){
        BufferedImage img =new BufferedImage(d,d,BufferedImage.TYPE_INT_RGB);
        for(int i=0;i<d;i++){
            for (int j = 0; j < d; j++){
                int v = terrain[i][j];
                img.setRGB(i, j, (new Color(v, v, v)).getRGB());
                if(touched[i][j])img.setRGB(i,j,(new Color(0,0, v).getRGB()));
                if(!safe(terrain, i, j))img.setRGB(i,j,(new Color(v,0,0)).getRGB());
            }
        }
        for(int i=0;i<500;i++){
            for(int j=0;j<500;j++){
                int v = terrain[i+cityx][j+cityy];
                img.setRGB(i+cityx, j+cityy, (new Color(0, v, 0)).getRGB());
            }
        }
        try{
            ImageIO.write(img,"png",new File(filename));
        }
        catch(Exception e){}
    }

    public static int[] getCityLoc(short[][] terrain){
        short[][] grid=new short[100][100];
        for(int i=0;i<100;i++){
            for(int j=0;j<100;j++){
                grid[i][j]=terrain[i*100][j*100];
            }
        }
        int min=-1;
        int[] coords=new int[2];
        for(int i=12;i<85;i++){
            for(int j=12;j<85;j++){
                int total=0;
                int height=grid[i][j];
                for(int k=-2;k<3;k++){
                    for(int l=-2;l<3;l++){
                        total+=Math.abs(grid[i+k][j+l]-height);
                    }
                }
                if(min==-1||total<min){
                    min=total;
                    coords[0]=i;
                    coords[1]=j;
                }
                if(total<275&&Math.random()<.10){
                    return new int[]{i,j};
                }
            }
        }
        return coords;
    }

    public static boolean cityOK(short[][] terrain){
        short height=terrain[cityx][cityy];
        for(int i=0;i<500;i++){
            for(int j=0;j<500;j++){
                if(terrain[cityx+i][cityy+j]!=height)return false;
            }
        }
        return true;
    }

    public static void main(String[] args){
        long start=System.nanoTime();
        short[][] terrain = getImageArray("terrain.png");
        short[][] original = new short[d][d];
        short[][] cost = getImageArray("costMap.png");
        {
            for(int i = 0; i < d; i++) {
                for (int j = 0; j < d; j++) {
                    original[i][j] = terrain[i][j];
                }
            }
        }
        cityx=(int)((d-1500)*Math.random())+500;
        cityy=(int)((d-1500)*Math.random())+500;
        int[] coords=getCityLoc(terrain);
        cityx=coords[0]*100;
        cityy=coords[1]*100;
        do {
            short cityHeight = terrain[cityx][cityy];
            for (int i = 0; i < 500; i++) {
                for (int j = 0; j < 500; j++) {
                    if (terrain[cityx + i][cityy + j] > cityHeight) cityHeight = terrain[cityx + i][cityy + j];
                }
            }

            for (int i = 0; i < 500; i++) {
                for (int j = 0; j < 500; j++) {
                    setHeight(terrain, i + cityx, j + cityy, cityHeight);
                }
            }
            for (int i = -1; i < 501; i++) {
                for (int j = -1; j < 501; j++) {
                    if (i == -1 || i == 500 || j == -1 || j == 500){
                        while(terrain[cityx+i][cityy+j]<cityHeight-1){
                            addDirt(terrain, i + cityx, j + cityy);
                        }
                    }
                }
            }
        }while(!cityOK(terrain));
        
        saveImage("output.png",terrain);

        long end=System.nanoTime();
        System.out.println("Time elapsed: "+((end-start)/Math.pow(10,9))+" seconds");

        long totalCost=0;
        for(int i=0;i<d;i++) {
            for (int j = 0; j < d; j++) {
                totalCost += (Math.abs(terrain[i][j] - original[i][j])) * cost[i][j];
            }
        }
        System.out.println("Total cost: "+totalCost+" acorns");
        System.out.println("City x: "+cityx+"\nCity y: "+cityy);

        end=System.nanoTime();
        System.out.println("Time elapsed: "+((end-start)/Math.pow(10,9))+" seconds");

    }

}
