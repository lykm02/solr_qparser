package com.tzls.search.solr.func;

import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;
import org.apache.solr.util.DateMathParser;

public class PromotionSalePriceValueSourceParser extends ValueSourceParser{
	private  String effectDateString = "EffectDate";
	private  String expireDateString = "ExpireDate";
	private  String promotionSalePriceFieldName  = null; 
	
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
		 String salePriceFieldName  = (String)list.get("PromotionSalePriceFieldName");
		 if(salePriceFieldName!=null && !"".equals(salePriceFieldName.trim())){
			 promotionSalePriceFieldName = salePriceFieldName;
		 }
	}
	@Override
	public ValueSource parse(FunctionQParser fqp) throws SyntaxError {
		ValueSource salePriceVs = null;
		if(promotionSalePriceFieldName == null){
			salePriceVs = fqp.parseValueSource();
		}else{
			salePriceVs = RealSalePriceValueSourceParser.getValueSource(fqp,promotionSalePriceFieldName);
		}
		ValueSource effectDateValueSource = RealSalePriceValueSourceParser.getValueSource(fqp,effectDateString);
		ValueSource expireDateValueSource = RealSalePriceValueSourceParser.getValueSource(fqp,expireDateString);
		DateMathParser p = new DateMathParser();
		long time = p.getNow().getTime();
		return new PromotionSalePriceValueSource(salePriceVs, effectDateValueSource, expireDateValueSource, time);
	}

}
