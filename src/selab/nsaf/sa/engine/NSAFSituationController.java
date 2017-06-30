package selab.nsaf.sa.engine;

import java.sql.Timestamp;

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
	public JSONObject result = null;	//최종 추론값 저장 변수
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces({ MediaType.APPLICATION_JSON })
	public String Situation(ApplicationVO appVO) throws Exception{
		System.out.println("Response Data : " +appVO.toString());	//전달 받은 데이터 출력
		JSONObject jsonObject = appVO.getApplicationJSON();	// 전달 받은 데이터 JSON 형태로 저장
		contextFilter.requestLoadOntology();	//기본 온톨로지 불러오기(Individual이 비어있는 온톨로지)
	
		System.out.println("Input JSON Message: " + jsonObject.toString() + "\n");	// 전달 받은 데이터가 JSON 형태로 되어있는지 확인
		contextFilter.filteringEmptyData(jsonObject);	//Context 요소 추출하기 위한 작업
		
		long startTime = System.currentTimeMillis();
		System.out.println("Start time: "+ new Timestamp(startTime));
		
        contextFilter.requestReloadOntology();	//Mapping된 데이터를 Individual 형태로 적용시킨 온톨로지 불러오기 
		result = contextFilter.inferSituation(appVO.getAppName());	//상황 추론
		
		long startTime2 = System.currentTimeMillis();
		System.out.println("End time: "+ new Timestamp(startTime2));
		
		return result.toString();	//결과값 리턴		
    }
}
