package com.tzls.search.solr.func;


import org.apache.lucene.queries.function.ValueSource;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.schema.DateField;
import org.apache.solr.schema.SchemaField;
import org.apache.solr.search.FunctionQParser;
import org.apache.solr.search.SyntaxError;
import org.apache.solr.search.ValueSourceParser;
import org.apache.solr.util.DateMathParser;
/**
 * @author kmchu
 * 
 * */
@Deprecated
public class RealPriceValueSourceParser extends ValueSourceParser{
	private static String effectDateString = "EffectDate";
	private static String expireDateString = "ExpireDate";
	private static String nextEffectDateString = "NextEffectDate";
	private static String nextExpireDateString = "NextEffectDate";
	private static String promotionModeString = "PromotionMode";
	private static String promotionValueString = "PromotionValue";
	private static String nextPromotionModeString = "NextPromotionMode";
	private static String nextPromotionValueString = "NextPromotionValue";
	private static boolean enableNext = true ;
	
	/**
	  * Initialize the plugin.
	  */
	@Override
	public void init(NamedList args) {
		 String effectFieldName  = (String)args.get("EffectDateFieldName");
		 if(effectFieldName!=null && !"".equals(effectFieldName.trim())){
			 effectDateString = effectFieldName;
		 }
		 String expireFieldName  = (String)args.get("ExpireDateFieldName");
		 if(expireFieldName!=null && !"".equals(expireFieldName.trim())){
			 expireDateString = expireFieldName;
		 }
		 String nextEffectFieldName  = (String)args.get("NextEffectDateFieldName");
		 if(nextEffectFieldName!=null && !"".equals(nextEffectFieldName.trim())){
			 nextEffectDateString = nextEffectFieldName;
		 }
		 String nextExpireFieldName  = (String)args.get("NextExpireDateFieldName");
		 if(nextExpireFieldName!=null && !"".equals(nextExpireFieldName.trim())){
			 nextExpireDateString = nextExpireFieldName;
		 }
		 String promotionModeFieldName  = (String)args.get("PromotionModeFieldName");
		 if(promotionModeFieldName!=null && !"".equals(promotionModeFieldName.trim())){
			 promotionModeString = promotionModeFieldName;
		 }
		 String promotionValueFieldName  = (String)args.get("PromotionValueFieldName");
		 if(promotionValueFieldName!=null && !"".equals(promotionValueFieldName.trim())){
			 promotionValueString = promotionValueFieldName;
		 }
		 
		 String nextPromotionModeFieldName  = (String)args.get("NextPromotionModeFieldName");
		 if(nextPromotionModeFieldName != null && !"".equals(nextPromotionModeFieldName.trim())){
			 nextPromotionModeString = nextPromotionModeFieldName;
		 }
		 
		 String nextPromotionValueFieldName  = (String)args.get("NextPromotionValueFieldName");
		 if(nextPromotionValueFieldName != null && !"".equals(nextPromotionValueFieldName.trim())){
			 nextPromotionValueString = nextPromotionValueFieldName;
		 }
		 String enableNextOpts  = (String)args.get("EnableNext");
		 if(enableNextOpts != null && "false".equals(enableNextOpts.trim())){
			 enableNext = false;
		 }
	}
	
	
	
	@Override
	public ValueSource parse(FunctionQParser fqp) throws SyntaxError {
		ValueSource salePriceValueSource = fqp.parseValueSource();
		DateMathParser p = new DateMathParser();
		long time = p.getNow().getTime();
		ValueSource promotionModeVs = getFieldValueSource(fqp,promotionModeString);
		ValueSource promotionValueVs = getFieldValueSource(fqp,promotionValueString);
		//effectDateValueSource
		ValueSource effectDateValueSource = getValueSource(fqp,effectDateString);
		//ExpireDateValueSource
		ValueSource expireDateValueSource = getValueSource(fqp,expireDateString);
		//value
		PromotionPriceValueSource promotionPriceValueSource = new PromotionPriceValueSource(salePriceValueSource, promotionModeVs, promotionValueVs, effectDateValueSource, expireDateValueSource, time);
		
		ValueSource nextPromotionPriceValueSource = null;
		if(enableNext){
			ValueSource nextPromotionModeVs = getFieldValueSource(fqp,nextPromotionModeString);
			ValueSource nextPromotionValueVs = getFieldValueSource(fqp,nextPromotionValueString);
			//next
			ValueSource nextEffectDateValueSource = getValueSource(fqp,nextEffectDateString);
			//next
			ValueSource nextExpireDateValueSource = getValueSource(fqp,nextExpireDateString);
			nextPromotionPriceValueSource = new PromotionPriceValueSource(salePriceValueSource, nextPromotionModeVs, nextPromotionValueVs, nextEffectDateValueSource, nextExpireDateValueSource, time);
		}else{
			nextPromotionPriceValueSource = new FalsePriceValueSource();
		}
		return new RealTimePriceValueSource(salePriceValueSource,promotionPriceValueSource,nextPromotionPriceValueSource);
	}
	
	
	//cannot use datavaluesourceparser for package invisible
//	private static ValueSource genDateTypeValueSource(String fieldName,FunctionQParser fqp){
//		ValueSourceParser vsp = ValueSourceParser.standardValueSourceParsers.get("ms");
//		ValueSourceParser.DateValueSourceParser dvsp = (ValueSourceParser.DateValueSourceParser)vsp;
//	}
	
	  public static ValueSource getValueSource(FunctionQParser fp, String arg) {
		    if (arg == null) return null;
		    SchemaField f = fp.getReq().getSchema().getField(arg);
		    if (f.getType().getClass() == DateField.class) {
		      throw new SolrException(SolrException.ErrorCode.BAD_REQUEST, "Can't use realPrice() function on non-numeric legacy date field " + arg);
		    }
		    return f.getType().getValueSource(f, fp);
	  }
	  
	  public static ValueSource getFieldValueSource(FunctionQParser fp, String fieldName){
		  if(fieldName == null) {
			  return null;
		  }
		  SchemaField f = fp.getReq().getSchema().getField(fieldName);
		  return f.getType().getValueSource(f, fp);
	  }
}
