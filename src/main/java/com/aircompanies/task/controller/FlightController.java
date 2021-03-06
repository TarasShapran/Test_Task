package com.aircompanies.task.controller;


import com.aircompanies.task.exception.ValidationException;
import com.aircompanies.task.model.AirCompany;
import com.aircompanies.task.model.Airplane;
import com.aircompanies.task.model.Flight;
import com.aircompanies.task.model.FlightStatus;
import com.aircompanies.task.service.AirCompanyService;
import com.aircompanies.task.service.AirplaneService;
import com.aircompanies.task.service.FlightService;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flight")
public class FlightController {
    private final AirCompanyService airCompanyService;
    private final AirplaneService airplaneService;
    private final FlightService flightService;

    public FlightController(AirCompanyService airCompanyService, AirplaneService airplaneService, FlightService flightService) {
        this.airCompanyService = airCompanyService;
        this.airplaneService = airplaneService;
        this.flightService = flightService;
    }

    @PostMapping("/{id}/create")
    public Flight create(@PathVariable long id,@RequestBody @Validated Flight flight  ,BindingResult result) {
        if (result.hasErrors()) {
            throw new ValidationException();
        }
        Airplane airplane = airplaneService.readById(id);
        flight.setFlightStatus(FlightStatus.PENDING);
        flight.setAirCompany(airplane.getMyAirCompany());
        flight.setAirplane(airplane);
        Flight newFlight = flightService.create(flight);
        return newFlight;
    }

    @GetMapping("/{id}/read")
    public Flight read(@PathVariable long id) {
        Flight flight = flightService.readById(id);
        return flight;
    }

    @PostMapping("/{id}/update")
    public Flight update(@PathVariable long id,
                         @RequestBody @Validated Flight flight, BindingResult result) {

        if (result.hasErrors()) {
            throw new ValidationException();
        }
        flightService.update(flight);
        return flight;
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable("id") long id) {
        flightService.delete(id);
        return "redirect:/flight/all";
    }

    @PostMapping("/{id}/find-by-status")
    public List<Flight> findByStatus(@PathVariable("id") long id, @RequestParam("status") String status) {
        AirCompany airCompany = airCompanyService.readById(id);
        List<Flight> flightList = flightService.findFlightByStatus(status, airCompany.getName());
        return flightList;
    }

    @GetMapping("/find-active")
    public List<Flight> findActiveFlight(){
        List<Flight> flights = flightService.findAllFlightInActive();
        return flights;
    }

    @PostMapping("/{id}/change-status")
    public Flight changeFlightStatus(@PathVariable("id") long id ,@RequestParam("status") String status){
        Flight flight = flightService.changeFlightStatus(status,id);
        return flight;
    }

    @GetMapping("/all")
    @ResponseBody
    public List<Flight> getAll() {
        List<Flight> flights = flightService.getAll();
        return flights;
    }
}
