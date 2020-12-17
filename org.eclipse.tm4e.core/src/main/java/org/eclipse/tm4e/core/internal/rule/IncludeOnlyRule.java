/**
 *  Copyright (c) 2015-2017 Angelo ZERR.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Initial code from https://github.com/Microsoft/vscode-textmate/
 * Initial copyright Copyright (C) Microsoft Corporation. All rights reserved.
 * Initial license: MIT
 *
 * Contributors:
 *  - Microsoft Corporation: Initial code, written in TypeScript, licensed under MIT license
 *  - Angelo Zerr <angelo.zerr@gmail.com> - translation and adaptation to Java
 */
package org.eclipse.tm4e.core.internal.rule;

public class IncludeOnlyRule extends Rule {

	public final boolean hasMissingPatterns;
	public final Rule[] patterns;
	private RegExpSourceList cachedCompiledPatterns;

	public IncludeOnlyRule(int id, String name, String contentName, ICompilePatternsResult patterns) {
		super(id, name, contentName);
		this.patterns = patterns.patterns;
		this.hasMissingPatterns = patterns.hasMissingPatterns;
		this.cachedCompiledPatterns = null;
	}

	@Override
	public void collectPatternsRecursive(IRuleRegistry grammar, RegExpSourceList out, boolean isFirst) {
		for (Rule pattern : this.patterns) {
			pattern.collectPatternsRecursive(grammar, out, false);
		}
	}

	@Override
	public ICompiledRule compile(IRuleRegistry grammar, String endRegexSource, boolean allowA, boolean allowG) {
		if (this.cachedCompiledPatterns == null) {
			this.cachedCompiledPatterns = new RegExpSourceList();
			this.collectPatternsRecursive(grammar, this.cachedCompiledPatterns, true);
		}
		return this.cachedCompiledPatterns.compile(grammar, allowA, allowG);
	}

}
