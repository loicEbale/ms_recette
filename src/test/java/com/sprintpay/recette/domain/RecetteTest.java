package com.sprintpay.recette.domain;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.sprintpay.recette.web.rest.TestUtil;

public class RecetteTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Recette.class);
        Recette recette1 = new Recette();
        recette1.setId(1L);
        Recette recette2 = new Recette();
        recette2.setId(recette1.getId());
        assertThat(recette1).isEqualTo(recette2);
        recette2.setId(2L);
        assertThat(recette1).isNotEqualTo(recette2);
        recette1.setId(null);
        assertThat(recette1).isNotEqualTo(recette2);
    }
}
