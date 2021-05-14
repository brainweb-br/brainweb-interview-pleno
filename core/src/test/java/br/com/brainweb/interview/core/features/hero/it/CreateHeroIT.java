package br.com.brainweb.interview.core.features.hero.it;

import br.com.brainweb.interview.core.exceptions.DuplicatedHeroNameException;
import br.com.brainweb.interview.core.features.hero.HeroRepository;
import br.com.brainweb.interview.core.features.hero.Utils;
import br.com.brainweb.interview.core.utils.Constants;
import br.com.brainweb.interview.model.Hero;
import br.com.brainweb.interview.model.PowerStats;
import br.com.brainweb.interview.model.enums.RaceType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.flywaydb.core.Flyway;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasValue;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
//@ActiveProfiles("it") tive problemas ao executar os testes utilizando esse profile
public class CreateHeroIT {

    private final String BASE_URI = "/heros";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private HeroRepository heroRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateHeroWhenSendValidHero() throws Exception {
        Hero hero = Utils.getValidHero();
        hero.setId(null);
        hero.getPowerStats().setId(null);
        hero.setName(hero.getName() + Timestamp.from(Instant.now()));
        mvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(hero)))
                .andExpect(status().isCreated());

        Hero createdHero = heroRepository.findByName(hero.getName());
        assertNotNull(createdHero.getId());

        verify(heroRepository, times(1)).findById(any());
        verify(heroRepository, times(1)).save(any());
    }

    @Test
    void shouldReturnBadRequestWhenNotSendValidHero() throws Exception {
        mvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", hasSize(1)));
    }

    @Test
    void shouldNotCreateHeroWithSameName() throws Exception {
        Hero hero = Utils.getValidHero();
        hero.setId(null);
        hero.getPowerStats().setId(null);

        mvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(hero)));

        mvc.perform(post(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hero))
        ).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", hasValue(Constants.DUPLICATED_HERO_NAME_MESSAGE)));
    }

    @Test
    void shouldGetHeroById() throws Exception {
        mvc.perform(get(BASE_URI + "/" + Utils.getValidHero().getId())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.powerStats", hasSize(1)));

        verify(heroRepository, times(1)).findById(any());
    }

    @Test
    void shouldReturnErrorWhenHeroIdNotFound() throws Exception {
        mvc.perform(get(BASE_URI + "/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", hasSize(0)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.powerStats", hasSize(0)));

        verify(heroRepository, times(1)).findById(any());
    }

}
