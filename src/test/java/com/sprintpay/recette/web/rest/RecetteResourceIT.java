package com.sprintpay.recette.web.rest;

import com.sprintpay.recette.MsRecetteApp;
import com.sprintpay.recette.config.SecurityBeanOverrideConfiguration;
import com.sprintpay.recette.domain.Recette;
import com.sprintpay.recette.repository.RecetteRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link RecetteResource} REST controller.
 */
@SpringBootTest(classes = { SecurityBeanOverrideConfiguration.class, MsRecetteApp.class })

@AutoConfigureMockMvc
@WithMockUser
public class RecetteResourceIT {

    private static final String DEFAULT_INTITULE = "AAAAAAAAAA";
    private static final String UPDATED_INTITULE = "BBBBBBBBBB";

    private static final BigDecimal DEFAULT_MONTANT = new BigDecimal(1);
    private static final BigDecimal UPDATED_MONTANT = new BigDecimal(2);

    private static final Long DEFAULT_ID_ORGANISATION = 1L;
    private static final Long UPDATED_ID_ORGANISATION = 2L;

    @Autowired
    private RecetteRepository recetteRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restRecetteMockMvc;

    private Recette recette;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recette createEntity(EntityManager em) {
        Recette recette = new Recette()
            .intitule(DEFAULT_INTITULE)
            .montant(DEFAULT_MONTANT)
            .idOrganisation(DEFAULT_ID_ORGANISATION);
        return recette;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Recette createUpdatedEntity(EntityManager em) {
        Recette recette = new Recette()
            .intitule(UPDATED_INTITULE)
            .montant(UPDATED_MONTANT)
            .idOrganisation(UPDATED_ID_ORGANISATION);
        return recette;
    }

    @BeforeEach
    public void initTest() {
        recette = createEntity(em);
    }

    @Test
    @Transactional
    public void createRecette() throws Exception {
        int databaseSizeBeforeCreate = recetteRepository.findAll().size();

        // Create the Recette
        restRecetteMockMvc.perform(post("/api/recettes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recette)))
            .andExpect(status().isCreated());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeCreate + 1);
        Recette testRecette = recetteList.get(recetteList.size() - 1);
        assertThat(testRecette.getIntitule()).isEqualTo(DEFAULT_INTITULE);
        assertThat(testRecette.getMontant()).isEqualTo(DEFAULT_MONTANT);
        assertThat(testRecette.getIdOrganisation()).isEqualTo(DEFAULT_ID_ORGANISATION);
    }

    @Test
    @Transactional
    public void createRecetteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = recetteRepository.findAll().size();

        // Create the Recette with an existing ID
        recette.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecetteMockMvc.perform(post("/api/recettes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recette)))
            .andExpect(status().isBadRequest());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void checkIdOrganisationIsRequired() throws Exception {
        int databaseSizeBeforeTest = recetteRepository.findAll().size();
        // set the field null
        recette.setIdOrganisation(null);

        // Create the Recette, which fails.

        restRecetteMockMvc.perform(post("/api/recettes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recette)))
            .andExpect(status().isBadRequest());

        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRecettes() throws Exception {
        // Initialize the database
        recetteRepository.saveAndFlush(recette);

        // Get all the recetteList
        restRecetteMockMvc.perform(get("/api/recettes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recette.getId().intValue())))
            .andExpect(jsonPath("$.[*].intitule").value(hasItem(DEFAULT_INTITULE)))
            .andExpect(jsonPath("$.[*].montant").value(hasItem(DEFAULT_MONTANT.intValue())))
            .andExpect(jsonPath("$.[*].idOrganisation").value(hasItem(DEFAULT_ID_ORGANISATION.intValue())));
    }
    
    @Test
    @Transactional
    public void getRecette() throws Exception {
        // Initialize the database
        recetteRepository.saveAndFlush(recette);

        // Get the recette
        restRecetteMockMvc.perform(get("/api/recettes/{id}", recette.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recette.getId().intValue()))
            .andExpect(jsonPath("$.intitule").value(DEFAULT_INTITULE))
            .andExpect(jsonPath("$.montant").value(DEFAULT_MONTANT.intValue()))
            .andExpect(jsonPath("$.idOrganisation").value(DEFAULT_ID_ORGANISATION.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingRecette() throws Exception {
        // Get the recette
        restRecetteMockMvc.perform(get("/api/recettes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRecette() throws Exception {
        // Initialize the database
        recetteRepository.saveAndFlush(recette);

        int databaseSizeBeforeUpdate = recetteRepository.findAll().size();

        // Update the recette
        Recette updatedRecette = recetteRepository.findById(recette.getId()).get();
        // Disconnect from session so that the updates on updatedRecette are not directly saved in db
        em.detach(updatedRecette);
        updatedRecette
            .intitule(UPDATED_INTITULE)
            .montant(UPDATED_MONTANT)
            .idOrganisation(UPDATED_ID_ORGANISATION);

        restRecetteMockMvc.perform(put("/api/recettes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedRecette)))
            .andExpect(status().isOk());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeUpdate);
        Recette testRecette = recetteList.get(recetteList.size() - 1);
        assertThat(testRecette.getIntitule()).isEqualTo(UPDATED_INTITULE);
        assertThat(testRecette.getMontant()).isEqualTo(UPDATED_MONTANT);
        assertThat(testRecette.getIdOrganisation()).isEqualTo(UPDATED_ID_ORGANISATION);
    }

    @Test
    @Transactional
    public void updateNonExistingRecette() throws Exception {
        int databaseSizeBeforeUpdate = recetteRepository.findAll().size();

        // Create the Recette

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecetteMockMvc.perform(put("/api/recettes").with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(recette)))
            .andExpect(status().isBadRequest());

        // Validate the Recette in the database
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteRecette() throws Exception {
        // Initialize the database
        recetteRepository.saveAndFlush(recette);

        int databaseSizeBeforeDelete = recetteRepository.findAll().size();

        // Delete the recette
        restRecetteMockMvc.perform(delete("/api/recettes/{id}", recette.getId()).with(csrf())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Recette> recetteList = recetteRepository.findAll();
        assertThat(recetteList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
