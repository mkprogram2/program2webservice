package com.program2.repository;

import org.springframework.stereotype.Repository;

import com.program2.table.Person;
import com.program2.table.Remuneration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
@Repository
public interface RemunerationRepository extends JpaRepository<Remuneration, String>
{
	public Remuneration findByPersonIdAndMonthAndYear(String a, int month, int year);
	public List<Remuneration> findByMonthAndYear(int month, int year);
}
