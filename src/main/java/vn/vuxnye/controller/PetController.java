package vn.vuxnye.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pet")
@Tag(name = "Pet Controller")
@RequiredArgsConstructor
@Slf4j(topic = "PET-CONTROLLER")
@Validated
public class PetController {

}
