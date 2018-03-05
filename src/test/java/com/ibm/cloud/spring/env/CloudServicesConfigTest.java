package com.ibm.cloud.spring.env;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.support.TestPropertySourceUtils;

import static org.junit.Assert.assertEquals;

/**
 *  This class directly tests CloudServicesPropertySource.
 *  CloudServicesConfigMap is completely, though indirectly,
 *  exercised by these tests.
 */
public class CloudServicesConfigTest {

    String VCAP_SERVICES = "{\"cloudantNoSQLDB\":[{\"credentials\":{\"username\":\"VCAP_SERVICES-username\",\"password\":\"VCAP_SERVICES-password\",\"host\":\"VCAP_SERVICES.cloudant.com\",\"port\":999,\"url\":\"https://VCAP_SERVICES.cloudant.com\"},\"syslog_drain_url\":null,\"volume_mounts\":[],\"label\":\"cloudantNoSQLDB\",\"provider\":null,\"plan\":\"Lite\",\"name\":\"VCAP_SERVICES-cloudantno-1234567890\",\"tags\":[\"data_management\",\"ibm_created\",\"lite\",\"ibm_dedicated_public\"]}]}";

    String CLOUDANT_CONFIG_JSON = "{\"cloudant_username\":\"env-json-username\"}";

    private final CloudServicesEnvironmentPostProcessor initializer =
            new CloudServicesEnvironmentPostProcessor();

    private final ConfigurableApplicationContext appContext =
            new AnnotationConfigApplicationContext();

    @Before
    public void setUp() {
        initializer.postProcessEnvironment(this.appContext.getEnvironment(), null);
        CloudServicesConfigMap.SingletonHelper.MAPPINGS = new CloudServicesConfigMap();
        CloudServicesConfigMap.SingletonHelper.MAPPINGS.appContext = appContext;
        CloudServicesConfigMap.SingletonHelper.MAPPINGS.config =
                CloudServicesConfigMap.SingletonHelper.MAPPINGS.getJson("/mappings.json");
    }

    @Test
    public void getValueCF() {
        System.setProperty("VCAP_SERVICES", VCAP_SERVICES);
        String userName = appContext.getEnvironment().getProperty("cloudant.username");
        System.clearProperty("VCAP_SERVICES");
        assertEquals("VCAP_SERVICES-username", userName);
    }

    @Test
    public void getValueEnv() {
        System.setProperty("cloudant_username", "env-username");
        String userName = appContext.getEnvironment().getProperty("cloudant.username");
        System.clearProperty("cloudant_username");
        assertEquals("env-username", userName);
    }

    @Test
    public void getValueEnvJSON() {
        System.setProperty("cloudant_config", CLOUDANT_CONFIG_JSON);
        String userName = appContext.getEnvironment().getProperty("cloudant.username");
        System.clearProperty("cloudant_config");
        assertEquals("env-json-username", userName);
    }

    @Test
    public void getValueFile() {
        String userName = appContext.getEnvironment().getProperty("cloudant.username");
        assertEquals("file-json-username", userName);
    }

    @Test
    public void getValueFileJSON() {
        String url = appContext.getEnvironment().getProperty("cloudant.url");
        assertEquals("https://file-url.cloudant.com", url);
    }

    @Test
    public void getValueApplicationProperties() {
        TestPropertySourceUtils.addPropertiesFilesToEnvironment(appContext, "/application.properties");
        String blah = appContext.getEnvironment().getProperty("blah.blah");
        assertEquals("The quick brown fox jumps over the lazy dog.", blah);
    }
}