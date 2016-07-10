import java.io.IOException;
import java.util.Locale;
import CSPMparser.analysis.*;
import CSPMparser.node.*;
import java.util.*;
import java.io.*;




public class SymbolCollector extends DepthFirstAdapter
{

	private int currentInParams;
	private HashMap<String,ArrayList<SymInfo>> symbols; //Identifier,Counter
	private int groundrep;
	private int letWithinCount; //renumber let-within blocks
	private int currentLetWithinNum; //Saves a reference to the current let-within block
	private HashMap<Integer,Integer> letWithinStruct;
				// counter ,predecessor
	public SymbolCollector()
	{
		letWithinCount = 0;
		currentLetWithinNum = 0;
		letWithinStruct = new HashMap<Integer,Integer>();
		currentInParams = 0;
		groundrep = 0;
		symbols = new HashMap<String,ArrayList<SymInfo>>();
		//currentParams = new ArrayList<String>();
	}
//***************************************************************************************************************************************************
//Datatype, Subtype, Nametype

    @Override
    public void caseANtypeTypes(ANtypeTypes node)
    {
        inANtypeTypes(node);
        if(node.getId() != null)
        {
            node.getId().apply(this);
			String str = node.getId().toString().replace(" ","");
			ArrayList<SymInfo> temp = new ArrayList<SymInfo>();
			SymInfo si = new SymInfo(node.getId(),"Nametype",0);
			temp.add(si);
			symbols.put(str,temp);
        }
        if(node.getTypeExp() != null)
        {
            node.getTypeExp().apply(this);
        }

        outANtypeTypes(node);
    }

    @Override
    public void caseATypedefTypes(ATypedefTypes node)
    {
        inATypedefTypes(node);
        if(node.getId() != null)
        {
            node.getId().apply(this);
			String str = node.getId().toString().replace(" ","");
			ArrayList<SymInfo> temp = new ArrayList<SymInfo>();
			SymInfo si = new SymInfo(node.getId(),"Datatype",0);
			temp.add(si);
			symbols.put(str,temp);
        }
        {
            List<PTypes> copy = new ArrayList<PTypes>(node.getTypedefList());
            for(PTypes e : copy)
            {
                e.apply(this);
            }		
        }
        outATypedefTypes(node);
    }

    @Override
    public void caseAClauseTypes(AClauseTypes node)
    {
        inAClauseTypes(node);	
        if(node.getClauseName() != null)
        {
            node.getClauseName().apply(this);
			String str = node.getClauseName().toString().replace(" ","");
			ArrayList<SymInfo> temp = new ArrayList<SymInfo>();
			SymInfo si = new SymInfo(node.getClauseName(),"Constructor of Datatype",0);
			temp.add(si);
			symbols.put(str,temp);
        }
        if(node.getDotted() != null)
        {
            node.getDotted().apply(this);
        }
        outAClauseTypes(node);
    }

//***************************************************************************************************************************************************
//Channels
	@Override
    public void caseAChannelDef(AChannelDef node)
    {
        inAChannelDef(node);
        {
            List<PId> copy = new ArrayList<PId>(node.getChanList());
            for(PId e : copy)
            {
                e.apply(this);
				String str = e.toString().replace(" ","");
				ArrayList<SymInfo> temp = new ArrayList<SymInfo>();
				SymInfo si = new SymInfo(e,"Channel",0);
				temp.add(si);
				symbols.put(str,temp);
            }
        }
        if(node.getChanType() != null)
        {
            node.getChanType().apply(this);
        }
        outAChannelDef(node);
    }
//***************************************************************************************************************************************************
//Patterns	
	
    @Override
    public void caseAPatternExp(APatternExp node)
    {
        inAPatternExp(node);
        if(node.getPattern1() != null)
        {
			groundrep +=1;
            node.getPattern1().apply(this);
			groundrep -=1;
        }
        if(node.getProc1() != null)
        {
            node.getProc1().apply(this);
        }
        outAPatternExp(node);
    }
	
    @Override
    public void caseAVarPattern(AVarPattern node)
    {
        inAVarPattern(node);
		String str = node.getId().toString().replaceAll(" ","");		
		
		if(groundrep>0)
		{
				if(symbols.containsKey(str))
				{
					ArrayList<SymInfo> temp = symbols.get(str);
					SymInfo si = new SymInfo(node.getId(),"Ident (Groundrep.)",currentLetWithinNum);
					temp.add(si);
					symbols.put(str,temp);
				}
				else
				{
					ArrayList<SymInfo> temp = new ArrayList<SymInfo>();
					SymInfo si = new SymInfo(node.getId(),"Ident (Groundrep.)",currentLetWithinNum);
					temp.add(si);
					symbols.put(str,temp);
				}
		}
		else if(currentInParams>0)
		{		
			//currentParams.add(str);
		
			if(symbols.containsKey(str))
			{
				ArrayList<SymInfo> temp = symbols.get(str);
				SymInfo si = new SymInfo(node.getId(),"Ident (Prolog Variable)",currentLetWithinNum);
				temp.add(si);
				symbols.put(str,temp);
			}
			else
			{
				ArrayList<SymInfo> temp = new ArrayList<SymInfo>();
				SymInfo si = new SymInfo(node.getId(),"Ident (Prolog Variable)",currentLetWithinNum);
				temp.add(si);
				symbols.put(str,temp);
			}
		}
		
        if(node.getId() != null)
        {
            node.getId().apply(this);
        }		
        outAVarPattern(node);
    }
//***************************************************************************************************************************************************
//Expressions

	@Override
    public void caseAFunctionExp(AFunctionExp node)
    {
        inAFunctionExp(node);
        if(node.getId() != null)
        {
            node.getId().apply(this);			
			String id = node.getId().toString().replace(" ","");
			boolean found = false;
			if(symbols.get(id) != null)
			{
				for(int k = 0;k<symbols.get(id).size();k++)
				{
					if(symbols.get(id).get(k).getSymbolInfo().equals("Function or Process"))
					{
						int v = symbols.get(id).get(k).getLetWithinCount();
						if(v == currentLetWithinNum)
						{
							found = true;
							//keep in symbols, do not replace !!!
							break;
						}
					}
					
				}
			}
			if(!found)
			{
				if(symbols.get(id) == null)
				{
					ArrayList<SymInfo> temp = new ArrayList<SymInfo>();
					SymInfo si = new SymInfo(node.getId(),"Function or Process",currentLetWithinNum);
					temp.add(si);
					symbols.put(id,temp);
				}
				else
				{
					ArrayList<SymInfo> temp = symbols.get(id);
					SymInfo si = new SymInfo(node.getId(),"Function or Process",currentLetWithinNum);
					temp.add(si);
					symbols.put(id,temp);
				}
			}
        }
        if(node.getParameters() != null)
        {
			currentInParams += 1;
            node.getParameters().apply(this);
			currentInParams -= 1;
        }
        if(node.getProc1() != null)
        {
            node.getProc1().apply(this);
        }
        outAFunctionExp(node);
    }	
	
    @Override
    public void caseALambdaExp(ALambdaExp node)
    {
        inALambdaExp(node);
        {
			List<PPattern> copy = new ArrayList<PPattern>(node.getPatternList());
			currentInParams +=1;
            for(PPattern e : copy)
            {
                e.apply(this);
            }
			currentInParams -=1;
        }
        if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outALambdaExp(node);
    }
	
    @Override
    public void caseALetWithinExp(ALetWithinExp node)
    {
        inALetWithinExp(node);
		letWithinCount++;
		letWithinStruct.put(letWithinCount,currentLetWithinNum);
		currentLetWithinNum = letWithinCount;
        {
            List<PDef> copy = new ArrayList<PDef>(node.getDefs());
            for(PDef e : copy)
            {
                e.apply(this);
            }
        }
		currentLetWithinNum = letWithinStruct.get(letWithinCount);
        if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outALetWithinExp(node);
    }
	
    @Override
    public void caseANondetInputPattern(ANondetInputPattern node)
    {
        inANondetInputPattern(node);
		currentInParams +=1;
        if(node.getPattern1() != null)
        {
            node.getPattern1().apply(this);
        }
		currentInParams -=1;
        if(node.getRestriction() != null)
        {
            node.getRestriction().apply(this);
        }
        outANondetInputPattern(node);
    }
	
    @Override
    public void caseAInputPattern(AInputPattern node)
    {
        inAInputPattern(node);
		currentInParams +=1;
        if(node.getPattern1() != null)
        {
            node.getPattern1().apply(this);
        }
		currentInParams -=1;
        if(node.getRestriction() != null)
        {
            node.getRestriction().apply(this);
        }
        outAInputPattern(node);
    }
	
	@Override
    public void caseAIdExp(AIdExp node)
    {
        inAIdExp(node);
		String str = node.getId().toString().replace(" ","");

        if(node.getId() != null)
        {
            node.getId().apply(this);
        }
		//System.out.println(currentParams);
		
        if(node.getTuple() != null)
        {
        }

		
        outAIdExp(node);
    }
//***************************************************************************************************************************************************
	public HashMap<String,ArrayList<SymInfo>> getSymbols()
	{
		return symbols;
	}
	
	
    // public ArrayList<Integer> getSrcLoc(Node node) 
    // {
		   // /* Data type of src_loc in cspmf 
			// * src_loc 
			// {
			   // fixedStartLine   = getStartLine s
			  // ,fixedStartCol    = getStartCol s
			  // ,fixedEndLine     = getEndLine e
			  // ,fixedEndCol      = getEndCol e
			  // ,fixedLen         = getEndOffset e - getStartOffset s
			  // ,fixedStartOffset = getStartOffset s
			// }
		  // */
		// ArrayList<Integer> al
		// p.printNumber(node.getStartPos().getLine());
		// p.printNumber(node.getStartPos().getPos());
		// p.printNumber(node.getEndPos().getLine());
		// p.printNumber(node.getEndPos().getPos());
		// p.printNumber(node.getEndPos().getPos()-node.getStartPos().getPos());
		// p.printNumber(node.getEndPos().getPos()-node.getStartPos().getPos());
		// p.closeTerm();
    // }
}
