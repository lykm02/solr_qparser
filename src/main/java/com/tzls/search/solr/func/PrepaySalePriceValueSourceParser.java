package com.tzls.search.solr.func;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;
import org.apache.solr.util.DateMathParser;

public class PrepaySalePriceValueSourceParser extends ValueSourceParser{
	private  String effectDateString = "EarnestStartTime";
	private  String expireDateString = "EarnestEndTime";
	private  String prepaySalePriceFieldName  = "EarnestAmount"; 
	private  String isPrepayFieldName = "IsEarnest";
	
	@SuppressWarnings("rawtypes")
	@Override
	public void init(NamedList list){
		 String effectFieldName  = (String)list.get("EffectDateFieldName");
		 if(effectFieldName!=null && !"".equals(effectFieldName.trim())){
			 effectDateString = effectFieldName;
		 }
		 String expireFieldName  = (String)list.get("ExpireDateFieldName");
		 if(expireFieldName!=null && !"".equals(expireFieldName.trim())){
			 expireDateString = expireFieldName;
		 }
		 String salePriceFieldName  = (String)list.get("PrepaySalePriceFieldName");
		 if(salePriceFieldName!=null && !"".equals(salePriceFieldName.trim())){
			 prepaySalePriceFieldName = salePriceFieldName;
		 }
		 String IsEarnest  = (String)list.get("IsEarnestFieldName");
		 if(IsEarnest!=null && !"".equals(IsEarnest.trim())){
			 isPrepayFieldName = IsEarnest;
		 }
	}
	@Override
	public ValueSource parse(FunctionQParser fqp) throws SyntaxError {
		ValueSource salePriceVs = null;
		if(prepaySalePriceFieldName == null){
			salePriceVs = fqp.parseValueSource();
		}else{
			salePriceVs = RealSalePriceValueSourceParser.getValueSource(fqp,prepaySalePriceFieldName);
		}
		ValueSource effectDateValueSource = RealSalePriceValueSourceParser.getValueSource(fqp,effectDateString);
		ValueSource expireDateValueSource = RealSalePriceValueSourceParser.getValueSource(fqp,expireDateString);
		ValueSource isPrepayValueSource = RealSalePriceValueSourceParser.getValueSource(fqp,isPrepayFieldName);
		DateMathParser p = new DateMathParser();
		long time = p.getNow().getTime();
		
		return new PrepaySalePriceValueSource(salePriceVs, effectDateValueSource, expireDateValueSource, time,isPrepayValueSource);
	}

}
