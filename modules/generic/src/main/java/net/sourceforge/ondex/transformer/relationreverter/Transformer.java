package net.sourceforge.ondex.transformer.relationreverter;

import net.sourceforge.ondex.annotations.Custodians;
import net.sourceforge.ondex.args.ArgumentDefinition;
import net.sourceforge.ondex.args.StringArgumentDefinition;
import net.sourceforge.ondex.core.ONDEXConcept;
import net.sourceforge.ondex.core.ONDEXRelation;
import net.sourceforge.ondex.core.RelationType;
import net.sourceforge.ondex.event.ONDEXEventHandler;
import net.sourceforge.ondex.event.type.WrongParameterEvent;
import net.sourceforge.ondex.exception.type.WrongParameterException;
import net.sourceforge.ondex.transformer.ONDEXTransformer;

import java.util.HashSet;
import java.util.Set;

@Custodians(custodians = { "Jochen Weile" }, emails = { "jweile at users.sourceforge.net" })
public class Transformer extends ONDEXTransformer implements ArgumentNames {

	@Override
	public ArgumentDefinition<?>[] getArgumentDefinitions() {
		return new ArgumentDefinition[] { new StringArgumentDefinition(
				ArgumentNames.RELATION_TYPE_ARG,
				ArgumentNames.RELATION_TYPE_ARG_DESC, true, null, true) };
	}

	@Override
	public String getName() {
		return "Relation Reverter";
	}

	@Override
	public String getVersion() {
		return "08.01.2008";
	}

	@Override
	public String getId() {
		return "relatonreverter";
	}

	@Override
	public boolean requiresIndexedGraph() {
		return false;
	}

	@Override
	public String[] requiresValidators() {
		return null;
	}

	@Override
	public void start() throws Exception {

		RelationType rt = graph.getMetaData().getRelationType(
				(String) args.getUniqueValue(ArgumentNames.RELATION_TYPE_ARG));

		if (rt == null) {
			ONDEXEventHandler
					.getEventHandlerForSID(graph.getSID())
					.fireEventOccurred(
							new WrongParameterEvent(
									args.getUniqueValue(ArgumentNames.RELATION_TYPE_ARG)
											+ " is not a valid RelationType.",
									"[Transformer - setONDEXGraph]"));
			throw new WrongParameterException(
					args.getUniqueValue(ArgumentNames.RELATION_TYPE_ARG)
							+ " is not a valid RelationType.");
		}

		Set<Integer> set = new HashSet<Integer>();

		for (ONDEXRelation r : graph.getRelationsOfRelationType(rt).toArray(
				new ONDEXRelation[0])) {
			ONDEXConcept from = r.getFromConcept();
			ONDEXConcept to = r.getToConcept();
			ONDEXRelation newr = graph.createRelation(to, from, rt,
					r.getEvidence());
			for (ONDEXConcept c : r.getTags()) {
				newr.addTag(c);
			}
			set.add(r.getId());
		}

		for (Integer i : set) {
			graph.deleteRelation(i);
		}
	}

}
