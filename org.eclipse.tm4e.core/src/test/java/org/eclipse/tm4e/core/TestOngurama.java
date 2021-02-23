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
package org.eclipse.tm4e.core;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.tm4e.core.internal.oniguruma.IOnigNextMatchResult;
import org.eclipse.tm4e.core.internal.oniguruma.OnigRegExp;
import org.eclipse.tm4e.core.internal.oniguruma.OnigResult;
import org.eclipse.tm4e.core.internal.oniguruma.OnigScanner;
import org.eclipse.tm4e.core.internal.oniguruma.OnigString;
import org.junit.Test;

public class TestOngurama {

	public static void main(String[] args) {

		OnigScanner scanner = new OnigScanner(new String[] { "c", "a(b)?" });
		IOnigNextMatchResult result = scanner.findNextMatchSync("abc", 0);
		System.err.println(result);

		scanner = new OnigScanner(new String[] { "a([b-d])c" });
		result = scanner.findNextMatchSync("!abcdef", 0);
		System.err.println(result);
	}

	@Test
	public void testSearchCache() {
		String line = "ifeq (version,$(firstword $(MAKECMDGOALS))\n";
		OnigString onigLine = new OnigString(line);
		OnigRegExp regexp = new OnigRegExp("\\G(MAKEFILES|VPATH|SHELL|MAKESHELL|MAKE|MAKELEVEL|MAKEFLAGS|MAKECMDGOALS|CURDIR|SUFFIXES|\\.LIBPATTERNS)(?=\\s*\\))");
		OnigResult result = regexp.search(onigLine, 10);
		assertNull(result);
		result = regexp.search(onigLine, 28);
		assertTrue(result.count() > 0);
	}
}
