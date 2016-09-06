package net.lr.orders.persistence.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.lr.orders.model.Task;
import net.lr.orders.model.TaskService;


public class TaskServiceImpl implements TaskService {
	Map<String, Task> taskMap;
	
	public TaskServiceImpl() {
		taskMap = new HashMap<String, Task>();
		
		Task task = new Task();
		task.setId("1");
		task.setTitle("Buy some Coffee");
		task.setDescription("Take the extra strong");
		addTask(task);
		
		task = new Task();
		task.setId("2");
		task.setTitle("Buy some Tea");
		task.setDescription("Add more suger");
		addTask(task);
		
		task = new Task();
		task.setId("4");
		task.setTitle("New testing product");
		task.setDescription("New testing product");
		addTask(task);
		
		task = new Task();
		task.setId("5");
		task.setTitle("Finish karaf tutorial");
		task.setDescription("Last check and wiki upload");
		addTask(task);		
		
	}
	
	@Override
	public Task getTask(String id) {
		return taskMap.get(id);
	}

	@Override
	public void addTask(Task task) {
		taskMap.put(task.getId(), task);
	}

	@Override
	public Collection<Task> getTasks() {
		// taskMap.values is not serializable
		return new ArrayList<Task>(taskMap.values());
	}

    @Override
    public void updateTask(Task task) {
        taskMap.put(task.getId(), task);        
    }

    @Override
    public void deleteTask(String id) {
        taskMap.remove(id);
    }

}
