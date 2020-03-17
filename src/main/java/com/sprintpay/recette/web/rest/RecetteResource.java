package com.sprintpay.recette.web.rest;

import com.sprintpay.recette.domain.Recette;
import com.sprintpay.recette.repository.RecetteRepository;
import com.sprintpay.recette.web.rest.errors.BadRequestAlertException;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.sprintpay.recette.domain.Recette}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RecetteResource {

    private final Logger log = LoggerFactory.getLogger(RecetteResource.class);

    private static final String ENTITY_NAME = "msRecetteRecette";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RecetteRepository recetteRepository;

    public RecetteResource(RecetteRepository recetteRepository) {
        this.recetteRepository = recetteRepository;
    }

    /**
     * {@code POST  /recettes} : Create a new recette.
     *
     * @param recette the recette to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recette, or with status {@code 400 (Bad Request)} if the recette has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/recettes")
    public ResponseEntity<Recette> createRecette(@Valid @RequestBody Recette recette) throws URISyntaxException {
        log.debug("REST request to save Recette : {}", recette);
        if (recette.getId() != null) {
            throw new BadRequestAlertException("A new recette cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Recette result = recetteRepository.save(recette);
        return ResponseEntity.created(new URI("/api/recettes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /recettes} : Updates an existing recette.
     *
     * @param recette the recette to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recette,
     * or with status {@code 400 (Bad Request)} if the recette is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recette couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/recettes")
    public ResponseEntity<Recette> updateRecette(@Valid @RequestBody Recette recette) throws URISyntaxException {
        log.debug("REST request to update Recette : {}", recette);
        if (recette.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Recette result = recetteRepository.save(recette);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recette.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /recettes} : get all the recettes.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recettes in body.
     */
    @GetMapping("/recettes")
    public List<Recette> getAllRecettes() {
        log.debug("REST request to get all Recettes");
        return recetteRepository.findAll();
    }

    /**
     * {@code GET  /recettes/:id} : get the "id" recette.
     *
     * @param id the id of the recette to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recette, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/recettes/{id}")
    public ResponseEntity<Recette> getRecette(@PathVariable Long id) {
        log.debug("REST request to get Recette : {}", id);
        Optional<Recette> recette = recetteRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(recette);
    }

    
    @GetMapping("/recettesorg/{id}")
    public List<Recette> getRecetteByIdOrg(@PathVariable Long id){
    	log.debug("REST request to get Recette : {}", id);
        return recetteRepository.findByIdOrganisation(id);
    }
    
    
    /**
     * {@code DELETE  /recettes/:id} : delete the "id" recette.
     *
     * @param id the id of the recette to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/recettes/{id}")
    public ResponseEntity<Void> deleteRecette(@PathVariable Long id) {
        log.debug("REST request to delete Recette : {}", id);
        recetteRepository.deleteById(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }
}
