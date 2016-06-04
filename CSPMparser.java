import java.io.*;
import java.*;
import java.util.*;
import java.lang.*;
import java.lang.Character;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.Charset;
import java.nio.file.Files;
import CSPMparser.parser.*;
import CSPMparser.lexer.*;
import CSPMparser.node.*;


public class CSPMparser
{
public String newstream;
public String currentFile;

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

public String getPath()
{
	
	File temp = new File("temp.temp");
	String absolutePath = temp.getAbsolutePath();				
	String filePath = absolutePath.
	substring(0,absolutePath.lastIndexOf(File.separator));						
	return filePath;
}


public int parseFilesInFolder(File folder, Boolean help)
{
	int i = 0;
	for (File fileEntry : folder.listFiles()) 
	{
		
		if (fileEntry.isDirectory())
		{
			i += parseFilesInFolder(fileEntry,help);
		} 
		else 
		{
			String ext = getExtension(fileEntry.toString());				
			if(ext.equals("csp"))
			{
				if(help)
				System.out.println("Analysiere Syntax für "+fileEntry.getName()+":");
				else
				System.out.println("Analysiere Syntax für "+fileEntry.getName()+"...");
				try 
				{
					StreamEdit se = new StreamEdit(fileEntry.toString(),help);
					currentFile = getStringFromFile(fileEntry.toString());
					newstream = se.editTokens();
					StringReader sr = new StringReader(newstream);
					BufferedReader br = new BufferedReader(sr); 
					Lexer l = new Lexer(new PushbackReader(br,20000));
					Parser p = new Parser(l);
					Start tree = p.parse();
					if(help)
					System.out.println("\nParsing für "+fileEntry.getName()+" erfolgreich.\n");
					else
					System.out.println("Parsing für "+fileEntry.getName()+" erfolgreich.\n");
					i++;
				} 	
				catch (Exception e) 
				{
					String[] pos = getPosFromException(e);
					int zeile  = Integer.parseInt(pos[0]);
					int spalte = Integer.parseInt(pos[1]);
					String h = sync(zeile,spalte,e);
					throw new RuntimeException("\n"+h);
				}
			}
		}
	}
	return i;
}

		
public void parseFile(String s, Boolean help)
{
	System.out.println("Analysiere Syntax:");
	try 
	{							
		StreamEdit se = new StreamEdit(s, help);
		currentFile = getStringFromFile(s);
		newstream = se.editTokens();
		StringReader sr = new StringReader(newstream);
		BufferedReader br = new BufferedReader(sr); 
		Lexer l = new Lexer(new PushbackReader(br,16384));
		Parser p = new Parser(l);
		Start tree = p.parse();
		System.out.println("\nIhr CSP_M-Code konnte erfolgreich geparst werden.");

	} 	
	catch (Exception e) 
	{
		String[] pos = getPosFromException(e);
		int zeile  = Integer.parseInt(pos[0]);
		int spalte = Integer.parseInt(pos[1]);
		String h = sync(zeile,spalte,e);
		throw new RuntimeException("\n"+h);
	}		
}


public String sync(int u, int o, Exception exc)
{	
	char[] cf = currentFile.toCharArray();
	char[] ns = newstream.toCharArray();
	
	int zeile = u;
	int spalte = o;
	int error_index = 0;
	int zcount = 1;
	int scount = 1;
	boolean b = true;
	
	//Ermittle Index in String aus Zeile und Spalte
	for(zcount=1;zcount<=zeile;zcount++)
	{
		b = true;
		scount = 0;
		while(b && !(zcount == zeile && scount == spalte-1))
		{
			if(ns[error_index] == '\r' && ns[error_index+1] == '\n')
			{
				error_index+=2;
				b = false;
			}
			else if(ns[error_index] == '\r' || ns[error_index] == '\n')
			{
				error_index++;
				b = false;
			}					
			else
			{
				error_index++;
				scount++;
			}					
		}
	}
					
	int e = 0; //e ist der äquivalente Fehlerindex im Dokument
	for(int d=0;d<error_index;d++)
	{
		while(ns[d] != cf[e])
		{
			e++;
		}
	}
	e++; //Fehler ist hinter dem letzten Zeichen*/
	
	//Nun ist der Fehlerindex im Dokument ermittelt.
	//Finde nun Zeile und Spalte des Fehlers mit Hilfe des Indexes.
	int icount = 0; //Fehlerindex in Array
	spalte = 1;
	zeile = 1;

	while(icount<e)
	{	
			if(cf[icount] == '\r' && cf[icount+1] == '\n')
			{
				zeile++;
				spalte = 1;
				icount+=2;

			}
			else if(cf[icount] == '\r' || cf[icount] == '\n')
			{
				zeile++;
				spalte = 1;
				icount++;
			}					
			else
			{
				icount++;
				spalte++;
			}	
	}
	
	//Exception String neu formatieren und austauschen
	char[] ex = exc.getMessage().toCharArray();
	int i = 0;
	
	while(ex[i] != ']')
	{
		i++;
	} 

	String newStr = "["+zeile+","+spalte+"]";
	for(int j = i+1; j<ex.length;j++)
	{
		newStr += ex[j];
	}
	return newStr;
}


public String[] getPosFromException(Exception f)
{
	char[] g = f.getMessage().toCharArray();
	boolean u = true;
	String n = "";
	int count = 0;
	while(u)
	{
		if(g[count] == '[')
		{
			count++;
		}
		if(g[count] == ']')
		{
			u = false;
		}
		else
		{
			n+=g[count];
			count++;
		}
	}
	String[] pos = n.split(",");
	return pos;
}


public String getStringFromFile(String fp)
{
	try 
	{	
		byte[] encoded = Files.readAllBytes(Paths.get(fp));
		String file_content = new String(encoded, Charset.defaultCharset());
		return file_content;
	}
	catch(Exception e) 		
	{
		throw new RuntimeException("\n"+e.getMessage());
	}	
	
}
public static void main(String arguments[]) 
{		
	CSPMparser cspm = new CSPMparser();
	if(arguments.length == 3)
	{
		if((arguments[0].toString().equals("-parse") 
			&& arguments[1].toString().equals("-h"))
			||(arguments[1].toString().equals("-parse") 
			&& arguments[0].toString().equals("-h")))
		{
			cspm.parseFile(arguments[2],true);
		}
	}
	else if(arguments.length == 2)
	{	
		if(arguments[0].toString().equals("-parse") 
			&& !(arguments[1].toString().equals("-h")) )
		{
			cspm.parseFile(arguments[1],false);
		}
		if((arguments[0].toString().equals("-parseAll") 
			&& arguments[1].toString().equals("-h"))
			||(arguments[1].toString().equals("-parseAll") 
			&& arguments[0].toString().equals("-h")))
		{
			Boolean help = true;
			File folder = new File(cspm.getPath());
			int k = cspm.parseFilesInFolder(folder,true);
			if(k == 1)
			{
				System.out.println("\nDie CSPM-Datei wurde erfolgreich geparst!");
			}
			else if(k==2)
			{
				System.out.println("\nDie beiden CSPM-Dateien wurden erfolgreich geparst!");
			}
			else
			{
				System.out.println("\nAlle "+k+" CSPM-Dateien wurden erfolgreich geparst!");
			}
		}
	}
	else if((arguments.length == 1) && (arguments[0].toString().equals("-parseAll")))
	{
		File folder = new File(cspm.getPath());
		int k = cspm.parseFilesInFolder(folder,false);
		if(k == 1)
		{
			System.out.println("\nDie CSPM-Datei wurde erfolgreich geparst!");
		}
		else if(k==2)
		{
			System.out.println("\nDie beiden CSPM-Dateien wurden erfolgreich geparst!");
		}
		else
		{
			System.out.println("\nAlle "+k+" CSPM-Dateien wurden erfolgreich geparst!");
		}
	}
	else
	{
		System.out.println("Eingabe ungueltig!");
		System.exit(1);
	}	
}

}