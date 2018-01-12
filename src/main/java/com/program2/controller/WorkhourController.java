package com.program2.controller;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



import com.program2.repository.WorkhourRepository;
import com.program2.repository.ShiftRepository;
import com.program2.repository.PersonRepository;
import com.program2.table.Workhour;
import com.program2.table.Shift;
import com.program2.table.Person;

@RestController
@RequestMapping(value = "/workhours")
public class WorkhourController {
	
	private SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
	private SimpleDateFormat DateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat DateFormat2 = new SimpleDateFormat("hh:mm:ss");
	
	@Autowired
	private WorkhourRepository WorkhourRepository;
	@Autowired
	private PersonRepository PersonRepository;
	
	
	@PostMapping
	public String saveWorkhour(@RequestBody Workhour wh)
	{
		WorkhourRepository.save(wh);
		return "sfd";
	}
	
	@GetMapping
	public List<Workhour> getWorkhours()
	{
		return WorkhourRepository.findAll();
		
	}
	
	@GetMapping("/{month}/{year}/{id}")
	public List<Workhour> getWorkhoursByMonthByYear(@PathVariable("month") double month, @PathVariable("year") double year, @PathVariable("id") String id)
	{
		
		return WorkhourRepository.findAllByMonthByYearById(month, year,id);
		
	}
	
	
	public long Interval (String personid, Date Date, boolean checkin) {
		long Interval = 0;
		String SDatefull = "";
		Person shift = PersonRepository.findById(personid);
		Date DateNow = Date;
		if (checkin) 
		{
			SDatefull = DateFormat1.format(DateNow)+" "+shift.Shift.workstart;
		}else 
		{
			SDatefull = DateFormat1.format(DateNow)+" "+shift.Shift.workend;
		}
		try
		{
			Date DateStart = DateFormat.parse(SDatefull);
			Interval = TimeUnit.MILLISECONDS.toSeconds(DateStart.getTime() - DateNow.getTime());
		}
		catch(Exception w) {}
		return Interval;
	}
	
	public Workhour getWorkhourOnlyByDate(String personid, Date Date) 
	{
		Workhour WorkhourCheck = new Workhour();
		Date DateNow = Date;
		Calendar Cal = Calendar.getInstance();
		Cal.setTime(DateNow);
		Date DateA = Cal.getTime();
		Cal.add(Calendar.DATE, 1);
		Date DateB = Cal.getTime();
		try
		{
			Date DateS1 = DateFormat1.parse(DateFormat1.format(DateA));
			Date DateS2 = DateFormat1.parse(DateFormat1.format(DateB));
			Timestamp Timestamp1 = new Timestamp(DateS1.getTime());
			Timestamp Timestamp2 = new Timestamp(DateS2.getTime());
			WorkhourCheck = WorkhourRepository.findByPersonidAndWorkstartBetween(personid, Timestamp1,Timestamp2);
		}
		catch(Exception w) {}
		return WorkhourCheck;
	}
	
	@PostMapping("/checkin")
	public Workhour Checkin(@RequestBody String personid)
	{
		Workhour Workhour = new Workhour();
		Date DateNow = new Date();
		Workhour WorkhourCheck = getWorkhourOnlyByDate(personid, DateNow);
		if (WorkhourCheck == null)
		{
			Workhour insert = new Workhour();
			insert.personid = personid;
			insert.workstart = new Timestamp(DateNow.getTime());
			insert.workstartinterval = (int)Interval(personid,DateNow,true);
			Workhour = WorkhourRepository.save(insert);
			return Workhour;
		}
		else 
		{
			WorkhourCheck.personid = null;
			return WorkhourCheck;
		}
		
	}
	
	@PutMapping("/checkout")
	public Workhour Checkout(@RequestBody String personid)
	{
		Date DateNow = new Date();
		Workhour Workhour = new Workhour();
		Workhour WorkhourCheck = getWorkhourOnlyByDate(personid, DateNow);
		if (WorkhourCheck != null)
		{
			WorkhourCheck.workend = new Timestamp(DateNow.getTime());
			WorkhourCheck.workendinterval = (int)Interval(personid,DateNow,false);
			long work_interval = TimeUnit.MILLISECONDS.toSeconds(WorkhourCheck.workend.getTime() - WorkhourCheck.workstart.getTime());
			WorkhourCheck.workinterval = (int) work_interval;
			WorkhourRepository.save(WorkhourCheck);
			Workhour = WorkhourCheck;
		}
		return Workhour;
	}
	
	@GetMapping("/check/{id}")
	public Workhour Check(@PathVariable("id") String personid)
	{
		Date DateNow = new Date();
		Workhour Workhour =  getWorkhourOnlyByDate(personid, DateNow);
		return Workhour;
	}
	
	@GetMapping("/delete")
	public void deleteWorkhour()
	{
		WorkhourRepository.deleteAll();
	}

}

//status = WorkhourRepository.tes(personid,new Timestamp(DateNow.getTime()), Long.toString(Interval));