package eu.openreq.milla.services.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import eu.openreq.milla.models.json.*;
import eu.openreq.milla.services.DetectionService;
import eu.openreq.milla.services.MallikasService;
import eu.openreq.milla.services.MulperiService;
import eu.openreq.milla.services.QtService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class QtServiceTest {

    @Value("${milla.mallikasAddress}")
    private String mallikasAddress;

    @Value("${milla.detectionGetAddresses}")
    private String[] detectionGetAddresses;

    @Value("${milla.detectionGetPostAddresses}")
    private String detectionGetPostAddresses;

    @Autowired
    private RestTemplate rt;

    @Mock
    MallikasService mallikasService = new MallikasService();

    @Mock
    MulperiService mulperiService = new MulperiService();

    @Mock
    DetectionService detectionService = new DetectionService();

    @InjectMocks
    private QtService qtService;

    Gson gson = new Gson();

    List<Dependency> dependencies;

    ObjectMapper mapper = new ObjectMapper();

    Type depListType = new TypeToken<List<Dependency>>() {
    }.getType();

    @Before
    public void setUp() throws Exception {
        Dependency dep = new Dependency();
        dep.setFromid("test-1");
        dep.setToid("test-2");
        dep.setDependency_score(1);
        dep.setDescription(Arrays.asList("testDesc"));
        dep.setStatus(Dependency_status.PROPOSED);

        Dependency dep2 = new Dependency();
        dep2.setFromid("test-1");
        dep2.setToid("test-3");
        dep2.setDependency_score(1);
        dep2.setDescription(Collections.singletonList("testDesc2"));
        dep2.setStatus(Dependency_status.PROPOSED);

        Dependency dep3 = new Dependency();
        dep3.setFromid("test-4");
        dep3.setToid("test-1");
        dep3.setDependency_score(1);
        dep3.setDescription(Collections.singletonList("testDesc3"));
        dep3.setStatus(Dependency_status.PROPOSED);

        Dependency dep4 = new Dependency();
        dep4.setFromid("test-1");
        dep4.setToid(("test-5"));
        dep4.setDependency_score(1);
        dep4.setStatus(Dependency_status.PROPOSED);

        dependencies = Arrays.asList(dep, dep2, dep3, dep4);
        String depContent = mapper.writeValueAsString(Arrays.asList(dep, dep2, dep3, dep4));
        depContent = "{\"dependencies\":" + depContent + "}";

        List<Requirement> reqs = new ArrayList<>();
        List<Requirement> reqs2 = new ArrayList<>();

        Requirement req = new Requirement();
        req.setId("test-1");
        req.setCreated_at(TimeUnit.MILLISECONDS.convert(2, TimeUnit.DAYS));
        reqs.add(req);
        reqs2.add(req);

        req = new Requirement();
        RequirementPart part = new RequirementPart();
        part.setName("Components");
        part.setText("TestComp");
        req.setRequirementParts(Collections.singletonList(part));
        req.setId("test-2");
        req.setCreated_at(TimeUnit.MILLISECONDS.convert(4, TimeUnit.DAYS));
        reqs.add(req);

        String reqContent = "{\"requirements\":" + mapper.writeValueAsString(reqs) + "}";

        req = new Requirement();
        req.setId("test-4");
        req.setCreated_at(TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
        reqs.add(req);
        reqs2.add(req);

        String reqContent2 = "{\"requirements\":" + mapper.writeValueAsString(reqs2) + "}";

        req = new Requirement();
        req.setId("test-5");
        reqs.add(req);

        String tcContent = "{\"requirements\":" + mapper.writeValueAsString(reqs) + "}";

        Mockito.when(detectionService.getDetectedFromServices(Matchers.any(), Matchers.any()))
                .thenReturn(new ArrayList<>());
        Mockito.when(mallikasService.requestWithParams(Matchers.any(), Matchers.anyString())).thenReturn(depContent,
                "{\"dependencies\": []}", depContent, depContent);
        Mockito.when(mallikasService.getSelectedRequirements(Matchers.any())).thenReturn(reqContent,
                reqContent2, "", "");
        Mockito.when(mulperiService.getTransitiveClosure(Arrays.asList("test-1"), 2))
                .thenReturn(new ResponseEntity<>(tcContent, HttpStatus.OK));
    }

    @Test
    public void prioritizeOrphansTest() {
        List<Dependency> deps = new ArrayList<>();
        Dependency testDep = new Dependency();
        testDep.setFromid("test-1");
        testDep.setToid("test-6");
        testDep.setDependency_score(1);
        deps.add(testDep);
        testDep = new Dependency();
        testDep.setFromid("test-1");
        testDep.setToid("test-2");
        testDep.setDependency_score(1);
        deps.add(testDep);
        List<Dependency> results = qtService.prioritizeOrphans("test-1", deps, 1.5);
        double totalScore = 0;
        for (Dependency dep : results) {
            totalScore += dep.getDependency_score();
        }
        assertEquals(2.5, totalScore, 0);
    }

    @Test
    public void prioritizeDistantTest() throws IOException {
        List<Dependency> results = qtService.prioritizeDistantDeps("test-1", dependencies, 3,
                2);
        double totalScore = 0;
        for (Dependency dep : results) {
            totalScore += dep.getDependency_score();
        }
        assertEquals(5, totalScore, 0);
    }

    @Test
    public void sumScoresTest() throws IOException {
        WeightParams params = new WeightParams();
        params.setOrphanFactor(1.5);
        params.setMinimumDistance(3);
        params.setMinDistanceFactor(2.0);
        String result = qtService.sumScoresAndGetTopProposed(Collections.singletonList("test-1"), 20,
                false, "", params).getBody();

        System.out.println(result);

        JsonObject obj = gson.fromJson(result, JsonObject.class);
        List<Dependency> dependencies = gson.fromJson(obj.get("dependencies"), depListType);

        double totalScore = 0;
        for (Dependency dep : dependencies) {
            totalScore += dep.getDependency_score();
        }
        assertEquals(5.5, totalScore, 0);
    }

    @Test
    public void sumScoresWithComponentParamAddedTest() throws IOException {
        WeightParams params = new WeightParams();
        params.setOrphanFactor(1.5);
        params.setMinimumDistance(3);
        params.setMinDistanceFactor(2.0);

        params.setComponentName("TestComp");
        params.setComponentFactor(3.0);

        String result = qtService.sumScoresAndGetTopProposed(Collections.singletonList("test-1"), 20,
                false, "", params).getBody();

        System.out.println(result);

        JsonObject obj = gson.fromJson(result, JsonObject.class);
        List<Dependency> dependencies = gson.fromJson(obj.get("dependencies"), depListType);

        double totalScore = 0;
        for (Dependency dep : dependencies) {
            totalScore += dep.getDependency_score();
        }
        assertEquals(8.5, totalScore, 0);

    }

    @Test
    public void sumScoresWithProjectIdTest() throws IOException {
        WeightParams params = new WeightParams();
        params.setProjectId("test");
        params.setProjectFactor(3.0);

        String result = qtService.sumScoresAndGetTopProposed(Collections.singletonList("test-1"), 20,
                true, "", params).getBody();

        JsonObject obj = gson.fromJson(result, JsonObject.class);
        List<Dependency> dependencies = gson.fromJson(obj.get("dependencies"), depListType);

        double totalScore = 0;
        for (Dependency dep : dependencies) {
            totalScore += dep.getDependency_score();
        }
        assertEquals(8, totalScore, 0);

    }

    @Test
    public void sumScoresWithDate() throws IOException {
        WeightParams params = new WeightParams();
        params.setDateDifference(2);
        params.setDateFactor(3.0);

        String result = qtService.sumScoresAndGetTopProposed(Collections.singletonList("test-1"), 20,
                false, "", params).getBody();

        JsonObject obj = gson.fromJson(result, JsonObject.class);
        List<Dependency> dependencies = gson.fromJson(obj.get("dependencies"), depListType);

        double totalScore = 0;
        for (Dependency dep : dependencies) {
            totalScore += dep.getDependency_score();
        }
        assertEquals(8, totalScore, 0);

    }

    @Test
    public void sumScoresWithDateTooFar() throws IOException {
        WeightParams params = new WeightParams();
        params.setDateDifference(1);
        params.setDateFactor(3.0);

        String result = qtService.sumScoresAndGetTopProposed(Collections.singletonList("test-1"), 20,
                false, "", params).getBody();

        JsonObject obj = gson.fromJson(result, JsonObject.class);
        List<Dependency> dependencies = gson.fromJson(obj.get("dependencies"), depListType);

        double totalScore = 0;
        for (Dependency dep : dependencies) {
            totalScore += dep.getDependency_score();
        }
        assertEquals(6, totalScore, 0);

    }

}