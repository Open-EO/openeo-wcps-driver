package eu.openeo.backend.wcps;

import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;

import jep.Jep;
import jep.JepException;

class TestPythonIntegration {

	@Test
	void testPythonIntegration() {

		try (Jep jep = new Jep(false)) {
			jep.eval("import sys");
			Object pythonVersion = jep.getValue("sys.version");
			// any of the following work, these are just pseudo-examples
			System.out.println("" + pythonVersion.toString());
			// using eval(String) to invoke methods
			// jep.setValue("arg", obj);
			jep.eval("hello = \"world\"");
			jep.eval("world = \"Hello \" + hello + \"!\"");
			Object result = jep.getValue("world");

			// using getValue(String) to invoke methods
			// Object result2 = jep.getValue("somePyModule.foo2()");

			// // using invoke to invoke methods
			// jep.eval("foo3 = somePyModule.foo3");
			// Object result3 = jep.invoke("foo3", obj);
			//
			// // using runScript
			// jep.runScript("path/To/Script");
		} catch (JepException e) {
			e.printStackTrace();
			fail("failed during python tests: " + e.getMessage());
		}
	}

}
