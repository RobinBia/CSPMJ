
import java.io.IOException;
import java.util.Locale;
import CSPMparser.analysis.*;
import CSPMparser.node.*;
import java.util.*;
import java.io.*;
import de.prob.prolog.output.IPrologTermOutput;
import de.prob.prolog.output.StructuredPrologOutput;
import de.prob.prolog.term.PrologTerm;

public class PrologGenerator extends DepthFirstAdapter
{
	private final IPrologTermOutput p;
	private final String currentStateID;
	private int currentInParams;
	private HashMap<String,ArrayList<SymInfo>> symbols; //Identifier,Counter
	private int groundrep;
	private ArrayList<String> currentParams;
	
	public PrologGenerator(final IPrologTermOutput pto,final String currentStateID) 
	{
		//super();
		this.p = pto;
		this.currentStateID = currentStateID;
		this.currentInParams = 0;
		groundrep = 0;
		symbols = new HashMap<String,ArrayList<SymInfo>>();
		currentParams = new ArrayList<String>();
	}

	protected void applyPrologGenerator(StructuredPrologOutput pto,String stateID, Start ast) 
	{
		final PrologGenerator prologGenerator = new PrologGenerator(pto,stateID);
		ast.apply(prologGenerator);
	}
	
	@Override
	public void defaultIn(final Node node) 
	{
	//	StringBuffer sb = new StringBuffer(node.getClass().getSimpleName());
	//	sb.setLength(sb.length() - 3);
	//  sb.deleteCharAt(0);
	//	String term = sb.toString().toLowerCase(Locale.ENGLISH);
	//	p.openTerm(term);
	//	p.openTerm("test");
	}
    @Override
	public void defaultOut(final Node node) 
	{
	//	p.closeTerm();
	}
	
	@Override
	public void inStart(final Start node) 
	{
		// Do not call default in Method
	}

	@Override
	public void outStart(final Start node) 
	{
		// Do not call default out Method
	}
	
	
	
	
	
	@Override
    public void caseADefsStart(ADefsStart node)
    {
        inADefsStart(node);
        {
			int count = 0;
            List<PDef> copy = new ArrayList<PDef>(node.getDef());
            for(PDef e : copy)
            {
                e.apply(this);
				p.fullstop();
            }
        }

		for (String key : symbols.keySet()) 
		{

			for(int i = 0;i<symbols.get(key).size();i++)
			{
				Node n = symbols.get(key).get(i).getNode();
				String s = "";
				p.openTerm("symbol");
				if(key.startsWith("_"))
				{
					s = key.substring(1);
				}
				else
				{
					s = key;
				}
				if(i == 0)
				{
					p.printAtom(s);
					p.printAtom(s);
				}
				else
				{
					p.printAtom(s+(i+1));
					p.printAtom(s);
				}				
		
				printSrcLoc(n);
				p.printAtom(symbols.get(key).get(i).getSymbolInfo());
				p.closeTerm();
				p.fullstop();
			}

		}
		
        outADefsStart(node);
    }
	
//***************************************************************************************************************************************************
//Expressions Left Side	
	@Override
    public void caseAExpressionDef(AExpressionDef node)
    {
        inAExpressionDef(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
		printSrcLoc(node);
		p.closeTerm();
		currentParams.clear();
        outAExpressionDef(node);
    }
	
	@Override
    public void caseAFunctionExp(AFunctionExp node)
    {
        inAFunctionExp(node);
		p.openTerm("agent");
        if(node.getId() != null)
        {
            node.getId().apply(this);
			String id = node.getId().toString().replace(" ","");

			ArrayList<SymInfo> temp = new ArrayList<SymInfo>();
			SymInfo si = new SymInfo(node,"Function or Process");
			temp.add(si);
			symbols.put(id,temp);
				
			p.openTerm(node.getId().toString().replace(" ",""));
        }
        if(node.getParameters() != null)
        {
			currentInParams += 1;
            node.getParameters().apply(this);
			currentInParams -= 1;
			p.closeTerm();
        }
        if(node.getProc1() != null)
        {
            node.getProc1().apply(this);
        }
        outAFunctionExp(node);
    }
	
    @Override
    public void caseAPatternExp(APatternExp node)
    {
        inAPatternExp(node);
		p.openTerm("bindval");
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
//***************************************************************************************************************************************************
//Patterns
	
    @Override
    public void caseAVarPattern(AVarPattern node)
    {
        inAVarPattern(node);
		String str = node.getId().toString().replaceAll(" ","");
		
		
		if(groundrep>0)
		{
				ArrayList<SymInfo> temp = new ArrayList<SymInfo>();
				SymInfo si = new SymInfo(node,"Ident (Groundrep.)");
				temp.add(si);
				symbols.put(str,temp);
		}
		else if(currentInParams>0)
		{		
			currentParams.add(str);
		
			if(symbols.containsKey("_"+str))
			{
				ArrayList<SymInfo> temp = symbols.get("_"+str);
				SymInfo si = new SymInfo(node,"Ident (Prolog Variable)");
				temp.add(si);
				symbols.put("_"+str,temp);
			}
			else
			{
				ArrayList<SymInfo> temp = new ArrayList<SymInfo>();
				SymInfo si = new SymInfo(node,"Ident (Prolog Variable)");
				temp.add(si);
				symbols.put("_"+str,temp);
			}
		}
		
        if(node.getId() != null)
        {
            node.getId().apply(this);
        }
		

		if(currentInParams<1)
		{
			p.printAtom(str);
		}
		else
		{
			int j = symbols.get("_"+str).size();
			if(j == 1)
			{
				p.printVariable("_"+str);
			}
			else
			{
				p.printVariable("_"+str+j);
			}
		}
		
        outAVarPattern(node);
    }	
//***************************************************************************************************************************************************	
//Expressions	

    @Override
    public void caseAHideExp(AHideExp node)
    {
        inAHideExp(node);
		p.openTerm("\\");
        if(node.getProc1() != null)
        {
            node.getProc1().apply(this);
        }
        if(node.getBSlash() != null)
        {
            node.getBSlash().apply(this);
        }
        if(node.getProc2() != null)
        {
            node.getProc2().apply(this);
        }
		p.openTerm("src_span_operator");
		p.printAtom("no_loc_info_available");
		printSrcLoc(node.getBSlash());
		p.closeTerm();
		printSrcLoc(node);
		p.closeTerm();
        outAHideExp(node);
    }

    @Override
    public void caseAIleaveExp(AIleaveExp node)
    {
        inAIleaveExp(node);
		p.openTerm("|||");
        if(node.getProc2() != null)
        {
            node.getProc2().apply(this);
        }
        if(node.getILeaving() != null)
        {
            node.getILeaving().apply(this);
        }
        if(node.getProc3() != null)
        {
            node.getProc3().apply(this);
        }
		p.openTerm("src_span_operator");
		p.printAtom("no_loc_info_available");
		printSrcLoc(node.getILeaving());
		p.closeTerm();
		printSrcLoc(node);
		p.closeTerm();
        outAIleaveExp(node);
    }

    @Override
    public void caseAExceptExp(AExceptExp node)
    {
        inAExceptExp(node);
        if(node.getProc3() != null)
        {
            node.getProc3().apply(this);
        }
        if(node.getEvent() != null)
        {
            node.getEvent().apply(this);
        }
        if(node.getProc4() != null)
        {
            node.getProc4().apply(this);
        }
        outAExceptExp(node);
    }

    @Override
    public void caseAGenParExp(AGenParExp node)
    {
        inAGenParExp(node);
        if(node.getProc3() != null)
        {
            node.getProc3().apply(this);
        }
        if(node.getEvent() != null)
        {
            node.getEvent().apply(this);
        }
        if(node.getProc4() != null)
        {
            node.getProc4().apply(this);
        }
        outAGenParExp(node);
    }

    @Override
    public void caseAAlphParExp(AAlphParExp node)
    {
        inAAlphParExp(node);
        if(node.getProc3() != null)
        {
            node.getProc3().apply(this);
        }
        if(node.getEventl() != null)
        {
            node.getEventl().apply(this);
        }
        if(node.getEventr() != null)
        {
            node.getEventr().apply(this);
        }
        if(node.getProc4() != null)
        {
            node.getProc4().apply(this);
        }
        outAAlphParExp(node);
    }

    @Override
    public void caseALinkedParExp(ALinkedParExp node)
    {
        inALinkedParExp(node);
        if(node.getProc3() != null)
        {
            node.getProc3().apply(this);
        }
        if(node.getLinkComp() != null)
        {
            node.getLinkComp().apply(this);
        }
        if(node.getProc4() != null)
        {
            node.getProc4().apply(this);
        }
        outALinkedParExp(node);
    }

    @Override
    public void caseAIntChoiceExp(AIntChoiceExp node)
    {
        inAIntChoiceExp(node);
        if(node.getProc4() != null)
        {
            node.getProc4().apply(this);
        }
        if(node.getProc5() != null)
        {
            node.getProc5().apply(this);
        }
        outAIntChoiceExp(node);
    }

    @Override
    public void caseAExtChoiceExp(AExtChoiceExp node)
    {
        inAExtChoiceExp(node);
        if(node.getProc5() != null)
        {
            node.getProc5().apply(this);
        }
        if(node.getProc6() != null)
        {
            node.getProc6().apply(this);
        }
        outAExtChoiceExp(node);
    }
	
    @Override
    public void caseASyncExtExp(ASyncExtExp node)
    {
        inASyncExtExp(node);
        if(node.getProc5() != null)
        {
            node.getProc5().apply(this);
        }
        if(node.getEvent() != null)
        {
            node.getEvent().apply(this);
        }
        if(node.getProc6() != null)
        {
            node.getProc6().apply(this);
        }
        outASyncExtExp(node);
    }

    @Override
    public void caseAInterruptExp(AInterruptExp node)
    {
        inAInterruptExp(node);
        if(node.getProc6() != null)
        {
            node.getProc6().apply(this);
        }
        if(node.getProc7() != null)
        {
            node.getProc7().apply(this);
        }
        outAInterruptExp(node);
    }

    @Override
    public void caseASyncInterruptExp(ASyncInterruptExp node)
    {
        inASyncInterruptExp(node);
        if(node.getProc6() != null)
        {
            node.getProc6().apply(this);
        }
        if(node.getEvent() != null)
        {
            node.getEvent().apply(this);
        }
        if(node.getProc7() != null)
        {
            node.getProc7().apply(this);
        }
        outASyncInterruptExp(node);
    }

    @Override
    public void caseASlidingChoiceExp(ASlidingChoiceExp node)
    {
        inASlidingChoiceExp(node);
        if(node.getProc7() != null)
        {
            node.getProc7().apply(this);
        }
        if(node.getProc8() != null)
        {
            node.getProc8().apply(this);
        }
        outASlidingChoiceExp(node);
    }

    @Override
    public void caseASeqCompositionExp(ASeqCompositionExp node)
    {
        inASeqCompositionExp(node);
        if(node.getProc8() != null)
        {
            node.getProc8().apply(this);
        }
        if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outASeqCompositionExp(node);
    }

    @Override
    public void caseAGuardExp(AGuardExp node)
    {
        inAGuardExp(node);
        if(node.getDotOp() != null)
        {
            node.getDotOp().apply(this);
        }
        if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outAGuardExp(node);
    }

    @Override
    public void caseAPrefixExp(APrefixExp node)
    {
        inAPrefixExp(node);
        if(node.getEvent() != null)
        {
            node.getEvent().apply(this);
        }
        if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outAPrefixExp(node);
    }

    @Override
    public void caseALambdaExp(ALambdaExp node)
    {
        inALambdaExp(node);
        {
            List<PPattern> copy = new ArrayList<PPattern>(node.getPatternList());
            for(PPattern e : copy)
            {
                e.apply(this);
            }
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
        {
            List<PDef> copy = new ArrayList<PDef>(node.getDefs());
            for(PDef e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outALetWithinExp(node);
    }

    @Override
    public void caseAIfElseExp(AIfElseExp node)
    {
        inAIfElseExp(node);
        if(node.getBoolExp() != null)
        {
            node.getBoolExp().apply(this);
        }
        if(node.getProc1() != null)
        {
            node.getProc1().apply(this);
        }
        if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outAIfElseExp(node);
    }

    @Override
    public void caseAReplicatedExp(AReplicatedExp node)
    {
        inAReplicatedExp(node);
        if(node.getRep() != null)
        {
            node.getRep().apply(this);
        }
        if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outAReplicatedExp(node);
    }

    @Override
    public void caseAExtChoiceRepExp(AExtChoiceRepExp node)
    {
        inAExtChoiceRepExp(node);
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outAExtChoiceRepExp(node);
    }

    @Override
    public void caseAIntChoiceRepExp(AIntChoiceRepExp node)
    {
        inAIntChoiceRepExp(node);
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outAIntChoiceRepExp(node);
    }

    @Override
    public void caseAILeaveRepExp(AILeaveRepExp node)
    {
        inAILeaveRepExp(node);
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outAILeaveRepExp(node);
    }

    @Override
    public void caseASeqCompositRepExp(ASeqCompositRepExp node)
    {
        inASeqCompositRepExp(node);
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outASeqCompositRepExp(node);
    }

    @Override
    public void caseAAlphParRepExp(AAlphParRepExp node)
    {
        inAAlphParRepExp(node);
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        if(node.getEvent() != null)
        {
            node.getEvent().apply(this);
        }
        outAAlphParRepExp(node);
    }
	
    @Override
    public void caseASharingRepExp(ASharingRepExp node)
    {
        inASharingRepExp(node);
        if(node.getValExp() != null)
        {
            node.getValExp().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outASharingRepExp(node);
    }

    @Override
    public void caseALinkedParRepExp(ALinkedParRepExp node)
    {
        inALinkedParRepExp(node);
        if(node.getLinkComp() != null)
        {
            node.getLinkComp().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outALinkedParRepExp(node);
    }

    @Override
    public void caseASyncParRepExp(ASyncParRepExp node)
    {
        inASyncParRepExp(node);
        if(node.getEvent() != null)
        {
            node.getEvent().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outASyncParRepExp(node);
    }

    @Override
    public void caseARenamingExp(ARenamingExp node)
    {
        inARenamingExp(node);
        if(node.getProc10() != null)
        {
            node.getProc10().apply(this);
        }
        if(node.getRenameComp() != null)
        {
            node.getRenameComp().apply(this);
        }
        outARenamingExp(node);
    }

    @Override
    public void caseAERenamingExp(AERenamingExp node)
    {
        inAERenamingExp(node);
        if(node.getProc10() != null)
        {
            node.getProc10().apply(this);
        }
        outAERenamingExp(node);
    }

    @Override
    public void caseAEventExp(AEventExp node)
    {
        inAEventExp(node);
        if(node.getDpattern() != null)
        {
            node.getDpattern().apply(this);
        }
        {
            List<PPattern> copy = new ArrayList<PPattern>(node.getF1List());
            for(PPattern e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PPattern> copy = new ArrayList<PPattern>(node.getF2List());
            for(PPattern e : copy)
            {
                e.apply(this);
            }
        }
        outAEventExp(node);
    }

    @Override
    public void caseADpatternExp(ADpatternExp node)
    {
        inADpatternExp(node);
        if(node.getDpattern() != null)
        {
            node.getDpattern().apply(this);
        }
        if(node.getDotOp() != null)
        {
            node.getDotOp().apply(this);
        }
        outADpatternExp(node);
    }

    @Override
    public void caseADotExp(ADotExp node)
    {
        inADotExp(node);
        if(node.getDotOp() != null)
        {
            node.getDotOp().apply(this);
        }
        if(node.getBoolExp() != null)
        {
            node.getBoolExp().apply(this);
        }
        outADotExp(node);
    }

    @Override
    public void caseAOrExp(AOrExp node)
    {
        inAOrExp(node);
        if(node.getBoolExp() != null)
        {
            node.getBoolExp().apply(this);
        }
        if(node.getBoolExp2() != null)
        {
            node.getBoolExp2().apply(this);
        }
        outAOrExp(node);
    }

    @Override
    public void caseAAndExp(AAndExp node)
    {
        inAAndExp(node);
        if(node.getBoolExp2() != null)
        {
            node.getBoolExp2().apply(this);
        }
        if(node.getBoolExp3() != null)
        {
            node.getBoolExp3().apply(this);
        }
        outAAndExp(node);
    }

    @Override
    public void caseANotExp(ANotExp node)
    {
        inANotExp(node);
        if(node.getBoolExp3() != null)
        {
            node.getBoolExp3().apply(this);
        }
        outANotExp(node);
    }

    @Override
    public void caseAEqualExp(AEqualExp node)
    {
        inAEqualExp(node);
        if(node.getBoolExp4() != null)
        {
            node.getBoolExp4().apply(this);
        }
        if(node.getValExp() != null)
        {
            node.getValExp().apply(this);
        }
        outAEqualExp(node);
    }
	
    @Override
    public void caseAOrderingLgeExp(AOrderingLgeExp node)
    {
        inAOrderingLgeExp(node);
        if(node.getBoolExp4() != null)
        {
            node.getBoolExp4().apply(this);
        }
        if(node.getValExp() != null)
        {
            node.getValExp().apply(this);
        }
        outAOrderingLgeExp(node);
    }

    @Override
    public void caseAOrderingLessExp(AOrderingLessExp node)
    {
        inAOrderingLessExp(node);
        if(node.getBoolExp4() != null)
        {
            node.getBoolExp4().apply(this);
        }
        if(node.getValExp() != null)
        {
            node.getValExp().apply(this);
        }
        outAOrderingLessExp(node);
    }

    @Override
    public void caseAOrderingGreaterExp(AOrderingGreaterExp node)
    {
        inAOrderingGreaterExp(node);
        if(node.getBoolExp4() != null)
        {
            node.getBoolExp4().apply(this);
        }
        if(node.getValExp() != null)
        {
            node.getValExp().apply(this);
        }
        outAOrderingGreaterExp(node);
    }

    @Override
    public void caseAAdditionExp(AAdditionExp node)
    {
        inAAdditionExp(node);
        if(node.getValExp() != null)
        {
            node.getValExp().apply(this);
        }
        if(node.getValExp1() != null)
        {
            node.getValExp1().apply(this);
        }
        outAAdditionExp(node);
    }

    @Override
    public void caseASubtractionExp(ASubtractionExp node)
    {
        inASubtractionExp(node);
        if(node.getValExp() != null)
        {
            node.getValExp().apply(this);
        }
        if(node.getValExp1() != null)
        {
            node.getValExp1().apply(this);
        }
        outASubtractionExp(node);
    }

    @Override
    public void caseAMultiplicationExp(AMultiplicationExp node)
    {
        inAMultiplicationExp(node);
        if(node.getValExp1() != null)
        {
            node.getValExp1().apply(this);
        }
        if(node.getValExp2() != null)
        {
            node.getValExp2().apply(this);
        }
        outAMultiplicationExp(node);
    }
	
    @Override
    public void caseAUnMinusExp(AUnMinusExp node)
    {
        inAUnMinusExp(node);
        if(node.getValExp2() != null)
        {
            node.getValExp2().apply(this);
        }
        outAUnMinusExp(node);
    }

    @Override
    public void caseALengthExp(ALengthExp node)
    {
        inALengthExp(node);
        if(node.getSequence0() != null)
        {
            node.getSequence0().apply(this);
        }
        outALengthExp(node);
    }
	
    @Override
    public void caseACatExp(ACatExp node)
    {
        inACatExp(node);
        if(node.getSequence1() != null)
        {
            node.getSequence1().apply(this);
        }
        if(node.getAtom() != null)
        {
            node.getAtom().apply(this);
        }
        outACatExp(node);
    }

    @Override
    public void caseAEmptySeqExp(AEmptySeqExp node)
    {
        inAEmptySeqExp(node);
        outAEmptySeqExp(node);
    }

    @Override
    public void caseAExplSeqExp(AExplSeqExp node)
    {
        inAExplSeqExp(node);
        if(node.getArguments() != null)
        {
            node.getArguments().apply(this);
        }
        outAExplSeqExp(node);
    }

    @Override
    public void caseARangedSeqExp(ARangedSeqExp node)
    {
        inARangedSeqExp(node);
        if(node.getLval() != null)
        {
            node.getLval().apply(this);
        }
        if(node.getRval() != null)
        {
            node.getRval().apply(this);
        }
        outARangedSeqExp(node);
    }

    @Override
    public void caseAInfiniteSeqExp(AInfiniteSeqExp node)
    {
        inAInfiniteSeqExp(node);
        if(node.getValExp() != null)
        {
            node.getValExp().apply(this);
        }
        outAInfiniteSeqExp(node);
    }
	
    @Override
    public void caseAComprSeqExp(AComprSeqExp node)
    {
        inAComprSeqExp(node);
        if(node.getArguments() != null)
        {
            node.getArguments().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outAComprSeqExp(node);
    }

    @Override
    public void caseARangedComprSeqExp(ARangedComprSeqExp node)
    {
        inARangedComprSeqExp(node);
        if(node.getLval() != null)
        {
            node.getLval().apply(this);
        }
        if(node.getRval() != null)
        {
            node.getRval().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outARangedComprSeqExp(node);
    }

    @Override
    public void caseAInfiniteComprSeqExp(AInfiniteComprSeqExp node)
    {
        inAInfiniteComprSeqExp(node);
        if(node.getValExp() != null)
        {
            node.getValExp().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outAInfiniteComprSeqExp(node);
    }

    @Override
    public void caseAEnumeratedSeqExp(AEnumeratedSeqExp node)
    {
        inAEnumeratedSeqExp(node);
        if(node.getArguments() != null)
        {
            node.getArguments().apply(this);
        }
        outAEnumeratedSeqExp(node);
    }

    @Override
    public void caseAEnumeratedComprSeqExp(AEnumeratedComprSeqExp node)
    {
        inAEnumeratedComprSeqExp(node);
        if(node.getArguments() != null)
        {
            node.getArguments().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outAEnumeratedComprSeqExp(node);
    }

    @Override
    public void caseAEmptySetExp(AEmptySetExp node)
    {
        inAEmptySetExp(node);
        outAEmptySetExp(node);
    }

    @Override
    public void caseASetExp(ASetExp node)
    {
        inASetExp(node);
        if(node.getArguments() != null)
        {
            node.getArguments().apply(this);
        }
        outASetExp(node);
    }

    @Override
    public void caseARangedSetExp(ARangedSetExp node)
    {
        inARangedSetExp(node);
        if(node.getLval() != null)
        {
            node.getLval().apply(this);
        }
        if(node.getRval() != null)
        {
            node.getRval().apply(this);
        }
        outARangedSetExp(node);
    }

    @Override
    public void caseAInfiniteSetExp(AInfiniteSetExp node)
    {
        inAInfiniteSetExp(node);
        if(node.getValExp() != null)
        {
            node.getValExp().apply(this);
        }
        outAInfiniteSetExp(node);
    }

    @Override
    public void caseAComprSetExp(AComprSetExp node)
    {
        inAComprSetExp(node);
        if(node.getArguments() != null)
        {
            node.getArguments().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outAComprSetExp(node);
    }

    @Override
    public void caseARangedComprSetExp(ARangedComprSetExp node)
    {
        inARangedComprSetExp(node);
        if(node.getLval() != null)
        {
            node.getLval().apply(this);
        }
        if(node.getRval() != null)
        {
            node.getRval().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outARangedComprSetExp(node);
    }

    @Override
    public void caseAInfiniteComprSetExp(AInfiniteComprSetExp node)
    {
        inAInfiniteComprSetExp(node);
        if(node.getValExp() != null)
        {
            node.getValExp().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outAInfiniteComprSetExp(node);
    }

    @Override
    public void caseAEnumeratedSetExp(AEnumeratedSetExp node)
    {
        inAEnumeratedSetExp(node);
        if(node.getArguments() != null)
        {
            node.getArguments().apply(this);
        }
        outAEnumeratedSetExp(node);
    }

    @Override
    public void caseAEnumeratedComprSetExp(AEnumeratedComprSetExp node)
    {
        inAEnumeratedComprSetExp(node);
        if(node.getArguments() != null)
        {
            node.getArguments().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        outAEnumeratedComprSetExp(node);
    }

    @Override
    public void caseAMapExp(AMapExp node)
    {
        inAMapExp(node);
		p.openTerm("mapExp");
		p.openList();
        if(node.getLbool() != null)
        {
            node.getLbool().apply(this);
        }
        if(node.getRbool() != null)
        {
            node.getRbool().apply(this);
        }
		p.closeList();
		p.closeTerm();
        outAMapExp(node);
    }

    @Override
    public void caseAEmptyMapExp(AEmptyMapExp node)
    {
        inAEmptyMapExp(node);
        if(node.getEmptyMap() != null)
        {
            node.getEmptyMap().apply(this);
			p.printAtom("emptyMap");
        }
        outAEmptyMapExp(node);
    }

    @Override
    public void caseATupleExp(ATupleExp node)
    {
        inATupleExp(node);
        if(node.getTuple() != null)
        {
			p.openTerm("tupleExp");
			p.openList();
            node.getTuple().apply(this);			
			p.closeList();
			p.closeTerm();
        }
        if(node.getLambda() != null)
        {
            node.getLambda().apply(this);
        }
        outATupleExp(node);
    }

    @Override
    public void caseAStringExp(AStringExp node)
    {
        inAStringExp(node);
        if(node.getString() != null)
        {
            node.getString().apply(this);
			p.openTerm("string");
			p.printString(node.getString().toString().replace("\"","").replace(" ",""));
			p.closeTerm();
        }
        outAStringExp(node);
    }

    @Override
    public void caseACharExp(ACharExp node)
    {
        inACharExp(node);
        if(node.getChar() != null)
        {
            node.getChar().apply(this);
			p.openTerm("char");
			p.printAtom(node.getChar().toString().replace("'","").replace(" ",""));
			p.closeTerm();
        }
        outACharExp(node);
    }

    @Override
    public void caseAWildcardExp(AWildcardExp node)
    {
        inAWildcardExp(node);
        if(node.getWildcard() != null)
        {
            node.getWildcard().apply(this);
        }
        outAWildcardExp(node);
    }

    @Override
    public void caseANumberExp(ANumberExp node)
    {
        inANumberExp(node);
        if(node.getNumber() != null)
        {
            node.getNumber().apply(this);
			p.openTerm("int");
			long l = Long.valueOf(node.getNumber().getText());
			p.printNumber(l);
			p.closeTerm();
        }
        outANumberExp(node);
    }

    @Override
    public void caseATrue1Exp(ATrue1Exp node)
    {
        inATrue1Exp(node);
        if(node.getTrue1() != null)
        {
            node.getTrue1().apply(this);
			p.printAtom("true");
        }
        outATrue1Exp(node);
    }

    @Override
    public void caseATrue2Exp(ATrue2Exp node)
    {
        inATrue2Exp(node);
        if(node.getTrue2() != null)
        {
            node.getTrue2().apply(this);
        }
        outATrue2Exp(node);
    }
	
    @Override
    public void caseAFalse1Exp(AFalse1Exp node)
    {
        inAFalse1Exp(node);
        if(node.getFalse1() != null)
        {
            node.getFalse1().apply(this);
			p.printAtom("false");
        }
        outAFalse1Exp(node);
    }

    @Override
    public void caseAFalse2Exp(AFalse2Exp node)
    {
        inAFalse2Exp(node);
        if(node.getFalse2() != null)
        {
            node.getFalse2().apply(this);
        }
        outAFalse2Exp(node);
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
			p.openTerm("agent_call");
			printSrcLoc(node);
			if(currentParams.contains(str))
			{
				int i = symbols.get("_"+str).size();
				p.printVariable("_"+str+i);
			}
			else if(!isBuiltin(str))
			{
				p.printAtom(str);
			}
			p.openList();
            node.getTuple().apply(this);
			p.closeList();
			p.closeTerm();
        }
		else
		{
			if(currentParams.contains(str))
			{
				int i = symbols.get("_"+str).size();
				p.printVariable("_"+str+i);
			}
			else if(!isBuiltin(str))
			{
				p.printAtom(str);
			}
		}
		
        outAIdExp(node);
    }
//***************************************************************************************************************************************************
//Builtins
	
    @Override
    public void caseALtlBuiltin(ALtlBuiltin node)
    {
        inALtlBuiltin(node);
        if(node.getLtl() != null)
        {
            p.printAtom("LTL");
        }
        outALtlBuiltin(node);
    }

    @Override
    public void caseACtlBuiltin(ACtlBuiltin node)
    {
        inACtlBuiltin(node);
        if(node.getCtl() != null)
        {
			p.printAtom("CTL");
        }
        outACtlBuiltin(node);
    }

	@Override
	public void caseASkipBuiltin(final ASkipBuiltin node) 
	{
		p.openTerm("skip");
		printSrcLoc(node);
		p.closeTerm();
	}
	@Override
	public void caseAStopBuiltin(final AStopBuiltin node) 
	{
		p.openTerm("stop");
		printSrcLoc(node);
		p.closeTerm();
	}

    @Override
    public void caseAChaosBuiltin(AChaosBuiltin node)
    {
        inAChaosBuiltin(node);
        if(node.getChaos() != null)
        {
			
        }
        outAChaosBuiltin(node);
    }

    @Override
    public void caseADivBuiltin(ADivBuiltin node)
    {
        inADivBuiltin(node);
        if(node.getDiv() != null)
        {
            p.openTerm("div");
			printSrcLoc(node);
			p.closeTerm();
        }
        outADivBuiltin(node);
    }

    @Override
    public void caseABoolConstBuiltin(ABoolConstBuiltin node)
    {
        inABoolConstBuiltin(node);
        if(node.getBoolConst() != null)
        {
            p.printAtom("boolType");
        }
        outABoolConstBuiltin(node);
    }

    @Override
    public void caseAEventsBuiltin(AEventsBuiltin node)
    {
        inAEventsBuiltin(node);
        if(node.getEvents() != null)
        {
            p.openTerm("events");
			printSrcLoc(node);
			p.closeTerm();
        }
        outAEventsBuiltin(node);
    }

    @Override
    public void caseAProcBuiltin(AProcBuiltin node)
    {
        inAProcBuiltin(node);
        if(node.getProc() != null)
        {
            p.openTerm("proc");
			printSrcLoc(node);
			p.closeTerm();
        }
        outAProcBuiltin(node);
    }

    @Override
    public void caseACharConstBuiltin(ACharConstBuiltin node)
    {
        inACharConstBuiltin(node);
        if(node.getCharConst() != null)
        {
			p.printAtom("charType");
        }
        outACharConstBuiltin(node);
    }

    @Override
    public void caseAIntConstBuiltin(AIntConstBuiltin node)
    {
        inAIntConstBuiltin(node);
        if(node.getIntConst() != null)
        {
            p.printAtom("intType");
        }
        outAIntConstBuiltin(node);
    }

//***************************************************************************************************************************************************
//Helpers	
    public boolean isBuiltin(String s)
	{
		ArrayList<String> builtin = new ArrayList<String>();

		builtin.add("STOP");
		builtin.add("SKIP");
		builtin.add("CHAOS");
		builtin.add("DIV");
		builtin.add("Events");
		builtin.add("Proc");
		builtin.add("Char"); 
		builtin.add("Bool");
		builtin.add("Int");
		builtin.add("true");
		builtin.add("false");
		builtin.add("True");
		builtin.add("False");	
		
		if(builtin.contains(s))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
    public void printSrcLoc(Node node) 
    {
		   /* Data type of src_loc in cspmf 
			* src_loc 
			{
			   fixedStartLine   = getStartLine s
			  ,fixedStartCol    = getStartCol s
			  ,fixedEndLine     = getEndLine e
			  ,fixedEndCol      = getEndCol e
			  ,fixedLen         = getEndOffset e - getStartOffset s
			  ,fixedStartOffset = getStartOffset s
			}
		  */
		p.openTerm("src_span");
        // src_loc(startline,startcolumn,endline,endcolumn,offset???,length)
		p.printNumber(node.getStartPos().getLine());
		p.printNumber(node.getStartPos().getPos());
		p.printNumber(node.getEndPos().getLine());
		p.printNumber(node.getEndPos().getPos());
		// TODO: do we need this?? the offset (start line (file), start column (file)) to (start line (node), start position (node))
		p.printNumber(node.getEndPos().getPos()-node.getStartPos().getPos());
		p.printNumber(node.getEndPos().getPos()-node.getStartPos().getPos());
		p.closeTerm();
    }
}
