package com.myplantdiary.enterprise;

import com.myplantdiary.enterprise.dao.ISpecimenDAO;
import com.myplantdiary.enterprise.dto.Photo;
import com.myplantdiary.enterprise.dto.Plant;
import com.myplantdiary.enterprise.dto.Specimen;
import com.myplantdiary.enterprise.service.ISpecimenService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import retrofit2.http.Path;

import java.io.IOException;
import java.io.SerializablePermission;
import java.util.List;
import java.util.Map;

/**
 * The controller fro Plant Diary REST endpoints and web UI
 * <p>
 * This class handles the CRUD operations for My Plant Diary specimens, via HTTP actions.
 * </p>
 * <p>
 * This class also serves HTML based web pages, for UI interactions with plant specimens.
 * </p>
 *
 * @author Brandan Jones
 */
@Controller
public class PlantDiaryController {

    @Autowired
    ISpecimenService specimenService;

    /**
     * Handle the root (/) endpoint and return a start page.
     *
     * @return
     */
    @RequestMapping("/")
    public String index(Model model) {
        Specimen specimen = new Specimen();
        specimen.setSpecimenId(1003);
        specimen.setDescription("Pawpaw fruit season");
        specimen.setLongitude("-84.51");
        specimen.setLatitude("39.74");
        specimen.setPlantId(84);
        model.addAttribute(specimen);
        return "start";
    }

    @GetMapping("/specimen")
    @ResponseBody
    public List<Specimen> fetchAllSpecimen() {
        specimenService.fetchAll();
        return specimenService.fetchAll();
    }

    /**
     * Fetch a specimen with the given ID.
     * <p>
     * Given the parameter id, find a specimen that corresponds to this unique ID.
     * <p>
     * Returns one of the following status codes:
     * 200: specimen found
     * 400: specimen not found
     *
     * @param id a unique identifier for this specimen
     */
    @GetMapping("/specimen/{id}/")
    //Could I use @ResponseBody instead of httpheaders headers.... responseentity stuff
    public ResponseEntity fetchSpecimenById(@PathVariable("id") int id) {
        Specimen foundSpecimen = specimenService.fetchByID(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity(foundSpecimen, headers, HttpStatus.OK);
    }

    /**
     * Create a new specimen object, given the data provided.
     * <p>
     * returns one of the following status codes:
     * 201: successfully created a new specimen.
     * 409: unable to create a specimen, because it already exists.
     *
     * @param specimen a JSON representation of a specimen object.
     * @return the newly created specimen object.
     */
    @PostMapping(value = "/specimen", consumes = "application/json", produces = "application/json")
    public ResponseEntity createSpecimen(@RequestBody Specimen specimen) {
        Specimen newSpecimen = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            newSpecimen = specimenService.save(specimen);
        } catch (Exception e) {

            return new ResponseEntity(headers, HttpStatus.OK);
        }
        return new ResponseEntity(newSpecimen, headers, HttpStatus.OK);
    }

    @DeleteMapping("/specimen/{id}/")
    public ResponseEntity deleteSpecimen(@PathVariable("id") int id) {
        try {
            specimenService.delete(id);
            return new ResponseEntity(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /*
    EXAMPLE OF @REQUESTPARAM VS @REQUESTBODY
        @GetMapping("/plants")
    public ResponseEntity searchPlants(@RequestParam Map<String, String> requestParams) {
        int params = requestParams.size();
        String searchValue = requestParams.get("searchTerm");
        return new ResponseEntity(HttpStatus.OK);
    }
     */
    //what triggers this one over the other mapping for /plants: If json is sent in somehow?? -yes
    @GetMapping(value = "/plants", consumes = "application/json", produces = "application/json")
    public ResponseEntity searchPlants(@RequestParam(value = "searchTerm", required = false, defaultValue = "None") String searchTerm) {
        try {
            List<Plant> plants = specimenService.fetchPlants(searchTerm);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity(plants, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/plants")
    public String searchPlantsForm(@RequestParam(value = "searchTerm", required = false, defaultValue = "None") String searchTerm, Model model) {
        try {
            List<Plant> plants = specimenService.fetchPlants(searchTerm);
            model.addAttribute("plants", plants);
            return "plants";
        } catch (IOException e) {
            e.printStackTrace();
            return "error";
        }
    }


    @RequestMapping("/sustainability")
    public String sustainability() {
        return "sustainability";
    }

    @PostMapping("/uploadImage")
    public String uploadImage() {
        String returnValue = "start";

        return returnValue;
    }

    @PostMapping("/saveSpecimen")
    public ModelAndView saveSpecimen(Specimen specimen, @RequestParam("imageFile") MultipartFile imageFile, Model model) {
        ModelAndView modelAndView = new ModelAndView();

        try {
            specimenService.save(specimen);
        } catch (Exception e) {
            e.printStackTrace();
            modelAndView.setViewName("error");
            return modelAndView;
        }

        Photo photo = new Photo();
        try {
            photo.setFileName(imageFile.getOriginalFilename());
            photo.setSpecimen(specimen);
            specimenService.saveImage(imageFile, photo);
        } catch (IOException e) {
            e.printStackTrace();
            modelAndView.setViewName("error");
            return modelAndView;
        }

        modelAndView.setViewName("success");
        modelAndView.addObject("photo", photo);
        modelAndView.addObject("specimen", specimen);
        return modelAndView;
    }

    @GetMapping("/specimensByPlant/{plantId}/")
    public String specimensByPlant(@PathVariable("plantId") int plantId){
        String returnValue = "specimenDetails";
        List<Specimen> specimens = specimenService.fetchSpecimensByPlantId(plantId);
        return returnValue;
    }
}
