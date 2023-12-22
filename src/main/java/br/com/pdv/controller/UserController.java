package br.com.pdv.controller;

import br.com.pdv.dto.ResponseDTO;
import br.com.pdv.dto.UserCreateRequestDTO;
import br.com.pdv.dto.UserEditRequestDTO;
import br.com.pdv.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping()
    public ResponseEntity getAll() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getById(@PathVariable Long id) {
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @PostMapping()
    public ResponseEntity post(@Valid @RequestBody UserCreateRequestDTO userCreateRequestDTO) {
        return new ResponseEntity<>(userService.save(userCreateRequestDTO), HttpStatus.CREATED);
    }

    @PutMapping()
    public ResponseEntity put(@Valid @RequestBody UserEditRequestDTO userEditRequestDTO) {
        return new ResponseEntity<>(userService.edit(userEditRequestDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable Long id) {
        userService.deleteById(id);
        return new ResponseEntity<>(new ResponseDTO("Usuário deletado com sucesso!"), HttpStatus.OK);
    }
}
