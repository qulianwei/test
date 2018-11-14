package com.jk.service.impl;

import java.io.Closeable;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jk.dao.IEmployeeDao;
import com.jk.model.Event;
import com.jk.service.IEmployeeService;
import com.jk.util.StringUtil;
import com.jk.util.UtilMap;
import com.sun.javafx.collections.MappingChange.Map;

import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

@Service
public class EmployeeServiceImpl implements IEmployeeService {
	
	@Autowired
	private IEmployeeDao employeeDao;
	
	@Autowired
	private ShardedJedisPool shardedJedisPool;
	

	public HashMap<String, Object> queryEventList(Event event, Integer currentPage, Integer size, String searchKey) {
		Integer start=(currentPage-1)*size;
		HashMap<String, Object> map=eventWhere(event,searchKey);
		String hql="select count(*) from Event where 1=1"+map.get("hql");
		Integer total=employeeDao.getCount(hql,map);
		String hql2="from Event where 1=1"+map.get("hql"); 
		List<Event> list=employeeDao.queryEventList(hql2,start,size,map);
		HashMap<String, Object> hashMap=new HashMap<String, Object>();
		hashMap.put("totle", total);
		hashMap.put("Data", list);
		return hashMap;
	}

	private HashMap<String, Object> eventWhere(Event event, String searchKey) {
		StringBuffer sbf=new StringBuffer();
		HashMap<String, Object> map=new HashMap<String, Object>();
		if (StringUtil.isNotEmpty(searchKey)) {
			sbf.append(" and eventBrand like :ebrand ");
			map.put("ebrand", "%"+searchKey+"%");
		}
		if (event!=null) {
			if (StringUtil.isNotEmpty(event.getYearTime())) {
				sbf.append(" and YEAR(startTime)= :ytime ");
				map.put("ytime", event.getYearTime());
				
			}
			if (StringUtil.isNotEmpty(event.getMonthTime())) {
				sbf.append(" and MONTH(startTime)= :mtime ");
				map.put("mtime", event.getMonthTime());
			}
			if (event.getActType()!=null) {
				sbf.append(" and actType=:atype ");
				map.put("atype", event.getActType());
			}
			if (StringUtil.isNotEmpty(event.getActLocation())) {
				sbf.append(" and actLocation like :alocation ");
				map.put("alocation", "%"+event.getActLocation()+"%");
			}
			if (event.getStatus()!=null) {
				sbf.append(" and status= :sta ");
				map.put("sta", event.getStatus());
			}
			if (event.getAccStatus()!=null) {
				sbf.append(" and accStatus= :astatus ");
				map.put("astatus", event.getAccStatus());
			}
		}
		map.put("hql", sbf.toString());
		return map;
	}
	
	public void saveOrUpdateEvent(Event event) {
		employeeDao.saveOrUpdateEvent(event);
		
	}
	
	public void deleteEvent(Event event) {
		employeeDao.deleteEvent(event);
	}
	
	public Event editEvent(Event event) {
		return employeeDao.editEvent(event);
	}
	
	public void activationEvent(Event event) {
		Event event3 = employeeDao.editEvent(event);
		event3.setStatus(1);
		employeeDao.saveOrUpdateEvent1(event3);
		 ShardedJedis jedis = shardedJedisPool.getResource();
		 HashMap<String, String> map=new HashMap<String, String>();
		 try {
			map = UtilMap.convertToMap(event3);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/* map.put("eventId", event3.getEventId().toString());
		 map.put("eventBrand", event3.getEventBrand());
		 map.put("languages", event3.getLanguages());
		 map.put("eventName", event3.getEventName());
		 map.put("actType", event3.getActType().toString());
		 map.put("synopsis", event3.getSynopsis());
		 map.put("actLocation", event3.getActLocation());
		 map.put("startLocation", event3.getStartLocation());
		 map.put("startTime", sdf.format(event3.getStartTime()));
		 map.put("endTile", sdf.format(event3.getEndTile()));
		 map.put("enroStart", sdf.format(event3.getEnroStart()));
		 map.put("enroEnd", sdf.format(event3.getEnroEnd()));
		 map.put("joinNo", event3.getJoinNo().toString());
		 map.put("eventDetails", event3.getEventDetails());
		 map.put("eventUrl", event3.getEventUrl());
		 map.put("servic", event3.getServic().toString());
		 map.put("lineType", event3.getLineType());
		 map.put("status", event3.getStatus().toString());
		 map.put("accStatus", event3.getAccStatus().toString());*/
     	try {
 			//得到一个不会重复的值
 			
 			//把这个id方法map中
 			map.put("event", event3.getEventId().toString());
 			//把map放到hash中
 			jedis.hmset("redis_"+event3.getEventId(), map);
 			
 			//把id方法list中
 			jedis.lpush("eventList", event3.getEventId().toString());
		system.out.println("hahahhahahhhah")
 		} catch (Exception e) {
 			
 		}finally {
 			 jedis.close();
 		}

		
	}
	
	public void activationEvent2(Event event) {
		Event event3 = employeeDao.editEvent(event);
		event3.setStatus(2);
		employeeDao.saveOrUpdateEvent1(event3);
		ShardedJedis jedis = shardedJedisPool.getResource();
		jedis.lrem("eventList", 1, event3.getEventId().toString());
		jedis.del("redis_"+event3.getEventId());
		
		
	}
	
	
	
	

}
