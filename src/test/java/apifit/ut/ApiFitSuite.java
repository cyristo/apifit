package apifit.ut;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	TestDTO.class,
	TestDynamicHttpFixture.class,
	TestJson.class,
	TestJsonToDTO.class,
	TestPattern.class,
	TestTheCacheSystem.class, 
	TestURLBuilder.class, 
	TestXml.class
})

public class ApiFitSuite {
}
