package edu.pitt.dbmi.deepphe.summarization.jess;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jess.Context;
import jess.Fact;
import jess.FactIDValue;
import jess.JessException;
import jess.Rete;
import jess.Value;

import org.apache.commons.io.FileUtils;
import org.apache.ctakes.assertion.medfacts.cleartk.ConditionalCleartkAnalysisEngine;
import org.apache.ctakes.assertion.medfacts.cleartk.GenericCleartkAnalysisEngine;
import org.apache.ctakes.assertion.medfacts.cleartk.HistoryCleartkAnalysisEngine;
import org.apache.ctakes.assertion.medfacts.cleartk.PolarityCleartkAnalysisEngine;
import org.apache.ctakes.assertion.medfacts.cleartk.SubjectCleartkAnalysisEngine;
import org.apache.ctakes.assertion.medfacts.cleartk.UncertaintyCleartkAnalysisEngine;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory.CopyNPChunksToLookupWindowAnnotations;
import org.apache.ctakes.clinicalpipeline.ClinicalPipelineFactory.RemoveEnclosedLookupWindows;
import org.apache.ctakes.constituency.parser.ae.ConstituencyParser;
import org.apache.ctakes.core.cc.XmiWriterCasConsumerCtakes;
import org.apache.ctakes.core.cr.FilesInDirectoryCollectionReader;
import org.apache.ctakes.dependency.parser.ae.ClearNLPDependencyParserAE;
import org.apache.ctakes.dictionary.lookup.ae.UmlsDictionaryLookupAnnotator;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.ctakes.typesystem.type.textsem.IdentifiedAnnotation;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.text.AnnotationIndex;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AggregateBuilder;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.resource.ResourceConfigurationException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.InvalidXMLException;

public class TnmExtractor {

	private JessTextOutputer jessTextOutputer;

	private JessTemplateGenerator templateGenerator = null;
	private AnalysisEngine ae = null;
	private CollectionReader collectionReader = null;

	private List<DeepPheDocument> encounterDocuments = new ArrayList<DeepPheDocument>();
	private List<JCas> encounterAnnotations = new ArrayList<JCas>();
	private DeepPheDocument currentDocument;
	private Rete engine;

	public static void main(String[] args) {
		TnmExtractor tnmExtractor = new TnmExtractor();
		try {
			// tnmExtractor.execute();
			tnmExtractor.executeCpe();
		} catch (ResourceInitializationException e) {
			e.printStackTrace();
		} catch (ResourceConfigurationException e) {
			e.printStackTrace();
		} catch (UIMAException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JessException e) {
			e.printStackTrace();
		}
	}

	public TnmExtractor() {
	}

	public void execute() {
		try {
			engine = new Rete();
			engine.reset();
		} catch (JessException e) {
			e.printStackTrace();
		}
	}

	private void initializeJess(JCas jCas) {
		try {
			if (templateGenerator == null) {
				templateGenerator = new JessTemplateGenerator();
				String importsAndTemplates = templateGenerator
						.generateImportsAndTemplates(jCas);
				File importsAndTemplatesFile = new File("DeepPheTemplates.clp");
				FileUtils.writeStringToFile(importsAndTemplatesFile,
						importsAndTemplates);
				engine.eval(importsAndTemplates);
			}
		} catch (JessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void clearJess() {
		executeJess("clear");
		templateGenerator = null;
		
	}

	public void executeJess(String command) {
		try {
			Value result = getEngine().eval(command);
			if (!jess.FactIDValue.class.isInstance(result)) {
				jessTextOutputer.appendText(result.stringValue(getEngine()
						.getGlobalContext()));
			} else {
				jess.Fact f = result.factValue(getEngine().getGlobalContext());
				jessTextOutputer.appendText(f.toStringWithParens() + "\n");
			}

		} catch (JessException e1) {
			jessTextOutputer.displayException(e1);
		}
	}

	@SuppressWarnings("unchecked")
	public void displayDeftemplates() {
		Iterator<jess.Deftemplate> defTemplatesIterator = getEngine()
				.listDeftemplates();
		while (defTemplatesIterator.hasNext()) {
			jess.Deftemplate template = (jess.Deftemplate) defTemplatesIterator
					.next();
			jessTextOutputer.appendText(template.toString());
		}
	}

	@SuppressWarnings("unchecked")
	public void displayFacts() {
		Iterator<jess.Fact> factsIterator = getEngine().listFacts();
		while (factsIterator.hasNext()) {
			jess.Fact fact = (jess.Fact) factsIterator.next();
			jessTextOutputer.appendText(fact.toStringWithParens());
		}
	}

	public void loadJCas() {
		try {
			executeCpe();
			for (int docIdx = 0; docIdx < encounterDocuments.size(); docIdx++) {
				currentDocument = encounterDocuments.get(docIdx);
				JCas jCas = getEncounterAnnotations().get(docIdx);
				AnnotationIndex<Annotation> annots = jCas
						.getAnnotationIndex(IdentifiedAnnotation.type);
				for (Annotation annot : annots) {
					int identifiedAnnotationId = factualizeIdentifiedAnnotation(annot);
					addAnnotationLinks(identifiedAnnotationId, annot);
					addDocumentLink(identifiedAnnotationId);
				}
			}
		} catch (UIMAException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (JessException e) {
			e.printStackTrace();
		}
	}

	public void executeCpe() throws UIMAException, IOException, JessException {

		buildCtakesAnalysisEngine();
		buildCollectionReader();
		initializeJess(ae.newJCas());

		encounterDocuments.clear();
		encounterAnnotations.clear();
		int documentSequence = 0;
		while (collectionReader.hasNext()) {
			JCas jCas = ae.newJCas();
			collectionReader.getNext(jCas.getCas());
			ae.process(jCas);
			DeepPheDocument dpDoc = new DeepPheDocument();
			dpDoc.setName("document-" + documentSequence);
			dpDoc.setSequence(documentSequence);
			factualizeDeepPheDocument(dpDoc);
			encounterDocuments.add(dpDoc);
			encounterAnnotations.add(jCas);
		}

	}
	
	private void buildCtakesAnalysisEngine() throws ResourceInitializationException, InvalidXMLException, IOException {
		if (ae == null) {
			File patientNotesXmiDirectory = new File(
					"\\summarization\\src\\main\\resources\\summarization\\xmi");
			AggregateBuilder builder = new AggregateBuilder();
			builder.add(ClinicalPipelineFactory.getTokenProcessingPipeline());
			builder.add(AnalysisEngineFactory
					.createEngineDescription(CopyNPChunksToLookupWindowAnnotations.class));
			builder.add(AnalysisEngineFactory
					.createEngineDescription(RemoveEnclosedLookupWindows.class));
			builder.add(AnalysisEngineFactory
					.createEngineDescription(ConstituencyParser.class));
			builder.add(UmlsDictionaryLookupAnnotator.createAnnotatorDescription());
			builder.add(AnalysisEngineFactory
					.createEngineDescriptionFromPath("summarization\\desc\\pae\\TnmAnalysisEngine.xml"));
			builder.add(ClearNLPDependencyParserAE.createAnnotatorDescription());
			builder.add(PolarityCleartkAnalysisEngine.createAnnotatorDescription());
			builder.add(UncertaintyCleartkAnalysisEngine
					.createAnnotatorDescription());
			builder.add(HistoryCleartkAnalysisEngine.createAnnotatorDescription());
			builder.add(ConditionalCleartkAnalysisEngine
					.createAnnotatorDescription());
			builder.add(GenericCleartkAnalysisEngine.createAnnotatorDescription());
			builder.add(SubjectCleartkAnalysisEngine.createAnnotatorDescription());
			builder.add(AnalysisEngineFactory.createEngineDescription(
					XmiWriterCasConsumerCtakes.class,
					XmiWriterCasConsumerCtakes.PARAM_OUTPUTDIR,
					patientNotesXmiDirectory));
			AnalysisEngineDescription aaeDesc = builder
					.createAggregateDescription();
			ae = UIMAFramework.produceAnalysisEngine(aaeDesc);
		}
		
	}
	
	private void buildCollectionReader() throws UIMAException, IOException {
		if (collectionReader == null) {
			File patientNotesRawDirectory = new File(
					"summarization\\src\\main\\resources\\summarization\\raw");
			collectionReader = CollectionReaderFactory
					.createReaderFromPath(
							"summarization\\desc\\collection_reader\\FilesInDirectoryCollectionReader.xml",
							FilesInDirectoryCollectionReader.PARAM_INPUTDIR,
							patientNotesRawDirectory);
		}	
	}

	private void addDocumentLink(int srcUuid) throws JessException {
		int tgtUuid = currentDocument.getUuid();
		factualizeDeepPheLink("Document", srcUuid, tgtUuid, 0);
	}

	private int factualizeIdentifiedAnnotation(TOP srcObj)
			throws JessException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		String typeName = srcObj.getType().getShortName();
		jessTextOutputer.appendText("definstance-ing a " + typeName);
		FactIDValue defInstanceReturn = (FactIDValue) engine.definstance(
				typeName, srcObj, false);
		return assignFactIdToIdField(defInstanceReturn);
	}

	private int assignFactIdToIdField(FactIDValue factIdValue)
			throws JessException {
		Fact fact = factIdValue.factValue(getGc());
		Value idValue = new Value(factIdValue.intValue(getGc()),
				jess.RU.INTEGER);
		fact.setSlotValue("id", idValue.resolveValue(getGc()));
		return factIdValue.intValue(getGc());
	}

	private void addAnnotationLinks(int srcFactId, Object srcObj)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, JessException {
		Class<?> tgtCls = srcObj.getClass();
		Method[] methods = tgtCls.getMethods();
		for (Method method : methods) {
			if (!isValidAccessor(method)) {
				continue;
			}
			Class<?> retCls = method.getReturnType();
			if (IdentifiedAnnotation.class.isAssignableFrom(retCls)) {
				IdentifiedAnnotation tgtObj = (IdentifiedAnnotation) method
						.invoke(srcObj);
				if (tgtObj != null) {
					int tgtFactId = factualizeIdentifiedAnnotation(tgtObj);
					int sequenceNumber = 0;
					factualizeDeepPheLink(method.getName(), srcFactId,
							tgtFactId, sequenceNumber);
					addDocumentLink(tgtFactId);
				}
			} else if (FSArray.class.isAssignableFrom(retCls)) {
				FSArray fsArray = (FSArray) method.invoke(srcObj);
				if (fsArray != null) {
					int sequenceNumber = 0;
					for (FeatureStructure fs : fsArray.toArray()) {
						factualizeAndLinkUmlsConcept(srcFactId, fs,
								sequenceNumber++);
					}
				}

			}
		}
	}

	private boolean isValidAccessor(Method method) {
		return method.getName().startsWith("get")
				&& method.getParameterTypes().length == 0;
	}

	private void factualizeAndLinkUmlsConcept(int srcFactId,
			FeatureStructure fs, int sequence) throws JessException {
		if (UmlsConcept.class.isAssignableFrom(fs.getClass())) {
			UmlsConcept tgtObj = (UmlsConcept) fs;
			int tgtFactId = factualizeUmlsConcept(tgtObj);
			factualizeDeepPheLink("UmlsConcept", srcFactId, tgtFactId, sequence);
			addDocumentLink(tgtFactId);
		}
	}

	private int factualizeUmlsConcept(UmlsConcept srcObj) throws JessException {
		int retValue = -1;
		String typeName = srcObj.getType().getShortName();
		jessTextOutputer.appendText("definstance-ing a " + typeName);
		Value defInstanceReturn = engine.definstance(typeName, srcObj, false);
		if (FactIDValue.class.isInstance(defInstanceReturn)) {
			FactIDValue factIdValue = (FactIDValue) defInstanceReturn;
			Fact fact = factIdValue.factValue(getGc());
			Value idValue = new Value(factIdValue.intValue(getGc()) + "",
					jess.RU.STRING);
			fact.setSlotValue("oui", idValue.resolveValue(getGc()));
			retValue = factIdValue.intValue(getGc());
		}
		return retValue;
	}

	private void factualizeDeepPheDocument(DeepPheDocument dpDoc)
			throws JessException {
		Value factIdValue = engine.definstance("DeepPheDocument", dpDoc, false,
				getGc());
		Fact fact = factIdValue.factValue(getGc());
		int factId = factIdValue.intValue(getGc());
		DeepPheDocument theDocument = (DeepPheDocument) fact.getSlotValue("OBJECT").javaObjectValue(getGc());
		theDocument.setUuid(factId);
		engine.updateObject(theDocument, getGc());
	}

	private void factualizeDeepPheLink(String name, int srcFactId,
			int tgtFactId, int sequence) throws JessException {
		DeepPheLink deepPheLink = new DeepPheLink();
		deepPheLink.setName(name);
		deepPheLink.setSrcUuid(srcFactId);
		deepPheLink.setTgtUuid(tgtFactId);
		deepPheLink.setSequence(sequence);
		Value factIdValue = engine.definstance("DeepPheLink", deepPheLink,
				false, getGc());
		Fact fact = factIdValue.factValue(getGc());
		int factId = factIdValue.intValue(getGc());
		DeepPheLink theLink = (DeepPheLink) fact.getSlotValue("OBJECT").javaObjectValue(getGc());
		theLink.setUuid(factId);
		engine.updateObject(theLink, getGc());
	}

	public List<JCas> getEncounterAnnotations() {
		return encounterAnnotations;
	}

	public Rete getEngine() {
		return engine;
	}

	private Context getGc() {
		return engine.getGlobalContext();
	}

	public void setJessTextOutputer(JessTextOutputer jessTextOutputer) {
		this.jessTextOutputer = jessTextOutputer;
	}

	

}
