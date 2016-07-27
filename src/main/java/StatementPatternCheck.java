//This checks, if x is a pattern in the following expressions by illegalizing some rules:
//{1,2,3|x<-{5},true}

import java.util.*;
import java.io.*;
import CSPMparser.analysis.*;
import CSPMparser.node.*;

public class StatementPatternCheck extends DepthFirstAdapter
{
	private int patternRequired = 0;
	private boolean setPatternRequired = false;

    @Override
    public void caseAHideExp(AHideExp node)
    {
        inAHideExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Hide not allowed).",node);
		}
        if(node.getProc1() != null)
        {
            node.getProc1().apply(this);
        }
        if(node.getProc2() != null)
        {
            node.getProc2().apply(this);
        }
        outAHideExp(node);
    }

    @Override
    public void caseAIleaveExp(AIleaveExp node)
    {
        inAIleaveExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Interleaving not allowed).",node);
		}
        if(node.getProc2() != null)
        {
            node.getProc2().apply(this);
        }
        if(node.getProc3() != null)
        {
            node.getProc3().apply(this);
        }
        outAIleaveExp(node);
    }

    @Override
    public void caseAExceptExp(AExceptExp node)
    {
        inAExceptExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Except not allowed).",node);
		}
        if(node.getProc3() != null)
        {
            node.getProc3().apply(this);
        }
        if(node.getDotOp() != null)
        {
            node.getDotOp().apply(this);
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (GenPar not allowed).",node);
		}
        if(node.getProc3() != null)
        {
            node.getProc3().apply(this);
        }
        if(node.getDotOp() != null)
        {
            node.getDotOp().apply(this);
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (AlphPar not allowed).",node);
		}
        if(node.getProc3() != null)
        {
            node.getProc3().apply(this);
        }
        if(node.getDotOpl() != null)
        {
            node.getDotOpl().apply(this);
        }
        if(node.getDotOpr() != null)
        {
            node.getDotOpr().apply(this);
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (LinkedPar not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Internal Choice not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (External Choice not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Synchronised External Choice not allowed).",node);
		}
        if(node.getProc5() != null)
        {
            node.getProc5().apply(this);
        }
        if(node.getDotOp() != null)
        {
            node.getDotOp().apply(this);
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Interrupt not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Sync. Interrupt not allowed).",node);
		}
        if(node.getProc6() != null)
        {
            node.getProc6().apply(this);
        }
        if(node.getDotOp() != null)
        {
            node.getDotOp().apply(this);
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Sliding Choice not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Sequential Composition not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Guarded not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Prefix not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Lambda Terms not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Let-Withins not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (If-Elses not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Replicated Operators are not allowed).",node);
		}
        if(node.getRep() != null)
        {
            node.getRep().apply(this);
        }
        outAReplicatedExp(node);
    }

    @Override
    public void caseAExtChoiceRepExp(AExtChoiceRepExp node)
    {
        inAExtChoiceRepExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Replicated Operators are not allowed).",node);
		}
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
		if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outAExtChoiceRepExp(node);
    }

    @Override
    public void caseAIntChoiceRepExp(AIntChoiceRepExp node)
    {
        inAIntChoiceRepExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Replicated Operators are not allowed).",node);
		}
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
		if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outAIntChoiceRepExp(node);
    }

    @Override
    public void caseAILeaveRepExp(AILeaveRepExp node)
    {
        inAILeaveRepExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Replicated Operators are not allowed).",node);
		}
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
		if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outAILeaveRepExp(node);
    }

    @Override
    public void caseASeqCompositRepExp(ASeqCompositRepExp node)
    {
        inASeqCompositRepExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Replicated Operators are not allowed).",node);
		}
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
		if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outASeqCompositRepExp(node);
    }

    @Override
    public void caseAAlphParRepExp(AAlphParRepExp node)
    {
        inAAlphParRepExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Replicated Operators are not allowed).",node);
		}
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
        if(node.getDotOp() != null)
        {
            node.getDotOp().apply(this);
        }
		if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outAAlphParRepExp(node);
    }

    @Override
    public void caseASharingRepExp(ASharingRepExp node)
    {
        inASharingRepExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Replicated Operators are not allowed).",node);
		}
        if(node.getValExp() != null)
        {
            node.getValExp().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
		if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outASharingRepExp(node);
    }

    @Override
    public void caseALinkedParRepExp(ALinkedParRepExp node)
    {
        inALinkedParRepExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Replicated Operators are not allowed).",node);
		}
        if(node.getLinkComp() != null)
        {
            node.getLinkComp().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
		if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outALinkedParRepExp(node);
    }


    @Override
    public void caseASyncParRepExp(ASyncParRepExp node)
    {
        inASyncParRepExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Replicated Operators are not allowed).",node);
		}
        if(node.getDotOp() != null)
        {
            node.getDotOp().apply(this);
        }
        if(node.getStmts() != null)
        {
            node.getStmts().apply(this);
        }
		if(node.getProc9() != null)
        {
            node.getProc9().apply(this);
        }
        outASyncParRepExp(node);
    }

    @Override
    public void caseARenamingExp(ARenamingExp node)
    {
        inARenamingExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Renaming not allowed).",node);
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Renaming not allowed).",node);
		}
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
			if(patternRequired>0 && copy.size()>0)
			{
				throw new NoPatternException("Expecting pattern (Events are not allowed).",node);
			}
            for(PPattern e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PPattern> copy = new ArrayList<PPattern>(node.getF2List());
			if(patternRequired>0 && copy.size()>0)
			{
				throw new NoPatternException("Expecting pattern (Events are not allowed).",node);
			}
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
        {
            List<PExp> copy = new ArrayList<PExp>(node.getDoubleList2());
			
			if(patternRequired == 0 && copy.size()>1)
			{
				throw new NoPatternException("Patterns are not allowed here.",node);
			}
			
            for(PExp e : copy)
            {
                e.apply(this);
            }
        }
        outADpatternExp(node);
    }

    @Override
    public void caseAOrExp(AOrExp node)
    {
        inAOrExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Or not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (And not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Not not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Equality Ops are not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Ordering Ops are not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Ordering Ops are not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Ordering Ops are not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Addition is not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Subtraction is not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Mult,Div and Mod not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Unary Minus not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Length Op. not allowed).",node);
		}
        if(node.getLength() != null)
        {
            node.getLength().apply(this);
        }
        outALengthExp(node);
    }
	
    @Override
    public void caseARangedSeqExp(ARangedSeqExp node)
    {
        inARangedSeqExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Ranged Seq. not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Infinite Seq. not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Seq Compr. not allowed).",node);
		}
        if(node.getExpressions() != null)
        {
            node.getExpressions().apply(this);
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Ranged Seq. Compr. not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Inf. Seq. Compr. not allowed).",node);
		}
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
    public void caseAEmptySetExp(AEmptySetExp node)
    {
        inAEmptySetExp(node);
        outAEmptySetExp(node);
    }

    @Override
    public void caseASetExp(ASetExp node)
    {
        inASetExp(node);
			
        if(node.getExpressions() != null)
        {
			if(patternRequired>0)
			{
				setPatternRequired = true;	
				node.getExpressions().apply(this);
				setPatternRequired = false;
			}
			else
			{
				node.getExpressions().apply(this);
			}
        }
        outASetExp(node);
    }
	
    @Override
    public void caseAExpressionListExpressions(AExpressionListExpressions node)
    {
        inAExpressionListExpressions(node);
        {
            List<PExp> copy = new ArrayList<PExp>(node.getExpressionList());
			if(setPatternRequired && copy.size()>1)
			{
				throw new NoPatternException("Expecting pattern (Set patterns must have 1 Element.",node);			
			}
            for(PExp e : copy)
            {
                e.apply(this);
            }
        }
        outAExpressionListExpressions(node);
    }
	
    @Override
    public void caseARangedSetExp(ARangedSetExp node)
    {
        inARangedSetExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Ranged Set not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Inf. Set not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Set Compr. not allowed).",node);
		}
        if(node.getExpressions() != null)
        {
            node.getExpressions().apply(this);
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Ranged Set Compr. not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Inf. Set Compr. not allowed).",node);
		}
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Enumerated Set not allowed).",node);
		}
        if(node.getExpressions() != null)
        {
            node.getExpressions().apply(this);
        }
        outAEnumeratedSetExp(node);
    }

    @Override
    public void caseAEnumeratedComprSetExp(AEnumeratedComprSetExp node)
    {
        inAEnumeratedComprSetExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Enumerated Set Compr. not allowed).",node);
		}
        if(node.getExpressions() != null)
        {
            node.getExpressions().apply(this);
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
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Maps are not allowed).",node);
		}
		List<PExp> copy = new ArrayList<PExp>(node.getMapList());
		for(PExp e : copy)
		{
			e.apply(this);
		}
        outAMapExp(node);
    }

    @Override
    public void caseAEmptyMapExp(AEmptyMapExp node)
    {
        inAEmptyMapExp(node);
		if(patternRequired>0)
		{
			throw new NoPatternException("Expecting pattern (Maps are not allowed).",node);
		}
        if(node.getEmptyMap() != null)
        {
            node.getEmptyMap().apply(this);
        }
        outAEmptyMapExp(node);
    }

    @Override
    public void caseATupleExp(ATupleExp node)
    {
        inATupleExp(node);
		List<PArguments> copy = new ArrayList<PArguments>(node.getArguments());
		if(patternRequired>0 && copy.size()>0)
		{
			throw new NoPatternException("Expecting Pattern (Tuple-Pattern does not allow lambda params).",node);
		}
        if(node.getTuple() != null)
        {
            node.getTuple().apply(this);
        }
        {     
            for(PArguments e : copy)
            {
                e.apply(this);
            }
        }

        outATupleExp(node);
    }
	
    @Override
    public void caseAParenthesisExp(AParenthesisExp node)
    {
        inAParenthesisExp(node);
		List<PArguments> copy = new ArrayList<PArguments>(node.getArguments());
		if(patternRequired>0 && copy.size()>0)
		{
			throw new NoPatternException("Expecting Pattern (Parenthesis-Pattern does not allow lambda params).",node);
		}
        if(node.getParL() != null)
        {
            node.getParL().apply(this);
        }
        if(node.getProc1() != null)
        {
            node.getProc1().apply(this);
        }
        if(node.getParR() != null)
        {
            node.getParR().apply(this);
        }
        {     
            for(PArguments e : copy)
            {
                e.apply(this);
            }
        }

        outAParenthesisExp(node);
    }
	
    @Override
    public void caseAStringExp(AStringExp node)
    {
        inAStringExp(node);
        if(node.getString() != null)
        {
            node.getString().apply(this);
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
        }
        outACharExp(node);
    }

    @Override
    public void caseAWildcardExp(AWildcardExp node)
    {
        inAWildcardExp(node);
		if(patternRequired == 0)
		{
			throw new NoPatternException("Patterns are not allowed here.",node);
		}
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
        }
        outANumberExp(node);
    }
    

    @Override
    public void caseAIdExp(AIdExp node)
    {
        inAIdExp(node);
		List<PArguments> copy = new ArrayList<PArguments>(node.getArguments());
		if(patternRequired>0 && copy.size() >0)
		{
			throw new NoPatternException("Expecting Pattern (Var. Pattern does not allow arguments).",node);
		}
        if(node.getId() != null)
        {
            node.getId().apply(this);
        }
        {        
            for(PArguments e : copy)
            {
                e.apply(this);
            }
        }

        outAIdExp(node);
    }

    @Override
    public void caseAGeneratorStmts(AGeneratorStmts node)
    {
        inAGeneratorStmts(node);
		patternRequired += 1;
        if(node.getDpattern() != null)
        {
            node.getDpattern().apply(this);
        }
		patternRequired -= 1;
        if(node.getGeneratorOp() != null)
        {
            node.getGeneratorOp().apply(this);
        }
        if(node.getProc1() != null)
        {
            node.getProc1().apply(this);
        }
        outAGeneratorStmts(node);
    }

    @Override
    public void caseAPredicateStmts(APredicateStmts node)
    {
        inAPredicateStmts(node);
        if(node.getProc1() != null)
        {
            node.getProc1().apply(this);
        }
        outAPredicateStmts(node);
    }


//End of AST
//***************************************************************************************************************************************************
	
	public int checkNumberOfArgs(String s)
	{
		char[] args = s.toCharArray();
		int depth = 0;
		int argCount = 1;
		for(int i = 0; i<args.length;i++)
		{
			if(args[i] == '(' || args[i] == '{' || args[i] == '\u00AB')
			{
				depth++;
			}
			else if(args[i] == ')' || args[i] == '}' || args[i] == '\u00BB')
			{
				depth -= 1;
			}
			else if(args[i] == ',' && depth == 0)
			{
				argCount++;
			}
		}
		return argCount;
	}
}
