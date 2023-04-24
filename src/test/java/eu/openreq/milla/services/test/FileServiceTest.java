package eu.openreq.milla.services.test;

import eu.openreq.milla.MillaApplication;
import eu.closedreq.bridge.models.json.Dependency;
import eu.closedreq.bridge.models.json.Dependency_status;
import eu.openreq.milla.services.FileService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=MillaApplication.class)
@SpringBootTest
@AutoConfigureWebClient
public class FileServiceTest {	
	
	@MockBean
	FileWriter writer;
	
	@Autowired
	FileService fs;
    
	List<Dependency> dependencies;
	
    @Before
    public void setUp() throws Exception {    
    	Dependency dep = new Dependency();
		dep.setId("test");
		dep.setStatus(Dependency_status.ACCEPTED);
		dep.setDescription(Arrays.asList("Description goes here"));
		
		Dependency dep2 = new Dependency();
		dep2.setId("test2");
		dep2.setStatus(Dependency_status.REJECTED);
		dependencies = Arrays.asList(dep, dep2);

		Path newFilePath = Paths.get("logs/testing.log");
		Files.write(newFilePath, "".getBytes(), StandardOpenOption.CREATE);

    }

    @After
	public void close() throws Exception {
		Path filePath = Paths.get("logs/testing.log");
    	Files.delete(filePath);
	}

    @Test
    public void testDependencyLogSuccess() throws Exception {
    	fs.setDependencyLogFilePath("logs/testing.log");
    	String result = fs.logDependencies(dependencies);

    	assertEquals("Success", result);
    }

	@Test
	public void testRequestLogSuccess() throws Exception {
		fs.setRequestLogFilePath("logs/testing.log");

		MockHttpServletRequest request = new MockHttpServletRequest();

		request.setRequestURI("/testUri");
		request.addParameter("TEST", "VALUE");

		String result = fs.logRequests(request);

		assertEquals("Success", result);
	}

}
