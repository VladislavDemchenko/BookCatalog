package org.example.bookcatalog.controller;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.checkerframework.checker.units.qual.A;
import org.example.bookcatalog.entity.Catalog;
import org.example.bookcatalog.service.DataService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@WebMvcTest(controllers = CatalogController.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class CatalogControllerTest {

    @Autowired
    private  MockMvc mockMvc;

    @MockBean
    private DataService dataService;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private CatalogController catalogController;



    @Test
    public void testCreateCatalog_Success() throws Exception {
        Catalog catalog = new Catalog();
//        catalog.setName("");
        catalog.setDescription("TEST Description");


        mockMvc.perform(MockMvcRequestBuilders.post("/catalogs/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(catalog)))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateCatalog_NegativeScenario() throws Exception {
        String requestBody = "{\"description\": \"TestDescription\"}"; //empty


        mockMvc.perform(MockMvcRequestBuilders.post("/catalogs/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

    }
}
