:- dynamic parserVersionNum/1, parserVersionStr/1, parseResult/5.
:- dynamic module/4.
'parserVersionStr'('CSPMJ V0.5').
'parseResult'('ok','',0,0,0).
:- dynamic channel/2, bindval/3, agent/3.
:- dynamic agent_curry/3, symbol/4.
:- dynamic dataTypeDef/2, subTypeDef/2, nameType/2.
:- dynamic cspTransparent/1.
:- dynamic cspPrint/1.
:- dynamic pragma/1.
:- dynamic comment/2.
:- dynamic assertBool/1, assertRef/5, assertTauPrio/6.
:- dynamic assertModelCheckExt/4, assertModelCheck/3.
:- dynamic assertLtl/4, assertCtl/4.
'parserVersionNum'([0,5]).
'parserVersionStr'('CSPMJ V0.5').
'bindval'('P','skip'('src_span'(6,5,6,9,4,4)),'src_span'(6,1,6,9,8,8)).
'bindval'('Q','[]'('val_of'('P','src_span'(12,5,12,6,1,1)),'val_of'('P','src_span'(14,1,14,2,1,1)),'src_span_operator'('no_loc_info_available','src_span'(12,6,12,13,7,7))),'src_span'(12,1,14,2,1,1)).
'symbol'('P','P','src_span'(6,1,6,2,1,1),'Ident (Groundrep.)').
'symbol'('Q','Q','src_span'(12,1,12,2,1,1),'Ident (Groundrep.)').

