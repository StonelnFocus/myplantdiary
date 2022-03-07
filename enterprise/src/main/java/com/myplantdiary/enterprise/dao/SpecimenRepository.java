package com.myplantdiary.enterprise.dao;

import com.myplantdiary.enterprise.dto.Specimen;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SpecimenRepository extends CrudRepository<Specimen, Integer> {

    List<Specimen> findByPlantId(int plantId);

}
