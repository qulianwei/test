package com.jk.service;

import java.util.HashMap;
import java.util.List;

import com.jk.model.Event;

public interface IEmployeeService {


	HashMap<String, Object> queryEventList(Event event, Integer currentPage, Integer size, String searchKey);

	void saveOrUpdateEvent(Event event);

	void deleteEvent(Event event);

	Event editEvent(Event event);


	void activationEvent(Event event);

	void activationEvent2(Event event);


}
