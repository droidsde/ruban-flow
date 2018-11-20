package cn.com.yusys.yusp.workflow.web.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.com.yusys.yusp.workflow.dto.NextNodeInfoDto;
import cn.com.yusys.yusp.workflow.dto.WFCommentDto;
import cn.com.yusys.yusp.workflow.dto.WFStratDto;
import cn.com.yusys.yusp.workflow.dto.WFSubmitDto;
import cn.com.yusys.yusp.workflow.dto.WFUserDto;
import cn.com.yusys.yusp.workflow.dto.result.ResultInstanceDto;
import cn.com.yusys.yusp.workflow.dto.result.ResultNodeDto;
import cn.com.yusys.yusp.workflow.dto.result.ResultWFMessageDto;
import cn.com.yusys.yusp.workflow.service.WorkflowCoreServiceInterface;
import cn.com.yusys.yusp.workflow.web.dto.ResultDto;
import cn.com.yusys.yusp.workflow.web.fillter.session.CurrentUser;

@RestController
public class WorkflowTestResource {
	@Autowired
	private WorkflowCoreServiceInterface WorkflowCoreServicee;
	
	@GetMapping("/")
	public ResultDto index(){
		ResultDto resultDto = new ResultDto();
		resultDto.setData(1);
		return resultDto;
	}	
	
	private List<String> nodeIdsTTT = new ArrayList();	
	private Map<String,List<String>> userIdsTTT = new HashMap<>();
	private String instanceIdTTT = null;
	
	@GetMapping("/1")
	public ResultDto WorkflowCoreServicee(){
		WFStratDto stratDto = new WFStratDto();
		stratDto.setBizType("bizType");
		stratDto.setBizId("bizId");
		stratDto.setBizUserId("客户id");
		stratDto.setBizUserName("客户名称");
		
		stratDto.setFlowId("1");
		stratDto.setOrgId(CurrentUser.info.get().getOrgId());
		stratDto.setSystemId(CurrentUser.info.get().getSystemId());
		stratDto.setUserId(CurrentUser.info.get().getUserId());
		stratDto.setUserName(CurrentUser.info.get().getUserName());
		ResultInstanceDto data = WorkflowCoreServicee.start(stratDto);
		ResultDto resultDto = new ResultDto();
		resultDto.setData(data);
		
		nodeIdsTTT.clear();
		userIdsTTT.clear();
		nodeIdsTTT.add(data.getNodeId());		
		userIdsTTT.put(data.getNodeId(),Arrays.asList(data.getFlowStarter()));
		
		instanceIdTTT = data.getInstanceId();
		return resultDto;
	}
	
	@GetMapping("/2")
	public ResultDto submit(){	
		ResultDto resultDto = new ResultDto();
		for(String nodeId:nodeIdsTTT){
			List<ResultNodeDto> data = WorkflowCoreServicee.getNextNodeInfos(instanceIdTTT,nodeId);
			for(ResultNodeDto node:data){
				WFSubmitDto sb = new WFSubmitDto();
				WFCommentDto comment = new WFCommentDto();
				comment.setCommentSign("1");
				comment.setInstanceId(instanceIdTTT);
				comment.setNodeId(nodeId);
				comment.setOrgId(CurrentUser.info.get().getOrgId());
				comment.setUserComment("userComment");
				comment.setUserId(CurrentUser.info.get().getUserId());
				sb.setComment(comment);
				List<NextNodeInfoDto> nextNodeInfos = new ArrayList<NextNodeInfoDto>();
				NextNodeInfoDto nextNodeInfoDto = new NextNodeInfoDto();
				
				nextNodeInfoDto.setNextNodeId(node.getNodeId());
				
				List<String> userIds = new ArrayList();
				for(WFUserDto sto:node.getUsers()){
					userIds.add(sto.getUserId());
				}
				nextNodeInfoDto.setNextNodeUserIds(userIds);
				nextNodeInfos.add(nextNodeInfoDto);
				sb.setNextNodeInfos(nextNodeInfos);
				List<ResultWFMessageDto> data1 = WorkflowCoreServicee.submit(sb);
				resultDto.setData(data1);
				
				nodeIdsTTT.clear();
				userIdsTTT.clear();
				nodeIdsTTT.add(node.getNodeId());
				userIdsTTT.put(node.getNodeId(),userIds);
			}
		}		
		return resultDto;
	}
	
}