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
package org.eclipse.tm4e.core.grammar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.eclipse.tm4e.core.internal.grammar.ScopeListElement;
import org.eclipse.tm4e.core.internal.rule.Rule;

/**
 * Represents a "pushed" state on the stack (as a linked list element).
 *
 * @see https://github.com/Microsoft/vscode-textmate/blob/master/src/grammar.ts
 *
 */
public class StackElement {

	public static final StackElement NULL = new StackElement(null, null, 0, null, null, null);

	/**
	 * The position on the current line where this state was pushed.
	 * This is relevant only while tokenizing a line, to detect endless loops.
	 * Its value is meaningless across lines.
	 */
	private int enterPosition;

	/**
	 * The previous state on the stack (or null for the root state).
	 */
	public final StackElement parent;
	/**
	 * The depth of the stack.
	 */
	public final int depth;

	/**
	 * The state (rule) that this element represents.
	 */
	public final Rule rule;
	/**
	 * The "pop" (end) condition for this state in case that it was dynamically generated through captured text.
	 */
	public final String endRule;
	/**
	 * The list of scopes containing the "name" for this state.
	 */
	public final ScopeListElement nameScopesList;
	/**
	 * The list of scopes containing the "contentName" (besides "name") for this state.
	 * This list **must** contain as an element `scopeName`.
	 */
	public final ScopeListElement contentNameScopesList;

	public StackElement(StackElement parent, Rule rule, int enterPos, String endRule, ScopeListElement nameScopesList, ScopeListElement contentNameScopesList) {
		this.parent = parent;
		this.depth = (this.parent != null ? this.parent.depth + 1 : 1);
		this.rule = rule;
		this.enterPosition = enterPos;
		this.endRule = endRule;
		this.nameScopesList = nameScopesList;
		this.contentNameScopesList = contentNameScopesList;
	}

	/**
	 * A structural equals check. Does not take into account `scopes`.
	 */
	private static boolean structuralEquals(StackElement a, StackElement b) {
		if (a == b) {
			return true;
		}
		if (a == null || b == null) {
			return false;
		}
		return a.depth == b.depth && Objects.equals(a.rule, b.rule) && !Objects.equals(a.endRule, b.endRule) && structuralEquals(a.parent, b.parent);
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (other == null) {
			return false;
		}
		if (!(other instanceof StackElement)) {
			return false;
		}
		StackElement stackElement = (StackElement)other;
		return structuralEquals(this, stackElement) && this.contentNameScopesList.equals(stackElement.contentNameScopesList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(depth, rule, endRule, parent, contentNameScopesList);
	}

	public void reset() {
		StackElement el = this;
		while (el != null) {
			el.enterPosition = -1;
			el = el.parent;
		}
	}

	public StackElement pop() {
		return this.parent;
	}

	public StackElement safePop() {
		if (this.parent != null) {
			return this.parent;
		}
		return this;
	}

	public StackElement push(Rule rule, int enterPos, String endRule, ScopeListElement nameScopesList, ScopeListElement contentNameScopesList) {
		return new StackElement(this, rule, enterPos, endRule, nameScopesList, contentNameScopesList);
	}

	public int getEnterPos() {
		return this.enterPosition;
	}

	private void appendString(List<String> res) {
		if (this.parent != null) {
			this.parent.appendString(res);
		}
		res.add('(' + rule.toString() + ')'); //, TODO-${this.nameScopesList}, TODO-${this.contentNameScopesList})`;
	}

	@Override
	public String toString() {
		List<String> r = new ArrayList<>();
		this.appendString(r);
		return '[' + String.join(", ", r) + ']';
	}

	public StackElement setContentNameScopesList(ScopeListElement contentNameScopesList) {
		if (this.contentNameScopesList.equals(contentNameScopesList)) {
			return this;
		}
		return this.parent.push(this.rule, this.enterPosition, this.endRule, this.nameScopesList, contentNameScopesList);
	}

	public StackElement setEndRule(String endRule) {
		if (this.endRule != null && this.endRule.equals(endRule)) {
			return this;
		}
		return new StackElement(this.parent, this.rule, this.enterPosition, endRule, this.nameScopesList, this.contentNameScopesList);
	}
}
