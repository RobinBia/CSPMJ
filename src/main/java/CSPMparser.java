import java.io.*;
import java.*;
import java.util.*;
import java.lang.*;
import java.util.regex.*;
import java.lang.Character;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import CSPMparser.parser.*;
import CSPMparser.lexer.*;
import CSPMparser.node.*;

public class CSPMparser
{
	private String versionNum;
	private String versionString;
	private String newstream;
	private String currentFile;
	private int exceptionCounter;
	private ArrayList<CommentInfo> commentList;
	private HashMap<Integer,Character> commentMap;

	public CSPMparser()
	{
		setVersion("0 64 160720");
		exceptionCounter = 0;
		commentMap = new HashMap<Integer,Character>();
		commentList = new ArrayList<CommentInfo>();
	}

	public int getExceptionCounter()
	{
		return exceptionCounter;
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

	public String getPath()
	{
		
		File temp = new File("temp.temp");
		String absolutePath = temp.getAbsolutePath();				
		String filePath = absolutePath.
		substring(0,absolutePath.lastIndexOf(File.separator));						
		return filePath;
	}


	public int parseFilesInFolder(File folder, Boolean show)
	{
		int fileCounter = 0;
		for (File fileEntry : folder.listFiles()) 
		{		
			if (fileEntry.isDirectory())
			{
				fileCounter += parseFilesInFolder(fileEntry,show);
			} 
			else if(getExtension(fileEntry.toString()).equals("csp"))
			{
				fileCounter++;
				System.out.println("\n\nParsing '"+fileEntry.getName()+"'...");			
				try 
				{
					String s = getStringFromFile(fileEntry.toString());
					parsingRoutine(s,true,true,true,fileEntry.toString(),""); 
				} 	
				catch(Exception e) 
				{
					exceptionCounter++;
					System.out.println(e.getMessage());
				}
			}
			commentList.clear();
		}
		return fileCounter;
	}

	
	public String parseString(String s, boolean renamingActivated) throws CSPMparserException 
	{
		String ret = "";
		try
		{
			ret = parsingRoutine(s,false,false,renamingActivated,"","");
			while(ret.endsWith("\n") || ret.endsWith("\r"))
			{
				ret = ret.substring(0,ret.length()-1);
			}
		}
		catch(CSPMparserException e) 
		{
			throw e;
		}
		
		return ret;
	}


	public void parseFile(String inputFile, String outputFile)
	{
		System.out.println("Parsing '"+inputFile+"'...");
		try 
		{			
			String s = getStringFromFile(inputFile);
			parsingRoutine(s,true,true,true,inputFile,outputFile);
		//	parsingRoutine(s,true,false,false,inputFile,outputFile);
		} 	
		catch(Exception e) 
		{
			System.out.println(e.getMessage());
		}

	}

	
	public String parsingRoutine(String newstream, boolean createPrologFile, boolean printSrc, boolean renamingActivated,
	String inputFile, String outputFile) throws CSPMparserException
	{
		try 
		{		
			newstream = saveComments(newstream);
			newstream = includeFile(newstream);
			newstream = saveComments(newstream);
			
			TriangleBracketSubstitution tbs = new TriangleBracketSubstitution(newstream);
			if(!newstream.equals(""))
			newstream = tbs.findTriangles();

			StringReader sr = new StringReader(newstream);
			BufferedReader br = new BufferedReader(sr); 
			Lexer l = new LexHelper(new PushbackReader(br,100000));
			Parser p = new Parser(l);
			Start tree = p.parse();	
			
			StatementPatternCheck spc = new StatementPatternCheck();
			tree.apply(spc);
			
			TreeLogicChecker tlc = new TreeLogicChecker();
			tree.apply(tlc);
			
			PrologTermOutput pto = new PrologTermOutput();
			SymbolCollector sc = new SymbolCollector();
			tree.apply(sc);
			ArrayList<SymInfo> symbols = sc.getSymbols();
			PrologGenerator pout = new PrologGenerator(pto,symbols,printSrc,renamingActivated,commentList);
			tree.apply(pout);										

			System.out.println("Generating Prolog-File.");
			
			if(createPrologFile)
			createPrologFile(pto,inputFile,null,outputFile);
			else
			return pto.getStringWriter().toString();

		} 
		catch (ParserException e) 
		{
			System.out.println("An Exception was thrown.");
			if(createPrologFile)
			createPrologFile(null,inputFile,e,outputFile);
			throw new CSPMparserException(e.getToken(),e.getLocalizedMessage());
		} 
		catch(LexerException e) 
		{
			System.out.println("An Exception was thrown.");
			if(createPrologFile)
			createPrologFile(null,inputFile,e,outputFile);			
			throw new RuntimeException(e.getMessage());
		} 
		catch(IOException e) 
		{
			System.out.println("An Exception was thrown.");
			if(createPrologFile)
			createPrologFile(null,inputFile,e,outputFile);
			throw new RuntimeException(e.getMessage());
		}		
		catch(Exception e) 
		{
			System.out.println("An Exception was thrown.");
			if(createPrologFile)
			createPrologFile(null,inputFile,e,outputFile);
			throw new RuntimeException(e.getMessage());
		}
		return "";
	}
	
	public void createPrologFile(PrologTermOutput pto,String filename, Exception e, String outputFile)
	{
		try
		{
			PrintWriter writer;
			if(outputFile.equals(""))
			{
				writer = new PrintWriter(filename+".pl", "UTF-8");
			}
			else
			{
				String name = outputFile.substring(12,outputFile.length());
				writer = new PrintWriter(name+".pl", "UTF-8");
			}
			if(e == null)
			{
				writer.println(":- dynamic parserVersionNum/1, parserVersionStr/1, parseResult/5."
				+"\n:- dynamic module/4."
				+"\n'parserVersionStr'('"+versionString+"')."
				+"\n'parseResult'('ok','',0,0,0)."
				+"\n:- dynamic channel/2, bindval/3, agent/3."
				+"\n:- dynamic agent_curry/3, symbol/4."
				+"\n:- dynamic dataTypeDef/2, subTypeDef/2, nameType/2."
				+"\n:- dynamic cspTransparent/1."
				+"\n:- dynamic cspPrint/1."
				+"\n:- dynamic pragma/1."
				+"\n:- dynamic comment/2."
				+"\n:- dynamic assertBool/1, assertRef/5, assertTauPrio/6."
				+"\n:- dynamic assertModelCheckExt/4, assertModelCheck/3."
				+"\n:- dynamic assertHasTrace/3, assertHasTraceExt/4"
				+"\n:- dynamic assertLtl/4, assertCtl/4."
				+"\n'parserVersionNum'(["+versionNum+"])."
				+"\n'parserVersionStr'('"+versionString+"').");
				File file = new File(filename+".pl");	
				String str = pto.getStringWriter().toString();

				while(str.endsWith("\n") || str.endsWith("\r"))
				{
					str = str.substring(0,str.length()-1);
				}
				writer.print(str);
				
				
				writer.close();
			}
			else
			{
				writer.print(":- dynamic parserVersionNum/1, parserVersionStr/1, parseResult/5."
				+"\n:- dynamic module/4."
				+"\n'parserVersionStr'('"+versionString+"')."
				+"\n'parseResult'('parseError',"+e.getMessage()+").");
				writer.close();
			}

		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex.getMessage());
		}
	}

	public String getStringFromFile(String fp)
	{
		try 
		{	
			byte[] encoded = Files.readAllBytes(Paths.get(fp));
			String file_content = new String(encoded, StandardCharsets.UTF_8);
			return file_content;
		}
		catch(Exception e) 		
		{
			throw new RuntimeException("Your File has not been converted to a String.");
		}
	}
	
	public int getLineFromPos(int pos, char[] c)
	{
		int lineCount = 1;
		for(int i = 0; i<pos;i++)
		{
			if(c[i] == '\r' && c[i] == '\n')
			{
				lineCount++;
				i++;
			}
			else if(c[i] == '\r')
			{
				lineCount++;
			}
			else if(c[i] == '\n')
			{
				lineCount++;
			}
		}
		return lineCount;
	}
	
	public int getColumnFromPos(int pos, char[] c)
	{
		int columnCount = 1;
		for(int i = 0; i<pos;i++)
		{
			if(c[i] == '\r' && c[i] == '\n')
			{
				columnCount = 1;
				i++;
			}
			else if(c[i] == '\r')
			{
				columnCount = 1;
			}
			else if(c[i] == '\n')
			{
				columnCount = 1;
			}
			else
			{
				columnCount ++;
			}
		}
		return columnCount;	
	}
	//Deletes content in range l-r in Chararray and saves it in commentMap
	public char[] saveRange(int l, int r, char[] q, boolean isMultiline)
	{		
		String temp = "";
		for(int i = l; i<=r;i++)
		{
			temp += q[i];
			q[i] = ' ';
		}
		
		CommentInfo cInfo = new CommentInfo(getLineFromPos(l,q),getColumnFromPos(l,q),l,r-l,isMultiline,temp);
		commentList.add(cInfo);
		return q;
	}

	
	public String saveComments(String ts)
	{
		String newTS = ts;	
		char[] c = ts.toCharArray();	
		int i = 0;
		
		while(i<c.length)
		{
			if(c[i] == '{' && c[i+1] == '-')
			{
				int v = 0;
				int count_crlf = 0;
				int count_cr = 0;
				int count_lf = 0;
				boolean b = true;
				while(b)
				{

					if(c[i+v] == '-' && c[i+v+1] == '}')
					{
						c = saveRange(i,i+v+1,c,true);
						b = false;
						i=i+v+1;
					}
					else
					{
						v++;
					}
				}
			}
			else if(c[i] == '-' && c[i+1] == '-')
			{
				int w = 0;
				boolean b = true;
				while(b)
				{
					if(c[i+w] == '\r' && c[i+w+1] == '\n')
					{
						c = saveRange(i,i+w-1,c,false); //nicht i+w oder i+w+1, da newline erhalten bleiben muss
						b = false;
						i = i+w+1;
					}
					else if(c[i+w] == '\r')
					{
						c = saveRange(i,i+w-1,c,false);
						b = false;
						i = i+w;
					}
					else if(c[i+w] == '\n')
					{
						c = saveRange(i,i+w-1,c,false);
						b = false;
						i = i+w;
					}
					else
					{
						w++;
						if((i+w) == c.length-1)
						{
							c = saveRange(i,i+w,c,false);
							b = false;
							i = i+w;
						}
					}
				}
			}
			i++;
		}	
		newTS = String.valueOf(c);
		return newTS; 
	}
	
	public String includeFile(String incl)
	{
		Pattern pattern = Pattern.compile("include \"(.*)\"");
		Matcher matcher = pattern.matcher(incl);
		ArrayList<String> al = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		String path = "";
		while(matcher.find())
		{	
			path = matcher.group(1);
			
			File f = new File(path);
			if(f.exists() && !f.isDirectory()) 
			{ 
				System.out.println(path+"\nhas been included successfully.");
			}
			else
			{
				throw new RuntimeException("File "+path+" was not found.");
			}
			String str = getStringFromFile(path);
			matcher.appendReplacement(sb,str);	
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
	
	public void setVersion(String s)
	{
		String[] subnum = s.split(" ");
		versionNum = "";
		versionString = "CSPMJ V";
		for(int i = 0;i<subnum.length;i++)
		{
			if(i<subnum.length-1)
			versionNum += subnum[i]+",";
			else
			versionNum += subnum[i];
		}
		for(int j = 0;j<subnum.length;j++)
		{
			if(j<subnum.length-1)
			versionString += subnum[j]+".";
			else
			versionString += subnum[j];
		}
	}
	
	public static void main(String arguments[]) 
	{		
		CSPMparser cspm = new CSPMparser();
		if(arguments.length == 3 && arguments[0].equals("-parse"))
		{	if(arguments[2].startsWith("--prologOut="))
			{
				cspm.parseFile(arguments[1],arguments[2]);
			}
			else
			{
				System.out.println("Incorrect input!");
				System.exit(1);
			}
		}
		else if(arguments.length == 2 && arguments[0].equals("-parse"))
		{	
			cspm.parseFile(arguments[1],"");
		}
		else if((arguments.length == 1) && (arguments[0].equals("-parseAll")))
		{
			File folder = new File(cspm.getPath());
			int fileCounter = cspm.parseFilesInFolder(folder,false);

			if(fileCounter-cspm.getExceptionCounter() == 0)
			{
				System.out.println("\nNo CSPM-File has been parsed successfully!");
			}
			else
			{
				System.out.println("\n"+(fileCounter-cspm.getExceptionCounter())+" of "+fileCounter+" CSPM-Files have been parsed successfully!");
			}
		}
		else
		{
			System.out.println("Incorrect input!");
			System.exit(1);
		}	
	}
}