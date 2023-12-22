package br.com.pdv.controller;

import br.com.pdv.dto.ResponseDTO;
import br.com.pdv.dto.SaleDTO;
import br.com.pdv.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/sale")
public class SaleController {

    @Autowired
    private SaleService saleService;

    @GetMapping()
    public ResponseEntity getAll() {
        return new ResponseEntity<>(saleService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity getAllByUser(@PathVariable Long userId) {
        return new ResponseEntity<>(saleService.findAllByUser(userId), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable Long id) {
        return new ResponseEntity<>(saleService.getById(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity post(@Valid @RequestBody SaleDTO saleDTO) {
        Long id = saleService.save(saleDTO);
        return new ResponseEntity<>(new ResponseDTO("Venda realizada com sucesso. ID: " + id), HttpStatus.CREATED);
    }
}
