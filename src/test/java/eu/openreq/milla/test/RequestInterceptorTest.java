package eu.openreq.milla.test;

import eu.openreq.milla.MillaApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { MillaApplication.class })
@SpringBootConfiguration
@ContextConfiguration
@RunWith(SpringRunner.class)
public class RequestInterceptorTest {
    public static final String TEST_URI = "/test";
    public static final String RESPONSE = "Test endpoint";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    public void testInterceptor_Session_cookie_present_Authorized() {
        ResponseEntity<String> responseEntity = testRestTemplate.getForEntity(TEST_URI, String.class);

        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), RESPONSE);

    }

}

