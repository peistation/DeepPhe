

/* First created by JCasGen Mon May 11 11:00:51 EDT 2015 */
package org.apache.ctakes.cancer.type.textsem;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSArray;
import org.apache.ctakes.typesystem.type.textsem.MeasurementAnnotation;


/** Size measurements for tumor, lesion, etc.  Can have multiple dimensions
 * Updated by JCasGen Mon May 11 11:00:51 EDT 2015
 * XML source: /home/tseytlin/Work/DeepPhe/ctakes-cancer/src/main/resources/org/apache/ctakes/cancer/types/TypeSystem.xml
 * @generated */
public class CancerSize extends MeasurementAnnotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CancerSize.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected CancerSize() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param addr low level Feature Structure reference
   * @param type the type of this Feature Structure 
   */
  public CancerSize(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CancerSize(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public CancerSize(JCas jcas, int begin, int end) {
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
  //* Feature: measurements

  /** getter for measurements - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getMeasurements() {
    if (CancerSize_Type.featOkTst && ((CancerSize_Type)jcasType).casFeat_measurements == null)
      jcasType.jcas.throwFeatMissing("measurements", "org.apache.ctakes.cancer.type.textsem.CancerSize");
    return (FSArray)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((CancerSize_Type)jcasType).casFeatCode_measurements)));}
    
  /** setter for measurements - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMeasurements(FSArray v) {
    if (CancerSize_Type.featOkTst && ((CancerSize_Type)jcasType).casFeat_measurements == null)
      jcasType.jcas.throwFeatMissing("measurements", "org.apache.ctakes.cancer.type.textsem.CancerSize");
    jcasType.ll_cas.ll_setRefValue(addr, ((CancerSize_Type)jcasType).casFeatCode_measurements, jcasType.ll_cas.ll_getFSRef(v));}    
    
  /** indexed getter for measurements - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public SizeMeasurement getMeasurements(int i) {
    if (CancerSize_Type.featOkTst && ((CancerSize_Type)jcasType).casFeat_measurements == null)
      jcasType.jcas.throwFeatMissing("measurements", "org.apache.ctakes.cancer.type.textsem.CancerSize");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CancerSize_Type)jcasType).casFeatCode_measurements), i);
    return (SizeMeasurement)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CancerSize_Type)jcasType).casFeatCode_measurements), i)));}

  /** indexed setter for measurements - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setMeasurements(int i, SizeMeasurement v) { 
    if (CancerSize_Type.featOkTst && ((CancerSize_Type)jcasType).casFeat_measurements == null)
      jcasType.jcas.throwFeatMissing("measurements", "org.apache.ctakes.cancer.type.textsem.CancerSize");
    jcasType.jcas.checkArrayBounds(jcasType.ll_cas.ll_getRefValue(addr, ((CancerSize_Type)jcasType).casFeatCode_measurements), i);
    jcasType.ll_cas.ll_setRefArrayValue(jcasType.ll_cas.ll_getRefValue(addr, ((CancerSize_Type)jcasType).casFeatCode_measurements), i, jcasType.ll_cas.ll_getFSRef(v));}
  }

    