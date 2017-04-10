package selab.nsaf.sa.engine;

import java.sql.Timestamp;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import selab.nsaf.sa.vo.ApplicationVO;



@Path("/situation")
public class NSAFSituationController {
	private static ContextFilter contextFilter = new ContextFilter();
	public Map<String, String> result = null;
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_JSON })
	public String Situation(ApplicationVO appVO) throws Exception{
		System.out.println("Response Data : " +appVO.toString());
		JSONObject jsonObject = appVO.getApplicationJSON();
		contextFilter.requestLoadOntology();
	
		System.out.println("Input JSON Message: " + jsonObject.toString() + "\n");
		contextFilter.filteringEmptyData(jsonObject);
		
		long startTime = System.currentTimeMillis();
		System.out.println("Start time: "+ new Timestamp(startTime));
        contextFilter.requestReloadOntology();
		result = contextFilter.inferSituation(appVO.getAppName());
		
		long startTime2 = System.currentTimeMillis();
		System.out.println("End time: "+ new Timestamp(startTime2));
		
		return result.toString();
		//return "";
		
    }

}
