package edu.pitt.dbmi.deepphe.summarization.jess.pae;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ctakes.typesystem.type.constants.CONST;
import org.apache.ctakes.typesystem.type.refsem.UmlsConcept;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;

import edu.pitt.dbmi.deepphe.summarization.typesystem.type.textsem.TnmMention;

public class TnmAnalysisEngine extends JCasAnnotator_ImplBase {

	public static void main(String[] args) {
		TnmAnalysisEngine e = new TnmAnalysisEngine();
		try {
			e.process(null);
		} catch (AnalysisEngineProcessException e1) {
			e1.printStackTrace();
		}
	}

	public TnmAnalysisEngine(final UimaContext aCtx, final Properties props) {
	}

	public TnmAnalysisEngine() {
		System.getProperties();
	}

	@Override
	public void initialize(final UimaContext context)
			throws ResourceInitializationException {
		super.initialize(context);
	}

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		if (aJCas == null) {
			StringBuilder sb = new StringBuilder();
			sb.append("Processing Patient is a 54 year old female with history of\n");
			sb.append("T2N0M0 left breast cancer ER-neg, PR-neg, HER2+, now");
			sb.append("undergoing neoadjuvant chemo with taxotere, carboplatin,\n");
			sb.append("Herceptin, and pertuzumab.");
			testRegex(sb.toString());
		} else {
			String documentText = aJCas.getDocumentText();
//			System.out.println("Processing " + documentText);
			applyRegex(aJCas, documentText);
		}

	}

	private void testRegex(String documentText) {
		StringBuilder sb = new StringBuilder();
		sb.append("(?i)T((?:1|2|3|4|i)(?:a|A|b|B|c|C|s)?)(?:\\s|,|;)*");
		sb.append("(?:N|pN)(0|1|1a|2|x|X)(?:\\s|,|;)*M(0|1|x)");
		Pattern tnmPattern = Pattern.compile(sb.toString(), Pattern.DOTALL);
		Matcher matcher = tnmPattern.matcher(documentText);
		while (matcher.find()) {
			System.out.println("Matched TNM pattern...");
			System.out.println(matcher.start());
			System.out.println(matcher.end());
			System.out.println(matcher.group(1) == null ? "NA" : matcher
					.group(1));
			System.out.println(matcher.group(2) == null ? "NA" : matcher
					.group(2));
			System.out.println(matcher.group(3) == null ? "NA" : matcher
					.group(3));
		}
	}

	private void applyRegex(JCas aJCas, String documentText) {
		StringBuilder sb = new StringBuilder();
		sb.append("(?i)T((?:1|2|3|4|i)(?:a|A|b|B|c|C|s)?)(?:\\s|,|;)*");
		sb.append("(?:N|pN)(0|1|1a|2|x|X)(?:\\s|,|;)*M(0|1|x)");
		Pattern tnmPattern = Pattern.compile(sb.toString(), Pattern.DOTALL);
		Matcher matcher = tnmPattern.matcher(documentText);
		while (matcher.find()) {
			TnmMention tnmMention = new TnmMention(aJCas);
			tnmMention.setBegin(matcher.start());
			tnmMention.setEnd(matcher.end());
			tnmMention.setTypeID(CONST.NE_TYPE_ID_FINDING);
			tnmMention
					.setDiscoveryTechnique(CONST.NE_DISCOVERY_TECH_DICT_LOOKUP);
			tnmMention.setTumor(matcher.group(1) == null ? "NA" : matcher
					.group(1));
			tnmMention.setNode(matcher.group(2) == null ? "NA" : matcher
					.group(2));
			tnmMention.setMetastasis(matcher.group(3) == null ? "NA" : matcher
					.group(3));

			final FSArray ocArr = new FSArray(aJCas, 1);
			final UmlsConcept oc = new UmlsConcept(aJCas);
			oc.setCodingScheme("SNOMED");
			oc.setCode("254294008");
			oc.setOid(null);
			oc.setOui(null);
			oc.setScore(0.0);
			oc.setDisambiguated(false);
			oc.setCui("C0332369");
			oc.setTui("T033");
			ocArr.set(0, oc);
			
			tnmMention.setOntologyConceptArr(ocArr);
			tnmMention.addToIndexes(aJCas);
		}

	}

	public void collectionProcessComplete()
			throws AnalysisEngineProcessException {

	}

	public void typeSystemInit(TypeSystem aTypeSystem)
			throws ResourceInitializationException {
		System.out
				.println(getClass().getName() + " typeSystemInit() called...");
	}

	public static AnalysisEngineDescription createAnnotatorDescription()
			throws ResourceInitializationException {
		return AnalysisEngineFactory
				.createEngineDescription(TnmAnalysisEngine.class);
	}

}
