

/* First created by JCasGen Wed Feb 25 13:18:09 EST 2015 */
package edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.ctakes.typesystem.type.textsem.EventMention;


/** Tnm Mention at Document Level
 * Updated by JCasGen Fri Feb 27 14:17:10 EST 2015
 * XML source: C:/Users/mitchellkj/git/DeepPhe/desc/types/DeepPheTypes.xml
 * @generated */
public class TnmMention extends EventMention {
  /** @generated
   * @ordered 
   */
  public final static int typeIndexID = JCasRegistry.register(TnmMention.class);
  /** @generated
   * @ordered 
   */
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected TnmMention() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public TnmMention(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public TnmMention(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public TnmMention(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: tumor

  /** getter for tumor - gets 
   * @generated
   * @return value of the feature 
   */
  public String getTumor() {
    if (TnmMention_Type.featOkTst && ((TnmMention_Type)jcasType).casFeat_tumor == null)
      jcasType.jcas.throwFeatMissing("tumor", "edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TnmMention_Type)jcasType).casFeatCode_tumor);}
    
  /** setter for tumor - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTumor(String v) {
    if (TnmMention_Type.featOkTst && ((TnmMention_Type)jcasType).casFeat_tumor == null)
      jcasType.jcas.throwFeatMissing("tumor", "edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((TnmMention_Type)jcasType).casFeatCode_tumor, v);}    
   
    
  //*--------------*
  //* Feature: node

  /** getter for node - gets 
   * @generated
   * @return value of the feature 
   */
  public String getNode() {
    if (TnmMention_Type.featOkTst && ((TnmMention_Type)jcasType).casFeat_node == null)
      jcasType.jcas.throwFeatMissing("node", "edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TnmMention_Type)jcasType).casFeatCode_node);}
    
  /** setter for node - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setNode(String v) {
    if (TnmMention_Type.featOkTst && ((TnmMention_Type)jcasType).casFeat_node == null)
      jcasType.jcas.throwFeatMissing("node", "edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((TnmMention_Type)jcasType).casFeatCode_node, v);}    
   
    
  //*--------------*
  //* Feature: metastasis

  /** getter for metastasis - gets 
   * @generated
   * @return value of the feature 
   */
  public String getMetastasis() {
    if (TnmMention_Type.featOkTst && ((TnmMention_Type)jcasType).casFeat_metastasis == null)
      jcasType.jcas.throwFeatMissing("metastasis", "edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
    return jcasType.ll_cas.ll_getStringValue(addr, ((TnmMention_Type)jcasType).casFeatCode_metastasis);}
    
  /** setter for metastasis - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMetastasis(String v) {
    if (TnmMention_Type.featOkTst && ((TnmMention_Type)jcasType).casFeat_metastasis == null)
      jcasType.jcas.throwFeatMissing("metastasis", "edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention");
    jcasType.ll_cas.ll_setStringValue(addr, ((TnmMention_Type)jcasType).casFeatCode_metastasis, v);}    
  }

    