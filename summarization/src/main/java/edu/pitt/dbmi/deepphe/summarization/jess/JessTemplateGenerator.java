package edu.pitt.dbmi.deepphe.summarization.jess;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.uima.cas.Type;
import org.apache.uima.cas.TypeSystem;
import org.apache.uima.jcas.JCas;

public class JessTemplateGenerator {

	public JessTemplateGenerator() {
	}

	public String generateImportsAndTemplates(JCas jcas) {
		final Queue<Type> typeQ = new LinkedList<Type>();
		final TypeSystem typeSystem = jcas.getTypeSystem();
		final StringBuilder importsStringBuilder = new StringBuilder();
		importsStringBuilder
				.append("(import edu.pitt.dbmi.deepphe.summarization.jess.DeepPheTop)\n");
		importsStringBuilder
				.append("(import edu.pitt.dbmi.deepphe.summarization.jess.DeepPheGoal)\n");
		importsStringBuilder
				.append("(import edu.pitt.dbmi.deepphe.summarization.jess.DeepPheDocument)\n");
		importsStringBuilder
				.append("(import edu.pitt.dbmi.deepphe.summarization.jess.DeepPheSize)\n");
		importsStringBuilder
				.append("(import edu.pitt.dbmi.deepphe.summarization.jess.DeepPheLink)\n");
		importsStringBuilder
				.append("(import edu.pitt.dbmi.deepphe.summarization.jess.DeepPheTnmResult)\n");
		final StringBuilder templatesStringBuilder = new StringBuilder();
		templatesStringBuilder.append(buildDeepPheExplicitTemplates());
		typeQ.add(typeSystem.getTopType());
		while (!typeQ.isEmpty()) {
			Type currentType = typeQ.poll();
			if (isJessXferable(currentType)) {
				generateImports(typeSystem, importsStringBuilder, currentType);
				generateTemplates(typeSystem, templatesStringBuilder,
						currentType);
			}
			for (Type childType : typeSystem.getDirectSubtypes(currentType)) {
				typeQ.add(childType);
			}
		}
		return importsStringBuilder.toString() + "\n\n"
				+ templatesStringBuilder.toString();
	}

	private String buildDeepPheExplicitTemplates() {
		StringBuffer sb = new StringBuffer();
		sb.append("(deftemplate DeepPheTop \"Top level DeepPhe Fact\"\n");
		sb.append("     (declare (from-class DeepPheTop)\n");
		sb.append("                  (include-variables TRUE)))\n");
		sb.append("(deftemplate DeepPheGoal extends DeepPheTop\n");
		sb.append("     (declare (from-class DeepPheGoal)\n");
		sb.append("                  (include-variables TRUE)))\n");
		sb.append("(deftemplate DeepPheLink extends DeepPheTop\n");
		sb.append("     (declare (from-class DeepPheLink)\n");
		sb.append("                  (include-variables TRUE)))\n");
		sb.append("(deftemplate DeepPheSize extends DeepPheTop\n");
		sb.append("     (declare (from-class DeepPheSize)\n");
		sb.append("                  (include-variables TRUE)))\n");
		sb.append("(deftemplate DeepPheDocument\n");
		sb.append("     (declare (from-class DeepPheDocument)\n");
		sb.append("                  (include-variables TRUE)))\n");
		sb.append("(deftemplate DeepPheTnmResult\n");
		sb.append("     (declare (from-class DeepPheTnmResult)\n");
		sb.append("                  (include-variables TRUE)))\n");
		return sb.toString();
	}

	private boolean isJessXferable(Type currentType) {
		boolean result = currentType != null;
		result = result && !currentType.getName().startsWith("uima.cas.");
		result = result && !currentType.getName().startsWith("uima.tcas.");
		return result;
	}

	private void generateImports(TypeSystem typeSystem,
			StringBuilder accumulator, Type currentType) {
		StringBuilder sb = new StringBuilder();
		sb.append("(import <classPath>)\n");
		accumulator.append(sb.toString().replaceAll("<classPath>",
				currentType.getName()));
	}

	private void generateTemplates(TypeSystem typeSystem,
			StringBuilder accumulator, Type currentType) {
		StringBuilder sb = new StringBuilder();
		sb.append(" (deftemplate <currentName> extends <parentName>            \n");
		sb.append("         (declare (from-class <currentName>)                \n");
		sb.append("                  (include-variables TRUE)))                \n");
		String templateAddition = sb.toString();
		templateAddition = templateAddition.replaceAll("<currentName>",
				currentType.getShortName());
		Type parentType = typeSystem.getParent(currentType);
		if (isJessXferable(parentType)) {
			templateAddition = templateAddition.replaceAll("<parentName>",
					parentType.getShortName());
		} else {
			String extensionSpecification = "extends DeepPheTop  ";
			extensionSpecification = "                    ";
			templateAddition = templateAddition.replaceAll(
					"extends <parentName>", extensionSpecification);
		}
		accumulator.append(templateAddition);
	}

}
