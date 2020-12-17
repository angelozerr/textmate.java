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

import java.util.Collection;

public class ICompilePatternsResult {

	public final Rule[] patterns;
	public final boolean hasMissingPatterns;

	public ICompilePatternsResult(Collection<Rule> patterns, boolean hasMissingPatterns) {
		this.hasMissingPatterns = hasMissingPatterns;
		this.patterns = patterns.toArray(Rule[]::new);
	}

}
