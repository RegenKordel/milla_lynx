package eu.openreq.milla.services.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import eu.closedreq.bridge.models.json.Requirement;
import eu.openreq.milla.services.DetectionService;
import eu.openreq.milla.services.MallikasService;
import eu.openreq.milla.services.MulperiService;
import eu.openreq.milla.services.OAuthService;
import eu.openreq.milla.services.UpdateService;

@RunWith(SpringJUnit4ClassRunner.class)
public class UpdateServiceTest {
	
	@Rule
	public WireMockRule wm = new WireMockRule(WireMockConfiguration.wireMockConfig().port(8091), false);
	private String JIRA_BASE_URL = "http://localhost:8091";
	
	@Mock
	MallikasService mallikasService = new MallikasService();	
	
	@Mock
	DetectionService detectionService = new DetectionService();
	
	@Mock
	MulperiService mulperiService = new MulperiService();
	
	@InjectMocks
	private UpdateService updateService = new UpdateService();

	private static OAuthService authService = new OAuthService("");
	
	@Before
    public void setUp() throws Exception {
    	authService.setJiraBaseUrl(JIRA_BASE_URL);
    	
    	Mockito.when(mallikasService.updateRequirements(new ArrayList<Requirement>(), "TEST"))
			.thenReturn("some response");
	
		Mockito.when(mallikasService.getListOfProjects())
			.thenReturn("TEST");
		
		Requirement req = new Requirement();
		req.setName("QTWB-35");
		req.setId("265267");
		req.setModified_at(0L);
		
		JsonObject testObject = new JsonObject();
		testObject.add("requirements", new Gson().toJsonTree(Arrays.asList(req)));
		
		Mockito.when(mallikasService.getSelectedRequirements(Matchers.any()))
			.thenReturn(testObject.toString());
		
		Mockito.when(mallikasService.updateDependencies(Matchers.any(), Matchers.anyBoolean(), Matchers.anyBoolean()))
			.thenReturn(new ResponseEntity<String>("Detection successful (supposedly)", HttpStatus.OK));
		
		Mockito.when(mulperiService.sendProjectUpdatesToMulperi(Matchers.anyString())).thenReturn(
				new ResponseEntity<String>("Caas updated successfully", HttpStatus.OK));
		
	}
	
	@Test
	public void updateIssuesTest() throws Exception {
		updateService.setUpdateFetchSize(10);
		
		String newestIssueUrl = "/rest/api/2/search?jql=project%3DTEST%20order%20by%20updated%20DESC&maxResults=1&startAt=0";
		String projectIssuesUrl = "/rest/api/2/search?jql=project%3DTEST%20order%20by%20updated%20DESC&maxResults=10&startAt=0";
		
		String dirPath = System.getProperty("user.dir") + "/src/test/resources/";
		String jsonString = new String(Files.readAllBytes(Paths.get(dirPath.toString() + 
				"issueForUpdate.json")));
		
		stubFor(get(urlEqualTo(newestIssueUrl))
				.willReturn(aResponse().withStatus(200)
			    .withHeader("Content-Type", "text/html").withBody(jsonString)));


		stubFor(get(urlEqualTo(projectIssuesUrl))
				.willReturn(aResponse().withStatus(200)
			    .withHeader("Content-Type", "text/html").withBody(jsonString)));

		
		ResponseEntity<String> response = updateService.getAllUpdatedIssues(Arrays.asList("TEST"), authService);
		assertTrue(response.getBody().contains("Caas updated successfully"));
	}
}
