
/* First created by JCasGen Mon May 11 11:00:52 EDT 2015 */
package org.apache.ctakes.typesystem.type.textsem;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;

/** The Generic modifier indicates that a named entity mention is generic, i.e., not related to the instance of a disorder, sign/symptom, etc.
 * Updated by JCasGen Mon May 11 11:00:52 EDT 2015
 * @generated */
public class GenericModifier_Type extends Modifier_Type {
  /** @generated 
   * @return the generator for this type
   */
  @Override
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (GenericModifier_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = GenericModifier_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new GenericModifier(addr, GenericModifier_Type.this);
  			   GenericModifier_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new GenericModifier(addr, GenericModifier_Type.this);
  	  }
    };
  /** @generated */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = GenericModifier.typeIndexID;
  /** @generated 
     @modifiable */
  @SuppressWarnings ("hiding")
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.apache.ctakes.typesystem.type.textsem.GenericModifier");
 
  /** @generated */
  final Feature casFeat_indicated;
  /** @generated */
  final int     casFeatCode_indicated;
  /** @generated
   * @param addr low level Feature Structure reference
   * @return the feature value 
   */ 
  public boolean getIndicated(int addr) {
        if (featOkTst && casFeat_indicated == null)
      jcas.throwFeatMissing("indicated", "org.apache.ctakes.typesystem.type.textsem.GenericModifier");
    return ll_cas.ll_getBooleanValue(addr, casFeatCode_indicated);
  }
  /** @generated
   * @param addr low level Feature Structure reference
   * @param v value to set 
   */    
  public void setIndicated(int addr, boolean v) {
        if (featOkTst && casFeat_indicated == null)
      jcas.throwFeatMissing("indicated", "org.apache.ctakes.typesystem.type.textsem.GenericModifier");
    ll_cas.ll_setBooleanValue(addr, casFeatCode_indicated, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	 * @generated
	 * @param jcas JCas
	 * @param casType Type 
	 */
  public GenericModifier_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_indicated = jcas.getRequiredFeatureDE(casType, "indicated", "uima.cas.Boolean", featOkTst);
    casFeatCode_indicated  = (null == casFeat_indicated) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_indicated).getCode();

  }
}



    