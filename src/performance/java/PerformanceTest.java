import java.io.*;
import java.text.*;
import java.math.*;
import java.util.*;
import java.lang.*;
import java.util.regex.*;
import java.lang.Character;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class PerformanceTest
{
	public int iterations = 10; // How often do you want to parse each File?
	public long elapsedTime = 0;
	public long startTime = 0;
	public long endTime = 0;
	public PrintWriter pw;
	public String s;
	public char[] c;
	public Process p;
	public String[] command = {"cmd"};
	private ArrayList<Long> average;
	public long totalCspmf;
	public long totalCspmj;

	public PerformanceTest(String searchLoc, String resultLoc) throws Exception
	{
		totalCspmj = 0;
		totalCspmf = 0;
		pw = new PrintWriter(new File(resultLoc+"/PerformanceResult.txt"));
		s = "file                              cspmf time (s)  cspmj time (s)  factor (s)\n\n";
		File folder = new File(searchLoc);
		elapsedTime = 0;
		startTime = 0;	
		average = new ArrayList<Long>();
		System.out.println("Generating performance results. Please wait...");
		generatePerformanceComparison(folder);
		double tcf = convert(totalCspmf);
		double tcj = convert(totalCspmj);
		double totalFactor = convert(divideLong(totalCspmf,totalCspmj));
//		parseAll();
//		double parseAllFactor = convert(divideLong(elapsedTime,totalCspmf));
//		double all = convert(elapsedTime);
		s += "TOTAL                             "+tcf+"           "+tcj+"           "+totalFactor+"\n";
//		s += "ParseAll                                "+tcf+"             "+all+"             "+parseAllFactor;
		pw.write(s);
		pw.close();
		System.out.println("Your performance results have been created succesfully!");
	}
	
	public void generatePerformanceComparison(File folder) throws Exception
	{
		for (File fileEntry : folder.listFiles()) 
		{		
			if (fileEntry.isDirectory())
			{
				generatePerformanceComparison(fileEntry);
			} 
			else if(getExtension(fileEntry.toString()).equals("csp"))
			{	
				long cspmfTime = 0;
				long cspmjTime = 0;
				cspmfCompile(fileEntry.toString());		
				totalCspmf += elapsedTime;
				c = Paths.get(fileEntry.toString()).getFileName().toString().toCharArray(); //get file name and convert to char
				for(int i=0;i<34;i++)
				{
					if(i<c.length){s+=c[i];}
					else{s+=" ";}
				}
				cspmfTime = elapsedTime;
				c = String.valueOf(convert(elapsedTime)).toCharArray();
				for(int i=0;i<16;i++)
				{
					if(i<c.length){s+=c[i];}
					else{s+=" ";}
				}				
				cspmjCompile(fileEntry.toString());
				cspmjTime = elapsedTime;
				totalCspmj += elapsedTime;
				c = String.valueOf(convert(elapsedTime)).toCharArray();
				for(int i=0;i<16;i++)
				{
					if(i<c.length){s+=c[i];}
					else{s+=" ";}
				}		
				s += String.valueOf(convert(divideLong(cspmfTime,cspmjTime)))+"\r\n";
			}

		}			
	}
	
	public void cspmfCompile(String filepath) throws Exception
	{
			for(int i=0;i<iterations;i++)
			{	
				startTime = System.nanoTime(); 
				p = Runtime.getRuntime().exec("cspmf.exe translate "+filepath+" --prologOut=cspmfOUT.temp");				
				p.waitFor();
				endTime = System.nanoTime();
				elapsedTime = endTime - startTime;
				p.destroy();
				average.add(elapsedTime);				
			}
			elapsedTime = getAverage();
			Files.delete(Paths.get("cspmfOUT.temp"));		
	}
	
	public void cspmjCompile(String filepath) throws Exception
	{	
		for(int i=0;i<iterations;i++)
		{	
			startTime = System.nanoTime(); 
			CSPMparser cspmj = new CSPMparser(filepath,"cspmjOUT.temp");
			endTime = System.nanoTime();
			elapsedTime = endTime - startTime;
			p.destroy();
			average.add(elapsedTime);
		}
			elapsedTime = getAverage();
			Files.delete(Paths.get("cspmjOUT.temp.pl"));
	}	
	
	public String getPath()
	{
		
		File temp = new File("temp.temp");
		String absolutePath = temp.getAbsolutePath();				
		String filePath = absolutePath.
		substring(0,absolutePath.lastIndexOf(File.separator));						
		return filePath;
	}	
	
	public String getExtension(String filename)
	{
		if (filename == null) 
		{
			return null;
		}
		int extensionPos = filename.lastIndexOf('.');
		int lastUnixPos = filename.lastIndexOf('/');
		int lastWindowsPos = filename.lastIndexOf('\\');
		int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);
		int index = lastSeparator > extensionPos ? -1 : extensionPos;
		if (index == -1) 
		{
			return "";
		} 
		else 
		{
			return filename.substring(index + 1);
		}
	}	
	
	public long getAverage()
	{
		long a = 0;
		for(int i=0;i<average.size();i++)
		{
			a += average.get(i);
		}
		a = a/average.size();
		average.clear();
		return a;
	}

	public double convert(long l)
	{
		double d = (double)l/1000000000.0;	
        return (double)Math.round(d * 1000d) / 1000d;
	}	
	
	public long divideLong(long l1,long l2)
	{
		return (long)((double)l1/(double)l2*1000000000.0);
	}	
	
	public static void main(String[] args)
	{
		try
		{
			PerformanceTest pt = new PerformanceTest(args[0],args[1]);
		}
		catch(Exception e)
		{
			System.out.println("An Exception was thrown. "+e.getMessage());
		}
	}
}