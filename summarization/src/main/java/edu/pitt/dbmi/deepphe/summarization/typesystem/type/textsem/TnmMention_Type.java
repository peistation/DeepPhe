
/* First created by JCasGen Wed Feb 25 13:18:09 EST 2015 */
package edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.ctakes.typesystem.type.textsem.EventMention_Type;

/** Tnm Mention at Document Level
 * Updated by JCasGen Fri Feb 27 14:17:10 EST 2015
 * @generated */
public class TnmMention_Type extends EventMention_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (TnmMention_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = TnmMention_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new TnmMention(addr, TnmMention_Type.this);
  			   TnmMention_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new TnmMention(addr, TnmMention_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = TnmMention.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
 
  /** @generated */
  final Feature casFeat_tumor;
  /** @generated */
  final int     casFeatCode_tumor;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getTumor(int addr) {
        if (featOkTst && casFeat_tumor == null)
      jcas.throwFeatMissing("tumor", "edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_tumor);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setTumor(int addr, String v) {
        if (featOkTst && casFeat_tumor == null)
      jcas.throwFeatMissing("tumor", "edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_tumor, v);}
    
  
 
  /** @generated */
  final Feature casFeat_node;
  /** @generated */
  final int     casFeatCode_node;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getNode(int addr) {
        if (featOkTst && casFeat_node == null)
      jcas.throwFeatMissing("node", "edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_node);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setNode(int addr, String v) {
        if (featOkTst && casFeat_node == null)
      jcas.throwFeatMissing("node", "edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_node, v);}
    
  
 
  /** @generated */
  final Feature casFeat_metastasis;
  /** @generated */
  final int     casFeatCode_metastasis;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public String getMetastasis(int addr) {
        if (featOkTst && casFeat_metastasis == null)
      jcas.throwFeatMissing("metastasis", "edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
    return ll_cas.ll_getStringValue(addr, casFeatCode_metastasis);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setMetastasis(int addr, String v) {
        if (featOkTst && casFeat_metastasis == null)
      jcas.throwFeatMissing("metastasis", "edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
    ll_cas.ll_setStringValue(addr, casFeatCode_metastasis, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public TnmMention_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_tumor = jcas.getRequiredFeatureDE(casType, "tumor", "uima.cas.String", featOkTst);
    casFeatCode_tumor  = (null == casFeat_tumor) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_tumor).getCode();

 
    casFeat_node = jcas.getRequiredFeatureDE(casType, "node", "uima.cas.String", featOkTst);
    casFeatCode_node  = (null == casFeat_node) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_node).getCode();

 
    casFeat_metastasis = jcas.getRequiredFeatureDE(casType, "metastasis", "uima.cas.String", featOkTst);
    casFeatCode_metastasis  = (null == casFeat_metastasis) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_metastasis).getCode();

  }
}



    