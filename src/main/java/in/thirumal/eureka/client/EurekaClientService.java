/**
 * 
 */
package in.thirumal.eureka.client;

import java.util.List;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Applications;

/**
 * @author Thirumal
 *
 */
@Service
public class EurekaClientService {

	private final DiscoveryClient discoveryClient;
	private final EurekaClient eurekaClient;	
	
	/**
	 * @param discoveryClient
	 * @param eurekaClient
	 */
	public EurekaClientService(DiscoveryClient discoveryClient, EurekaClient eurekaClient) {
		super();
		this.discoveryClient = discoveryClient;
		this.eurekaClient = eurekaClient;
	}

	public Applications getAllInstances(String applicationName) {
		List<String> apps = discoveryClient.getServices();  
        for (String app : apps ) {
        	System.out.print("App: " + app);
        	List<ServiceInstance> instances = discoveryClient.getInstances(app); //Need to pass service id not the name
        	for (ServiceInstance instance : instances) {
            	String url = "http://"+ instance.getHost() + ":"+ instance.getPort();
            	System.out.println(url);
            }
        }
        Applications applications = eurekaClient.getApplications();
        applications.getRegisteredApplications().forEach(registeredApplication -> 
	        registeredApplication.getInstances().forEach(instance -> 
	            System.out.println(instance.getAppName() + " (" + instance.getInstanceId() + ") : ")));
        return applications;
	}

}
