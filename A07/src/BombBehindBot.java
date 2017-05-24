import java.util.*;
import java.io.*;
import java.net.*;

public class BombBehindBot
{
	public static void main(String[] args) throws UnknownHostException,IOException
	{
		Socket s = new Socket("localhost", 9090);
		PrintWriter out=new PrintWriter(s.getOutputStream(), true);
		BufferedReader in=new BufferedReader(new InputStreamReader(s.getInputStream()));
		
		//SEND YOUR NAME SO I KNOW WHO EACH PERSON IS
		int num=(int)(Math.random()*100);
		out.println("BombBehindBot"+num);
		
		
		while(true)
		{
			String field=in.readLine();
			int location=findLocation(field); //find column where the s is
			int otherLocation=findOtherLocation(field); //find column where the o is
			if(Math.random()<.15 && otherLocation>location) //possibly bomb behind if the s hasn't passed the o
			{
				int size=(int)(Math.random()*3+1);
				out.println("bomb "+(location-1-size)+" "+size);
				System.out.println("bomb "+(location-1-size)+" "+size);
			}
			else //run like crazy if the s has passed the o
			{
				out.println("move right");
				System.out.println("move right");
				
			}
		}
	}
	
	public static int findLocation(String field)
	{
		String[] lines=field.split(";");
		for(int i=0;i<lines.length;i++)
		{
			if(lines[i].indexOf("s")>-1)return lines[i].indexOf("s");
		}
		return 0;
	}
	
	public static int findOtherLocation(String field)
	{
		String[] lines=field.split(";");
		for(int i=0;i<lines.length;i++)
		{
			if(lines[i].indexOf("o")>-1)return lines[i].indexOf("o");
		}
		return 0;
	}
}
