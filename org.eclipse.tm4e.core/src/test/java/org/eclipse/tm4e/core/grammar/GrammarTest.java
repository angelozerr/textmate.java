/**
 *  Copyright (c) 2015-2017 Angelo ZERR.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.tm4e.core.grammar;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.tm4e.core.Data;
import org.eclipse.tm4e.core.internal.oniguruma.OnigRegExp;
import org.eclipse.tm4e.core.internal.oniguruma.OnigResult;
import org.eclipse.tm4e.core.internal.oniguruma.OnigString;
import org.eclipse.tm4e.core.registry.Registry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test for grammar tokenizer.
 *
 */
public class GrammarTest {

	private static final String[] EXPECTED_TOKENS = {
			"Token from 0 to 8 with scopes [source.js, meta.function.js, storage.type.function.js]",
			"Token from 8 to 9 with scopes [source.js, meta.function.js]",
			"Token from 9 to 12 with scopes [source.js, meta.function.js, entity.name.function.js]",
			"Token from 12 to 13 with scopes [source.js, meta.function.js, meta.function.type.parameter.js, meta.brace.round.js]",
			"Token from 13 to 14 with scopes [source.js, meta.function.js, meta.function.type.parameter.js, parameter.name.js, variable.parameter.js]",
			"Token from 14 to 15 with scopes [source.js, meta.function.js, meta.function.type.parameter.js]",
			"Token from 15 to 16 with scopes [source.js, meta.function.js, meta.function.type.parameter.js, parameter.name.js, variable.parameter.js]",
			"Token from 16 to 17 with scopes [source.js, meta.function.js, meta.function.type.parameter.js, meta.brace.round.js]",
			"Token from 17 to 18 with scopes [source.js, meta.function.js]",
			"Token from 18 to 19 with scopes [source.js, meta.function.js, meta.decl.block.js, meta.brace.curly.js]",
			"Token from 19 to 20 with scopes [source.js, meta.function.js, meta.decl.block.js]",
			"Token from 20 to 26 with scopes [source.js, meta.function.js, meta.decl.block.js, keyword.control.js]",
			"Token from 26 to 28 with scopes [source.js, meta.function.js, meta.decl.block.js]",
			"Token from 28 to 29 with scopes [source.js, meta.function.js, meta.decl.block.js, keyword.operator.arithmetic.js]",
			"Token from 29 to 32 with scopes [source.js, meta.function.js, meta.decl.block.js]",
			"Token from 32 to 33 with scopes [source.js, meta.function.js, meta.decl.block.js, meta.brace.curly.js]" };

	private static final String[] EXPECTED_MULTI_LINE_TOKENS = {
			"Token from 0 to 8 with scopes [source.js, meta.function.js, storage.type.function.js]",
			"Token from 8 to 9 with scopes [source.js, meta.function.js]",
			"Token from 9 to 12 with scopes [source.js, meta.function.js, entity.name.function.js]",
			"Token from 12 to 13 with scopes [source.js, meta.function.js, meta.function.type.parameter.js, meta.brace.round.js]",
			"Token from 13 to 14 with scopes [source.js, meta.function.js, meta.function.type.parameter.js, parameter.name.js, variable.parameter.js]",
			"Token from 14 to 15 with scopes [source.js, meta.function.js, meta.function.type.parameter.js]",
			"Token from 15 to 16 with scopes [source.js, meta.function.js, meta.function.type.parameter.js, parameter.name.js, variable.parameter.js]",
			"Token from 16 to 17 with scopes [source.js, meta.function.js, meta.function.type.parameter.js, meta.brace.round.js]",
			"Token from 0 to 1 with scopes [source.js, meta.function.js, meta.decl.block.js, meta.brace.curly.js]",
			"Token from 1 to 2 with scopes [source.js, meta.function.js, meta.decl.block.js]",
			"Token from 2 to 8 with scopes [source.js, meta.function.js, meta.decl.block.js, keyword.control.js]",
			"Token from 8 to 10 with scopes [source.js, meta.function.js, meta.decl.block.js]",
			"Token from 10 to 11 with scopes [source.js, meta.function.js, meta.decl.block.js, keyword.operator.arithmetic.js]",
			"Token from 11 to 14 with scopes [source.js, meta.function.js, meta.decl.block.js]",
			"Token from 14 to 15 with scopes [source.js, meta.function.js, meta.decl.block.js, meta.brace.curly.js]" };

	@Test
	public void tokenizeLine() throws Exception {
		Registry registry = new Registry();
		String path = "JavaScript.tmLanguage";
		IGrammar grammar = registry.loadGrammarFromPathSync(path, Data.class.getResourceAsStream(path));
		ITokenizeLineResult lineTokens = grammar.tokenizeLine("function add(a,b) { return a+b; }");
		for (int i = 0; i < lineTokens.getTokens().length; i++) {
			IToken token = lineTokens.getTokens()[i];
			String s = "Token from " + token.getStartIndex() + " to " + token.getEndIndex() + " with scopes "
					+ token.getScopes();
			Assertions.assertEquals(EXPECTED_TOKENS[i], s);
		}
	}

	@Test
	public void tokenizeLines() throws Exception {
		Registry registry = new Registry();
		String path = "JavaScript.tmLanguage";
		IGrammar grammar = registry.loadGrammarFromPathSync(path, Data.class.getResourceAsStream(path));

		StackElement ruleStack = null;
		int i = 0;
		int j = 0;
		String[] lines = { "function add(a,b)", "{ return a+b; }" };
		for (int l = 0; l < lines.length; l++) {
			ITokenizeLineResult lineTokens = grammar.tokenizeLine(lines[l], ruleStack);
			ruleStack = lineTokens.getRuleStack();
			for (i = 0; i < lineTokens.getTokens().length; i++) {
				IToken token = lineTokens.getTokens()[i];
				String s = "Token from " + token.getStartIndex() + " to " + token.getEndIndex() + " with scopes "
						+ token.getScopes();
				Assertions.assertEquals(EXPECTED_MULTI_LINE_TOKENS[i + j], s);
			}
			j = i;
		}
	}

	@Test
	public void testVSCodeTextMateIssue8() throws Exception {
		String line = "ifeq (version,$(firstword $(MAKECMDGOALS))\n";
		OnigRegExp regexp = new OnigRegExp("\\G(MAKEFILES|VPATH|SHELL|MAKESHELL|MAKE|MAKELEVEL|MAKEFLAGS|MAKECMDGOALS|CURDIR|SUFFIXES|\\.LIBPATTERNS)(?=\\s*\\))");
		OnigResult result = regexp.search(new OnigString(line), 28);
		assertTrue(result.count() > 0);
		String path = "makefile.json";
		IGrammar grammar = new Registry().loadGrammarFromPathSync(path, Data.class.getResourceAsStream(path));

		ITokenizeLineResult res = grammar.tokenizeLine(line);
		assertArrayEquals(
			new String[] { "source.makefile",
					"meta.scope.conditional.makefile",
					"meta.scope.condition.makefile",
					"string.interpolated.makefile",
					"meta.scope.function-call.makefile",
					"string.interpolated.makefile",
					"variable.language.makefile" },	
			res.getTokens()[6].getScopes().toArray(String[]::new));
		/*
This test currently fails at stack

Thread [main] (Suspended)	
	OnigSearcher.search(OnigString, int) line: 41	
	OnigScanner.findNextMatchSync(OnigString, int) line: 29	
	>>> LineTokenizer.matchRule(Grammar, OnigString, boolean, int, StackElement, int) line: 265	
	LineTokenizer.matchRuleOrInjections(Grammar, OnigString, boolean, int, StackElement, int) line: 287	
	LineTokenizer.scanNext() line: 112	
	LineTokenizer.scan() line: 103	
	LineTokenizer.tokenizeString(Grammar, OnigString, boolean, int, StackElement, LineTokens) line: 524	
	Grammar.tokenize(String, StackElement, boolean) line: 242	
	Grammar.tokenizeLine(String, StackElement) line: 194	
	Grammar.tokenizeLine(String) line: 189	
	GrammarTest.testVSCodeTextMateIssue8() line: 104	

with conditional breakpoint on >>> `linePos >= 28 && "string.interpolated.makefile".equals(rule.getName(null, null))`
regExp.search(...) doesn't return result for the Regexp of rule 17/variable.language.makefile
2 possibilities:
=> rule not well compiled to OniRegexp, or
=> joni not properly processing rule.

		 */
	}
}
